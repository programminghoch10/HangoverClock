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
    public static int overhang = 20;
    public static boolean controlsvisible = false;
    public static String CLOCK_WIDGET_UPDATE = "com.JJ.hangoverclock.widgetupdate";
    public static String controlbutton = "controlbuttonclicklistener";
    public static String plusbutton = "plusbuttonclicklistener";
    public static String minusbutton = "minusbuttonclicklistener";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (controlbutton.equals(intent.getAction())) {
            Log.i(TAG, "onReceive: controlbutton pressed");
            controlsvisible = !controlsvisible;
            Log.i(TAG, "onReceive: new controlbutton state: " + controlsvisible);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            for (int appWidgetID: ids) {
                if (controlsvisible) {
                    remoteViews.setViewVisibility(R.id.plus, View.VISIBLE);
                    remoteViews.setViewVisibility(R.id.minus, View.VISIBLE);
                    remoteViews.setTextViewText(R.id.clock, "+" + overhang);
                    Log.i(TAG, "onReceive: enabled controls");
                } else {
                    remoteViews.setViewVisibility(R.id.plus, View.GONE);
                    remoteViews.setViewVisibility(R.id.minus, View.GONE);
                    remoteViews.setTextViewText(R.id.clock, calculatetime((double)Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*60*60+Calendar.getInstance().get(Calendar.MINUTE)*60,overhang));
                    Log.i(TAG, "onReceive: disabled controls");
                }
                appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
            }
        }
        if (plusbutton.equals(intent.getAction())) {
            Log.i(TAG, "onReceive: plusbutton pressed");
            overhang += 10;
            overhang = Math.min(overhang, 60);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            for (int appWidgetID: ids) {
                remoteViews.setTextViewText(R.id.clock, "+" + overhang);
                appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putInt("overhang", overhang);
            editor.apply();
            Log.i(TAG, "onReceive: new overhang value: " + overhang);
        }
        if (minusbutton.equals(intent.getAction())) {
            Log.i(TAG, "onReceive: minusbutton pressed");
            overhang -= 10;
            overhang = Math.max(overhang, 0);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            for (int appWidgetID: ids) {
                remoteViews.setTextViewText(R.id.clock, "+" + overhang);
                appWidgetManager.updateAppWidget(appWidgetID, remoteViews);
            }
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putInt("overhang", overhang);
            editor.apply();
            Log.i(TAG, "onReceive: new overhang value: " + overhang);
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
        Log.i(TAG, "Widget Provider disabled. Turning off timer");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        overhang = sharedPreferences.getInt("overhang", overhang);
        Log.i(TAG, "Widget Provider enabled.  Starting timer to update widget every minute");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 60 - calendar.get(Calendar.SECOND));
        alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000, createClockTickIntent(context));
    }

    public void updateAppWidget(Context context,	AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i("info", "updateAppWidget: i have been called");
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        //int seconds = Calendar.getInstance().get(Calendar.SECOND);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        //remoteViews.setTextViewText(R.id.clock, hour + ":" + minutes);
        remoteViews.setTextViewText(R.id.clock, calculatetime((double)hour*60*60+minutes*60,overhang));
        //remoteViews.setTextViewText(R.id.clock, hour + ":" + minutes + ":" + seconds);
        remoteViews.setOnClickPendingIntent(R.id.controlbutton, getPendingSelfIntent(context, controlbutton));
        remoteViews.setOnClickPendingIntent(R.id.plus, getPendingSelfIntent(context, plusbutton));
        remoteViews.setOnClickPendingIntent(R.id.minus, getPendingSelfIntent(context, minusbutton));
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        Log.i("info", "updateAppWidget: updated widget " + appWidgetId);
    }

    public static String calculatetime(double time, int overhang) {
        //inputs: double time in seconds
        //        int overhang in seconds
        if (overhang>60) overhang = 60;
        int h = (int) Math.floor(time / 60 / 60);
        int m = (int) Math.floor(time / 60) - (h*60);
        //int s = (int) Math.floor(time) - (m*60) - (h*60*60);
        //int ms = (int) ((time - (h*60*60) - (m*60) - s) * 100);
        //final String timetext2 = String.format();
        //final String timetext2 = String.format("%02d", h)+":"+String.format("%02d", m)+":"+String.format("%02d", s)+"."+String.format("%02d", ms);
        if (h>0 & m<overhang) {
            m = m+60;
            if(m>=60) h--;
        }
        /*if (m>0 & s<overhang) {
            s = s+60;
            if(s>=60) m--;
        }*/
        //final String timetext = h+":"+m+":"+s+"."+ms;
        //final String timetext = String.format("%02d", h)+":"+String.format("%02d", m)+":"+String.format("%02d", s)+"."+String.format("%02d", ms);
        return String.format("%02d", h)+":"+String.format("%02d", m);
    }

}
