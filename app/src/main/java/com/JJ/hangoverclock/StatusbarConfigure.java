package com.JJ.hangoverclock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class StatusbarConfigure extends Activity {
	
	String TAG = this.getClass().getName();
	
	public StatusbarConfigure() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the view layout resource to use.
		setContentView(R.layout.statusbar_configure);
		Context context = StatusbarConfigure.this;
		SharedPreferences sharedPreferences = getSharedPreferences(context.getResources().getString(R.string.statusbarpreferencesfilename), MODE_PRIVATE);
		Configure configure = new Configure(context, this, "daydream");
		configure.onCreate(sharedPreferences);
		
		findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				configure.savesettings(sharedPreferences);
				finish();
			}
		});
	}
}
