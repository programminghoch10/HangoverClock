package com.JJ.hangoverclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.format.DateFormat;

public class ClockConfig {
	//list of keys saved in sharedPreferences
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
	public boolean twelvehours = false;
	public boolean autoTwelveHours = true;
	public boolean enableseconds = false;
	public boolean enabledate = false;
	public String font = null;
	public float fontscale = 5;
	public int color = 0xFFFFFFFF;
	public int dayoverhang = 0;
	public int monthoverhang = 0;
	public int secondoverhang = 0;
	public int minuteoverhang = 0;
	public int houroverhang = 0;
	
	public ClockConfig() {
	
	}
	
	public ClockConfig(SharedPreferences sharedPreferences, ClockConfig defaults, Context context) {
		this(sharedPreferences, defaults);
		setAutoTwelveHours(context);
	}
	
	public ClockConfig(SharedPreferences sharedPreferences, ClockConfig defaults) {
		this(sharedPreferences, defaults, "");
	}
	
	public ClockConfig(SharedPreferences sharedPreferences, ClockConfig defaults, String keyappend) {
		autoTwelveHours = !sharedPreferences.contains("twelvehours" + keyappend) && defaults.autoTwelveHours;
		//when autoTwelveHours is enabled, following does not automatically select system property
		twelvehours = sharedPreferences.getBoolean("twelvehours" + keyappend, defaults.twelvehours);
		enableseconds = sharedPreferences.getBoolean("enableseconds" + keyappend, defaults.enableseconds);
		enabledate = sharedPreferences.getBoolean("enabledate" + keyappend, defaults.enabledate);
		font = sharedPreferences.getString("font" + keyappend, defaults.font);
		font = font != null ? font.replace("_", " ") : null;
		fontscale = sharedPreferences.getFloat("fontscale" + keyappend, defaults.fontscale);
		color = sharedPreferences.getInt("color" + keyappend, defaults.color);
		dayoverhang = sharedPreferences.getInt("dayoverhang" + keyappend, defaults.dayoverhang);
		monthoverhang = sharedPreferences.getInt("monthoverhang" + keyappend, defaults.monthoverhang);
		secondoverhang = sharedPreferences.getInt("secondoverhang" + keyappend, defaults.secondoverhang);
		minuteoverhang = sharedPreferences.getInt("minuteoverhang" + keyappend, defaults.minuteoverhang);
		houroverhang = sharedPreferences.getInt("houroverhang" + keyappend, defaults.houroverhang);
	}
	
	public static ClockConfig getDefaultsFromResources(Resources resources, String scope) {
		ClockConfig config = new ClockConfig();
		config.enableseconds = resources.getBoolean(getDefaultValueIdentifier(resources, scope, "enableseconds", "bool"));
		config.font = resources.getString(R.string.defaultfonttext);
		config.fontscale = resources.getInteger(getDefaultValueIdentifier(resources, scope, "fontscale", "integer"));
		config.houroverhang = resources.getInteger(getDefaultValueIdentifier(resources, scope, "houroverhang", "integer"));
		config.minuteoverhang = resources.getInteger(getDefaultValueIdentifier(resources, scope, "minuteoverhang", "integer"));
		config.secondoverhang = resources.getInteger(getDefaultValueIdentifier(resources, scope, "secondoverhang", "integer"));
		config.dayoverhang = resources.getInteger(getDefaultValueIdentifier(resources, scope, "dayoverhang", "integer"));
		config.monthoverhang = resources.getInteger(getDefaultValueIdentifier(resources, scope, "monthoverhang", "integer"));
		config.color = resources.getColor(getDefaultValueIdentifier(resources, scope, "color", "color"));
		config.enabledate = resources.getBoolean(getDefaultValueIdentifier(resources, scope, "enabledate", "bool"));
		config.autoTwelveHours = resources.getBoolean(getDefaultValueIdentifier(resources, scope, "autotimeselector", "bool"));
		config.twelvehours = resources.getBoolean(getDefaultValueIdentifier(resources, scope, "twelvehours", "bool"));
		return config;
	}
	
	private static int getDefaultValueIdentifier(Resources resources, String scope, String key, String type) {
		return resources.getIdentifier(scope + "default" + key, type, "com.JJ.hangoverclock");
	}
	
	public void setAutoTwelveHours(Context context) {
		if (autoTwelveHours) twelvehours = !DateFormat.is24HourFormat(context);
	}
	
	public void saveToSharedPreferences(SharedPreferences sharedPreferences, ClockConfig defaults, boolean forceSave) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		//flush all keys
		for (String key : keys) {
			editor.remove(key);
		}
		if (forceSave || defaults.enableseconds != enableseconds)
			editor.putBoolean("enableseconds", enableseconds);
		if (forceSave || !defaults.font.equals(font))
			editor.putString("font", font.replace(" ", "_"));
		if (forceSave || (defaults.fontscale != fontscale && enabledate))
			editor.putFloat("fontscale", fontscale);
		if (forceSave || defaults.houroverhang != houroverhang)
			editor.putInt("houroverhang", houroverhang);
		if (forceSave || defaults.minuteoverhang != minuteoverhang)
			editor.putInt("minuteoverhang", minuteoverhang);
		if (forceSave || defaults.secondoverhang != secondoverhang)
			editor.putInt("secondoverhang", secondoverhang);
		if (forceSave || defaults.dayoverhang != dayoverhang)
			editor.putInt("dayoverhang", dayoverhang);
		if (forceSave || defaults.monthoverhang != monthoverhang)
			editor.putInt("monthoverhang", monthoverhang);
		if (forceSave || defaults.color != color)
			editor.putInt("color", color);
		if (!autoTwelveHours)
			editor.putBoolean("twelvehours", twelvehours);
		if (forceSave || defaults.enabledate != enabledate)
			editor.putBoolean("enabledate", enabledate);
		editor.apply();
	}
}
