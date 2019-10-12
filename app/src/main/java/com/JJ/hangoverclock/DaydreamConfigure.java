package com.JJ.hangoverclock;

import android.app.Activity;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

public class DaydreamConfigure extends Activity {
	
	String TAG = "DaydreamConfigure";
	
	public void savesettings() {
		final Context context = DaydreamConfigure.this;
		SeekBar seekbarred = findViewById(R.id.seekbarred);
		SeekBar seekbargreen = findViewById(R.id.seekbargreen);
		SeekBar seekbarblue = findViewById(R.id.seekbarblue);
		SeekBar seekbaralpha = findViewById(R.id.seekbaralpha);
		int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
		SharedPreferences sharedPreferences = getSharedPreferences(context.getResources().getString(R.string.daydreampreferencesfilename), MODE_PRIVATE);
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
		SeekBar fontsizedividerseekbar = findViewById(R.id.datefontsizeseekbar);
		float fontscale = context.getResources().getInteger(R.integer.maxfontscale) - (float) (fontsizedividerseekbar.getMax() - fontsizedividerseekbar.getProgress()) / 100;
		boolean enableseconds = ((Switch) findViewById(R.id.secondsselector)).isChecked();
		boolean autotwelvehours = ((Switch) findViewById(R.id.autohourselector)).isChecked();
		boolean twelvehours = ((Switch) findViewById(R.id.hourselector)).isChecked();
		boolean enabledate = ((Switch) findViewById(R.id.dateselector)).isChecked();
		boolean as = context.getResources().getBoolean(R.bool.alwayssavepreference); //wether preferences should always be saved
		int[] flushkeys = {
				R.string.keyenableseconds,
				R.string.keyfont,
				R.string.keyfontscale,
				R.string.keyhouroverhang,
				R.string.keyminuteoverhang,
				R.string.keysecondoverhang,
				R.string.keydayoverhang,
				R.string.keymonthoverhang,
				R.string.keycolor,
				R.string.keytwelvehour,
				R.string.keyenabledate,
		};
		for (int key:flushkeys) {
			editor.remove(context.getResources().getString(key));
		}
		if (as | context.getResources().getBoolean(R.bool.daydreamdefaultenableseconds) != enableseconds)
			editor.putBoolean(context.getResources().getString(R.string.daydreamkeyenableseconds), enableseconds);
		if (as | !context.getResources().getString(R.string.defaultfonttext).equals(font))
			editor.putString(context.getResources().getString(R.string.daydreamkeyfont), font);
		if (as | (context.getResources().getInteger(R.integer.daydreamdefaultdatefontscale) != fontscale & enabledate))
			editor.putFloat(context.getResources().getString(R.string.daydreamkeyfontscale), fontscale);
		if (as | context.getResources().getInteger(R.integer.daydreamdefaulthouroverhang) != houroverhang)
			editor.putInt(context.getResources().getString(R.string.daydreamkeyhouroverhang), houroverhang);
		if (as | context.getResources().getInteger(R.integer.daydreamdefaultminuteoverhang) != minuteoverhang)
			editor.putInt(context.getResources().getString(R.string.daydreamkeyminuteoverhang), minuteoverhang);
		if (as | context.getResources().getInteger(R.integer.daydreamdefaultsecondoverhang) != secondoverhang)
			editor.putInt(context.getResources().getString(R.string.daydreamkeysecondoverhang), secondoverhang);
		if (as | context.getResources().getInteger(R.integer.daydreamdefaultdayoverhang) != dayoverhang)
			editor.putInt(context.getResources().getString(R.string.daydreamkeydayoverhang), dayoverhang);
		if (as | context.getResources().getInteger(R.integer.daydreamdefaultmonthoverhang) != monthoverhang)
			editor.putInt(context.getResources().getString(R.string.daydreamkeymonthoverhang), monthoverhang);
		if (as | context.getResources().getColor(R.color.daydreamdefaultWidgetColor) != color)
			editor.putInt(context.getResources().getString(R.string.daydreamkeycolor), color);
		if (!autotwelvehours)
			editor.putBoolean(context.getResources().getString(R.string.daydreamkeytwelvehour), twelvehours);
		if (as | context.getResources().getBoolean(R.bool.daydreamdefaultenabledate) != enabledate)
			editor.putBoolean(context.getResources().getString(R.string.daydreamkeyenabledate), enabledate);
		editor.apply();
	}
	
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
	
