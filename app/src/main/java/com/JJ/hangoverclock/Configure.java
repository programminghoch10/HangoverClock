package com.JJ.hangoverclock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
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

import java.util.ArrayList;
import java.util.Calendar;

public class Configure {
	public static final String[] keys = {
			"twelvehours",
			"enableseconds",
			"enabledate",
			"font",
			"fontscale",
			"color",
			"dayoverhang",
			"monthoverhang",
			"secondoverhang",
			"minuteoverhang",
			"houroverhang",
	};
	private static final String TAG = Configure.class.getName();
	final String scope;
	final Context context;
	final Activity activity;
	private SharedPreferences instantApply = null;
	SeekBar.OnSeekBarChangeListener colorseekbarlistener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			View viewred = activity.findViewById(R.id.viewred);
			View viewgreen = activity.findViewById(R.id.viewgreen);
			View viewblue = activity.findViewById(R.id.viewblue);
			View viewalpha = activity.findViewById(R.id.viewalpha);
			View viewcolor = activity.findViewById(R.id.viewcolor);
			SeekBar seekbarred = activity.findViewById(R.id.seekbarred);
			SeekBar seekbargreen = activity.findViewById(R.id.seekbargreen);
			SeekBar seekbarblue = activity.findViewById(R.id.seekbarblue);
			SeekBar seekbaralpha = activity.findViewById(R.id.seekbaralpha);
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
	
	Configure(Context context, Activity activity, String scope) {
		this.context = context;
		this.activity = activity;
		this.scope = scope;
	}
	
	public void setInstantApply(SharedPreferences sharedPreferences) {
		instantApply = sharedPreferences;
	}
	
