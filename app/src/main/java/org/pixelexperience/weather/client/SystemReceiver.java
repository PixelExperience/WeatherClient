package org.pixelexperience.weather.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static org.pixelexperience.weather.client.Constants.DEBUG;

public class SystemReceiver extends BroadcastReceiver {
    private static final String TAG = "WeatherService:SystemReceiver";

    @Override
    public void onReceive(final Context context, Intent intent) {
        final String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) && Utils.isBuildValid()) {
            if (DEBUG) Log.d(TAG, "boot completed");
            WeatherService.scheduleUpdate(context, true);
        }
    }
}