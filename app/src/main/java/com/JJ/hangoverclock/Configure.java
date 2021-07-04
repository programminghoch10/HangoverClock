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
import java.util.List;

public class Configure {
	private static final String TAG = Configure.class.getName();
	final String scope;
	final Context context;
	final Activity activity;
	SeekBar seekBarRed;
	SeekBar seekBarGreen;
	SeekBar seekBarBlue;
	SeekBar seekBarAlpha;
	View colorViewRed;
	View colorViewGreen;
	View colorViewBlue;
	View colorViewAlpha;
	View colorViewColor;
	EditText overhangInputTimeHours;
	EditText overhangInputTimeMinutes;
	EditText overhangInputTimeSeconds;
	EditText overhangInputDateMonths;
	EditText overhangInputDateDays;
	Switch twelveHourSwitch;
	Switch dateSwitch;
	Switch secondsSwitch;
	Switch autoTwelveHourSwitch;
	ImageView previewClock;
	Spinner fontSpinner;
	SeekBar dateFontSizeSeekBar;
	boolean imagebased = true;
	boolean dateavailable = true;
	boolean secondsavailable = true;
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
	
	public static Bitmap generatePreview(Context context, ClockConfig config) {
		return ClockGenerator.generateClock(context, Calendar.getInstance().getTimeInMillis(), config);
	}
	
	public void setInstantApply(SharedPreferences sharedPreferences) {
		instantApply = sharedPreferences;
	}
	
	public void onCreate(SharedPreferences sharedPreferences) {
		onCreate(sharedPreferences, true);
	}
	
	public void onCreate(SharedPreferences sharedPreferences, boolean imagebased) {
		onCreate(sharedPreferences, imagebased, true, true);
	}
	
	private void getViewReferences() {
		seekBarRed = activity.findViewById(R.id.seekbarred);
		seekBarGreen = activity.findViewById(R.id.seekbargreen);
		seekBarBlue = activity.findViewById(R.id.seekbarblue);
		seekBarAlpha = activity.findViewById(R.id.seekbaralpha);
		colorViewRed = activity.findViewById(R.id.viewred);
		colorViewGreen = activity.findViewById(R.id.viewgreen);
		colorViewBlue = activity.findViewById(R.id.viewblue);
		colorViewAlpha = activity.findViewById(R.id.viewalpha);
		colorViewColor = activity.findViewById(R.id.viewcolor);
		overhangInputTimeHours = activity.findViewById(R.id.overhangInputTimeHours);
		overhangInputTimeMinutes = activity.findViewById(R.id.overhangInputTimeMinutes);
		overhangInputTimeSeconds = activity.findViewById(R.id.overhangInputTimeSeconds);
		overhangInputDateMonths = activity.findViewById(R.id.overhangInputDateMonths);
		overhangInputDateDays = activity.findViewById(R.id.overhangInputDateDays);
		twelveHourSwitch = activity.findViewById(R.id.twelveHourSwitch);
		dateSwitch = activity.findViewById(R.id.dateSwitch);
		secondsSwitch = activity.findViewById(R.id.secondsSwitch);
		autoTwelveHourSwitch = activity.findViewById(R.id.autoTwelveHourSwitch);
		previewClock = activity.findViewById(R.id.previewClock);
		fontSpinner = activity.findViewById(R.id.fontSpinner);
		dateFontSizeSeekBar = activity.findViewById(R.id.dateFontSizeSeekBar);
		
	}
	
	public void onCreate(ClockConfig config) {
		onCreate(config, true);
	}
	
	public void onCreate(ClockConfig config, boolean imagebased) {
		onCreate(config, imagebased, true, true);
	}
	
	public void onCreate(SharedPreferences sharedPreferences, boolean imagebased, boolean dateavailable, boolean secondsavailable) {
		onCreate(new ClockConfig(sharedPreferences, ClockConfig.getDefaultsFromResources(context.getResources(), scope)), imagebased, dateavailable, secondsavailable);
	}
	
