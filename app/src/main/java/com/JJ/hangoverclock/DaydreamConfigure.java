package com.JJ.hangoverclock;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class DaydreamConfigure extends Activity {
	
	String TAG = "DaydreamConfigure";
	
	public DaydreamConfigure() {
		super();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the view layout resource to use.
		setContentView(R.layout.daydream_configure);
		Context context = DaydreamConfigure.this;
		SharedPreferences sharedPreferences = getSharedPreferences(context.getResources().getString(R.string.daydreampreferencesfilename), MODE_PRIVATE);
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