	public void onCreate(SharedPreferences sharedPreferences) {
		FontsProvider.collectfonts(context);
		// Change seekbar colors
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			((SeekBar) activity.findViewById(R.id.seekbarred)).getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
			((SeekBar) activity.findViewById(R.id.seekbargreen)).getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
			((SeekBar) activity.findViewById(R.id.seekbarblue)).getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
		}
		((SeekBar) activity.findViewById(R.id.seekbarred)).getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
		((SeekBar) activity.findViewById(R.id.seekbargreen)).getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
		((SeekBar) activity.findViewById(R.id.seekbarblue)).getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
		int color = sharedPreferences.getInt("color", context.getResources().getInteger(getDefaultValueIdentifier("color", "color")));
		((SeekBar) activity.findViewById(R.id.seekbarred)).setProgress(Color.red(color));
		((SeekBar) activity.findViewById(R.id.seekbargreen)).setProgress(Color.green(color));
		((SeekBar) activity.findViewById(R.id.seekbarblue)).setProgress(Color.blue(color));
		((SeekBar) activity.findViewById(R.id.seekbaralpha)).setProgress(Color.alpha(color));
		((SeekBar) activity.findViewById(R.id.seekbarred)).setOnSeekBarChangeListener(colorseekbarlistener);
		((SeekBar) activity.findViewById(R.id.seekbargreen)).setOnSeekBarChangeListener(colorseekbarlistener);
		((SeekBar) activity.findViewById(R.id.seekbarblue)).setOnSeekBarChangeListener(colorseekbarlistener);
		((SeekBar) activity.findViewById(R.id.seekbaralpha)).setOnSeekBarChangeListener(colorseekbarlistener);
		View viewred = activity.findViewById(R.id.viewred);
		View viewgreen = activity.findViewById(R.id.viewgreen);
		View viewblue = activity.findViewById(R.id.viewblue);
		View viewalpha = activity.findViewById(R.id.viewalpha);
		View viewcolor = activity.findViewById(R.id.viewcolor);
		//SeekBar seekbarred = activity.findViewById(R.id.seekbarred);
		//SeekBar seekbargreen = activity.findViewById(R.id.seekbargreen);
		//SeekBar seekbarblue = activity.findViewById(R.id.seekbarblue);
		//SeekBar seekbaralpha = activity.findViewById(R.id.seekbaralpha);
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
					R.id.overhanginputtimeseconds,
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
					EditText editText = activity.findViewById(id);
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
		((EditText) activity.findViewById(R.id.overhanginputtimeminutes)).addTextChangedListener(inputwatcher);
		((EditText) activity.findViewById(R.id.overhanginputtimeseconds)).addTextChangedListener(inputwatcher);
		((EditText) activity.findViewById(R.id.overhanginputdatedays)).addTextChangedListener(inputwatcher);
		((EditText) activity.findViewById(R.id.overhanginputtimehours)).addTextChangedListener(inputwatcher);
		((EditText) activity.findViewById(R.id.overhanginputdatemonths)).addTextChangedListener(inputwatcher);
		((EditText) activity.findViewById(R.id.overhanginputtimeminutes)).getText().append(String.valueOf(sharedPreferences.getInt("minuteoverhang", context.getResources().getInteger(getDefaultValueIdentifier("minuteoverhang", "integer")))));
		if (Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimeminutes)).getText().toString()) == 0)
			((EditText) activity.findViewById(R.id.overhanginputtimeminutes)).getText().clear();
		((EditText) activity.findViewById(R.id.overhanginputtimehours)).getText().append(String.valueOf(sharedPreferences.getInt("houroverhang", context.getResources().getInteger(getDefaultValueIdentifier("houroverhang", "integer")))));
		if (Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimehours)).getText().toString()) == 0)
			((EditText) activity.findViewById(R.id.overhanginputtimehours)).getText().clear();
		((EditText) activity.findViewById(R.id.overhanginputtimeseconds)).getText().append(String.valueOf(sharedPreferences.getInt("secondoverhang", context.getResources().getInteger(getDefaultValueIdentifier("secondoverhang", "integer")))));
		if (Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimeseconds)).getText().toString()) == 0)
			((EditText) activity.findViewById(R.id.overhanginputtimeseconds)).getText().clear();
		((EditText) activity.findViewById(R.id.overhanginputdatedays)).getText().append(String.valueOf(sharedPreferences.getInt("dayoverhang", context.getResources().getInteger(getDefaultValueIdentifier("dayoverhang", "integer")))));
		if (Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputdatedays)).getText().toString()) == 0)
			((EditText) activity.findViewById(R.id.overhanginputdatedays)).getText().clear();
		((EditText) activity.findViewById(R.id.overhanginputdatemonths)).getText().append(String.valueOf(sharedPreferences.getInt("monthoverhang", context.getResources().getInteger(getDefaultValueIdentifier("monthoverhang", "integer")))));
		if (Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputdatemonths)).getText().toString()) == 0)
			((EditText) activity.findViewById(R.id.overhanginputdatemonths)).getText().clear();
		
		CompoundButton.OnCheckedChangeListener updatepreviewlistener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updatepreview();
			}
		};
		((Switch) activity.findViewById(R.id.hourselector)).setChecked(sharedPreferences.getBoolean("twelvehours", !DateFormat.is24HourFormat(context)));
		((Switch) activity.findViewById(R.id.dateselector)).setChecked(sharedPreferences.getBoolean("enabledate", context.getResources().getBoolean(getDefaultValueIdentifier("enabledate", "bool"))));
		((Switch) activity.findViewById(R.id.secondsselector)).setChecked(sharedPreferences.getBoolean("enableseconds", context.getResources().getBoolean(getDefaultValueIdentifier("enableseconds", "bool"))));
		((Switch) activity.findViewById(R.id.autohourselector)).setChecked(!sharedPreferences.contains("twelvehours") && context.getResources().getBoolean(getDefaultValueIdentifier("autotimeselector", "bool")));
		activity.findViewById(R.id.hourselector).setEnabled(!((Switch) activity.findViewById(R.id.autohourselector)).isChecked());
		((Switch) activity.findViewById(R.id.hourselector)).setOnCheckedChangeListener(updatepreviewlistener);
		((Switch) activity.findViewById(R.id.dateselector)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				EditText[] editTexts = {
						activity.findViewById(R.id.overhanginputtimeminutes),
						activity.findViewById(R.id.overhanginputtimehours),
						activity.findViewById(R.id.overhanginputtimeseconds),
				};
				if (isChecked) {
					for (EditText editText : editTexts) {
						try {
							if (Integer.valueOf((editText.getText().toString())) >= Math.pow(2, 16))
								editText.setText("");
						} catch (NumberFormatException ignored) {
						}
					}
				}
				updatepreview();
			}
		});
		((Switch) activity.findViewById(R.id.secondsselector)).setOnCheckedChangeListener(updatepreviewlistener);
		((Switch) activity.findViewById(R.id.autohourselector)).setOnCheckedChangeListener(updatepreviewlistener);
		activity.findViewById(R.id.previewclock).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				updatepreview();
			}
		});
		Spinner fontspinner = activity.findViewById(R.id.fontspinner);
		ArrayList<RowItem> rowItems = new ArrayList<RowItem>();
		ArrayList<String> fonts = FontsProvider.getFonts();
		int fontselected = 0;
		String savedfont = sharedPreferences.getString("font", "");
		savedfont = savedfont != null ? savedfont.replace("_", " ") : null;
		for (int i = 0; i < fonts.size(); i++) {
			String font = fonts.get(i);
			RowItem item = new RowItem(context, font, i);
			if (item.getVisibility() == View.VISIBLE) rowItems.add(item);
			if (font.equals(savedfont)) fontselected = i;
		}
		final SpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(activity, R.layout.listitems_layout, R.id.spinnerview, rowItems);
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
		((SeekBar) activity.findViewById(R.id.datefontsizeseekbar)).setMax((context.getResources().getInteger(R.integer.maxfontscale) - 3) * 100);
		((SeekBar) activity.findViewById(R.id.datefontsizeseekbar)).setProgress(
				((SeekBar) activity.findViewById(R.id.datefontsizeseekbar)).getMax() -
						(context.getResources().getInteger(R.integer.maxfontscale) * 100 -
								(int) (sharedPreferences.getFloat("fontscale",
										((SeekBar) activity.findViewById(R.id.datefontsizeseekbar)).getMax() * 2 - context.getResources().getInteger(getDefaultValueIdentifier("fontscale", "integer")) - 3
								) * 100)
						)
		);
		((SeekBar) activity.findViewById(R.id.datefontsizeseekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
		activity.findViewById(R.id.viewcolor).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((SeekBar) activity.findViewById(R.id.seekbarred)).setProgress((int) (Math.random() * 255));
				((SeekBar) activity.findViewById(R.id.seekbarblue)).setProgress((int) (Math.random() * 255));
				((SeekBar) activity.findViewById(R.id.seekbargreen)).setProgress((int) (Math.random() * 255));
			}
		});
		updatepreview();
	}
	
	
	private void updatepreview() {
		//Log.d(TAG, "updatepreview: i have been called");
		//Log.d(TAG, "updatepreview: trace is " + Arrays.toString(Thread.currentThread().getStackTrace()));
		ImageView imageView = activity.findViewById(R.id.previewclock);
		boolean withdate = ((Switch) activity.findViewById(R.id.dateselector)).isChecked();
		int minuteoverhang = 0;
		try {
			minuteoverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimeminutes)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		int houroverhang = 0;
		try {
			houroverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimehours)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		int secondoverhang = 0;
		try {
			secondoverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimeseconds)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		int dayoverhang = 0;
		try {
			dayoverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputdatedays)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		int monthoverhang = 0;
		try {
			monthoverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputdatemonths)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		boolean withseconds = ((Switch) activity.findViewById(R.id.secondsselector)).isChecked();
		SeekBar seekbarred = activity.findViewById(R.id.seekbarred);
		SeekBar seekbargreen = activity.findViewById(R.id.seekbargreen);
		SeekBar seekbarblue = activity.findViewById(R.id.seekbarblue);
		SeekBar seekbaralpha = activity.findViewById(R.id.seekbaralpha);
		int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
		//int backgroundColor = Color.rgb((int)(seekbarred.getProgress() * 0.3), (int)(seekbargreen.getProgress()*0.6f), (int)(seekbarblue.getProgress() * 0.1f));
		int backgroundColor = 0xFF303030;
		if (seekbarred.getProgress() * 0.3 + seekbargreen.getProgress() * 0.6f + seekbarblue.getProgress() * 0.1f < 186) {
			backgroundColor = 0xFF808080;
		}
		activity.findViewById(R.id.previewclock).setBackgroundColor(backgroundColor);
		SeekBar fontsizedividerseekbar = activity.findViewById(R.id.datefontsizeseekbar);
		float fontsizedivider = context.getResources().getInteger(R.integer.maxfontscale) - (float) fontsizedividerseekbar.getProgress() / 100;
		if (((Switch) activity.findViewById(R.id.dateselector)).isChecked()) {
			((View) activity.findViewById(R.id.overhanginputdatedays).getParent()).setVisibility(View.VISIBLE);
			((View) activity.findViewById(R.id.overhanginputdatemonths).getParent()).setVisibility(View.VISIBLE);
			fontsizedividerseekbar.setVisibility(View.VISIBLE);
		} else {
			((View) activity.findViewById(R.id.overhanginputdatedays).getParent()).setVisibility(View.GONE);
			((View) activity.findViewById(R.id.overhanginputdatemonths).getParent()).setVisibility(View.GONE);
			fontsizedividerseekbar.setVisibility(View.GONE);
		}
		if (((Switch) activity.findViewById(R.id.secondsselector)).isChecked()) {
			//findViewById(R.id.secondsinfo).setVisibility(View.VISIBLE);
			//((TextView) activity.findViewById(R.id.secondsinfo)).setWidth(findViewById(R.id.secondsselector).getWidth());
			((View) activity.findViewById(R.id.overhanginputtimeseconds).getParent()).setVisibility(View.VISIBLE);
		} else {
			//findViewById(R.id.secondsinfo).setVisibility(View.GONE);
			((View) activity.findViewById(R.id.overhanginputtimeseconds).getParent()).setVisibility(View.GONE);
		}
		if (((Switch) activity.findViewById(R.id.autohourselector)).isChecked()) {
			activity.findViewById(R.id.hourselector).setEnabled(false);
			((Switch) activity.findViewById(R.id.hourselector)).setChecked(!DateFormat.is24HourFormat(context));
		} else {
			activity.findViewById(R.id.hourselector).setEnabled(true);
		}
		boolean twelvehour = ((Switch) activity.findViewById(R.id.hourselector)).isChecked();
		Spinner spinner = activity.findViewById(R.id.fontspinner);
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
		if (instantApply != null) savesettings(instantApply);
	}
	
	public void savesettings(SharedPreferences sharedPreferences) {
		SeekBar seekbarred = activity.findViewById(R.id.seekbarred);
		SeekBar seekbargreen = activity.findViewById(R.id.seekbargreen);
		SeekBar seekbarblue = activity.findViewById(R.id.seekbarblue);
		SeekBar seekbaralpha = activity.findViewById(R.id.seekbaralpha);
		int color = Color.argb(seekbaralpha.getProgress(), seekbarred.getProgress(), seekbargreen.getProgress(), seekbarblue.getProgress());
		SharedPreferences.Editor editor = sharedPreferences.edit();
		int minuteoverhang = 0;
		try {
			minuteoverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimeminutes)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		int houroverhang = 0;
		try {
			houroverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimehours)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		int secondoverhang = 0;
		try {
			secondoverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputtimeseconds)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		int dayoverhang = 0;
		try {
			dayoverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputdatedays)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		int monthoverhang = 0;
		try {
			monthoverhang = Integer.valueOf(((EditText) activity.findViewById(R.id.overhanginputdatemonths)).getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		String font;
		try {
			font = ((Spinner) activity.findViewById(R.id.fontspinner)).getSelectedItem().toString().replace(" ", "_");
		} catch (NullPointerException nulle) {
			//maybe default language?
			font = "default";
		}
		SeekBar fontsizedividerseekbar = activity.findViewById(R.id.datefontsizeseekbar);
		float fontscale = context.getResources().getInteger(R.integer.maxfontscale) - (float) (fontsizedividerseekbar.getMax() - fontsizedividerseekbar.getProgress()) / 100;
		boolean enableseconds = ((Switch) activity.findViewById(R.id.secondsselector)).isChecked();
		boolean autotwelvehours = ((Switch) activity.findViewById(R.id.autohourselector)).isChecked();
		boolean twelvehours = ((Switch) activity.findViewById(R.id.hourselector)).isChecked();
		boolean enabledate = ((Switch) activity.findViewById(R.id.dateselector)).isChecked();
		boolean as = context.getResources().getBoolean(R.bool.alwayssavepreference); //wether preferences should always be saved
		//flush all keys
		for (String key : keys) {
			editor.remove(key);
		}
		if (as | context.getResources().getBoolean(getDefaultValueIdentifier("enableseconds", "bool")) != enableseconds)
			editor.putBoolean("enableseconds", enableseconds);
		if (as | !context.getResources().getString(R.string.defaultfonttext).equals(font))
			editor.putString("font", font);
		if (as | (context.getResources().getInteger(getDefaultValueIdentifier("fontscale", "integer")) != fontscale & enabledate))
			editor.putFloat("fontscale", fontscale);
		if (as | context.getResources().getInteger(getDefaultValueIdentifier("houroverhang", "integer")) != houroverhang)
			editor.putInt("houroverhang", houroverhang);
		if (as | context.getResources().getInteger(getDefaultValueIdentifier("minuteoverhang", "integer")) != minuteoverhang)
			editor.putInt("minuteoverhang", minuteoverhang);
		if (as | context.getResources().getInteger(getDefaultValueIdentifier("secondoverhang", "integer")) != secondoverhang)
			editor.putInt("secondoverhang", secondoverhang);
		if (as | context.getResources().getInteger(getDefaultValueIdentifier("dayoverhang", "integer")) != dayoverhang)
			editor.putInt("dayoverhang", dayoverhang);
		if (as | context.getResources().getInteger(getDefaultValueIdentifier("monthoverhang", "integer")) != monthoverhang)
			editor.putInt("monthoverhang", monthoverhang);
		if (as | context.getResources().getColor(getDefaultValueIdentifier("color", "color")) != color)
			editor.putInt("color", color);
		if (!autotwelvehours)
			editor.putBoolean("twelvehours", twelvehours);
		if (as | context.getResources().getBoolean(getDefaultValueIdentifier("enabledate", "bool")) != enabledate)
			editor.putBoolean("enabledate", enabledate);
		editor.apply();
	}
	
	private int getDefaultValueIdentifier(String key, String type) {
		return context.getResources().getIdentifier(scope + "default" + key, type, context.getPackageName());
	}
}
