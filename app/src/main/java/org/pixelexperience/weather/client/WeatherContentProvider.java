package org.pixelexperience.weather.client;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static org.pixelexperience.weather.client.Constants.DEBUG;

public class WeatherContentProvider extends ContentProvider {
    public static final String AUTHORITY = "org.pixelexperience.weather.client.provider";
    private static final String TAG = "WeatherService:WeatherContentProvider";
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

    public static void notify(Context context) {
        context.getContentResolver().notifyChange(Uri.parse("content://" + WeatherContentProvider.AUTHORITY + "/weather"), null);
    }

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

        final MatrixCursor result = new MatrixCursor(PROJECTION_DEFAULT_WEATHER);
        if (DEBUG) Log.i(TAG, "query: " + uri.toString());
        WeatherInfo weather = WeatherData.getWeatherData(getContext());
        if (weather != null) {
            result.newRow()
                    .add(COLUMN_STATUS, weather.getStatus())
                    .add(COLUMN_CONDITIONS, weather.getConditions())
                    .add(COLUMN_TEMPERATURE_METRIC, weather.getTemperature(true))
                    .add(COLUMN_TEMPERATURE_IMPERIAL, weather.getTemperature(false));
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