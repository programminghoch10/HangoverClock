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
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.AlarmManagerCompat;

import java.util.Calendar;

public class WidgetProvider extends AppWidgetProvider {
	
	static final String TAG = "WidgetProvider";
	
	private static String CLOCK_WIDGET_UPDATE = "com.JJ.hangoverclock.widgetupdate";
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
		setAlarmManager(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), Context.MODE_PRIVATE);
		if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
			ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
			boolean increaserefreshrate = false;
			for (int appWidgetID : ids) {
				if (sharedPreferences.getBoolean("enableseconds" + appWidgetID,
						context.getResources().getBoolean(R.bool.widgetdefaultenableseconds)))
					increaserefreshrate = true;
				updateAppWidget(context, appWidgetManager, appWidgetID);
			}
			SharedPreferences.Editor editor = sharedPreferences.edit();
			if (context.getResources().getBoolean(R.bool.alwayssavepreference)
					| increaserefreshrate != context.getResources().getBoolean(R.bool.widgetdefaultincreaserefreshrate))
				editor.putBoolean("increaserefreshrate", increaserefreshrate);
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
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), Context.MODE_PRIVATE);
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
		FontsProvider.collectfonts(context);
	}
	
	private void setAlarmManager(Context context) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		if (context.getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), Context.MODE_PRIVATE).getBoolean(
				"increaserefreshrate",
				context.getResources().getBoolean(R.bool.widgetdefaultincreaserefreshrate))) {
			calendar.add(Calendar.SECOND, 1);
		} else {
			calendar.add(Calendar.SECOND, (60 - calendar.get(Calendar.SECOND)));
		}
		AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC, calendar.getTimeInMillis(), createClockTickIntent(context));
	}
	
	public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), Context.MODE_PRIVATE);
		int houroverhang = sharedPreferences.getInt("houroverhang" + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaulthouroverhang));
		int minuteoverhang = sharedPreferences.getInt("minuteoverhang" + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultminuteoverhang));
		int secondoverhang = sharedPreferences.getInt("secondoverhang" + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultsecondoverhang));
		int dayoverhang = sharedPreferences.getInt("dayoverhang" + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultdayoverhang));
		int monthoverhang = sharedPreferences.getInt("monthoverhang" + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultmonthoverhang));
		boolean twelvehour = sharedPreferences.getBoolean("twelvehours" + appWidgetId, !DateFormat.is24HourFormat(context));
		boolean enableseconds = sharedPreferences.getBoolean("enableseconds" + appWidgetId, context.getResources().getBoolean(R.bool.widgetdefaultenableseconds));
		boolean enabledate = sharedPreferences.getBoolean("enabledate" + appWidgetId, context.getResources().getBoolean(R.bool.widgetdefaultenabledate));
		String font = sharedPreferences.getString("font" + appWidgetId, context.getResources().getString(R.string.defaultfonttext));
		float fontscale = sharedPreferences.getFloat("fontscale" + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultfontscale));
		int color = sharedPreferences.getInt("color" + appWidgetId, context.getResources().getColor(R.color.widgetdefaultclockcolor));
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
		remoteViews.setImageViewBitmap(R.id.clock,
				ClockGenerator.generateWidget(
						context, Calendar.getInstance().getTimeInMillis(),
						secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
						twelvehour, enableseconds, enabledate, font, color, fontscale)
		);
		try {
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		} catch (Exception e) {
			Log.e(TAG, "updateAppWidget: error pushing bitmap", e);
		}
	}
}