	public DaydreamConfigure() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the view layout resource to use.
		setContentView(R.layout.daydream_configure);
		WidgetProvider.collectfonts(DaydreamConfigure.this);
		SharedPreferences sharedPreferences = getSharedPreferences(DaydreamConfigure.this.getResources().getString(R.string.daydreampreferencesfilename), MODE_PRIVATE);
		Context context = DaydreamConfigure.this;
		// Change seekbar colors
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			((SeekBar) findViewById(R.id.seekbarred)).getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
			((SeekBar) findViewById(R.id.seekbargreen)).getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
			((SeekBar) findViewById(R.id.seekbarblue)).getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
		}
		((SeekBar) findViewById(R.id.seekbarred)).getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
		((SeekBar) findViewById(R.id.seekbargreen)).getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
		((SeekBar) findViewById(R.id.seekbarblue)).getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
		int color = sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeycolor), getResources().getColor(R.color.daydreamdefaultWidgetColor));
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
					} catch (NumberFormatException ignored) {}
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
		
		int test = context.getResources().getInteger(R.integer.daydreamdefaultminuteoverhang);
		String test2 = context.getResources().getString(R.string.daydreamkeyminuteoverhang);
		
		((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().append(String.valueOf(sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeyminuteoverhang), context.getResources().getInteger(R.integer.daydreamdefaultminuteoverhang))));
		if (Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().toString()) == (context.getResources().getInteger(R.integer.daydreamdefaultminuteoverhang))) ((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().clear();
		((EditText) findViewById(R.id.overhanginputtimehours)).getText().append(String.valueOf(sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeyhouroverhang), context.getResources().getInteger(R.integer.daydreamdefaulthouroverhang))));
		if (Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimehours)).getText().toString()) == (context.getResources().getInteger(R.integer.daydreamdefaulthouroverhang))) ((EditText) findViewById(R.id.overhanginputtimehours)).getText().clear();
		((EditText) findViewById(R.id.overhanginputdatedays)).getText().append(String.valueOf(sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeydayoverhang), context.getResources().getInteger(R.integer.daydreamdefaultdayoverhang))));
		if (Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatedays)).getText().toString()) == (context.getResources().getInteger(R.integer.daydreamdefaultdayoverhang))) ((EditText) findViewById(R.id.overhanginputdatedays)).getText().clear();
		((EditText) findViewById(R.id.overhanginputdatemonths)).getText().append(String.valueOf(sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeymonthoverhang), context.getResources().getInteger(R.integer.daydreamdefaultmonthoverhang))));
		if (Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatemonths)).getText().toString()) == (context.getResources().getInteger(R.integer.daydreamdefaultmonthoverhang))) ((EditText) findViewById(R.id.overhanginputdatemonths)).getText().clear();
		
		CompoundButton.OnCheckedChangeListener updatepreviewlistener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updatepreview();
			}
		};
		((Switch) findViewById(R.id.hourselector)).setChecked(sharedPreferences.getBoolean(context.getResources().getString(R.string.daydreamkeytwelvehour), !DateFormat.is24HourFormat(context)));
		((Switch) findViewById(R.id.dateselector)).setChecked(sharedPreferences.getBoolean(context.getResources().getString(R.string.daydreamkeyenabledate), context.getResources().getBoolean(R.bool.daydreamdefaultenabledate)));
		((Switch) findViewById(R.id.secondsselector)).setChecked(sharedPreferences.getBoolean(context.getResources().getString(R.string.daydreamkeyenableseconds), context.getResources().getBoolean(R.bool.daydreamdefaultenableseconds)));
		((Switch) findViewById(R.id.autohourselector)).setChecked(!sharedPreferences.contains(context.getResources().getString(R.string.daydreamkeytwelvehour)) && context.getResources().getBoolean(R.bool.daydreamdefaultautotimeselector));
		findViewById(R.id.hourselector).setEnabled(!((Switch) findViewById(R.id.autohourselector)).isChecked());
		((Switch) findViewById(R.id.hourselector)).setOnCheckedChangeListener(updatepreviewlistener);
		((Switch) findViewById(R.id.dateselector)).setOnCheckedChangeListener(updatepreviewlistener);
		((Switch) findViewById(R.id.secondsselector)).setOnCheckedChangeListener(updatepreviewlistener);
		((Switch) findViewById(R.id.autohourselector)).setOnCheckedChangeListener(updatepreviewlistener);
		Spinner fontspinner = findViewById(R.id.fontspinner);
		ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
		ArrayList<String> fonts = WidgetProvider.fonts;
		int fontselected = 0;
		for (int i = 0; i < fonts.size(); i++) {
			String font = fonts.get(i);
			RowItem item = new RowItem(context, font, i);
			if (item.getVisibility() == View.VISIBLE) rowItems.add(item);
			if (font.equals(sharedPreferences.getString(context.getResources().getString(R.string.daydreamkeyfont), ""))) fontselected = i;
		}
		final SpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(DaydreamConfigure.this, R.layout.listitems_layout, R.id.spinnerview, rowItems);
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
				(int)(sharedPreferences.getFloat(context.getResources().getString(R.string.daydreamkeyfontscale),
						((SeekBar) findViewById(R.id.datefontsizeseekbar)).getMax()*2 - context.getResources().getInteger(R.integer.daydreamdefaultdatefontscale) - 3
				) * 100))
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
		((Button) findViewById(R.id.save)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				savesettings();
				finish();
			}
		});
		updatepreview();
	}
	
	private void updatepreview() {
		//Log.d(TAG, "updatepreview: i have been called");
		//Log.d(TAG, "updatepreview: trace is " + Arrays.toString(Thread.currentThread().getStackTrace()));
		final Context context = DaydreamConfigure.this;
		ImageView imageView = findViewById(R.id.previewclock);
		boolean withdate = ((Switch) findViewById(R.id.dateselector)).isChecked();
		int minuteoverhang;
		try {
			minuteoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimeminutes)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			minuteoverhang = getResources().getInteger(R.integer.daydreamdefaultminuteoverhang);
		}
		int houroverhang;
		try {
			houroverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputtimehours)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			houroverhang = context.getResources().getInteger(R.integer.daydreamdefaulthouroverhang);
		}
		int secondoverhang = context.getResources().getInteger(R.integer.defaultsecondoverhang);
		secondoverhang = minuteoverhang;
		int dayoverhang;
		try {
			dayoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatedays)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			dayoverhang = getResources().getInteger(R.integer.daydreamdefaultdayoverhang);
		}
		int monthoverhang;
		try {
			monthoverhang = Integer.valueOf(((EditText) findViewById(R.id.overhanginputdatemonths)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen, just set to default value
			monthoverhang = context.getResources().getInteger(R.integer.daydreamdefaultmonthoverhang);
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
			findViewById(R.id.overhanginputdate).setVisibility(View.VISIBLE);
			fontsizedividerseekbar.setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.overhanginputdate).setVisibility(View.GONE);
			fontsizedividerseekbar.setVisibility(View.GONE);
		}
		/*if (((Switch) findViewById(R.id.secondsselector)).isChecked()) {
			findViewById(R.id.secondsinfo).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.secondsinfo)).setWidth(findViewById(R.id.secondsselector).getWidth());
		} else {
			findViewById(R.id.secondsinfo).setVisibility(View.GONE);
		}*/
		if (((Switch) findViewById(R.id.autohourselector)).isChecked()) {
			findViewById(R.id.hourselector).setEnabled(false);
			((Switch) findViewById(R.id.hourselector)).setChecked(!DateFormat.is24HourFormat(DaydreamConfigure.this));
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
		Bitmap bitmap = WidgetGenerator.generateWidget(
				context, Calendar.getInstance().getTimeInMillis(),
				secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
				twelvehour, withseconds, withdate,
				font, color, fontsizedivider
		);
		bitmap.prepareToDraw();
		imageView.setImageBitmap(bitmap);
	}
}
