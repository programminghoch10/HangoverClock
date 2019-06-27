package com.JJ.hangoverclock;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive: BOOT COMPLETE RECEIVED");
        ClockWidgetProvider clockWidgetProvider = new ClockWidgetProvider();
        clockWidgetProvider.onEnabled(context);
    }
}
