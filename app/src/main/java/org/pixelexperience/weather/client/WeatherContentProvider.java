package org.pixelexperience.weather.client;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static org.pixelexperience.weather.client.Constants.DEBUG;

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

    private WeatherChannelApi mWeatherChannelApi;

    @Override
    public boolean onCreate() {
        return true;
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
        mWeatherChannelApi = new WeatherChannelApi(getContext());
        mWeatherChannelApi.queryLocation();
        while (mWeatherChannelApi.isRunning()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        WeatherInfo weatherInfo = mWeatherChannelApi.getResult();
        if (DEBUG) Log.d(TAG,weatherInfo.toString());
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