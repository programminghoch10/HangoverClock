package com.JJ.hangoverclock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
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

public class WidgetConfigure extends Activity {
	
	String TAG = "WidgetConfigure";
	int appWidgetID = AppWidgetManager.INVALID_APPWIDGET_ID;
	View.OnClickListener savelistener = new View.OnClickListener() {
		public void onClick(View v) {
			final Context context = WidgetConfigure.this;
			SeekBar seekbarred = findViewById(R.id.seekbarred);
			SeekBar seekbargreen = findViewById(R.id.seekbargreen);
			SeekBar seekbarblue = findViewById(R.id.seekbarblue);
			SeekBar seekbaralpha = findViewById(R.id.seekbaralpha);
			int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
			@SuppressLint("CommitPrefEdits") //apply later in foreach loop
			SharedPreferences.Editor[] editors = {
					getSharedPreferences(context.getResources().getString(R.string.widgetpreferencesfilename), MODE_PRIVATE).edit(),
					getSharedPreferences(context.getResources().getString(R.string.lastwidgetpreferencesfilename), MODE_PRIVATE).edit(),
			};
			int minuteoverhang;
			try {
				minuteoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().toString());
			} catch (NumberFormatException numerr) {
				//Expected error if no value was choosen, just set to default value
				minuteoverhang = context.getResources().getInteger(R.integer.widgetdefaultminuteoverhang);
			}
			int houroverhang;
			try {
				houroverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimehours)).getText().toString());
			} catch (NumberFormatException numerr) {
				//Expected error if no value was choosen, just set to default value
				houroverhang = context.getResources().getInteger(R.integer.widgetdefaulthouroverhang);
			}
			int secondoverhang;
			try {
				secondoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeseconds)).getText().toString());
			} catch (NumberFormatException numerr) {
				//Expected error if no value was choosen, just set to default value
				secondoverhang = context.getResources().getInteger(R.integer.widgetdefaultsecondoverhang);
			}
			int dayoverhang;
			try {
				dayoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatedays)).getText().toString());
			} catch (NumberFormatException numerr) {
				//Expected error if no value was choosen, just set to default value
				dayoverhang = context.getResources().getInteger(R.integer.widgetdefaultdayoverhang);
			}
			int monthoverhang;
			try {
				monthoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatemonths)).getText().toString());
			} catch (NumberFormatException numerr) {
				//Expected error if no value was choosen, just set to default value
				monthoverhang = context.getResources().getInteger(R.integer.widgetdefaultmonthoverhang);
			}
			String font;
			try {
				font = ((Spinner) findViewById(R.id.fontspinner)).getSelectedItem().toString().replace(" ", "_");
			} catch (NullPointerException nulle) {
				//maybe default language?
				font = "default";
			}
			SeekBar fontsizedividerseekbar = findViewById(R.id.datefontsizeseekbar);
			float fontscale = context.getResources().getInteger(R.integer.maxfontscale) - (float) (fontsizedividerseekbar.getMax() - fontsizedividerseekbar.getProgress()) / 100;
			boolean enableseconds = ((Switch) findViewById(R.id.secondsselector)).isChecked();
			boolean autotwelvehours = ((Switch) findViewById(R.id.autohourselector)).isChecked();
			boolean twelvehours = ((Switch) findViewById(R.id.hourselector)).isChecked();
			boolean enabledate = ((Switch) findViewById(R.id.dateselector)).isChecked();
			boolean as = context.getResources().getBoolean(R.bool.alwayssavepreference); //wether preferences should always be saved
			for (SharedPreferences.Editor editor:editors) {
				String appWidgetIdSuffix = "";
				if (editor.equals(editors[0])) appWidgetIdSuffix = String.valueOf(appWidgetID);
				int[] flushwidgetkeys = {
						R.string.widgetkeyenableseconds,
						R.string.widgetkeyfont,
						R.string.widgetkeyfontscale,
						R.string.widgetkeyhouroverhang,
						R.string.widgetkeyminuteoverhang,
						R.string.widgetkeysecondoverhang,
						R.string.widgetkeydayoverhang,
						R.string.widgetkeymonthoverhang,
						R.string.widgetkeycolor,
						R.string.widgetkeytwelvehour,
						R.string.widgetkeyenabledate,
				};
				for (int widgetkey : flushwidgetkeys) {
					editor.remove(context.getResources().getString(widgetkey) + appWidgetIdSuffix);
				}
				if (as | context.getResources().getBoolean(R.bool.widgetdefaultenableseconds) != enableseconds)
					editor.putBoolean(context.getResources().getString(R.string.widgetkeyenableseconds) + appWidgetIdSuffix, enableseconds);
				if (as | !context.getResources().getString(R.string.defaultfonttext).equals(font))
					editor.putString(context.getResources().getString(R.string.widgetkeyfont) + appWidgetIdSuffix, font);
				if (as | (context.getResources().getInteger(R.integer.widgetdefaultdatefontscale) != fontscale & enabledate))
					editor.putFloat(context.getResources().getString(R.string.widgetkeyfontscale) + appWidgetIdSuffix, fontscale);
				if (as | context.getResources().getInteger(R.integer.widgetdefaulthouroverhang) != houroverhang)
					editor.putInt(context.getResources().getString(R.string.widgetkeyhouroverhang) + appWidgetIdSuffix, houroverhang);
				if (as | context.getResources().getInteger(R.integer.widgetdefaultminuteoverhang) != minuteoverhang)
					editor.putInt(context.getResources().getString(R.string.widgetkeyminuteoverhang) + appWidgetIdSuffix, minuteoverhang);
				if (as | context.getResources().getInteger(R.integer.widgetdefaultsecondoverhang) != secondoverhang)
					editor.putInt(context.getResources().getString(R.string.widgetkeysecondoverhang) + appWidgetIdSuffix, secondoverhang);
				if (as | context.getResources().getInteger(R.integer.widgetdefaultdayoverhang) != dayoverhang)
					editor.putInt(context.getResources().getString(R.string.widgetkeydayoverhang) + appWidgetIdSuffix, dayoverhang);
				if (as | context.getResources().getInteger(R.integer.widgetdefaultmonthoverhang) != monthoverhang)
					editor.putInt(context.getResources().getString(R.string.widgetkeymonthoverhang) + appWidgetIdSuffix, monthoverhang);
				if (as | context.getResources().getColor(R.color.widgetdefaultclockcolor) != color)
					editor.putInt(context.getResources().getString(R.string.widgetkeycolor) + appWidgetIdSuffix, color);
				if (!autotwelvehours)
					editor.putBoolean(context.getResources().getString(R.string.widgetkeytwelvehour) + appWidgetIdSuffix, twelvehours);
				if (as | context.getResources().getBoolean(R.bool.widgetdefaultenabledate) != enabledate)
					editor.putBoolean(context.getResources().getString(R.string.widgetkeyenabledate) + appWidgetIdSuffix, enabledate);
				editor.apply();
			}
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
	};
	SeekBar.OnSeekBarChangeListener colorseekbarlistener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			View viewred = findViewById(R.id.viewred);
			View viewgreen = findViewById(R.id.viewgreen);
			View viewblue = findViewById(R.id.viewblue);
			View viewalpha = findViewById(R.id.viewalpha);
			View viewcolor = findViewById(R.id.viewcolor);
			SeekBar seekbarred = findViewById(R.id.seekbarred);
			SeekBar seekbargreen = findViewById(R.id.seekbargreen);
			SeekBar seekbarblue = findViewById(R.id.seekbarblue);
			SeekBar seekbaralpha = findViewById(R.id.seekbaralpha);
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
		FontsProvider.collectfonts(WidgetConfigure.this);
		SharedPreferences sharedPreferences = getSharedPreferences(WidgetConfigure.this.getResources().getString(R.string.lastwidgetpreferencesfilename), MODE_PRIVATE);
		Context context = WidgetConfigure.this;
		// Change seekbar colors
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			((SeekBar) findViewById(R.id.seekbarred)).getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
			((SeekBar) findViewById(R.id.seekbargreen)).getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
			((SeekBar) findViewById(R.id.seekbarblue)).getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
		}
		((SeekBar) findViewById(R.id.seekbarred)).getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
		((SeekBar) findViewById(R.id.seekbargreen)).getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
		((SeekBar) findViewById(R.id.seekbarblue)).getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
		int color = sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeycolor), getResources().getColor(R.color.widgetdefaultclockcolor));
		((SeekBar) findViewById(R.id.seekbarred)).setProgress(Color.red(color));
		((SeekBar) findViewById(R.id.seekbargreen)).setProgress(Color.green(color));
		((SeekBar) findViewById(R.id.seekbarblue)).setProgress(Color.blue(color));
		((SeekBar) findViewById(R.id.seekbaralpha)).setProgress(Color.alpha(color));
		((SeekBar) findViewById(R.id.seekbarred)).setOnSeekBarChangeListener(colorseekbarlistener);
		((SeekBar) findViewById(R.id.seekbargreen)).setOnSeekBarChangeListener(colorseekbarlistener);
		((SeekBar) findViewById(R.id.seekbarblue)).setOnSeekBarChangeListener(colorseekbarlistener);
		((SeekBar) findViewById(R.id.seekbaralpha)).setOnSeekBarChangeListener(colorseekbarlistener);
		View viewred = findViewById(R.id.viewred);
		View viewgreen = findViewById(R.id.viewgreen);
		View viewblue = findViewById(R.id.viewblue);
		View viewalpha = findViewById(R.id.viewalpha);
		View viewcolor = findViewById(R.id.viewcolor);
		//SeekBar seekbarred = findViewById(R.id.seekbarred);
		//SeekBar seekbargreen = findViewById(R.id.seekbargreen);
		//SeekBar seekbarblue = findViewById(R.id.seekbarblue);
		//SeekBar seekbaralpha = findViewById(R.id.seekbaralpha);
		//int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
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
				boolean updatepreview = true;
				for (int id : ids) {
					EditText editText = findViewById(id);
					String input = editText.getText().toString();
					String regexed = input.replaceAll("[^0-9]", "");
					try {
						if (Long.parseLong(regexed) > Integer.MAX_VALUE)
							regexed = String.valueOf(Integer.MAX_VALUE);
					} catch (NumberFormatException ignored) {
					}
					if (!input.equals(regexed)) {
						editText.setText(regexed);
						updatepreview = false;
					}
				}
				if (updatepreview) updatepreview();
			}
		};
		((EditText) findViewById(R.id.overhanginputtimeminutes)).addTextChangedListener(inputwatcher);
		((EditText) findViewById(R.id.overhanginputdatedays)).addTextChangedListener(inputwatcher);
		((EditText) findViewById(R.id.overhanginputtimehours)).addTextChangedListener(inputwatcher);
		((EditText) findViewById(R.id.overhanginputdatemonths)).addTextChangedListener(inputwatcher);
		((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().append(String.valueOf(sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeyminuteoverhang), context.getResources().getInteger(R.integer.widgetdefaultminuteoverhang))));
		if (Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().toString()) == (context.getResources().getInteger(R.integer.widgetdefaultminuteoverhang)))
			((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().clear();
		((EditText) findViewById(R.id.overhanginputtimehours)).getText().append(String.valueOf(sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeyhouroverhang), context.getResources().getInteger(R.integer.widgetdefaulthouroverhang))));
		if (Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimehours)).getText().toString()) == (context.getResources().getInteger(R.integer.widgetdefaulthouroverhang)))
			((EditText) findViewById(R.id.overhanginputtimehours)).getText().clear();
		((EditText) findViewById(R.id.overhanginputdatedays)).getText().append(String.valueOf(sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeydayoverhang), context.getResources().getInteger(R.integer.widgetdefaultdayoverhang))));
		if (Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatedays)).getText().toString()) == (context.getResources().getInteger(R.integer.widgetdefaultdayoverhang)))
			((EditText) findViewById(R.id.overhanginputdatedays)).getText().clear();
		((EditText) findViewById(R.id.overhanginputdatemonths)).getText().append(String.valueOf(sharedPreferences.getInt(context.getResources().getString(R.string.widgetkeymonthoverhang), context.getResources().getInteger(R.integer.widgetdefaultmonthoverhang))));
		if (Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatemonths)).getText().toString()) == (context.getResources().getInteger(R.integer.widgetdefaultmonthoverhang)))
			((EditText) findViewById(R.id.overhanginputdatemonths)).getText().clear();
		
		CompoundButton.OnCheckedChangeListener updatepreviewlistener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updatepreview();
			}
		};
		((Switch) findViewById(R.id.hourselector)).setChecked(sharedPreferences.getBoolean(context.getResources().getString(R.string.widgetkeytwelvehour), !DateFormat.is24HourFormat(context)));
		((Switch) findViewById(R.id.dateselector)).setChecked(sharedPreferences.getBoolean(context.getResources().getString(R.string.widgetkeyenabledate), context.getResources().getBoolean(R.bool.widgetdefaultenabledate)));
		((Switch) findViewById(R.id.secondsselector)).setChecked(sharedPreferences.getBoolean(context.getResources().getString(R.string.widgetkeyenableseconds), context.getResources().getBoolean(R.bool.widgetdefaultenableseconds)));
		((Switch) findViewById(R.id.autohourselector)).setChecked(!sharedPreferences.contains(context.getResources().getString(R.string.widgetkeytwelvehour)) && context.getResources().getBoolean(R.bool.widgetdefaultautotimeselector));
		findViewById(R.id.hourselector).setEnabled(!((Switch) findViewById(R.id.autohourselector)).isChecked());
		((Switch) findViewById(R.id.hourselector)).setOnCheckedChangeListener(updatepreviewlistener);
		((Switch) findViewById(R.id.secondsselector)).setOnCheckedChangeListener(updatepreviewlistener);
		((Switch) findViewById(R.id.autohourselector)).setOnCheckedChangeListener(updatepreviewlistener);
		findViewById(R.id.previewclock).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updatepreview();
			}
		});
		((Switch) findViewById(R.id.dateselector)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				EditText[] editTexts = {
						findViewById(R.id.overhanginputtimeminutes),
						findViewById(R.id.overhanginputtimehours),
				};
				if (isChecked) {
					for (EditText editText : editTexts) {
						try {
							if (Integer.valueOf((editText.getText().toString())) >= (2 ^ 16))
								editText.setText("");
						} catch (NumberFormatException ignored) {}
					}
				}
				updatepreview();
			}
		});
		Spinner fontspinner = findViewById(R.id.fontspinner);
		ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
		ArrayList<String> fonts = FontsProvider.getFonts();
		int fontselected = 0;
		String savedfont = sharedPreferences.getString(context.getResources().getString(R.string.daydreamkeyfont), "");
		savedfont = savedfont != null ? savedfont.replace("_", " ") : null;
		for (int i = 0; i < fonts.size(); i++) {
			String font = fonts.get(i);
			RowItem item = new RowItem(context, font, i);
			if (item.getVisibility() == View.VISIBLE) rowItems.add(item);
			if (font.equals(savedfont)) fontselected = i;
		}
		final SpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(WidgetConfigure.this, R.layout.listitems_layout, R.id.spinnerview, rowItems);
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
		if (fontselected != 0) fontspinner.setSelection(fontselected);
		((SeekBar) findViewById(R.id.datefontsizeseekbar)).setMax((context.getResources().getInteger(R.integer.maxfontscale) - 3) * 100);
		((SeekBar) findViewById(R.id.datefontsizeseekbar)).setProgress(
				((SeekBar) findViewById(R.id.datefontsizeseekbar)).getMax() -
						(context.getResources().getInteger(R.integer.maxfontscale) * 100 -
								(int) (sharedPreferences.getFloat(context.getResources().getString(R.string.widgetkeyfontscale),
										((SeekBar) findViewById(R.id.datefontsizeseekbar)).getMax() * 2 - context.getResources().getInteger(R.integer.widgetdefaultdatefontscale) - 3
								) * 100)
						)
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
		findViewById(R.id.viewcolor).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SeekBar) findViewById(R.id.seekbarred)).setProgress((int) (Math.random() * 255));
				((SeekBar) findViewById(R.id.seekbarblue)).setProgress((int) (Math.random() * 255));
				((SeekBar) findViewById(R.id.seekbargreen)).setProgress((int) (Math.random() * 255));
			}
		});
		findViewById(R.id.save).setOnClickListener(savelistener);
		updatepreview();
	}
	
	private void updatepreview() {
		//Log.d(TAG, "updatepreview: i have been called");
		//Log.d(TAG, "updatepreview: trace is " + Arrays.toString(Thread.currentThread().getStackTrace()));
		final Context context = WidgetConfigure.this;
		ImageView imageView = findViewById(R.id.previewclock);
		boolean withdate = ((Switch) findViewById(R.id.dateselector)).isChecked();
		int minuteoverhang;
		try {
			minuteoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			minuteoverhang = context.getResources().getInteger(R.integer.widgetdefaultminuteoverhang);
		}
		int houroverhang;
		try {
			houroverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimehours)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			houroverhang = context.getResources().getInteger(R.integer.widgetdefaulthouroverhang);
		}
		int secondoverhang;
		try {
			secondoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeseconds)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			secondoverhang = context.getResources().getInteger(R.integer.widgetdefaultsecondoverhang);
		}
		int dayoverhang;
		try {
			dayoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatedays)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			dayoverhang = context.getResources().getInteger(R.integer.widgetdefaultdayoverhang);
		}
		int monthoverhang;
		try {
			monthoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatemonths)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			monthoverhang = context.getResources().getInteger(R.integer.widgetdefaultmonthoverhang);
		}
		boolean withseconds = ((Switch) findViewById(R.id.secondsselector)).isChecked();
		SeekBar seekbarred = findViewById(R.id.seekbarred);
		SeekBar seekbargreen = findViewById(R.id.seekbargreen);
		SeekBar seekbarblue = findViewById(R.id.seekbarblue);
		SeekBar seekbaralpha = findViewById(R.id.seekbaralpha);
		int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
		SeekBar fontsizedividerseekbar = findViewById(R.id.datefontsizeseekbar);
		float fontsizedivider = context.getResources().getInteger(R.integer.maxfontscale) - (float) fontsizedividerseekbar.getProgress() / 100;
		if (((Switch) findViewById(R.id.dateselector)).isChecked()) {
			findViewById(R.id.overhanginputdatedays).setVisibility(View.VISIBLE);
			findViewById(R.id.overhanginputdatemonths).setVisibility(View.VISIBLE);
			fontsizedividerseekbar.setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.overhanginputdatedays).setVisibility(View.GONE);
			findViewById(R.id.overhanginputdatemonths).setVisibility(View.GONE);
			fontsizedividerseekbar.setVisibility(View.GONE);
		}
		if (((Switch) findViewById(R.id.secondsselector)).isChecked()) {
			findViewById(R.id.secondsinfo).setVisibility(View.VISIBLE);
			findViewById(R.id.overhanginputtimeseconds).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.secondsinfo)).setWidth(findViewById(R.id.secondsselector).getWidth());
		} else {
			findViewById(R.id.secondsinfo).setVisibility(View.GONE);
			findViewById(R.id.overhanginputtimeseconds).setVisibility(View.GONE);
		}
		if (((Switch) findViewById(R.id.autohourselector)).isChecked()) {
			findViewById(R.id.hourselector).setEnabled(false);
			((Switch) findViewById(R.id.hourselector)).setChecked(!DateFormat.is24HourFormat(WidgetConfigure.this));
		} else {
			findViewById(R.id.hourselector).setEnabled(true);
		}
		boolean twelvehour = ((Switch) findViewById(R.id.hourselector)).isChecked();
		Spinner spinner = findViewById(R.id.fontspinner);
		String font;
		try {
			font = spinner.getSelectedItem().toString();
		} catch (NullPointerException nulle) {
			//Expected if called to early
			font = context.getResources().getString(R.string.defaultfonttext);
		}
		Bitmap bitmap = ClockGenerator.generateWidget(
				context, Calendar.getInstance().getTimeInMillis(),
				secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
				twelvehour, withseconds, withdate,
				font, color, fontsizedivider
		);
		bitmap.prepareToDraw();
		imageView.setImageBitmap(bitmap);
	}
}
