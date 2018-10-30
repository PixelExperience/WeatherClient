package org.pixelexperience.weather.client;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.TaskParams;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import static org.pixelexperience.weather.client.Constants.DEBUG;
import static org.pixelexperience.weather.client.Constants.MAX_CONNECTION_ATTEMPTS;
import static org.pixelexperience.weather.client.Constants.UPDATE_INTERVAL;
import static org.pixelexperience.weather.client.WeatherData.WEATHER_UPDATE_ERROR;
import static org.pixelexperience.weather.client.WeatherData.WEATHER_UPDATE_NO_DATA;
import static org.pixelexperience.weather.client.WeatherData.WEATHER_UPDATE_RUNNING;
import static org.pixelexperience.weather.client.WeatherData.WEATHER_UPDATE_SUCCESS;

public class WeatherService extends GcmTaskService {
    private static final String TAG = "WeatherService";
    private GoogleApiClient mGoogleApiClient;
    private volatile boolean running = false;
    int connectionAttempts;

    public static void scheduleUpdate(Context context, Boolean onBoot) {
        if (!Utils.isBuildValid()) {
            return;
        }
        GcmNetworkManager.getInstance(context).cancelAllTasks(WeatherService.class);
        if (onBoot) {
            WeatherData.setUpdateStatus(context, WEATHER_UPDATE_NO_DATA);
            OneoffTask task = new OneoffTask.Builder()
                    .setTag(TAG)
                    .setService(WeatherService.class)
                    .setExecutionWindow(5, 15)
                    .setUpdateCurrent(true)
                    .setPersisted(true)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(task);
        } else {
            PeriodicTask task = new PeriodicTask.Builder()
                    .setTag(TAG)
                    .setService(WeatherService.class)
                    .setPeriod(UPDATE_INTERVAL)
                    .setUpdateCurrent(true)
                    .setPersisted(true)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(task);
        }
    }


    @Override
    public void onInitializeTasks() {
        super.onInitializeTasks();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        if (!Utils.isBuildValid() || running) {
            if (DEBUG) Log.d(TAG, "onRunTask already running");
            return GcmNetworkManager.RESULT_SUCCESS;
        }
        if (DEBUG) Log.d(TAG, "onRunTask");
        connectionAttempts = 0;
        running = true;
        try {
            loadWeatherData();
        } catch (Exception e) {
            running = false;
            e.printStackTrace();
        }
        while (running) {
            SystemClock.sleep(1000);
        }
        if (DEBUG) Log.d(TAG, "Task completed");
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    @SuppressWarnings("deprecation")
    private void loadWeatherData() {
        if (!Utils.isBuildValid()) {
            return;
        }
        WeatherData.setUpdateStatus(WeatherService.this, WEATHER_UPDATE_RUNNING);
        mGoogleApiClient = new GoogleApiClient.Builder(WeatherService.this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (DEBUG) Log.d(TAG, "onConnected");
                        Awareness.SnapshotApi.getWeather(mGoogleApiClient).setResultCallback(new ResultCallback<WeatherResult>() {
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
                                    WeatherData.setUpdateStatus(WeatherService.this, WEATHER_UPDATE_ERROR);
                                    connectionAttempts = 0;
                                    scheduleUpdate(WeatherService.this, false);
                                    running = false;
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
                                            TimeZone tz = TimeZone.getDefault();
                                            Location location = new Location(locationResult.getLocation().getLatitude(), locationResult.getLocation().getLongitude());
                                            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, tz.getID());
                                            Calendar officialSunset = calculator.getOfficialSunsetCalendarForDate(currentCalendar);
                                            if (currentCalendar.getTimeInMillis() >= officialSunset.getTimeInMillis()) {
                                                sunCondition = "n";
                                            } else {
                                                sunCondition = "d";
                                            }
                                        }
                                        Weather weather = weatherResult.getWeather();
                                        String conditions = sunCondition + "," + Arrays.toString(weather.getConditions()).replace("[", "").replace("]", "").replace(" ", "");
                                        WeatherInfo weatherInfo = new WeatherInfo(WEATHER_UPDATE_SUCCESS, conditions, Math.round((weather.getTemperature(Weather.CELSIUS))), Math.round((weather.getTemperature(Weather.FAHRENHEIT))));
                                        WeatherData.setWeatherData(WeatherService.this, weatherInfo);
                                        if (DEBUG) Log.d(TAG, weatherInfo.toString());
                                        connectionAttempts = 0;
                                        scheduleUpdate(WeatherService.this, false);
                                        running = false;
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
                        WeatherData.setUpdateStatus(WeatherService.this, WEATHER_UPDATE_ERROR);
                        connectionAttempts = 0;
                        scheduleUpdate(WeatherService.this, false);
                        running = false;
                    }
                })
                .build();
        if (connectionAttempts > 0) {
            SystemClock.sleep(10000);
        }
        mGoogleApiClient.connect();
    }
}