	public void onCreate(ClockConfig config, boolean _imagebased, boolean _dateavailable, boolean _secondsavailable) {
		getViewReferences();
		
		imagebased = _imagebased;
		dateavailable = _dateavailable;
		secondsavailable = _secondsavailable;
		
		if (!imagebased) dateavailable = false;
		
		// we need to prepare UI elements before loading data
		FontsProvider.collectfonts(context);
		ArrayList<RowItem> rowItems = new ArrayList<>();
		List<String> fonts = FontsProvider.getFonts();
		for (int i = 0; i < fonts.size(); i++) {
			String font = fonts.get(i);
			RowItem item = new RowItem(context, font, i);
			if (item.getVisibility() == View.VISIBLE) rowItems.add(item);
		}
		final SpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(activity, R.layout.listitems_layout, R.id.spinnerview, rowItems);
		fontSpinner.setAdapter(spinnerAdapter);
		
		//load data
		loadFromConfig(config);
		
		//visual changes and attach listeners
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			seekBarRed.getThumb().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
			seekBarGreen.getThumb().setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
			seekBarBlue.getThumb().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
		}
		seekBarRed.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
		seekBarGreen.getProgressDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
		seekBarBlue.getProgressDrawable().setColorFilter(Color.BLUE, PorterDuff.Mode.MULTIPLY);
		seekBarRed.setOnSeekBarChangeListener(colorseekbarlistener);
		seekBarGreen.setOnSeekBarChangeListener(colorseekbarlistener);
		seekBarBlue.setOnSeekBarChangeListener(colorseekbarlistener);
		seekBarAlpha.setOnSeekBarChangeListener(colorseekbarlistener);
		int color = Color.argb(seekBarAlpha.getProgress(), seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress());
		colorViewRed.setBackgroundColor(Color.argb(255, Color.red(color), 0, 0));
		colorViewGreen.setBackgroundColor(Color.argb(255, 0, Color.green(color), 0));
		colorViewBlue.setBackgroundColor(Color.argb(255, 0, 0, Color.blue(color)));
		colorViewAlpha.setBackgroundColor(Color.argb(255, Color.alpha(color), Color.alpha(color), Color.alpha(color)));
		colorViewColor.setBackgroundColor(color);
		TextWatcher inputwatcher = new TextWatcher() {
			final int[] ids = {
					R.id.overhangInputTimeMinutes,
					R.id.overhangInputTimeHours,
					R.id.overhangInputTimeSeconds,
					R.id.overhangInputDateDays,
					R.id.overhangInputDateMonths,
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
		overhangInputTimeHours.addTextChangedListener(inputwatcher);
		overhangInputTimeMinutes.addTextChangedListener(inputwatcher);
		overhangInputTimeSeconds.addTextChangedListener(inputwatcher);
		overhangInputDateMonths.addTextChangedListener(inputwatcher);
		overhangInputDateDays.addTextChangedListener(inputwatcher);
		
		twelveHourSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> updatepreview());
		dateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				EditText[] editTexts = {
						overhangInputTimeHours,
						overhangInputTimeMinutes,
						overhangInputTimeSeconds,
				};
				if (isChecked) {
					((View) overhangInputDateDays.getParent()).setVisibility(View.VISIBLE);
					((View) overhangInputDateMonths.getParent()).setVisibility(View.VISIBLE);
					dateFontSizeSeekBar.setVisibility(View.VISIBLE);
					for (EditText editText : editTexts) {
						try {
							//limit max int number to prevent ANR
							if (Integer.parseInt(editText.getText().toString()) >= Math.pow(2, 16))
								editText.setText("");
						} catch (NumberFormatException ignored) {
						}
					}
				} else {
					((View) overhangInputDateDays.getParent()).setVisibility(View.GONE);
					((View) overhangInputDateMonths.getParent()).setVisibility(View.GONE);
					dateFontSizeSeekBar.setVisibility(View.GONE);
				}
				updatepreview();
			}
		});
		secondsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					((View) overhangInputTimeSeconds.getParent()).setVisibility(View.VISIBLE);
					//findViewById(R.id.secondsinfo).setVisibility(View.VISIBLE);
				} else {
					((View) overhangInputTimeSeconds.getParent()).setVisibility(View.GONE);
					//findViewById(R.id.secondsinfo).setVisibility(View.GONE);
				}
				updatepreview();
			}
		});
		autoTwelveHourSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					twelveHourSwitch.setEnabled(false);
					twelveHourSwitch.setChecked(!DateFormat.is24HourFormat(context));
				} else {
					twelveHourSwitch.setEnabled(true);
				}
				updatepreview();
			}
		});
		previewClock.setOnClickListener(v -> updatepreview());
		fontSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				updatepreview();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		dateFontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
		colorViewColor.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				seekBarRed.setProgress((int) (Math.random() * 255));
				seekBarGreen.setProgress((int) (Math.random() * 255));
				seekBarBlue.setProgress((int) (Math.random() * 255));
			}
		});
		updatepreview();
	}
	
	private void loadFromConfig(ClockConfig config) {
		if (!imagebased) dateavailable = false;
		int color = config.color;
		seekBarRed.setProgress(Color.red(color));
		seekBarGreen.setProgress(Color.green(color));
		seekBarBlue.setProgress(Color.blue(color));
		seekBarAlpha.setProgress(Color.alpha(color));
		overhangInputTimeHours.getText().clear();
		if (config.houroverhang > 0)
			overhangInputTimeHours.getText().append(String.valueOf(config.houroverhang));
		overhangInputTimeMinutes.getText().clear();
		if (config.minuteoverhang > 0)
			overhangInputTimeMinutes.getText().append(String.valueOf(config.minuteoverhang));
		overhangInputTimeSeconds.getText().clear();
		if (secondsavailable) {
			if (config.secondoverhang > 0)
				overhangInputTimeSeconds.getText().append(String.valueOf(config.secondoverhang));
		}
		overhangInputDateMonths.getText().clear();
		overhangInputDateDays.getText().clear();
		if (dateavailable) {
			if (config.monthoverhang > 0)
				overhangInputDateMonths.getText().append(String.valueOf(config.monthoverhang));
			if (config.dayoverhang > 0)
				overhangInputDateDays.getText().append(String.valueOf(config.dayoverhang));
		}
		autoTwelveHourSwitch.setChecked(config.autoTwelveHours);
		twelveHourSwitch.setChecked(config.autoTwelveHours ? !DateFormat.is24HourFormat(context) : config.twelvehours);
		twelveHourSwitch.setEnabled(!config.autoTwelveHours);
		secondsSwitch.setChecked(secondsavailable && config.enableseconds);
		secondsSwitch.setVisibility(secondsavailable ? View.VISIBLE : View.GONE);
		((View) overhangInputTimeSeconds.getParent()).setVisibility(secondsSwitch.isChecked() ? View.VISIBLE : View.GONE);
		dateSwitch.setChecked(dateavailable && config.enabledate);
		((View) overhangInputDateMonths.getParent()).setVisibility(dateSwitch.isChecked() ? View.VISIBLE : View.GONE);
		((View) overhangInputDateDays.getParent()).setVisibility(dateSwitch.isChecked() ? View.VISIBLE : View.GONE);
		dateFontSizeSeekBar.setProgress((int) (dateFontSizeSeekBar.getMax() * config.fontscale));
		dateFontSizeSeekBar.setVisibility(dateSwitch.isChecked() ? View.VISIBLE : View.GONE);
		activity.findViewById(R.id.dateSelector).setVisibility(dateavailable ? View.VISIBLE : View.GONE);
		if (imagebased) {
			List<String> fonts = FontsProvider.getFonts();
			int fontselected = 0;
			for (int i = 0; i < fonts.size(); i++) {
				String font = fonts.get(i);
				if (font.equals(config.font)) fontselected = i;
			}
			if (fontselected != 0) fontSpinner.setSelection(fontselected);
		} else {
			fontSpinner.setSelection(0);
		}
		activity.findViewById(R.id.fontSelector).setVisibility(imagebased ? View.VISIBLE : View.GONE);
	}
	
	private ClockConfig getCurrentConfig() {
		ClockConfig config = new ClockConfig();
		config.enabledate = dateSwitch.isChecked();
		config.houroverhang = 0;
		try {
			config.houroverhang = Integer.parseInt(overhangInputTimeHours.getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		config.minuteoverhang = 0;
		try {
			config.minuteoverhang = Integer.parseInt(overhangInputTimeMinutes.getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		config.secondoverhang = 0;
		try {
			config.secondoverhang = Integer.parseInt(overhangInputTimeSeconds.getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		config.dayoverhang = 0;
		try {
			config.dayoverhang = Integer.parseInt(overhangInputDateDays.getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		config.monthoverhang = 0;
		try {
			config.monthoverhang = Integer.parseInt(overhangInputDateMonths.getText().toString());
		} catch (NumberFormatException numerr) {
			//Expected error if no value was choosen
		}
		config.enableseconds = secondsSwitch.isChecked();
		config.color = Color.argb(seekBarAlpha.getProgress(), seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress());
		config.fontscale = (float) dateFontSizeSeekBar.getProgress() / 100;
		config.enabledate = dateSwitch.isChecked();
		config.enableseconds = secondsSwitch.isChecked();
		config.autoTwelveHours = autoTwelveHourSwitch.isChecked();
		config.twelvehours = twelveHourSwitch.isChecked();
		try {
			config.font = fontSpinner.getSelectedItem().toString();
		} catch (NullPointerException nulle) {
			//Expected if called to early
			config.font = context.getResources().getString(R.string.defaultfonttext);
		}
		return config;
	}
	
	private void updatepreview() {
		//Log.d(TAG, "updatepreview: i have been called");
		//Log.d(TAG, "updatepreview: trace is " + Arrays.toString(Thread.currentThread().getStackTrace()));
		//int backgroundColor = Color.rgb((int)(seekBarRed.getProgress() * 0.3), (int)(seekBarGreen.getProgress()*0.6f), (int)(seekBarBlue.getProgress() * 0.1f));
		int backgroundColor = 0xFF303030;
		if (seekBarRed.getProgress() * 0.3 + seekBarGreen.getProgress() * 0.6f + seekBarBlue.getProgress() * 0.1f < 186) {
			backgroundColor = 0xFF808080;
		}
		previewClock.setBackgroundColor(backgroundColor);
		Bitmap bitmap = generatePreview(context, getCurrentConfig());
		bitmap.prepareToDraw();
		previewClock.setImageBitmap(bitmap);
		if (instantApply != null) savesettings(instantApply);
	}
	
	public void savesettings(SharedPreferences sharedPreferences) {
		boolean alwaysSave = activity.getResources().getBoolean(R.bool.alwayssavepreference);
		getCurrentConfig().saveToSharedPreferences(sharedPreferences, ClockConfig.getDefaultsFromResources(context.getResources(), scope), alwaysSave);
	}
}
