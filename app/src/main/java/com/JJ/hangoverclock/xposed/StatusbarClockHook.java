package com.JJ.hangoverclock.xposed;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.JJ.hangoverclock.ClockGenerator;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class StatusbarClockHook {
	
	@RequiresApi(api = Build.VERSION_CODES.Q)
	protected static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
		findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodReplacement() {
			@Override
			protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
				try {
					Context context = AndroidAppHelper.currentApplication().createPackageContext("com.JJ.hangoverclock", Context.CONTEXT_IGNORE_SECURITY);
					if (context == null) throw new NullPointerException();
					TextView textView = (TextView) param.thisObject;
					long timestamp = System.currentTimeMillis();
					Bitmap bitmap = ClockGenerator.generateWidget(context, timestamp,
							60, 60, 5, 31, 5,
							false, true, false,
							"nosifer", Color.WHITE, 3);
					BitmapDrawable drawable = new BitmapDrawable(bitmap);
					drawable.setTargetDensity(20);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
					}
				} catch (Exception ignored) {
					//well we're modding the system afterall
					// anything could happen, so just fallback to text based clock
					//XposedBridge.log("Something went wrong creating the bitmap.");
					TextView tv = (TextView) param.thisObject;
					long timestamp = System.currentTimeMillis();
					String text = ClockGenerator.calculatetime(timestamp, 5, 60, 60, true, true);
					tv.setText(text);
				}
				return null;
			}
		});
		
	}
	
}
