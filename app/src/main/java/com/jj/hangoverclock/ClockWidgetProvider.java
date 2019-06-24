package com.JJ.hangoverclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
    public static String CLOCK_WIDGET_UPDATE = "com.JJ.hangoverclock.widgetupdate";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

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
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
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
        Log.i(TAG, "Widget Provider enabled.  Starting timer to update widget every minute");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.MINUTE, 1);
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 60000, createClockTickIntent(context));
    }

    public static void updateAppWidget(Context context,	AppWidgetManager appWidgetManager, int appWidgetId) {
        Log.i("info", "updateAppWidget: i have been called");
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        remoteViews.setTextViewText(R.id.clock, hour + ":" + minutes + ":" + seconds);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        Log.i("info", "updateAppWidget: updated widget " + appWidgetId);
    }
}
