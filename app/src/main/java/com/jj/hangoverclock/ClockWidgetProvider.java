package com.JJ.hangoverclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;

public class ClockWidgetProvider extends AppWidgetProvider {

    static final int houroverhang = 1;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static int overhang;
    public static boolean controlsvisible = false;
    public static String CLOCK_WIDGET_UPDATE = "com.JJ.hangoverclock.widgetupdate";
    public static String controlbutton = "controlbuttonclicklistener";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getAction().split("#").length != 1) {
            overhang = sharedPreferences.getInt("overhang" + intent.getAction().split("#")[1], overhang);
            controlsvisible = sharedPreferences.getBoolean("controlsvisible" + intent.getAction().split("#")[1], false);
        }

        if (controlbutton.equals(intent.getAction().split("#")[0])) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            boolean switchcheck = true;
            for (int appWidgetID : ids) {
                if (!sharedPreferences.getBoolean("controlsvisible" + appWidgetID, false)) {
                    String timebefore = sharedPreferences.getString("time" + appWidgetID, "");
                    updateAppWidget(context, appWidgetManager, appWidgetID);
                    String timeafter = sharedPreferences.getString("time" + appWidgetID, "");
                    if (!timebefore.equals(timeafter)) switchcheck = false;
                }
            }
            if (!switchcheck) return;
        }

        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            // Get the widget manager and ids for this widget provider, then call the shared clock update method.
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID: ids) {
                updateAppWidget(context, appWidgetManager, appWidgetID);
            }
        }
    }

    private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(CLOCK_WIDGET_UPDATE);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60 - calendar.get(Calendar.SECOND));
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000, createClockTickIntent(context));
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        overhang = sharedPreferences.getInt("overhang" + appWidgetId, overhang);
        controlsvisible = sharedPreferences.getBoolean("controlsvisible" + appWidgetId, false);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        String time = calculatetime((double)hour*60*60+minutes*60,overhang);
        remoteViews.setTextViewText(R.id.clock, time);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time" + appWidgetId, time);
        editor.apply();
        remoteViews.setOnClickPendingIntent(R.id.controlbutton, getPendingSelfIntent(context, controlbutton + "#" + appWidgetId));
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static String calculatetime(double time, int overhang) {
        //inputs: double time in seconds
        //        int overhang in seconds
        int h = (int) Math.floor(time / 60 / 60);
        int m = (int) Math.floor(time / 60) - (h*60);
        while (m<overhang) {
            m = m+60;
            if(m>=60) h--;
            if(h<houroverhang) h+=24;
        }
        return String.format("%02d", h)+":"+String.format("%02d", m);
    }

}
