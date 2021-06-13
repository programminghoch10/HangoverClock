package com.JJ.hangoverclock;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

public class SettingsActivity extends Activity {
	private static final String TAG = SettingsActivity.class.getName();
	
	SharedPreferences sharedPreferences;
	
	Switch statusbarclockenabled;
	RadioButton statusbarclocktextbased;
	RadioButton statusbarclockimagebased;
	RadioGroup statusbarclocktype;
	Switch lockscreenclockenabled;
	RadioButton lockscreenclocktextbased;
	RadioButton lockscreenclockimagebased;
	RadioGroup lockscreenclocktype;
	CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			radioButtonChanged();
		}
	};
	RadioGroup.OnCheckedChangeListener onCheckedChangeListener1 = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			radioButtonChanged();
		}
	};
	
	private void radioButtonChanged() {
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
		sharedPreferences = getSharedPreferences(getString(R.string.systempreferencesfilename), MODE_PRIVATE);
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
		
		statusbarclockenabled = findViewById(R.id.statusbarclockenabled);
		statusbarclocktextbased = findViewById(R.id.statusbarclocktextbased);
		statusbarclockimagebased = findViewById(R.id.statusbarclockimagebased);
		statusbarclocktype = findViewById(R.id.statusbarclocktype);
		lockscreenclockenabled = findViewById(R.id.lockscreenclockenabled);
		lockscreenclocktextbased = findViewById(R.id.lockscreenclocktextbased);
		lockscreenclockimagebased = findViewById(R.id.lockscreenclockimagebased);
		lockscreenclocktype = findViewById(R.id.lockscreenclocktype);
		
		loadConfig();
	}
	
	private void addListeners() {
		statusbarclockenabled.setOnCheckedChangeListener(onCheckedChangeListener);
		statusbarclocktype.setOnCheckedChangeListener(onCheckedChangeListener1);
		lockscreenclockenabled.setOnCheckedChangeListener(onCheckedChangeListener);
		lockscreenclocktype.setOnCheckedChangeListener(onCheckedChangeListener1);
	}
	
	private void removeListeners() {
		statusbarclockenabled.setOnCheckedChangeListener(null);
		statusbarclocktype.setOnCheckedChangeListener(null);
		lockscreenclockenabled.setOnCheckedChangeListener(null);
		lockscreenclocktype.setOnCheckedChangeListener(null);
	}
	
	@SuppressLint("ApplySharedPref")
	private void saveconfig() {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("statusbarenabled", statusbarclockenabled.isChecked());
		if (statusbarclockenabled.isChecked())
			editor.putBoolean("statusbarimagebased", statusbarclockimagebased.isChecked());
		editor.putBoolean("lockscreenenabled", lockscreenclockenabled.isChecked());
		if (lockscreenclockenabled.isChecked())
			editor.putBoolean("lockscreenimagebased", lockscreenclockimagebased.isChecked());
		//editor.apply();
		editor.commit(); //we need commit as we are reading from it directly afterwards
	}
	
	private void loadConfig() {
		removeListeners();
		statusbarclockenabled.setChecked(sharedPreferences.getBoolean("statusbarenabled", false));
		boolean statusbarclockimagebasedbool = sharedPreferences.getBoolean("statusbarimagebased", false);
		statusbarclockimagebased.setChecked(statusbarclockimagebasedbool);
		statusbarclocktextbased.setChecked(!statusbarclockimagebasedbool);
		findViewById(R.id.statusbarclocktype).setVisibility(statusbarclockenabled.isChecked() ? View.VISIBLE : View.GONE);
		lockscreenclockenabled.setChecked(sharedPreferences.getBoolean("lockscreenenabled", false));
		boolean lockscreenclockimagebasedbool = sharedPreferences.getBoolean("lockscreenimagebased", false);
		lockscreenclockimagebased.setChecked(lockscreenclockimagebasedbool);
		lockscreenclocktextbased.setChecked(!lockscreenclockimagebasedbool);
		findViewById(R.id.lockscreenclocktype).setVisibility(lockscreenclockenabled.isChecked() ? View.VISIBLE : View.GONE);
		addListeners();
	}
}
