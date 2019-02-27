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
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static org.pixelexperience.weather.client.BuildConfig.DEBUG;
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
    private OkHttpClient mHttpClient;
    @SuppressLint("UseSparseArrays")
    private final Map<Integer, String> skyConditions = new HashMap<>();

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
        mLocationRequest = LocationRequest.create();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mContext = context;
        final File cacheFile = new File(mContext.getCacheDir(), "WeatherChannelApiCacheV2");
        final Cache cache = new Cache(cacheFile, 10 * 1024 * 1024);
        Interceptor REWRITE_RESPONSE_INTERCEPTOR = new Interceptor() {
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
        Interceptor OFFLINE_INTERCEPTOR = new Interceptor() {
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
        skyConditions.put(30, "partly-cloudy");
        skyConditions.put(29, "partly-cloudy-night");
        skyConditions.put(28, "mostly-cloudy");
        skyConditions.put(27, "mostly-cloudy-night");
        skyConditions.put(26, "cloudy");
        skyConditions.put(31, "clear-night");
        skyConditions.put(33, "mostly-clear-night");
        skyConditions.put(32, "sunny");
        skyConditions.put(34, "mostly-sunny");
        skyConditions.put(24, "windy");
        skyConditions.put(14, "snow");
        skyConditions.put(16, "snow");
        skyConditions.put(5, "snow");
        skyConditions.put(13, "snow");
        skyConditions.put(46, "snow");
        skyConditions.put(4, "thunderstorms");
        skyConditions.put(20, "foggy");
        skyConditions.put(38, "scattered-thunderstorms");
        skyConditions.put(47, "scattered-thunderstorms-night");
        skyConditions.put(40, "rain");
        skyConditions.put(12, "rain");
        skyConditions.put(45, "scattered-showers-placeholder");
        skyConditions.put(9, "scattered-showers-placeholder");
        skyConditions.put(11, "scattered-showers-placeholder");
        skyConditions.put(37, "isolated-thunderstorms-placeholder");
    }

    boolean isRunning() {
        return running;
    }

    WeatherInfo getResult() {
        if (isRunning() || mLocationResult == null || mLocationResult.getLastLocation() == null) {
            return new WeatherInfo(WEATHER_UPDATE_ERROR, "", 0, 0);
        }
        Location location = mLocationResult.getLastLocation();
        String latitudeFmt = String.format(Locale.getDefault(), "%.2f", location.getLatitude()).replace(",", ".");
        String longitudeFmt = String.format(Locale.getDefault(), "%.2f", location.getLongitude()).replace(",", ".");
        if (DEBUG) Log.d(TAG, "getResult");
        if (DEBUG)
            Log.d(TAG, "latitude=" + latitudeFmt + ",longitude=" + longitudeFmt);
        try {
            Response response = mHttpClient.newCall(new Request.Builder()
                    .tag("WeatherChannelApi")
                    .url("https://dsx.weather.com/wxd/v2/MORecord/en_US/" + latitudeFmt + "," + longitudeFmt)
                    .build()).execute();
            if (response.body() != null && response.isSuccessful()) {
                String result = response.body().string();
                WeatherChannelApiResult jsonResult = new Gson().fromJson(result, WeatherChannelApiResult.class);
                WeatherChannelApiResult.MOData jsonData = jsonResult.getMOData();
                if (jsonData != null) {
                    String sunCondition = jsonData.getDyNght().toLowerCase();
                    int tempImperial = jsonData.getTmpF();
                    int tempMetric = jsonData.getTmpC();
                    String parsedConditions = parseCondition(jsonData.getWx(), jsonData.getSky(), sunCondition);
                    if (DEBUG)
                        Log.d(TAG, "tempImperial: " + tempImperial + " tempMetric: " + tempMetric + " parsedConditions: " + parsedConditions);
                    return new WeatherInfo(WEATHER_UPDATE_SUCCESS, parsedConditions, tempMetric, tempImperial);
                }
            }
        } catch (Exception e) {
            if (DEBUG) Log.e(TAG, "Exception", e);
        }
        return new WeatherInfo(WEATHER_UPDATE_ERROR, "", 0, 0);
    }

    private String parseCondition(String wx, int skyCondition, String sunCondition) {
        String nightFix = sunCondition.equals("n") ? "-night" : "";
        if (DEBUG)
            Log.d(TAG, "parseCondition: wx: " + wx + " skyCondition: " + Integer.toString(skyCondition) + " nightFix: " + nightFix);
        if (skyConditions.containsKey(skyCondition)) {
            final String condition = skyConditions.get(skyCondition);
            return condition != null ? condition.replace("-placeholder", nightFix) : "";
        } else {
            wx = wx.toLowerCase();
            if (wx.contains("isolated") && (wx.contains("shower") || wx.contains("rain"))) {
                return "rain";
            } else if (wx.contains("rain") || wx.contains("shower")) {
                return "rain";
            } else if (wx.contains("isolated") && wx.contains("snow")) {
                return "snow";
            } else if (wx.contains("frezzing") && wx.contains("drizzle")) {
                return "snow";
            } else if (wx.contains("snow")) {
                return "snow";
            } else if (wx.contains("isolated") && wx.contains("thunderstorm")) {
                return "isolated-thunderstorms" + nightFix;
            }
        }
        return "mostly-cloudy" + nightFix;
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

