package com.JJ.hangoverclock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class ConfigureWidget extends Activity {

    String TAG = "ConfigureWidget";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public ConfigureWidget() {
        super();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClockWidgetProvider.collectfonts();
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if they press the back button.
        setResult(RESULT_CANCELED);
        // Set the view layout resource to use.
        setContentView(R.layout.widget_configure);
        findViewById(R.id.save).setOnClickListener(savelistener);
        // Change seekbar colors
        //SeekBar sb = (SeekBar) findViewById(R.id.seekBar2);
        //sb.getProgressDrawable().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        ((SeekBar)findViewById(R.id.seekbarred)).getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        ((SeekBar)findViewById(R.id.seekbarblue)).getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
        ((SeekBar)findViewById(R.id.seekbargreen)).getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
        ((SeekBar)findViewById(R.id.seekbarred)).getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        ((SeekBar)findViewById(R.id.seekbarblue)).getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        ((SeekBar)findViewById(R.id.seekbargreen)).getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        ((SeekBar)findViewById(R.id.seekbarred)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar)findViewById(R.id.seekbargreen)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar)findViewById(R.id.seekbarblue)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar)findViewById(R.id.seekbaralpha)).setOnSeekBarChangeListener(colorseekbarlistener);
        int defaultColor = getResources().getColor(R.color.defaultWidgetColor);
        ((SeekBar)findViewById(R.id.seekbarred)).setProgress(Color.red(defaultColor));
        ((SeekBar)findViewById(R.id.seekbargreen)).setProgress(Color.green(defaultColor));
        ((SeekBar)findViewById(R.id.seekbarblue)).setProgress(Color.blue(defaultColor));
        ((EditText) findViewById(R.id.overhanginput)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                EditText editText = ((EditText) findViewById(R.id.overhanginput));
                String input = editText.getText().toString();
                String regexed = input.replaceAll("[^0-9]", "");
                if (!regexed.equals(input)) editText.setText(regexed);
                updatepreview();
            }
        });
        ((Switch) findViewById(R.id.hourselector)).setChecked(!DateFormat.is24HourFormat(ConfigureWidget.this));
        ((Switch) findViewById(R.id.hourselector)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatepreview();
            }
        });
        Spinner fontspinner = (Spinner) findViewById(R.id.fontspinner);
        ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
        for (String font : ClockWidgetProvider.fonts) {
            RowItem item = new RowItem(font);
            rowItems.add(item);
        }
        final SpinnerAdapter spinnerAdapter = new CustomAdapter(ConfigureWidget.this, R.layout.listitems_layout, R.id.spinnerview, rowItems);
        fontspinner.setAdapter(spinnerAdapter);
        fontspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatepreview();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
            });
        updatepreview();
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
            String text;
            try {
                text = ((Spinner) findViewById(R.id.fontspinner)).getSelectedItem().toString().replace(" ", "_");
            } catch (NullPointerException nulle) {
                //maybe default language?
                text = "default";
            }
            editor.putString("font" + mAppWidgetId, text);
            editor.putInt("overhang" + mAppWidgetId, overhang);
            editor.putInt("color" + mAppWidgetId, color);
            editor.putBoolean("twelvehour" + mAppWidgetId, ((Switch) findViewById(R.id.hourselector)).isChecked());
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
            viewred.setBackgroundColor(Color.argb(255, Color.red(color), 0, 0));
            viewblue.setBackgroundColor(Color.argb(255,0, 0, Color.blue(color)));
            viewgreen.setBackgroundColor(Color.argb(255, 0, Color.green(color), 0));
            viewalpha.setBackgroundColor(Color.argb(Color.alpha(color), 255, 255, 255));
            viewcolor.setBackgroundColor(color);
            updatepreview();
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private void updatepreview() {
        TextView previewclock = (TextView) findViewById(R.id.previewclock);
        boolean twelvehour = ((Switch) findViewById(R.id.hourselector)).isChecked();
        int hour;
        if (twelvehour) {
            hour = Calendar.getInstance().get(Calendar.HOUR);
        } else {
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        }
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int overhang = 0;
        try {
            overhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginput)).getText().toString());
        } catch (NumberFormatException numerr) {
            //Expected error if no value was choosen, just set to default value
            overhang = getResources().getInteger(R.integer.defaultoverhang);
        }
        String time = ClockWidgetProvider.calculatetime((double) hour * 60 * 60 + minutes * 60, overhang, twelvehour);
        SeekBar seekbarred = (SeekBar) findViewById(R.id.seekbarred);
        SeekBar seekbargreen = (SeekBar) findViewById(R.id.seekbargreen);
        SeekBar seekbarblue = (SeekBar) findViewById(R.id.seekbarblue);
        SeekBar seekbaralpha = (SeekBar) findViewById(R.id.seekbaralpha);
        int color = Color.argb(seekbaralpha.getProgress() ,seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
        previewclock.setText(time);
        previewclock.setTextColor(color);
        previewclock.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        Spinner spinner = (Spinner) findViewById(R.id.fontspinner);
        String text;
        try {
            text = spinner.getSelectedItem().toString();
        } catch (NullPointerException nulle) {
            //Expected if called to early
            return;
        }
        if (text.equals(ClockWidgetProvider.fonts.get(0))) return;
        try {
            //Log.d(TAG, "updatepreview: tf string is " + rowItem.getTitleFont(position));
            //Log.d(TAG, "updatepreview: identifier is " + getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName()));
            //Log.d(TAG, "updatepreview: typefont is " + ResourcesCompat.getFont(getContext(), getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName())));
            previewclock.setTypeface(ResourcesCompat.getFont(ConfigureWidget.this, ConfigureWidget.this.getResources().getIdentifier(text.replace(" ", "_"), "font", ConfigureWidget.this.getPackageName())));
        } catch (Exception e) {
            Log.e(TAG, "updatepreview: error occured while determiting font", e);
        }

    }
}
