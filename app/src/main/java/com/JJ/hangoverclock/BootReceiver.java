package com.JJ.hangoverclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        ClockWidgetProvider clockWidgetProvider = new ClockWidgetProvider();
        clockWidgetProvider.onEnabled(context);
    }
}
