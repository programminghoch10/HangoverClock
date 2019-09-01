package com.JJ.hangoverclock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;


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
        findViewById(R.id.save).setOnClickListener(savelistener);
        // Change seekbar colors
        //SeekBar sb = (SeekBar) findViewById(R.id.seekBar2);
        //sb.getProgressDrawable().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        int defaultColor = getResources().getColor(R.color.defaultWidgetColor);
        ((SeekBar)findViewById(R.id.seekbarred)).setProgress(Color.red(defaultColor));
        ((SeekBar)findViewById(R.id.seekbargreen)).setProgress(Color.green(defaultColor));
        ((SeekBar)findViewById(R.id.seekbarblue)).setProgress(Color.blue(defaultColor));
        ((SeekBar)findViewById(R.id.seekbarred)).getThumb().setColorFilter(getResources().getColor(R.color.sliderred), PorterDuff.Mode.MULTIPLY);
        ((SeekBar)findViewById(R.id.seekbarblue)).getThumb().setColorFilter(getResources().getColor(R.color.sliderblue), PorterDuff.Mode.MULTIPLY);
        ((SeekBar)findViewById(R.id.seekbargreen)).getThumb().setColorFilter(getResources().getColor(R.color.slidergreen), PorterDuff.Mode.MULTIPLY);
        ((SeekBar)findViewById(R.id.seekbarred)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar)findViewById(R.id.seekbargreen)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar)findViewById(R.id.seekbarblue)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar)findViewById(R.id.seekbaralpha)).setOnSeekBarChangeListener(colorseekbarlistener);
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
            SeekBar seekbarred = (SeekBar) findViewById(R.id.seekbarred);
            SeekBar seekbargreen = (SeekBar) findViewById(R.id.seekbargreen);
            SeekBar seekbarblue = (SeekBar) findViewById(R.id.seekbarblue);
            SeekBar seekbaralpha = (SeekBar) findViewById(R.id.seekbaralpha);
            int color = Color.argb(seekbaralpha.getProgress() ,seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int overhang = 0;
            try {
                overhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginput)).getText().toString());
            } catch (NumberFormatException numerr) {
                //Expected error if no value was choosen, just set to default value
                overhang = getResources().getInteger(R.integer.defaultoverhang);
            }
            editor.putInt("overhang" + mAppWidgetId, overhang);
            editor.putInt("color" + mAppWidgetId, color);
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
    SeekBar.OnSeekBarChangeListener colorseekbarlistener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            View viewred = (View) findViewById(R.id.viewred);
            View viewgreen = (View) findViewById(R.id.viewgreen);
            View viewblue = (View) findViewById(R.id.viewblue);
            View viewalpha = (View) findViewById(R.id.viewalpha);
            View viewcolor = (View) findViewById(R.id.viewcolor);
            SeekBar seekbarred = (SeekBar) findViewById(R.id.seekbarred);
            SeekBar seekbargreen = (SeekBar) findViewById(R.id.seekbargreen);
            SeekBar seekbarblue = (SeekBar) findViewById(R.id.seekbarblue);
            SeekBar seekbaralpha = (SeekBar) findViewById(R.id.seekbaralpha);
            int color = Color.argb(seekbaralpha.getProgress() ,seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
            viewred.setBackgroundColor(Color.red(color));
            viewblue.setBackgroundColor(Color.blue(color));
            viewgreen.setBackgroundColor(Color.green(color));
            viewalpha.setBackgroundColor(Color.alpha(color));
            viewcolor.setBackgroundColor(color);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
}
