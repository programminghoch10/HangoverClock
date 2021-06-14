package com.JJ.hangoverclock.xposed;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.JJ.hangoverclock.ClockGenerator;
import com.JJ.hangoverclock.R;
import com.crossbowffs.remotepreferences.RemotePreferences;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LockscreenClockHook {
	private static final String TAG = "hangoverclock";
	
	@RequiresApi(api = Build.VERSION_CODES.Q)
	@SuppressLint("PrivateApi")
	protected static void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		XposedHelpers.findAndHookMethod(TextClock.class, "onTimeChanged", new XC_MethodReplacement() {
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
				
				Context context = AndroidAppHelper.currentApplication().createPackageContext("com.JJ.hangoverclock", Context.CONTEXT_IGNORE_SECURITY);
				if (context == null) return null;
				SharedPreferences sharedPreferences = new RemotePreferences(context, "com.JJ.hangoverclock.PreferencesProvider", "lockscreen");
				if (!sharedPreferences.getBoolean("enabled", false)) return null;
				int houroverhang = sharedPreferences.getInt("houroverhang", context.getResources().getInteger(R.integer.daydreamdefaulthouroverhang));
				int minuteoverhang = sharedPreferences.getInt("minuteoverhang", context.getResources().getInteger(R.integer.daydreamdefaultminuteoverhang));
				int secondoverhang = sharedPreferences.getInt("secondoverhang", context.getResources().getInteger(R.integer.daydreamdefaultsecondoverhang));
				int dayoverhang = sharedPreferences.getInt("dayoverhang", context.getResources().getInteger(R.integer.daydreamdefaultdayoverhang));
				int monthoverhang = sharedPreferences.getInt("monthoverhang", context.getResources().getInteger(R.integer.daydreamdefaultmonthoverhang));
				boolean twelvehour = sharedPreferences.getBoolean("twelvehours", !DateFormat.is24HourFormat(context));
				boolean enableseconds = sharedPreferences.getBoolean("enableseconds", context.getResources().getBoolean(R.bool.daydreamdefaultenableseconds));
				boolean enabledate = sharedPreferences.getBoolean("enabledate", context.getResources().getBoolean(R.bool.daydreamdefaultenabledate));
				String font = sharedPreferences.getString("font", context.getResources().getString(R.string.defaultfonttext));
				float fontscale = sharedPreferences.getFloat("fontscale", context.getResources().getInteger(R.integer.daydreamdefaultfontscale));
				int color = sharedPreferences.getInt("color", context.getResources().getColor(R.color.daydreamdefaultclockcolor));
				TextClock textClock = (TextClock) param.thisObject;
				long timestamp = System.currentTimeMillis();
				
				
				String text = ClockGenerator.calculatetime(timestamp, houroverhang, minuteoverhang, secondoverhang, twelvehour, enableseconds);
				textClock.setText(text);
				
				try {
					if (!sharedPreferences.getBoolean("imagebased", false))
						throw new Exception();
					Bitmap bitmap = ClockGenerator.generateWidget(context, timestamp,
							secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
							twelvehour, enableseconds, enabledate, font, color, fontscale);
					BitmapDrawable drawable = new BitmapDrawable(bitmap);
					drawable.setTargetDensity(90);
					textClock.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
					textClock.setText("");
				} catch (Exception e) {
					//Log.e(TAG, "replaceHookedMethod: ", e);
				}
				return null;
			}
		});
	}
	
}
