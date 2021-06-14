package com.JJ.hangoverclock.xposed;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.JJ.hangoverclock.ClockGenerator;
import com.JJ.hangoverclock.R;
import com.crossbowffs.remotepreferences.RemotePreferences;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class StatusbarClockHook {
	
	private static Result result;
	
	@RequiresApi(api = Build.VERSION_CODES.Q)
	protected static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
		findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodReplacement() {
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
				if (result != null) {
					TextView textView = (TextView) param.thisObject;
					try {
						if (!result.imagebased) throw new Exception();
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
							textView.setCompoundDrawablesWithIntrinsicBounds(result.drawable, null, null, null);
						}
						textView.setText("");
					} catch (Exception e) {
						//well we're modding the system afterall
						// anything could happen, so just fallback to text based clock
						textView.setTextColor(result.color);
						textView.setText(result.text);
						textView.setCompoundDrawables(null, null, null, null);
					}
				}
				
				new Thread() {
					@Override
					public void run() {
						super.run();
						result = calculateClock();
					}
				}.start();
				
				return null;
			}
		});
	}
	
	private static Result calculateClock() {
		Context context = null;
		try {
			context = AndroidAppHelper.currentApplication().createPackageContext("com.JJ.hangoverclock", Context.CONTEXT_IGNORE_SECURITY);
		} catch (PackageManager.NameNotFoundException e) {
			Log.e("hangoverclock", "run: could not get context", e);
			return null;
		}
		if (context == null) return null;
		SharedPreferences sharedPreferences = new RemotePreferences(context, "com.JJ.hangoverclock.PreferencesProvider", "statusbar");
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
		long timestamp = System.currentTimeMillis() + 1000;
		boolean imagebased = sharedPreferences.getBoolean("imagebased", false);
		Result result = new Result();
		result.imagebased = imagebased;
		result.color = color;
		result.text = ClockGenerator.calculatetime(timestamp, houroverhang, minuteoverhang, secondoverhang, twelvehour, enableseconds);
		if (imagebased) {
			Bitmap bitmap = ClockGenerator.generateWidget(context, timestamp,
					secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
					twelvehour, enableseconds, enabledate, font, color, fontscale);
			BitmapDrawable drawable = new BitmapDrawable(bitmap);
			drawable.setTargetDensity(20);
			result.drawable = drawable;
		}
		return result;
	}
	
	static class Result {
		boolean imagebased;
		String text;
		BitmapDrawable drawable;
		int color;
	}
	
}
