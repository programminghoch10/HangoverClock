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
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;

//import static android.content.ContentValues.TAG;

public class ClockWidgetProvider extends AppWidgetProvider {
    
    static String TAG = "info";
    static final int houroverhang = 1;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("info", "onUpdate: i have been called");
        //int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        //int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        //int seconds = Calendar.getInstance().get(Calendar.SECOND);
        for (int i = 0; i<appWidgetIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            //remoteViews.setTextViewText(R.id.clock, hour + ":" + minutes);
            //appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
            Log.i("info", "onUpdate: updated widget "+i);

            int appWidgetId = appWidgetIds[i];
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /**
     * Custom Intent name that is used by the AlarmManager to tell us to update the clock once per second.
     */
    public static int overhang;
    public static boolean controlsvisible = false;
    public static String CLOCK_WIDGET_UPDATE = "com.JJ.hangoverclock.widgetupdate";
    public static String controlbutton = "controlbuttonclicklistener";
    public static String plusbutton = "plusbuttonclicklistener";
    public static String minusbutton = "minusbuttonclicklistener";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getAction().split("#").length != 1) {
            overhang = sharedPreferences.getInt("overhang" + intent.getAction().split("#")[1], overhang);
            controlsvisible = sharedPreferences.getBoolean("controlsvisible" + intent.getAction().split("#")[1], false);
        }

