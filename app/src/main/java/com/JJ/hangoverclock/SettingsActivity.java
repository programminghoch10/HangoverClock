package com.JJ.hangoverclock;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public class SettingsActivity extends Activity {
	private static final String TAG = SettingsActivity.class.getName();
	private static boolean xposedHooked = false;
	SharedPreferences sharedPreferencesStatusbar;
	SharedPreferences sharedPreferencesLockscreen;
	ImageView daydreamclockpreview;
	Switch statusbarclockenabled;
	ImageView statusbarclockpreview;
	RadioButton statusbarclocktextbased;
	RadioButton statusbarclockimagebased;
	RadioGroup statusbarclocktype;
	SeekBar statusbarclockdensity;
	Switch lockscreenclockenabled;
	ImageView lockscreenclockpreview;
	RadioButton lockscreenclocktextbased;
	RadioButton lockscreenclockimagebased;
	RadioGroup lockscreenclocktype;
	RadioGroup.OnCheckedChangeListener onCheckedChangeListener1 = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			configChanged();
		}
	};
	CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			configChanged();
		}
	};
	LinearLayout xposednotinstalled;
	LinearLayout xposedinstalled;
	LinearLayout xposednotcompatible;
	LinearLayout daydreamclock;
	
	private void configChanged() {
		saveconfig();
		loadConfig();
	}
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		saveconfig();
		finish();
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updatePreviews();
	}
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		sharedPreferencesStatusbar = getSharedPreferences(getString(R.string.statusbarpreferencesfilename), MODE_PRIVATE);
		sharedPreferencesLockscreen = getSharedPreferences(getString(R.string.lockscreenpreferencesfilename), MODE_PRIVATE);
		findViewById(R.id.restartsystemui).setOnClickListener(v -> {
			Log.i(TAG, "onClick: Trying to restart SystemUI");
			try {
				Runtime.getRuntime().exec("su -c killall com.android.systemui");
			} catch (IOException ignored) {
			}
		});
		
		///add back button
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
		
		findViewById(R.id.statusbarclockconfigure).setOnClickListener(v -> {
			Intent intent = new Intent(SettingsActivity.this, StatusbarConfigure.class);
			startActivity(intent);
		});
		findViewById(R.id.lockscreenclockconfigure).setOnClickListener(v -> {
			Intent intent = new Intent(SettingsActivity.this, LockscreenConfigure.class);
			startActivity(intent);
		});
		findViewById(R.id.daydreamopensettingsbutton).setOnClickListener(v -> {
			Intent intent;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				intent = new Intent(Settings.ACTION_DREAM_SETTINGS);
			} else {
				intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
			}
			startActivity(intent);
		});
		findViewById(R.id.widgetopensettingsbutton).setOnClickListener(v -> {
			Intent intent = new Intent(SettingsActivity.this, SettingsWidgetActivity.class);
			startActivity(intent);
		});
		
		daydreamclockpreview = findViewById(R.id.daydreamclockpreview);
		daydreamclock = findViewById(R.id.daydreamlayout);
		statusbarclockenabled = findViewById(R.id.statusbarclockenabled);
		statusbarclockpreview = findViewById(R.id.statusbarclockpreview);
		statusbarclocktextbased = findViewById(R.id.statusbarclocktextbased);
		statusbarclockimagebased = findViewById(R.id.statusbarclockimagebased);
		statusbarclocktype = findViewById(R.id.statusbarclocktype);
		statusbarclockdensity = findViewById(R.id.statusbarclockdensity);
		lockscreenclockenabled = findViewById(R.id.lockscreenclockenabled);
		lockscreenclockpreview = findViewById(R.id.lockscreenclockpreview);
		lockscreenclocktextbased = findViewById(R.id.lockscreenclocktextbased);
		lockscreenclockimagebased = findViewById(R.id.lockscreenclockimagebased);
		lockscreenclocktype = findViewById(R.id.lockscreenclocktype);
		xposedinstalled = findViewById(R.id.xposedinstalled);
		xposednotinstalled = findViewById(R.id.xposednotinstalled);
		xposednotcompatible = findViewById(R.id.xposednotcompatible);
		
		daydreamclock.setVisibility(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 ? View.VISIBLE : View.GONE);
		daydreamclockpreview.setOnClickListener(v -> updatePreviews());
		statusbarclockpreview.setOnClickListener(v -> updatePreviews());
		lockscreenclockpreview.setOnClickListener(v -> updatePreviews());
		
		boolean xposedactive = xposedHooked;
		boolean xposedcompatible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
		xposedinstalled.setVisibility(xposedactive && xposedcompatible ? View.VISIBLE : View.GONE);
		xposednotinstalled.setVisibility(xposedcompatible && !xposedactive ? View.VISIBLE : View.GONE);
		xposednotcompatible.setVisibility(!xposedcompatible ? View.VISIBLE : View.GONE);
		
		loadConfig();
		updatePreviews();
	}
	
	private void addListeners() {
		statusbarclockenabled.setOnCheckedChangeListener(onCheckedChangeListener);
		statusbarclocktype.setOnCheckedChangeListener(onCheckedChangeListener1);
		statusbarclockdensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				configChanged();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		lockscreenclockenabled.setOnCheckedChangeListener(onCheckedChangeListener);
		lockscreenclocktype.setOnCheckedChangeListener(onCheckedChangeListener1);
	}
	
	private void removeListeners() {
		statusbarclockenabled.setOnCheckedChangeListener(null);
		statusbarclocktype.setOnCheckedChangeListener(null);
		statusbarclockdensity.setOnSeekBarChangeListener(null);
		lockscreenclockenabled.setOnCheckedChangeListener(null);
		lockscreenclocktype.setOnCheckedChangeListener(null);
	}
	
	@SuppressLint("ApplySharedPref")
	private void saveconfig() {
		SharedPreferences.Editor editorStatusbar = sharedPreferencesStatusbar.edit();
		SharedPreferences.Editor editorLockscreen = sharedPreferencesLockscreen.edit();
		editorStatusbar.putBoolean("enabled", statusbarclockenabled.isChecked());
		if (statusbarclockenabled.isChecked())
			editorStatusbar.putBoolean("imagebased", statusbarclockimagebased.isChecked());
		float statusbarclockdensityfloat = (float) statusbarclockdensity.getProgress() / statusbarclockdensity.getMax() + 0.5f;
		editorStatusbar.putFloat("density", statusbarclockdensityfloat);
		Log.d(TAG, "saveconfig: density=" + statusbarclockdensityfloat);
		editorLockscreen.putBoolean("enabled", lockscreenclockenabled.isChecked());
		if (lockscreenclockenabled.isChecked())
			editorLockscreen.putBoolean("imagebased", lockscreenclockimagebased.isChecked());
		// we need commit as we are reading from it directly afterwards
		editorStatusbar.commit();
		editorLockscreen.commit();
	}
	
	private void loadConfig() {
		removeListeners();
		statusbarclockenabled.setChecked(sharedPreferencesStatusbar.getBoolean("enabled", false));
		statusbarclockpreview.setVisibility(statusbarclockenabled.isChecked() ? View.VISIBLE : View.GONE);
		boolean statusbarclockimagebasedbool = sharedPreferencesStatusbar.getBoolean("imagebased", false);
		statusbarclockimagebased.setChecked(statusbarclockimagebasedbool);
		statusbarclocktextbased.setChecked(!statusbarclockimagebasedbool);
		statusbarclockdensity.setVisibility(statusbarclockimagebased.isChecked() ? View.VISIBLE : View.GONE);
		statusbarclockdensity.setProgress((int) ((sharedPreferencesStatusbar.getFloat("density", 1) - 0.5f) * statusbarclockdensity.getMax()));
		findViewById(R.id.statusbarclocktype).setVisibility(statusbarclockenabled.isChecked() ? View.VISIBLE : View.GONE);
		lockscreenclockenabled.setChecked(sharedPreferencesLockscreen.getBoolean("enabled", false));
		lockscreenclockpreview.setVisibility(lockscreenclockenabled.isChecked() ? View.VISIBLE : View.GONE);
		boolean lockscreenclockimagebasedbool = sharedPreferencesLockscreen.getBoolean("imagebased", false);
		lockscreenclockimagebased.setChecked(lockscreenclockimagebasedbool);
		lockscreenclocktextbased.setChecked(!lockscreenclockimagebasedbool);
		findViewById(R.id.lockscreenclocktype).setVisibility(lockscreenclockenabled.isChecked() ? View.VISIBLE : View.GONE);
		addListeners();
	}
	
	private void updatePreviews() {
		Object[][] previewMeta = {
				{R.id.daydreamclockpreview, R.string.daydreampreferencesfilename, "daydream"},
				{R.id.statusbarclockpreview, R.string.statusbarpreferencesfilename, "statusbar"},
				{R.id.lockscreenclockpreview, R.string.lockscreenpreferencesfilename, "lockscreen"},
		};
		for (Object[] thisPreviewMeta : previewMeta) {
			ImageView view = findViewById((Integer) thisPreviewMeta[0]);
			Bitmap bitmap = getPreview(getString((Integer) thisPreviewMeta[1]), (String) thisPreviewMeta[2]);
			view.setImageDrawable(new BitmapDrawable(bitmap));
		}
	}
	
	private Bitmap getPreview(String sharedPreferencesName, String scope) {
		ClockConfig config = new ClockConfig(getSharedPreferences(sharedPreferencesName, MODE_PRIVATE), ClockConfig.getDefaultsFromResources(getResources(), scope));
		return ClockGenerator.generateClock(this, System.currentTimeMillis(), config);
	}
}
