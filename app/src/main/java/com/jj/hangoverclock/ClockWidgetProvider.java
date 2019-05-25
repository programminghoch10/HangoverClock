package com.jj.hangoverclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

public class ClockWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        Log.i("info", "onUpdate: i have been called");
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);
        for (int i = 0; i<appWidgetIds.length; i++) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setTextViewText(R.id.clock, hour + ":" + minutes + ":" + seconds);
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
            Log.i("info", "onUpdate: updated widget "+i);
        }
    }

}
