package org.pixelexperience.weather.client;

import android.support.annotation.NonNull;

public class WeatherInfo {

    static final int WEATHER_UPDATE_SUCCESS = 0; // Success
    static final int WEATHER_UPDATE_ERROR = 2; // Error

    private int status;
    private String conditions;
    private int temperatureMetric;
    private int temperatureImperial;

    WeatherInfo(int status, String conditions, int temperatureMetric, int temperatureImperial) {
        this.status = status;
        this.conditions = conditions;
        this.temperatureMetric = temperatureMetric;
        this.temperatureImperial = temperatureImperial;
    }

    int getTemperature(boolean metric) {
        return metric ? this.temperatureMetric : this.temperatureImperial;
    }

    int getStatus() {
        return this.status;
    }

    String getConditions() {
        return this.conditions;
    }

    @NonNull
    @Override
    public String toString() {
        return "WeatherInfo: " +
                "status=" + getStatus() + "," +
                "conditions=" + getConditions() + "," +
                "temperatureMetric=" + getTemperature(true) + "," +
                "temperatureImperial=" + getTemperature(false);
    }
}