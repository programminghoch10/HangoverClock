package com.JJ.hangoverclock;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
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
	
	SharedPreferences sharedPreferencesStatusbar;
	SharedPreferences sharedPreferencesLockscreen;
	
	Switch statusbarclockenabled;
	RadioButton statusbarclocktextbased;
	RadioButton statusbarclockimagebased;
	RadioGroup statusbarclocktype;
	SeekBar statusbarclockdensity;
	Switch lockscreenclockenabled;
	RadioButton lockscreenclocktextbased;
	RadioButton lockscreenclockimagebased;
	RadioGroup lockscreenclocktype;
	CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			configChanged();
		}
	};
	RadioGroup.OnCheckedChangeListener onCheckedChangeListener1 = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			configChanged();
		}
	};
	LinearLayout xposednotinstalled;
	LinearLayout xposedinstalled;
	
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
		
		findViewById(R.id.statusbarclockconfigure).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this, StatusbarConfigure.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.lockscreenclockconfigure).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingsActivity.this, LockscreenConfigure.class);
				startActivity(intent);
			}
		});
		
		statusbarclockenabled = findViewById(R.id.statusbarclockenabled);
		statusbarclocktextbased = findViewById(R.id.statusbarclocktextbased);
		statusbarclockimagebased = findViewById(R.id.statusbarclockimagebased);
		statusbarclocktype = findViewById(R.id.statusbarclocktype);
		statusbarclockdensity = findViewById(R.id.statusbarclockdensity);
		lockscreenclockenabled = findViewById(R.id.lockscreenclockenabled);
		lockscreenclocktextbased = findViewById(R.id.lockscreenclocktextbased);
		lockscreenclockimagebased = findViewById(R.id.lockscreenclockimagebased);
		lockscreenclocktype = findViewById(R.id.lockscreenclocktype);
		xposedinstalled = findViewById(R.id.xposedinstalled);
		xposednotinstalled = findViewById(R.id.xposednotinstalled);
		
		boolean xposedactive = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
		xposedinstalled.setVisibility(xposedactive ? View.VISIBLE : View.GONE);
		xposednotinstalled.setVisibility(xposedactive ? View.GONE : View.VISIBLE);
		
		loadConfig();
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
		boolean statusbarclockimagebasedbool = sharedPreferencesStatusbar.getBoolean("imagebased", false);
		statusbarclockimagebased.setChecked(statusbarclockimagebasedbool);
		statusbarclocktextbased.setChecked(!statusbarclockimagebasedbool);
		statusbarclockdensity.setVisibility(statusbarclockimagebased.isChecked() ? View.VISIBLE : View.GONE);
		statusbarclockdensity.setProgress((int) ((sharedPreferencesStatusbar.getFloat("density", 1) - 0.5f) * statusbarclockdensity.getMax()));
		findViewById(R.id.statusbarclocktype).setVisibility(statusbarclockenabled.isChecked() ? View.VISIBLE : View.GONE);
		lockscreenclockenabled.setChecked(sharedPreferencesLockscreen.getBoolean("enabled", false));
		boolean lockscreenclockimagebasedbool = sharedPreferencesLockscreen.getBoolean("imagebased", false);
		lockscreenclockimagebased.setChecked(lockscreenclockimagebasedbool);
		lockscreenclocktextbased.setChecked(!lockscreenclockimagebasedbool);
		findViewById(R.id.lockscreenclocktype).setVisibility(lockscreenclockenabled.isChecked() ? View.VISIBLE : View.GONE);
		addListeners();
	}
}
