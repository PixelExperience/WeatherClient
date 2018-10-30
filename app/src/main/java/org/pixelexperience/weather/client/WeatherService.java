package org.pixelexperience.weather.client;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
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

import static org.pixelexperience.weather.client.Constants.*;
import static org.pixelexperience.weather.client.WeatherData.WEATHER_UPDATE_SUCCESS;
import static org.pixelexperience.weather.client.WeatherData.WEATHER_UPDATE_RUNNING;
import static org.pixelexperience.weather.client.WeatherData.WEATHER_UPDATE_NO_DATA;
import static org.pixelexperience.weather.client.WeatherData.WEATHER_UPDATE_ERROR;

public class WeatherService extends JobService {
    private static final String TAG = "WeatherService";
    private GoogleApiClient mGoogleApiClient;
    boolean isRunning = false;
    boolean jobCancelled = false;

    public static void scheduleUpdate(Context context, Boolean onBoot) {
        if (!Utils.isBuildValid()){
            return;
        }
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            Log.d(TAG, "Unable to get JobScheduler service");
            return;
        }
        jobScheduler.cancelAll();
        if (onBoot) {
            WeatherData.setUpdateStatus(context, WEATHER_UPDATE_NO_DATA);
            jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(context, WeatherService.class))
                    .setPeriodic(UPDATE_INTERVAL)
                    .setPersisted(true)
                    .build());
        } else {
            jobScheduler.schedule(new JobInfo.Builder(0, new ComponentName(context, WeatherService.class))
                    .setMinimumLatency(UPDATE_INTERVAL)
                    .setPersisted(true)
                    .build());
        }
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (!Utils.isBuildValid()){
            return false;
        }
        jobCancelled = true;
        WeatherData.setUpdateStatus(WeatherService.this, WEATHER_UPDATE_ERROR);
        boolean needsReschedule = isRunning;
        jobFinished(jobParameters, needsReschedule);
        return needsReschedule;
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        if (!Utils.isBuildValid()){
            return true;
        }
        if (DEBUG) Log.d(TAG, "onStartJob");
        isRunning = true;
        try{
            loadWeatherData(jobParameters);
        }catch (Exception e){
            e.printStackTrace();
        }
        return isRunning;
    }

    private void loadWeatherData(final JobParameters jobParameters) {
        if (!Utils.isBuildValid()){
            return;
        }
        new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                WeatherData.setUpdateStatus(WeatherService.this, WEATHER_UPDATE_RUNNING);
                mGoogleApiClient = new GoogleApiClient.Builder(WeatherService.this)
                        .addApi(Awareness.API)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                if (jobCancelled)
                                    return;
                                if (DEBUG) Log.d(TAG, "onConnected");
                                Awareness.SnapshotApi.getWeather(mGoogleApiClient).setResultCallback(new ResultCallback<WeatherResult>() {
                                    @Override
                                    public void onResult(@NonNull final WeatherResult weatherResult) {
                                        if (jobCancelled)
                                            return;
                                        if (DEBUG) Log.d(TAG, "WeatherResult: onResult");
                                        if (!weatherResult.getStatus().isSuccess()) {
                                            if (DEBUG) Log.d(TAG, "Could not get weather.");
                                            WeatherData.setUpdateStatus(WeatherService.this, WEATHER_UPDATE_ERROR);
                                            scheduleUpdate(WeatherService.this, false);
                                            isRunning = false;
                                            jobFinished(jobParameters, false);
                                            return;
                                        }
                                        Awareness.SnapshotApi.getLocation(mGoogleApiClient).setResultCallback(new ResultCallback<LocationResult>() {
                                            @Override
                                            public void onResult(@NonNull LocationResult locationResult) {
                                                if (jobCancelled)
                                                    return;
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
                                                scheduleUpdate(WeatherService.this, false);
                                                isRunning = false;
                                                jobFinished(jobParameters, false);
                                            }
                                        });
                                    }
                                });
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                if (jobCancelled)
                                    return;
                                if (DEBUG) Log.d(TAG, "onConnectionSuspended");
                                WeatherData.setUpdateStatus(WeatherService.this, WEATHER_UPDATE_ERROR);
                                scheduleUpdate(WeatherService.this, false);
                                isRunning = false;
                                jobFinished(jobParameters, false);
                            }
                        })
                        .build();
                mGoogleApiClient.connect();
            }
        }).start();
    }
}