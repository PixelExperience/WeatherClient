package org.pixelexperience.weather.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.pixelexperience.weather.client.Constants.DEBUG;
import static org.pixelexperience.weather.client.WeatherInfo.WEATHER_UPDATE_ERROR;
import static org.pixelexperience.weather.client.WeatherInfo.WEATHER_UPDATE_SUCCESS;

public class WeatherChannelApi implements OnFailureListener, OnCanceledListener {
    private String TAG = "WeatherChannelApi";
    private boolean running;
    private LocationResult mLocationResult;
    private Handler mHandler;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private String mSunCondition;
    private OkHttpClient mHttpClient;
    private SunriseSunsetRestApi mSunriseSunsetRestApi;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (DEBUG) Log.d(TAG, "onLocationResult");
            mFusedLocationClient.removeLocationUpdates(this);
            mLocationResult = locationResult;
            running = false;
        }
    };

    private Runnable removeLocationUpdatesRunnable = new Runnable() {
        @Override
        public void run() {
            if (DEBUG) Log.d(TAG, "removeLocationUpdatesRunnable");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
            mLocationResult = null;
            running = false;
        }
    };

    @Override
    public void onFailure(@NonNull Exception e) {
        if (DEBUG) Log.d(TAG, "onFailure");
        mHandler.removeCallbacks(removeLocationUpdatesRunnable);
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        mLocationResult = null;
        running = false;
    }

    @Override
    public void onCanceled() {
        if (DEBUG) Log.d(TAG, "onCanceled");
        mHandler.removeCallbacks(removeLocationUpdatesRunnable);
        mFusedLocationClient.removeLocationUpdates(locationCallback);
        mLocationResult = null;
        running = false;
    }

    WeatherChannelApi(Context context) {
        running = false;
        mHandler = new Handler(Looper.getMainLooper());
        // power balanced location check (~100 mt precision)
        mLocationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).create();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mContext = context;
        final File cacheFile = new File(mContext.getCacheDir(), "WeatherChannelApiCache");
        final Cache cache = new Cache(cacheFile, 10 * 1024 * 1024);
        mHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(false)
                .followSslRedirects(false)
                .addNetworkInterceptor(REWRITE_RESPONSE_INTERCEPTOR)
                .addInterceptor(new Utils.GzipRequestInterceptor())
                .addInterceptor(OFFLINE_INTERCEPTOR)
                .cache(cache)
                .build();
        mSunriseSunsetRestApi = new SunriseSunsetRestApi(mContext);
    }

    boolean isRunning() {
        return running;
    }

    private final Interceptor REWRITE_RESPONSE_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            String cacheControl = originalResponse.header("Cache-Control");
            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + 10)
                        .build();
            } else {
                return originalResponse;
            }
        }
    };

    private final Interceptor OFFLINE_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!(Utils.isNetworkAvailable(mContext))) {
                request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + Constants.API_CACHE_NO_CONNECTION_MAX_TIME)
                        .build();
            }
            return chain.proceed(request);
        }
    };

    WeatherInfo getResult() {
        if (isRunning() || mLocationResult == null || mLocationResult.getLastLocation() == null) {
            return new WeatherInfo(WEATHER_UPDATE_ERROR, "", 0, 0);
        }
        Location location = mLocationResult.getLastLocation();
        if (DEBUG) Log.d(TAG, "getResult");
        if (DEBUG)
            Log.d(TAG, "latitude=" + location.getLatitude() + ",longitude=" + location.getLongitude());

        try {
            Response response = mHttpClient.newCall(new Request.Builder()
                    .tag("WeatherChannelApi")
                    .url("https://weather.com/weather/today/l/" + location.getLatitude() + "," + location.getLongitude() + "?par=google")
                    .build()).execute();
            if (response.body() != null && response.isSuccessful()) {
                mSunCondition = getSunCondition(location.getLatitude(), location.getLongitude());
                String result = response.body().string();
                Document doc = Jsoup.parse(result);
                Element tempElement = doc.selectFirst("div[class=today_nowcard-temp] span");
                Element conditionIconElement = doc.selectFirst("div[class=today_nowcard-section today_nowcard-condition] div icon");
                String conditionIconElementClassName = conditionIconElement.className().replace("", "");
                String tempImperial = tempElement.text().replace("°", "");
                if (tempImperial.equals("") || conditionIconElementClassName.equals("")) {
                    throw new Exception("tempImperial or conditionIconElementClassName is empty");
                }
                String parsedConditions = parseCondition(conditionIconElement.className());
                int tempMetric = (int) Math.round((Integer.valueOf(tempImperial) - 32.0) * 5 / 9);
                if (DEBUG)
                    Log.d(TAG, "tempImperial: " + tempImperial + " tempMetric: " + tempMetric + " parsedConditions: " + parsedConditions);
                return new WeatherInfo(WEATHER_UPDATE_SUCCESS, parsedConditions, tempMetric, Integer.valueOf(tempImperial));
            }
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "Exception", e);
        }
        return new WeatherInfo(WEATHER_UPDATE_ERROR, "", 0, 0);
    }

    private String parseCondition(String toCompare) {
        String nightFix = mSunCondition.equals("n") ? "-night" : "";
        if (DEBUG)
            Log.d(TAG, "parseCondition: toCompare: " + toCompare + " nightFix: " + nightFix);
        Map<String, String> conditions = new HashMap<>();
        conditions.put("icon-partly-cloudy", "partly-cloudy");
        conditions.put("icon-partly-cloudy-night", "partly-cloudy-night");
        conditions.put("icon-mostly-cloudy", "mostly-cloudy");
        conditions.put("icon-mostly-cloudy-night", "mostly-cloudy-night");
        conditions.put("icon-cloudy", "cloudy");
        conditions.put("icon-clear-night", "clear-night");
        conditions.put("icon-mostly-clear-night", "mostly-clear-night");
        conditions.put("icon-sunny", "sunny");
        conditions.put("icon-mostly-sunny", "mostly-sunny");
        conditions.put("icon-scattered-showers", "scattered-showers" + nightFix);
        conditions.put("icon-isolated-showers", "rain");
        conditions.put("icon-showers", "rain");
        conditions.put("icon-rain", "rain");
        conditions.put("icon-wind", "windy");
        conditions.put("icon-snow", "snow");
        conditions.put("icon-rain-snow", "snow");
        conditions.put("icon-scattered-snow", "snow");
        conditions.put("icon-isolated-snow", "snow");
        conditions.put("icon-freezing-drizzle", "snow");
        conditions.put("icon-scattered-thunderstorms", "scattered-thunderstorms" + nightFix);
        conditions.put("icon-isolated-thunderstorms", "isolated-thunderstorms" + nightFix);
        conditions.put("icon-thunderstorms", "thunderstorms");
        conditions.put("icon-foggy", "foggy");
        for (String condition : conditions.keySet()) {
            if (toCompare.contains(condition + " ")) {
                return conditions.get(condition);
            }
        }
        return "mostly-cloudy" + nightFix;
    }

    private String getSunCondition(double latitude, double longitude) {
        Calendar currentCalendar = GregorianCalendar.getInstance();
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        String sunCondition = (currentHour >= 7 && currentHour <= 18) ? "d" : "n";
        int sunriseSunsetRestApiResult = mSunriseSunsetRestApi.queryApi(Double.toString(latitude), Double.toString(longitude));
        if (sunriseSunsetRestApiResult == SunriseSunsetRestApi.RESULT_DAY) {
            sunCondition = "d";
        } else if (sunriseSunsetRestApiResult == SunriseSunsetRestApi.RESULT_NIGHT) {
            sunCondition = "n";
        } else {
            try {
                TimeZone tz = TimeZone.getDefault();
                com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(latitude, longitude);
                SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, tz.getID());
                Calendar officialSunrise = calculator.getOfficialSunriseCalendarForDate(currentCalendar);
                Calendar officialSunset = calculator.getOfficialSunsetCalendarForDate(currentCalendar);
                if (DEBUG) {
                    Log.d("SunriseSunsetCalculator", "Current time is: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentCalendar.getTime()));
                    Log.d("SunriseSunsetCalculator", "Sunrise time is: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(officialSunrise.getTime()));
                    Log.d("SunriseSunsetCalculator", "Sunset time is: " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(officialSunset.getTime()));
                }
                if (currentCalendar.getTimeInMillis() >= officialSunrise.getTimeInMillis() && currentCalendar.getTimeInMillis() < officialSunset.getTimeInMillis()) {
                    if (DEBUG) Log.d(TAG, "It's day");
                    sunCondition = "d";
                } else {
                    if (DEBUG) Log.d(TAG, "It's night");
                    sunCondition = "n";
                }
            } catch (Exception e) {
                if (DEBUG)
                    Log.e(TAG, "Exception when calculating sunset/sunrise", e);
            }
        }
        return sunCondition;
    }

    @SuppressLint("MissingPermission")
    void queryLocation() {
        if (running) {
            return;
        }
        running = true;
        mLocationResult = null;
        // check location for max LOCATION_QUERY_MAX_TIME seconds
        // and stop the check on the first location result
        mLocationRequest.setExpirationDuration(Constants.LOCATION_QUERY_MAX_TIME)
                .setNumUpdates(1).setInterval(4000).setFastestInterval(2000);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper()).addOnCanceledListener(this).addOnFailureListener(this);
    }
}

