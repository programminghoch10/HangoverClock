package com.JJ.hangoverclock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;


public class ConfigureWidget extends Activity {
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public ConfigureWidget() {
        super();
    }
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        // Set the view layout resource to use.
        setContentView(R.layout.widget_configure);
        // Find the EditText
        //mAppWidgetPrefix = (EditText)findViewById(R.id.appwidget_prefix);
        // Bind the action for the save button.
        findViewById(R.id.save).setOnClickListener(savelistener);
        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }
    View.OnClickListener savelistener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ConfigureWidget.this;
            // When the button is clicked, save the string in our prefs and return that they
            // clicked OK.
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("overhang" + mAppWidgetId, Integer.valueOf(((EditText)findViewById(R.id.overhanginput)).getText().toString()));
            editor.apply();
            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ClockWidgetProvider clockWidgetProvider = new ClockWidgetProvider();
            clockWidgetProvider.updateAppWidget(context, appWidgetManager, mAppWidgetId);
            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
}
