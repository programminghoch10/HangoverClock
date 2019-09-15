package com.JJ.hangoverclock;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class ConfigureWidget extends Activity {
    
    String TAG = "ConfigureWidget";
    int appWidgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
    View.OnClickListener savelistener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ConfigureWidget.this;
            SeekBar seekbarred = (SeekBar) findViewById(R.id.seekbarred);
            SeekBar seekbargreen = (SeekBar) findViewById(R.id.seekbargreen);
            SeekBar seekbarblue = (SeekBar) findViewById(R.id.seekbarblue);
            SeekBar seekbaralpha = (SeekBar) findViewById(R.id.seekbaralpha);
            int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            int minuteoverhang;
            try {
                minuteoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().toString());
            } catch (NumberFormatException numerr) {
                //Expected error if no value was choosen, just set to default value
                minuteoverhang = getResources().getInteger(R.integer.defaultminuteoverhang);
            }
            int houroverhang;
            try {
                houroverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimehours)).getText().toString());
            } catch (NumberFormatException numerr) {
                //Expected error if no value was choosen, just set to default value
                houroverhang = context.getResources().getInteger(R.integer.defaulthouroverhang);
            }
            int secondoverhang = context.getResources().getInteger(R.integer.defaultsecondoverhang);
            secondoverhang = minuteoverhang;
            int dayoverhang;
            try {
                dayoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatedays)).getText().toString());
            } catch (NumberFormatException numerr) {
                //Expected error if no value was choosen, just set to default value
                dayoverhang = getResources().getInteger(R.integer.defaultdayoverhang);
            }
            int monthoverhang;
            try {
                monthoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatemonths)).getText().toString());
            } catch (NumberFormatException numerr) {
                //Expected error if no value was choosen, just set to default value
                monthoverhang = context.getResources().getInteger(R.integer.defaultmonthoverhang);
            }
            String font;
            try {
                font = ((Spinner) findViewById(R.id.fontspinner)).getSelectedItem().toString().replace(" ", "_");
            } catch (NullPointerException nulle) {
                //maybe default language?
                font = "default";
            }
            SeekBar fontsizedividerseekbar = ((SeekBar) findViewById(R.id.datefontsizeseekbar));
            float fontscale = context.getResources().getInteger(R.integer.maxfontscale) - (float) (fontsizedividerseekbar.getMax() - fontsizedividerseekbar.getProgress()) / 100;
            boolean enableseconds = ((Switch) findViewById(R.id.secondsselector)).isChecked();
            boolean autotwelvehours = ((Switch) findViewById(R.id.autohourselector)).isChecked();
            boolean twelvehours = ((Switch) findViewById(R.id.hourselector)).isChecked();
            boolean enabledate = ((Switch) findViewById(R.id.dateselector)).isChecked();
            boolean as = context.getResources().getBoolean(R.bool.alwayssavepreference); //wether preferences should always be saved
            if (as | context.getResources().getBoolean(R.bool.defaultenableseconds) != enableseconds)
                editor.putBoolean(context.getResources().getString(R.string.keyenableseconds) + appWidgetID, enableseconds);
            if (as | !context.getResources().getString(R.string.defaultfonttext).equals(font))
                editor.putString(context.getResources().getString(R.string.keyfont) + appWidgetID, font);
            if (as | (context.getResources().getInteger(R.integer.defaultdatefontscale) != fontscale & enabledate))
                editor.putFloat(context.getResources().getString(R.string.keyfontscale) + appWidgetID, fontscale);
            if (as | context.getResources().getInteger(R.integer.defaulthouroverhang) != houroverhang)
                editor.putInt(context.getResources().getString(R.string.keyhouroverhang) + appWidgetID, houroverhang);
            if (as | context.getResources().getInteger(R.integer.defaultminuteoverhang) != minuteoverhang)
                editor.putInt(context.getResources().getString(R.string.keyminuteoverhang) + appWidgetID, minuteoverhang);
            if (as | context.getResources().getInteger(R.integer.defaultsecondoverhang) != secondoverhang)
                editor.putInt(context.getResources().getString(R.string.keysecondoverhang) + appWidgetID, secondoverhang);
            if (as | context.getResources().getInteger(R.integer.defaultdayoverhang) != dayoverhang)
                editor.putInt(context.getResources().getString(R.string.keydayoverhang) + appWidgetID, dayoverhang);
            if (as | context.getResources().getInteger(R.integer.defaultmonthoverhang) != monthoverhang)
                editor.putInt(context.getResources().getString(R.string.keymonthoverhang) + appWidgetID, monthoverhang);
            if (as | context.getResources().getColor(R.color.defaultWidgetColor) != color)
                editor.putInt(context.getResources().getString(R.string.keycolor) + appWidgetID, color);
            if (!autotwelvehours)
                editor.putBoolean(context.getResources().getString(R.string.keytwelvehour) + appWidgetID, twelvehours);
            if (as | context.getResources().getBoolean(R.bool.defaultenabledate) != enabledate)
                editor.putBoolean(context.getResources().getString(R.string.keyenabledate) + appWidgetID, enabledate);
            editor.apply();
            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ClockWidgetProvider clockWidgetProvider = new ClockWidgetProvider();
            clockWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetID);
            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetID);
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
            int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
            viewred.setBackgroundColor(Color.argb(255, Color.red(color), 0, 0));
            viewblue.setBackgroundColor(Color.argb(255, 0, 0, Color.blue(color)));
            viewgreen.setBackgroundColor(Color.argb(255, 0, Color.green(color), 0));
            viewalpha.setBackgroundColor(Color.argb(255, Color.alpha(color), Color.alpha(color), Color.alpha(color)));
            viewcolor.setBackgroundColor(color);
            updatepreview();
        }
        
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }
        
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
    
    public ConfigureWidget() {
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
        ClockWidgetProvider.collectfonts(ConfigureWidget.this);
        findViewById(R.id.save).setOnClickListener(savelistener);
        // Change seekbar colors
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			((SeekBar) findViewById(R.id.seekbarred)).getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
			((SeekBar) findViewById(R.id.seekbargreen)).getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
			((SeekBar) findViewById(R.id.seekbarblue)).getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
		}
        ((SeekBar) findViewById(R.id.seekbarred)).getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        ((SeekBar) findViewById(R.id.seekbargreen)).getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
        ((SeekBar) findViewById(R.id.seekbarblue)).getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
        int defaultColor = getResources().getColor(R.color.defaultWidgetColor);
        ((SeekBar) findViewById(R.id.seekbarred)).setProgress(Color.red(defaultColor));
        ((SeekBar) findViewById(R.id.seekbargreen)).setProgress(Color.green(defaultColor));
        ((SeekBar) findViewById(R.id.seekbarblue)).setProgress(Color.blue(defaultColor));
        ((SeekBar) findViewById(R.id.seekbaralpha)).setProgress(Color.alpha(defaultColor));
        ((SeekBar) findViewById(R.id.seekbarred)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar) findViewById(R.id.seekbargreen)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar) findViewById(R.id.seekbarblue)).setOnSeekBarChangeListener(colorseekbarlistener);
        ((SeekBar) findViewById(R.id.seekbaralpha)).setOnSeekBarChangeListener(colorseekbarlistener);
        View viewred = (View) findViewById(R.id.viewred);
        View viewgreen = (View) findViewById(R.id.viewgreen);
        View viewblue = (View) findViewById(R.id.viewblue);
        View viewalpha = (View) findViewById(R.id.viewalpha);
        View viewcolor = (View) findViewById(R.id.viewcolor);
        SeekBar seekbarred = (SeekBar) findViewById(R.id.seekbarred);
        SeekBar seekbargreen = (SeekBar) findViewById(R.id.seekbargreen);
        SeekBar seekbarblue = (SeekBar) findViewById(R.id.seekbarblue);
        SeekBar seekbaralpha = (SeekBar) findViewById(R.id.seekbaralpha);
        int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
        viewred.setBackgroundColor(Color.argb(255, Color.red(color), 0, 0));
        viewgreen.setBackgroundColor(Color.argb(255, 0, Color.green(color), 0));
        viewblue.setBackgroundColor(Color.argb(255, 0, 0, Color.blue(color)));
        viewalpha.setBackgroundColor(Color.argb(255, Color.alpha(color), Color.alpha(color), Color.alpha(color)));
        viewcolor.setBackgroundColor(color);
        TextWatcher inputwatcher = new TextWatcher() {
            final int[] ids = {
                    R.id.overhanginputtimeminutes,
                    R.id.overhanginputtimehours,
                    R.id.overhanginputdatedays,
                    R.id.overhanginputdatemonths,
            };
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                for (int id : ids) {
                    EditText editText = ((EditText) findViewById(id));
                    String input = editText.getText().toString();
                    String regexed = input.replaceAll("[^0-9]", "");
                    if (!input.equals(regexed)) editText.setText(regexed);
                }
                updatepreview();
            }
        };
        ((EditText) findViewById(R.id.overhanginputtimeminutes)).addTextChangedListener(inputwatcher);
        ((EditText) findViewById(R.id.overhanginputdatedays)).addTextChangedListener(inputwatcher);
        ((EditText) findViewById(R.id.overhanginputtimehours)).addTextChangedListener(inputwatcher);
        ((EditText) findViewById(R.id.overhanginputdatemonths)).addTextChangedListener(inputwatcher);
        CompoundButton.OnCheckedChangeListener updatepreviewlistener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updatepreview();
            }
        };
        ((Switch) findViewById(R.id.hourselector)).setChecked(!DateFormat.is24HourFormat(ConfigureWidget.this));
        ((Switch) findViewById(R.id.dateselector)).setChecked(ConfigureWidget.this.getResources().getBoolean(R.bool.defaultenabledate));
        ((Switch) findViewById(R.id.secondsselector)).setChecked(ConfigureWidget.this.getResources().getBoolean(R.bool.defaultenableseconds));
        ((Switch) findViewById(R.id.autohourselector)).setChecked(ConfigureWidget.this.getResources().getBoolean(R.bool.defaultautotimeselector));
        ((Switch) findViewById(R.id.hourselector)).setEnabled(!((Switch) findViewById(R.id.autohourselector)).isChecked());
        ((Switch) findViewById(R.id.hourselector)).setOnCheckedChangeListener(updatepreviewlistener);
        ((Switch) findViewById(R.id.dateselector)).setOnCheckedChangeListener(updatepreviewlistener);
        ((Switch) findViewById(R.id.secondsselector)).setOnCheckedChangeListener(updatepreviewlistener);
        ((Switch) findViewById(R.id.autohourselector)).setOnCheckedChangeListener(updatepreviewlistener);
        //TODO: replace spinner with recyclerview (big project ._.)
        Spinner fontspinner = (Spinner) findViewById(R.id.fontspinner);
        ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
        ArrayList<String> fonts = ClockWidgetProvider.fonts;
        for (int i = 0; i < fonts.size(); i++) {
            String font = fonts.get(i);
            RowItem item = new RowItem(ConfigureWidget.this, font, i);
            if (item.getVisibility() == View.VISIBLE) rowItems.add(item);
        }
        final SpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(ConfigureWidget.this, R.layout.listitems_layout, R.id.spinnerview, rowItems);
        fontspinner.setAdapter(spinnerAdapter);
        fontspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatepreview();
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        ((SeekBar) findViewById(R.id.datefontsizeseekbar)).setMax((ConfigureWidget.this.getResources().getInteger(R.integer.maxfontscale) - 3) * 100);
        ((SeekBar) findViewById(R.id.datefontsizeseekbar)).setProgress(
                ConfigureWidget.this.getResources().getInteger(R.integer.defaultdatefontscale) * 100
                        - ((SeekBar) findViewById(R.id.datefontsizeseekbar)).getMax()
        );
        ((SeekBar) findViewById(R.id.datefontsizeseekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatepreview();
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        ((View) findViewById(R.id.viewcolor)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SeekBar) findViewById(R.id.seekbarred)).setProgress((int) (Math.random() * 255));
                ((SeekBar) findViewById(R.id.seekbarblue)).setProgress((int) (Math.random() * 255));
                ((SeekBar) findViewById(R.id.seekbargreen)).setProgress((int) (Math.random() * 255));
            }
        });
        updatepreview();
    }
    
    private void updatepreview() {
        //Log.d(TAG, "updatepreview: i have been called");
        //Log.d(TAG, "updatepreview: trace is " + Arrays.toString(Thread.currentThread().getStackTrace()));
        final Context context = ConfigureWidget.this;
        ImageView imageView = (ImageView) findViewById(R.id.previewclock);
        boolean twelvehour = ((Switch) findViewById(R.id.hourselector)).isChecked();
        boolean withdate = ((Switch) findViewById(R.id.dateselector)).isChecked();
        int minuteoverhang;
        try {
            minuteoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().toString());
        } catch (NumberFormatException numerr) {
            //Expected error if no value was choosen, just set to default value
            minuteoverhang = getResources().getInteger(R.integer.defaultminuteoverhang);
        }
        int houroverhang;
        try {
            houroverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimehours)).getText().toString());
        } catch (NumberFormatException numerr) {
            //Expected error if no value was choosen, just set to default value
            houroverhang = context.getResources().getInteger(R.integer.defaulthouroverhang);
        }
        int secondoverhang = context.getResources().getInteger(R.integer.defaultsecondoverhang);
        secondoverhang = minuteoverhang;
        int dayoverhang;
        try {
            dayoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatedays)).getText().toString());
        } catch (NumberFormatException numerr) {
            //Expected error if no value was choosen, just set to default value
            dayoverhang = getResources().getInteger(R.integer.defaultdayoverhang);
        }
        int monthoverhang;
        try {
            monthoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatemonths)).getText().toString());
        } catch (NumberFormatException numerr) {
            //Expected error if no value was choosen, just set to default value
            monthoverhang = context.getResources().getInteger(R.integer.defaultmonthoverhang);
        }
        boolean withseconds = ((Switch) findViewById(R.id.secondsselector)).isChecked();
        SeekBar seekbarred = (SeekBar) findViewById(R.id.seekbarred);
        SeekBar seekbargreen = (SeekBar) findViewById(R.id.seekbargreen);
        SeekBar seekbarblue = (SeekBar) findViewById(R.id.seekbarblue);
        SeekBar seekbaralpha = (SeekBar) findViewById(R.id.seekbaralpha);
        int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
        SeekBar fontsizedividerseekbar = ((SeekBar) findViewById(R.id.datefontsizeseekbar));
        float fontsizedivider = context.getResources().getInteger(R.integer.maxfontscale) - (float) (fontsizedividerseekbar.getMax() - fontsizedividerseekbar.getProgress()) / 100;
        if (((Switch) findViewById(R.id.dateselector)).isChecked()) {
            findViewById(R.id.overhanginputdate).setVisibility(View.VISIBLE);
            fontsizedividerseekbar.setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.overhanginputdate).setVisibility(View.GONE);
            fontsizedividerseekbar.setVisibility(View.GONE);
        }
        if (((Switch) findViewById(R.id.secondsselector)).isChecked()) {
            findViewById(R.id.secondsinfo).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.secondsinfo)).setWidth(((Switch) findViewById(R.id.secondsselector)).getWidth());
        } else {
            findViewById(R.id.secondsinfo).setVisibility(View.GONE);
        }
        if (((Switch) findViewById(R.id.autohourselector)).isChecked()) {
            findViewById(R.id.hourselector).setEnabled(false);
            ((Switch) findViewById(R.id.hourselector)).setChecked(!DateFormat.is24HourFormat(ConfigureWidget.this));
        } else {
            findViewById(R.id.hourselector).setEnabled(true);
        }
        Spinner spinner = (Spinner) findViewById(R.id.fontspinner);
        String font;
        try {
            font = spinner.getSelectedItem().toString();
        } catch (NullPointerException nulle) {
            //Expected if called to early
            font = context.getResources().getString(R.string.defaultfonttext);
        }
        int fontresolution = context.getResources().getInteger(R.integer.widgetfontresolution);
        Bitmap bitmap = WidgetGenerator.generateWidget(
                context, Calendar.getInstance().getTimeInMillis(),
                secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
                twelvehour, withseconds, withdate,
                font, color, fontsizedivider, fontresolution
        );
        bitmap.prepareToDraw();
        imageView.setImageBitmap(bitmap);
        
    }
}
