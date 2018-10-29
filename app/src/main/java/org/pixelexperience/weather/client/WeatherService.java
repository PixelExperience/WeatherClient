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
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import java.util.Arrays;

import static org.pixelexperience.weather.client.Constants.DEBUG;
import static org.pixelexperience.weather.client.Constants.UPDATE_INTERVAL;

public class WeatherService extends JobService {
    private static final String TAG = "WeatherService";
    private GoogleApiClient mGoogleApiClient;

    public static void scheduleUpdate(Context context, Boolean onBoot) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            if (DEBUG) Log.d(TAG, "Unable to get JobScheduler service");
            return;
        }
        jobScheduler.cancelAll();
        if (onBoot) {
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
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        if (DEBUG) Log.d(TAG, "onStartJob");
        WeatherData.setUpdateError(this, false);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        if (DEBUG) Log.d(TAG, "onConnected");
                        Awareness.SnapshotApi.getWeather(mGoogleApiClient).setResultCallback(new ResultCallback<WeatherResult>() {
                            @Override
                            public void onResult(@NonNull WeatherResult weatherResult) {
                                if (DEBUG) Log.d(TAG, "onResult");
                                if (!weatherResult.getStatus().isSuccess()) {
                                    if (DEBUG) Log.d(TAG, "Could not get weather.");
                                    scheduleUpdate(WeatherService.this, false);
                                    jobFinished(jobParameters, false);
                                    return;
                                }
                                Weather weather = weatherResult.getWeather();
                                String conditions = Arrays.toString(weather.getConditions()).replace("[", "").replace("]", "").replace(" ", "");
                                WeatherInfo weatherInfo = new WeatherInfo(1, conditions, Math.round((weather.getTemperature(Weather.CELSIUS))), Math.round((weather.getTemperature(Weather.FAHRENHEIT))));
                                WeatherData.setWeatherData(WeatherService.this, weatherInfo);
                                if (DEBUG) Log.d(TAG, weatherInfo.toString());
                                scheduleUpdate(WeatherService.this, false);
                                jobFinished(jobParameters, false);
                            }
                        });
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        if (DEBUG) Log.d(TAG, "onConnectionSuspended");
                        WeatherData.setUpdateError(WeatherService.this, true);
                        scheduleUpdate(WeatherService.this, false);
                        jobFinished(jobParameters, false);
                    }
                })
                .build();
        mGoogleApiClient.connect();
        return true;
    }
}