package com.JJ.hangoverclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.AlarmManagerCompat;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

public class ClockWidgetProvider extends AppWidgetProvider {
    
    static final String TAG = "ClockWidgetProvider";
    public static ArrayList<String> fonts = new ArrayList<String>() {{
        add("default");
    }};

    private static String CLOCK_WIDGET_UPDATE = "com.JJ.hangoverclock.widgetupdate";
    
    public static void collectfonts(Context context) {
        Field[] fontfields = R.font.class.getFields();
        fonts.set(0, context.getString(R.string.defaultfonttext));
        for (int i = 1; i < fontfields.length + 1; i++) {
            Field fontfield = fontfields[i - 1];
            String fontname;
            try {
                fontname = fontfield.getName().replace("_", " ");
            } catch (Exception e) {
                fontname = null;
            }
            //Log.d(TAG, "collectfonts: fontname " + i + " is \"" + fontname + "\"");
            if (fontname != null) {
                boolean indexexists;
                try {
                    //noinspection ResultOfMethodCallIgnored
                    fonts.get(i);
                    indexexists = true;
                } catch (IndexOutOfBoundsException indexerr) {
                    indexexists = false;
                }
                if (!indexexists) {
                    fonts.add(i, fontname);
                } else {
                    fonts.set(i, fontname);
                }
            }
        }
    }
    
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            updateAppWidget(context, appWidgetManager, appWidgetId);
            onEnabled(context);
        }
        setAlarmManager(context);
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            boolean increaserefreshrate = false;
            for (int appWidgetID : ids) {
                if (sharedPreferences.getBoolean(context.getResources().getString(R.string.keyenableseconds) + appWidgetID,
                        context.getResources().getBoolean(R.bool.defaultenableseconds)))
                    increaserefreshrate = true;
                updateAppWidget(context, appWidgetManager, appWidgetID);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (context.getResources().getBoolean(R.bool.alwayssavepreference)
                    | increaserefreshrate != context.getResources().getBoolean(R.bool.defaultincreaserefreshrate))
                editor.putBoolean(context.getResources().getString(R.string.keyincreaserefreshrate), increaserefreshrate);
            editor.apply();
            setAlarmManager(context);
        }
    }
    
    private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(CLOCK_WIDGET_UPDATE);
        return PendingIntent.getBroadcast(context, 23, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    
    @SuppressLint("ApplySharedPref")
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit(); //commit is intentional, i want to wait until cleanup is done
        Log.i(TAG, "Final cleanup done, Goodbye!");
    }
    
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i(TAG, "Hello World!");
        setAlarmManager(context);
        collectfonts(context);
    }
    
    private void setAlarmManager(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                context.getResources().getString(R.string.keyincreaserefreshrate),
                context.getResources().getBoolean(R.bool.defaultincreaserefreshrate))) {
            calendar.add(Calendar.SECOND, 1);
        } else {
            calendar.add(Calendar.SECOND, (60 - calendar.get(Calendar.SECOND)));
        }
        AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC, calendar.getTimeInMillis(), createClockTickIntent(context));
    }
    
    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int houroverhang = sharedPreferences.getInt(context.getResources().getString(R.string.keyhouroverhang) + appWidgetId, context.getResources().getInteger(R.integer.defaulthouroverhang));
        int minuteoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.keyminuteoverhang) + appWidgetId, context.getResources().getInteger(R.integer.defaultminuteoverhang));
        int secondoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.keysecondoverhang) + appWidgetId, context.getResources().getInteger(R.integer.defaultsecondoverhang));
        int dayoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.keydayoverhang) + appWidgetId, context.getResources().getInteger(R.integer.defaultdayoverhang));
        int monthoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.keymonthoverhang) + appWidgetId, context.getResources().getInteger(R.integer.defaultmonthoverhang));
        boolean twelvehour = sharedPreferences.getBoolean(context.getResources().getString(R.string.keytwelvehour) + appWidgetId, !DateFormat.is24HourFormat(context));
        boolean enableseconds = sharedPreferences.getBoolean(context.getResources().getString(R.string.keyenableseconds) + appWidgetId, context.getResources().getBoolean(R.bool.defaultenableseconds));
        boolean enabledate = sharedPreferences.getBoolean(context.getResources().getString(R.string.keyenabledate) + appWidgetId, context.getResources().getBoolean(R.bool.defaultenabledate));
        String font = sharedPreferences.getString(context.getResources().getString(R.string.keyfont) + appWidgetId, context.getResources().getString(R.string.defaultfonttext));
        float fontscale = sharedPreferences.getFloat(context.getResources().getString(R.string.keyfontscale) + appWidgetId, context.getResources().getInteger(R.integer.defaultdatefontscale));
        int color = sharedPreferences.getInt(context.getResources().getString(R.string.keycolor) + appWidgetId, context.getResources().getColor(R.color.defaultWidgetColor));
        int fontresolution = context.getResources().getInteger(R.integer.widgetfontresolution);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setImageViewBitmap(R.id.clock,
                WidgetGenerator.generateWidget(
                        context, Calendar.getInstance().getTimeInMillis(),
                        secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
                        twelvehour, enableseconds, enabledate, font, color, fontscale, fontresolution)
        );
        try {
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        } catch (Exception e) {
            Log.e(TAG, "updateAppWidget: error pushing bitmap", e);
        }
    }
}
