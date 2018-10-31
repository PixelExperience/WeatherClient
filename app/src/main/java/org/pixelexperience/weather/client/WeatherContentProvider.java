package org.pixelexperience.weather.client;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import static org.pixelexperience.weather.client.Constants.DEBUG;
import static org.pixelexperience.weather.client.Constants.MAX_CONNECTION_ATTEMPTS;
import static org.pixelexperience.weather.client.WeatherInfo.WEATHER_UPDATE_ERROR;
import static org.pixelexperience.weather.client.WeatherInfo.WEATHER_UPDATE_RUNNING;
import static org.pixelexperience.weather.client.WeatherInfo.WEATHER_UPDATE_SUCCESS;

public class WeatherContentProvider extends ContentProvider {
    private static final String TAG = "WeatherClient:WeatherContentProvider";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CONDITIONS = "conditions";
    private static final String COLUMN_TEMPERATURE_METRIC = "temperatureMetric";
    private static final String COLUMN_TEMPERATURE_IMPERIAL = "temperatureImperial";
    private static final String[] PROJECTION_DEFAULT_WEATHER = new String[]{
            COLUMN_STATUS,
            COLUMN_CONDITIONS,
            COLUMN_TEMPERATURE_METRIC,
            COLUMN_TEMPERATURE_IMPERIAL
    };
    private GoogleApiClient mGoogleApiClient;
    private volatile boolean running = false;
    private int connectionAttempts = 1;
    private WeatherInfo weatherInfo;

    private void resetVars() {
        connectionAttempts = 1;
        running = false;
        try{
            mGoogleApiClient.disconnect();
        }catch (Exception ignored){
        }
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @SuppressWarnings("deprecation")
    private void loadWeatherData() {
        weatherInfo = new WeatherInfo(WEATHER_UPDATE_RUNNING, "", 0, 0);
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Awareness.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (DEBUG) Log.d(TAG, "onConnected");
                        Awareness.SnapshotApi.getWeather(mGoogleApiClient).setResultCallback(new ResultCallback<WeatherResult>() {
                            @SuppressWarnings("deprecation")
                            @Override
                            public void onResult(@NonNull final WeatherResult weatherResult) {
                                if (DEBUG) Log.d(TAG, "WeatherResult: onResult");
                                if (!weatherResult.getStatus().isSuccess()) {
                                    connectionAttempts++;
                                    if (connectionAttempts <= MAX_CONNECTION_ATTEMPTS) {
                                        if (DEBUG)
                                            Log.d(TAG, "Could not get weather, trying again (" + connectionAttempts + "/" + MAX_CONNECTION_ATTEMPTS + ")");
                                        loadWeatherData();
                                        return;
                                    }
                                    if (DEBUG) Log.d(TAG, "Could not get weather.");
                                    weatherInfo.setStatus(WEATHER_UPDATE_ERROR);
                                    resetVars();
                                    return;
                                }
                                Awareness.SnapshotApi.getLocation(mGoogleApiClient).setResultCallback(new ResultCallback<LocationResult>() {
                                    @Override
                                    public void onResult(@NonNull LocationResult locationResult) {
                                        if (DEBUG) Log.d(TAG, "LocationResult: onResult");
                                        Calendar currentCalendar = Calendar.getInstance();
                                        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                                        String sunCondition = (currentHour >= 7 && currentHour <= 18) ? "d" : "n";
                                        if (!locationResult.getStatus().isSuccess()) {
                                            if (DEBUG) Log.d(TAG, "Could not get user location");
                                        } else {
                                            try {
                                                TimeZone tz = TimeZone.getDefault();
                                                Location location = new Location(locationResult.getLocation().getLatitude(), locationResult.getLocation().getLongitude());
                                                SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, tz.getID());
                                                Calendar officialSunrise = calculator.getOfficialSunriseCalendarForDate(currentCalendar);
                                                Calendar officialSunset = calculator.getOfficialSunsetCalendarForDate(currentCalendar);
                                                if (currentCalendar.getTimeInMillis() >= officialSunrise.getTimeInMillis() && currentCalendar.getTimeInMillis() < officialSunset.getTimeInMillis()) {
                                                    sunCondition = "d";
                                                } else {
                                                    sunCondition = "n";
                                                }
                                            } catch (Exception e) {
                                                Log.e(TAG, "Exception when calculating sunset/sunrise", e);
                                            }
                                        }
                                        Weather weather = weatherResult.getWeather();
                                        String conditions = sunCondition + "," + Arrays.toString(weather.getConditions()).replace("[", "").replace("]", "").replace(" ", "");
                                        weatherInfo = new WeatherInfo(WEATHER_UPDATE_SUCCESS, conditions, Math.round((weather.getTemperature(Weather.CELSIUS))), Math.round((weather.getTemperature(Weather.FAHRENHEIT))));
                                        if (DEBUG) Log.d(TAG, weatherInfo.toString());
                                        resetVars();
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        connectionAttempts++;
                        if (connectionAttempts <= MAX_CONNECTION_ATTEMPTS) {
                            if (DEBUG)
                                Log.d(TAG, "onConnectionSuspended, trying to reconnect (" + connectionAttempts + "/" + MAX_CONNECTION_ATTEMPTS + ")");
                            loadWeatherData();
                            return;
                        }
                        if (DEBUG) Log.d(TAG, "onConnectionSuspended");
                        weatherInfo.setStatus(WEATHER_UPDATE_ERROR);
                        resetVars();
                    }
                })
                .build();
        if (connectionAttempts > 1) {
            SystemClock.sleep(5000);
        }
        mGoogleApiClient.connect();
    }

    @Override
    public Cursor query(
            @NonNull Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        if (!Utils.isBuildValid(getContext())) {
            Log.e(TAG, "It seems that you're not using PixelExperience. Please update your sources or in case of cherry-picking our stuff, please revert this and build your own WeatherClient.");
            return null;
        }

        if (DEBUG) Log.i(TAG, "query: " + uri.toString());
        resetVars();
        running = true;
        try {
            loadWeatherData();
        } catch (Exception e) {
            Log.e(TAG, "Exception on loadWeatherData", e);
        }
        while (running) {
            SystemClock.sleep(1000);
        }
        resetVars();

        final MatrixCursor result = new MatrixCursor(PROJECTION_DEFAULT_WEATHER);
        if (weatherInfo != null) {
            result.newRow()
                    .add(COLUMN_STATUS, weatherInfo.getStatus())
                    .add(COLUMN_CONDITIONS, weatherInfo.getConditions())
                    .add(COLUMN_TEMPERATURE_METRIC, weatherInfo.getTemperature(true))
                    .add(COLUMN_TEMPERATURE_IMPERIAL, weatherInfo.getTemperature(false));
            return result;
        }

        return null;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}