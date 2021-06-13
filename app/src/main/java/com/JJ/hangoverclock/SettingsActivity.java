package com.JJ.hangoverclock;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.io.IOException;

public class SettingsActivity extends Activity {
	private static final String TAG = SettingsActivity.class.getName();
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		findViewById(R.id.restartsystemui).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "onClick: Trying to restart SystemUI");
				try {
					Runtime.getRuntime().exec("su -c killall com.android.systemui");
				} catch (IOException ignored) {}
			}
		});
	}
}
