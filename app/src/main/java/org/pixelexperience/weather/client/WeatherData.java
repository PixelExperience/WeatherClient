package org.pixelexperience.weather.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class WeatherData {
    private static final String PREF_KEY_WEATHER_DATA = "weather_data";

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

    static void setUpdateError(Context context, boolean error) {
        WeatherInfo weatherInfo = getWeatherData(context);
        if (weatherInfo == null) {
            weatherInfo = new WeatherInfo(error ? 0 : 1, "", 0, 0);
        } else {
            weatherInfo.setStatus(error ? 0 : 1);
        }
        setWeatherData(context, weatherInfo);
    }

}
