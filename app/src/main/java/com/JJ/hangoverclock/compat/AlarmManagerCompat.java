package com.JJ.hangoverclock.compat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Build;

public class AlarmManagerCompat {
    public static void setExact(AlarmManager alarmManager, int type, long triggerAtMillis, PendingIntent operation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setWindow(android.app.AlarmManager.RTC, triggerAtMillis, 1000, operation);
            return;
        }
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(type, triggerAtMillis, operation);
        } else {
            alarmManager.set(type, triggerAtMillis, operation);
        }
    }
}
