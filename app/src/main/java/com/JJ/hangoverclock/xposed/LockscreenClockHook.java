package com.JJ.hangoverclock.xposed;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextClock;

import androidx.annotation.RequiresApi;

import com.JJ.hangoverclock.ClockGenerator;
import com.JJ.hangoverclock.R;
import com.crossbowffs.remotepreferences.RemotePreferences;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LockscreenClockHook {
    private static final String TAG = "HangoverClock";
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("PrivateApi")
    protected static void hook(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedHelpers.findAndHookMethod("com.android.keyguard.KeyguardClockSwitch", lpparam.classLoader, "refresh", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(TAG, "beforeHookedMethod: lockscreen hook called");
                
                Context context = AndroidAppHelper.currentApplication().createPackageContext("com.JJ.hangoverclock", Context.CONTEXT_IGNORE_SECURITY);
                if (context == null) return;
                SharedPreferences sharedPreferences = new RemotePreferences(context, "com.JJ.hangoverclock.PreferencesProvider", "lockscreen");
                if (!sharedPreferences.getBoolean("enabled", false)) return;
                param.setResult(null);
                int houroverhang = sharedPreferences.getInt("houroverhang", context.getResources().getInteger(R.integer.lockscreendefaulthouroverhang));
                int minuteoverhang = sharedPreferences.getInt("minuteoverhang", context.getResources().getInteger(R.integer.lockscreendefaultminuteoverhang));
                int secondoverhang = sharedPreferences.getInt("secondoverhang", context.getResources().getInteger(R.integer.lockscreendefaultsecondoverhang));
                int dayoverhang = sharedPreferences.getInt("dayoverhang", context.getResources().getInteger(R.integer.lockscreendefaultdayoverhang));
                int monthoverhang = sharedPreferences.getInt("monthoverhang", context.getResources().getInteger(R.integer.lockscreendefaultmonthoverhang));
                boolean twelvehour = sharedPreferences.getBoolean("twelvehours", !DateFormat.is24HourFormat(context));
                boolean enableseconds = sharedPreferences.getBoolean("enableseconds", context.getResources().getBoolean(R.bool.lockscreendefaultenableseconds));
                boolean enabledate = sharedPreferences.getBoolean("enabledate", context.getResources().getBoolean(R.bool.lockscreendefaultenabledate));
                String font = sharedPreferences.getString("font", context.getResources().getString(R.string.defaultfonttext));
                float fontscale = sharedPreferences.getFloat("fontscale", context.getResources().getInteger(R.integer.lockscreendefaultfontscale));
                int color = sharedPreferences.getInt("color", context.getResources().getColor(R.color.lockscreendefaultcolor));
                long timestamp = System.currentTimeMillis();
                
                Field textClockField = XposedHelpers.findField(param.thisObject.getClass(), "mClockView");
                TextClock textClock = (TextClock) textClockField.get(param.thisObject);
                Field clockPluginField = XposedHelpers.findField(param.thisObject.getClass(), "mClockPlugin");
                Object mClockPlugin = clockPluginField.get(param.thisObject);
                if (mClockPlugin != null) {
                    textClockField = XposedHelpers.findFirstFieldByExactType(mClockPlugin.getClass(), TextClock.class);
                    textClock = (TextClock) textClockField.get(mClockPlugin);
                }
                Log.d(TAG, "beforeHookedMethod: clockplugin=" + mClockPlugin);
                Log.d(TAG, "beforeHookedMethod: textClockField=" + textClockField);
                Log.d(TAG, "beforeHookedMethod: textClock=" + textClock);
                
                // prevent TextClock auto ticking
                XposedHelpers.setBooleanField(textClock, "mStopTicking", true);
                
                boolean imagebased = sharedPreferences.getBoolean("imagebased", false);
                ((View) XposedHelpers.getObjectField(param.thisObject, "mKeyguardStatusArea"))
                        .setVisibility(imagebased && enabledate ? View.GONE : View.VISIBLE);
                if (imagebased) {
                    try {
                        Bitmap bitmap = ClockGenerator.generateClock(context, timestamp,
                                secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
                                twelvehour, enableseconds, enabledate, font, color, fontscale);
                        BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
                        //drawable.setTargetDensity(90);
                        drawable.setTargetDensity((int) (context.getResources().getDisplayMetrics().densityDpi * 0.25));
                        textClock.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                        textClock.setText("");
                    } catch (Exception e) {
                        Log.e(TAG, "beforeHookedMethod: ", e);
                    }
                } else {
                    String text = ClockGenerator.calculatetime(timestamp, houroverhang, minuteoverhang, secondoverhang, twelvehour, enableseconds);
                    textClock.setText(text);
                    textClock.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        });
    }
    
}
