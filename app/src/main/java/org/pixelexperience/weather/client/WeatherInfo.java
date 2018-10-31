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

    static WeatherInfo fromSerializedString(String input) {
        if (input == null) {
            return null;
        }

        String[] parts = input.split("\\|");

        int status;
        String conditions;
        int temperatureMetric;
        int temperatureImperial;

        // Parse the core data
        status = Integer.parseInt(parts[0]);
        conditions = parts[1];
        temperatureMetric = Integer.parseInt(parts[2]);
        temperatureImperial = Integer.parseInt(parts[3]);

        return new WeatherInfo(status, conditions, temperatureMetric, temperatureImperial);
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

    String toSerializedString() {
        return String.valueOf(status) + '|' +
                conditions + '|' +
                getTemperature(true) + '|' +
                getTemperature(false);
    }
}