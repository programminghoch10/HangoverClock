package com.JJ.hangoverclock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.Map;

public class WidgetConfigure extends Activity {
	
	String TAG = "WidgetConfigure";
	int appWidgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
	
	public WidgetConfigure() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the result to CANCELED.  This will cause the widget host to cancel
		// out of the widget placement if they press the back button.
		setResult(RESULT_CANCELED);
		// Set the view layout resource to use.
		setContentView(R.layout.widget_configure);
		// Find the widget id from the intent.
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			appWidgetID = extras.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
		// If they gave us an intent without the widget id, just bail.
		if (appWidgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}
		SharedPreferences sharedPreferences = getSharedPreferences(WidgetConfigure.this.getResources().getString(R.string.lastwidgetpreferencesfilename), MODE_PRIVATE);
		Context context = WidgetConfigure.this;
		
		Configure configure = new Configure(context, this, "widget");
		configure.onCreate(sharedPreferences, true, true, false);
		findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				configure.savesettings(sharedPreferences);
				
				//copy preferences into widget specific sharedPreferences
				SharedPreferences.Editor editor = getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), MODE_PRIVATE).edit();
				Map<String, ?> entries = sharedPreferences.getAll();
				for (String key : ClockConfig.keys) {
					if (!entries.containsKey(key)) continue;
					String newKey = key + appWidgetID;
					Object obj = entries.get(key);
					if (obj instanceof Boolean)
						editor.putBoolean(newKey, (Boolean) obj);
					else if (obj instanceof Float)
						editor.putFloat(newKey, (Float) obj);
					else if (obj instanceof Integer)
						editor.putInt(newKey, (Integer) obj);
					else if (obj instanceof Long)
						editor.putLong(newKey, (Long) obj);
					else if (obj instanceof String)
						editor.putString(newKey, (String) obj);
				}
				editor.apply();
				
				// Push widget update to surface with newly set prefix
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				WidgetProvider widgetProvider = new WidgetProvider();
				widgetProvider.updateAppWidget(context, appWidgetManager, appWidgetID);
				// Make sure we pass back the original appWidgetId
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
				setResult(RESULT_OK, resultValue);
				finish();
			}
		});
	}
	
}
