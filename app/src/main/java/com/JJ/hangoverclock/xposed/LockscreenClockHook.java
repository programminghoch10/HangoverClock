package com.JJ.hangoverclock.xposed;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.Log;
import android.widget.TextClock;

import androidx.annotation.RequiresApi;

import com.JJ.hangoverclock.ClockGenerator;

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
				TextClock textClock = (TextClock) param.thisObject;
				
				long timestamp = System.currentTimeMillis();
				String text = ClockGenerator.calculatetime(timestamp, 5, 60, 60, true, false);
				textClock.setText(text);
				
				Context applicationContext = AndroidAppHelper.currentApplication();
				try {
					Context context = AndroidAppHelper.currentApplication().createPackageContext("com.JJ.hangoverclock", Context.CONTEXT_IGNORE_SECURITY);
					if (context == null) throw new NullPointerException();
					Bitmap bitmap = ClockGenerator.generateWidget(context, timestamp,
							60, 60, 5, 31, 5,
							false, false, false,
							"nosifer", Color.WHITE, 3);
					BitmapDrawable drawable = new BitmapDrawable(bitmap);
					drawable.setTargetDensity(90);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						textClock.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
						//textClock.setBackground(drawable);
					}
					textClock.setText("");
				} catch (Exception e) {
					Log.e(TAG, "replaceHookedMethod: ", e);
				}
				return null;
			}
		});
	}
	
}
