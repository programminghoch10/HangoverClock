package com.JJ.hangoverclock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SettingsWidgetActivity extends Activity {
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_widget);
		setupPreviews();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		setupPreviews();
	}
	
	private void setupPreviews() {
		Context context = this;
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		
		LinearLayout rootLayout = findViewById(R.id.settings_widget_list);
		rootLayout.removeAllViews();
		
		SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.widgetpreferencesfilename), MODE_PRIVATE);
		ClockConfig defaultWidgetConfig = ClockConfig.getDefaultsFromResources(getResources(), "widget");
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName componentName = new ComponentName(context, WidgetProvider.class);
		int[] widgetIds = appWidgetManager.getAppWidgetIds(componentName);
		
		for (int widgetId : widgetIds) {
			LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.settings_widget_listlayout, null);
			
			TextView textView = layout.findViewById(R.id.settings_widget_title);
			textView.setText("Widget " + widgetId);
			
			ImageView imageView = layout.findViewById(R.id.settings_widget_preview);
			ClockConfig config = new ClockConfig(sharedPreferences, defaultWidgetConfig, String.valueOf(widgetId));
			Bitmap bitmap = ClockGenerator.generateClock(context, System.currentTimeMillis(), config);
			imageView.setImageDrawable(new BitmapDrawable(bitmap));
			
			layout.setOnClickListener(v -> {
				Intent intent = new Intent(this, WidgetConfigure.class);
				intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
				intent.putExtra("editWidget", true);
				startActivity(intent);
			});
			
			rootLayout.addView(layout);
		}
	}
}
