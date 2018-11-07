package org.pixelexperience.weather.client;

public class WeatherInfo {

    public static final int WEATHER_UPDATE_SUCCESS = 0; // Success
    public static final int WEATHER_UPDATE_RUNNING = 1; // Update running
    public static final int WEATHER_UPDATE_ERROR = 2; // Error

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

    void setStatus(int status) {
        this.status = status;
    }

    String getConditions() {
        return this.conditions;
    }

    @Override
    public String toString() {
        return "WeatherInfo: " +
                "status=" + getStatus() + "," +
                "conditions=" + getConditions() + "," +
                "temperatureMetric=" + getTemperature(true) + "," +
                "temperatureImperial=" + getTemperature(false);
    }
}