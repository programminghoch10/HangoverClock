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
import android.support.v4.app.AlarmManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.Calendar;

public class ClockWidgetProvider extends AppWidgetProvider {

    static final String TAG = "ClockWidgetProvider";
    static final int houroverhang = 1;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static int overhang;
    public static String CLOCK_WIDGET_UPDATE = "com.JJ.hangoverclock.widgetupdate";
    public static String controlbutton = "controlbuttonclicklistener";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Log.d(TAG, "onReceive: got intent " + intent.getAction());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getAction().split("#").length != 1) {
            overhang = sharedPreferences.getInt("overhang" + intent.getAction().split("#")[1], overhang);
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
                    onEnabled(context);
                    String timeafter = sharedPreferences.getString("time" + appWidgetID, "");
                    if (!timebefore.equals(timeafter)) switchcheck = false;
                }
            }
            if (!switchcheck) return;
        }

        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            //Log.d(TAG, "onReceive: Recieved Clock Update");
            // Get the widget manager and ids for this widget provider, then call the shared clock update method.
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID: ids) {
                updateAppWidget(context, appWidgetManager, appWidgetID);
            }
            setAlarmManager(context);
        }
    }

    private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(CLOCK_WIDGET_UPDATE);
        return PendingIntent.getBroadcast(context, 23, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        Log.i(TAG, "onDisabled: Final cleanup done, Goodbye!");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i(TAG, "onEnabled: Hello World!");
        setAlarmManager(context);
    }
    private void setAlarmManager(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, (60 - calendar.get(Calendar.SECOND)));
        //Log.d(TAG, "setAlarmManager: scheduling timer: " + ((calendar.getTimeInMillis() - System.currentTimeMillis())/1000) + "s");
        //alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000, createClockTickIntent(context));
        //alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), createClockTickIntent(context));
        AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC, calendar.getTimeInMillis(), createClockTickIntent(context));
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        overhang = sharedPreferences.getInt("overhang" + appWidgetId, overhang);
        boolean twelvehour = sharedPreferences.getBoolean("twelvehour" + appWidgetId, false);
        int hour;
        if (twelvehour) {
            hour = Calendar.getInstance().get(Calendar.HOUR);
        } else {
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        }
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        String time = calculatetime((double)hour*60*60+minutes*60, overhang);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setTextViewText(R.id.clock, time);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time" + appWidgetId, time);
        editor.apply();
        remoteViews.setOnClickPendingIntent(R.id.controlbutton, getPendingSelfIntent(context, controlbutton + "#" + appWidgetId));
        remoteViews.setTextColor(R.id.clock, sharedPreferences.getInt("color" + appWidgetId, context.getResources().getColor(R.color.defaultWidgetColor)));
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