        if (controlbutton.equals(intent.getAction().split("#")[0])) {
            Log.i(TAG, "onReceive: controlbutton pressed on widget " + intent.getAction().split("#")[1]);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            if(!controlsvisible) {
                boolean switchcheck = true;
                for (int appWidgetID : ids) {
                    if (!sharedPreferences.getBoolean("controlsvisible" + appWidgetID, false)) {
                        String timebefore = sharedPreferences.getString("time" + appWidgetID, "");
                        updateAppWidget(context, appWidgetManager, appWidgetID);
                        String timeafter = sharedPreferences.getString("time" + appWidgetID, "");
                        Log.i(TAG, "onReceive: comparing widget " + appWidgetID + ", before " + timebefore + " with after " + timeafter + ", result: " + !timebefore.equals(timeafter));
                        if (!timebefore.equals(timeafter)) switchcheck = false;
                    }
                }
                if (!switchcheck) return;
            }
            controlsvisible = !controlsvisible;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("controlsvisible" + intent.getAction().split("#")[1], controlsvisible);
            editor.apply();
            Log.i(TAG, "onReceive: new controlbutton state: " + controlsvisible);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            if (controlsvisible) {
                remoteViews.setViewVisibility(R.id.plus, View.VISIBLE);
                remoteViews.setViewVisibility(R.id.minus, View.VISIBLE);
                remoteViews.setTextViewText(R.id.clock, "+" + overhang);
                Log.i(TAG, "onReceive: enabled controls");
            } else {
                remoteViews.setViewVisibility(R.id.plus, View.GONE);
                remoteViews.setViewVisibility(R.id.minus, View.GONE);
                updateAppWidget(context, appWidgetManager, Integer.valueOf(intent.getAction().split("#")[1]));
                Log.i(TAG, "onReceive: disabled controls");
            }
            appWidgetManager.updateAppWidget(Integer.valueOf(intent.getAction().split("#")[1]), remoteViews);
        }
        if (plusbutton.equals(intent.getAction().split("#")[0])) {
            Log.i(TAG, "onReceive: plusbutton pressed");
            if (overhang>=60) overhang += 60;
            if (overhang<60) overhang += 10;
            //overhang = Math.min(overhang, 60); //Limit of 1 hour
            //overhang = Math.min(overhang, 60*24); //Limit of a day
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setTextViewText(R.id.clock, "+" + overhang);
            appWidgetManager.updateAppWidget(Integer.valueOf(intent.getAction().split("#")[1]), remoteViews);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("overhang" + Integer.valueOf(intent.getAction().split("#")[1]), overhang);
            editor.apply();
            Log.i(TAG, "onReceive: new overhang " + Integer.valueOf(intent.getAction().split("#")[1]) + " value: " + overhang);
        }
        if (minusbutton.equals(intent.getAction().split("#")[0])) {
            Log.i(TAG, "onReceive: minusbutton pressed");
            if (overhang<=60) overhang -= 10;
            if (overhang>60) overhang -= 60;
            overhang = Math.max(overhang, 0);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setTextViewText(R.id.clock, "+" + overhang);
            appWidgetManager.updateAppWidget(Integer.valueOf(intent.getAction().split("#")[1]), remoteViews);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putInt("overhang" + Integer.valueOf(intent.getAction().split("#")[1]), overhang);
            editor.apply();
            Log.i(TAG, "onReceive: new overhang " + Integer.valueOf(intent.getAction().split("#")[1]) + " value: " + overhang);
        }

        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            Log.i(TAG, "Clock update");
            // Get the widget manager and ids for this widget provider, then call the shared
            // clock update method.
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
        Log.i(TAG, "Widget Provider disabled. Turning off timer and deleting all values");
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
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //overhang = sharedPreferences.getInt("overhang", overhang);
        Log.i(TAG, "Widget Provider enabled.  Starting timer to update widget every minute");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60 - calendar.get(Calendar.SECOND));
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000, createClockTickIntent(context));
    }

    public void updateAppWidget(Context context,	AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        overhang = sharedPreferences.getInt("overhang" + appWidgetId, overhang);
        controlsvisible = sharedPreferences.getBoolean("controlsvisible" + appWidgetId, false);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        //int seconds = Calendar.getInstance().get(Calendar.SECOND);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        //remoteViews.setTextViewText(R.id.clock, hour + ":" + minutes);
        String time = calculatetime((double)hour*60*60+minutes*60,overhang);
        if (controlsvisible) {
            remoteViews.setViewVisibility(R.id.plus, View.GONE);
            remoteViews.setViewVisibility(R.id.minus, View.GONE);
            controlsvisible = false;
            Log.i(TAG, "updateAppWidget: disabled controls");
        }
        remoteViews.setTextViewText(R.id.clock, time);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time" + appWidgetId, time);
        editor.apply();
        //remoteViews.setTextViewText(R.id.clock, hour + ":" + minutes + ":" + seconds);
        remoteViews.setOnClickPendingIntent(R.id.controlbutton, getPendingSelfIntent(context, controlbutton + "#" + appWidgetId));
        remoteViews.setOnClickPendingIntent(R.id.plus, getPendingSelfIntent(context, plusbutton + "#" + appWidgetId));
        remoteViews.setOnClickPendingIntent(R.id.minus, getPendingSelfIntent(context, minusbutton + "#" + appWidgetId));
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        Log.i("info", "updateAppWidget: updated widget " + appWidgetId);
    }

    public static String calculatetime(double time, int overhang) {
        //inputs: double time in seconds
        //        int overhang in seconds
        //if (overhang>60) overhang = 60;
        int h = (int) Math.floor(time / 60 / 60);
        int m = (int) Math.floor(time / 60) - (h*60);
        //int s = (int) Math.floor(time) - (m*60) - (h*60*60);
        //int ms = (int) ((time - (h*60*60) - (m*60) - s) * 100);
        //final String timetext2 = String.format();
        //final String timetext2 = String.format("%02d", h)+":"+String.format("%02d", m)+":"+String.format("%02d", s)+"."+String.format("%02d", ms);
        /*if (h>0 & m<overhang) {
            m = m+60;
            if(m>=60) h--;
        }/*
        /*if (m>0 & s<overhang) {
            s = s+60;
            if(s>=60) m--;
        }*/
        while (m<overhang) {
            m = m+60;
            if(m>=60) h--;
            if(h<houroverhang) h+=24;
        }
        //final String timetext = h+":"+m+":"+s+"."+ms;
        //final String timetext = String.format("%02d", h)+":"+String.format("%02d", m)+":"+String.format("%02d", s)+"."+String.format("%02d", ms);
        return String.format("%02d", h)+":"+String.format("%02d", m);
    }

}
