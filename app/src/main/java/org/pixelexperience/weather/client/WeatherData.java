package org.pixelexperience.weather.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class WeatherData {
    private static final String PREF_KEY_WEATHER_DATA = "weather_data";
    static final int WEATHER_UPDATE_SUCCESS = 0; // Success
    static final int WEATHER_UPDATE_RUNNING = 1; // Update running
    static final int WEATHER_UPDATE_NO_DATA = 2; // On boot event
    static final int WEATHER_UPDATE_ERROR = 3; // Error

    static WeatherInfo getWeatherData(Context context) {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        String str = prefs.getString(PREF_KEY_WEATHER_DATA, null);
        if (str != null) {
            return WeatherInfo.fromSerializedString(str);
        }
        return null;
    }

    @SuppressLint("ApplySharedPref")
    static void setWeatherData(Context context, WeatherInfo data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(PREF_KEY_WEATHER_DATA, data.toSerializedString()).commit();
        WeatherContentProvider.notify(context);
    }

    static void setUpdateStatus(Context context, int status) {
        WeatherInfo weatherInfo = getWeatherData(context);
        if (weatherInfo == null) {
            weatherInfo = new WeatherInfo(status, "", 0, 0);
        } else {
            weatherInfo.setStatus(status);
        }
        setWeatherData(context, weatherInfo);
    }

}
