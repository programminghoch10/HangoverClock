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
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WidgetProvider extends AppWidgetProvider {
	
	static final String TAG = "WidgetProvider";
	
	static final String CLOCK_WIDGET_UPDATE_INTENT_TAG = "com.JJ.hangoverclock.widgetupdate";
	static final String CLOCK_WIDGET_UPDATE_UNIQUE_WORK_TAG = "widgetUpdate";
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}
		schedule(context);
	}
	
	@SuppressLint("ApplySharedPref")
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), Context.MODE_PRIVATE);
		if (CLOCK_WIDGET_UPDATE_INTENT_TAG.equals(intent.getAction())) {
			ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
			AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
			int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
			boolean increaserefreshrate = false;
			for (int appWidgetID : ids) {
				if (sharedPreferences.getBoolean(context.getResources().getString(R.string.widgetkeyenableseconds) + appWidgetID,
						context.getResources().getBoolean(R.bool.widgetdefaultenableseconds)))
					increaserefreshrate = true;
				updateAppWidget(context, appWidgetManager, appWidgetID);
			}
			boolean refreshrateChanged = increaserefreshrate != getIncreaseRefreshRate(context);
			if (context.getResources().getBoolean(R.bool.alwayssavepreference) || refreshrateChanged) {
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean(context.getResources().getString(R.string.widgetkeyincreaserefreshrate), increaserefreshrate);
				if (refreshrateChanged) {
					editor.commit(); //commit intentional, schedule depends on saved values
					schedule(context);
				} else {
					editor.apply(); //we dont need to reschedule, so saving may be asyncronous
				}
			}
		}
		setAlarmManager(context, getIncreaseRefreshRate(context) ? 1 : 60);
	}
	
	static PendingIntent createClockTickIntent(Context context) {
		Intent intent = new Intent(context, WidgetProvider.class);
		intent.setAction(CLOCK_WIDGET_UPDATE_INTENT_TAG);
		return PendingIntent.getBroadcast(context, 23, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	@SuppressLint("ApplySharedPref")
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		cancelschedule(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.commit(); //commit is intentional, cleanup should be done when method exits
		Log.i(TAG, "Final cleanup done, Goodbye!");
	}
	
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		Log.i(TAG, "Hello World!");
		schedule(context);
		FontsProvider.collectfonts(context);
	}
	
	private void schedule(Context context) {
		int seconds;
		if (getIncreaseRefreshRate(context)) {
			seconds = 1;
		} else {
			seconds = 60;
		}
		int offset = (60 - Calendar.getInstance().get(Calendar.SECOND));
		Log.d(TAG, "schedule: scheduling " + seconds + "s with " + offset + "s initial delay");
		setWorkManager(context, seconds, offset);
		setAlarmManager(context, offset);
	}
	
	private boolean getIncreaseRefreshRate(Context context) {
		return context.getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), Context.MODE_PRIVATE).getBoolean(
				context.getResources().getString(R.string.widgetkeyincreaserefreshrate),
				context.getResources().getBoolean(R.bool.widgetdefaultincreaserefreshrate));
	}
	
	private void cancelschedule(Context context) {
		WorkManager.getInstance(context).cancelUniqueWork(CLOCK_WIDGET_UPDATE_UNIQUE_WORK_TAG);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(createClockTickIntent(context));
		Log.d(TAG, "cancelschedule: cancelled clock update handlers");
	}
	
	private void setAlarmManager(Context context, int seconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, seconds);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC, calendar.getTimeInMillis(), createClockTickIntent(context));
	}
	
	private void setWorkManager(Context context, int seconds, int initialSecondsOffset) {
		WorkManager workManager = WorkManager.getInstance(context);
		PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(WidgetWorker.class, 15, TimeUnit.MINUTES)
				.setInitialDelay(Math.min(initialSecondsOffset, seconds), TimeUnit.SECONDS)
				.setInputData(
						new Data.Builder()
								.putInt(WidgetWorker.DATA_KEY_SECONDS, seconds)
								.putBoolean(WidgetWorker.DATA_KEY_SCHEDULE, true)
								.build()
				)
				.build();
		workManager.enqueueUniquePeriodicWork(CLOCK_WIDGET_UPDATE_UNIQUE_WORK_TAG, ExistingPeriodicWorkPolicy.REPLACE, request);
		Log.d(TAG, "setWorkManager: enqueued periodic scheduling work");
	}
	
	public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), Context.MODE_PRIVATE);
		int houroverhang = sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeyhouroverhang) + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaulthouroverhang));
		int minuteoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeyminuteoverhang) + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultminuteoverhang));
		int secondoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeysecondoverhang) + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultsecondoverhang));
		int dayoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeydayoverhang) + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultdayoverhang));
		int monthoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeymonthoverhang) + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultmonthoverhang));
		boolean twelvehour = sharedPreferences.getBoolean(context.getResources().getString(R.string.widgetkeytwelvehour) + appWidgetId, !DateFormat.is24HourFormat(context));
		boolean enableseconds = sharedPreferences.getBoolean(context.getResources().getString(R.string.widgetkeyenableseconds) + appWidgetId, context.getResources().getBoolean(R.bool.widgetdefaultenableseconds));
		boolean enabledate = sharedPreferences.getBoolean(context.getResources().getString(R.string.widgetkeyenabledate) + appWidgetId, context.getResources().getBoolean(R.bool.widgetdefaultenabledate));
		String font = sharedPreferences.getString(context.getResources().getString(R.string.widgetkeyfont) + appWidgetId, context.getResources().getString(R.string.defaultfonttext));
		float fontscale = sharedPreferences.getFloat(context.getResources().getString(R.string.widgetkeyfontscale) + appWidgetId, context.getResources().getInteger(R.integer.widgetdefaultdatefontscale));
		int color = sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeycolor) + appWidgetId, context.getResources().getColor(R.color.widgetdefaultclockcolor));
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
