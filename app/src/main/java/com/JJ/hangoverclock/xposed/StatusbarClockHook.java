package com.JJ.hangoverclock.xposed;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.JJ.hangoverclock.ClockGenerator;
import com.JJ.hangoverclock.R;
import com.crossbowffs.remotepreferences.RemotePreferences;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class StatusbarClockHook {
    
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1, r -> {
        Thread thread = new Thread(r);
        thread.setPriority(Thread.MAX_PRIORITY);
        return thread;
    });
    private static volatile Result result;
    private static volatile boolean enabled = false;
    private static volatile boolean secondsAvailable = false;
    private static final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            result = calculateClock();
        }
    };
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected static void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                TextView textView = (TextView) param.thisObject;
                if (enabled) {
                    param.setResult(null);
                    secondsAvailable = XposedHelpers.getBooleanField(textView, "mShowSeconds");
                    if (result != null) {
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
                        startThread();
                    } else {
                        enabled = false;
                        //disable the service
                        Log.d("hangoverclock", "beforeHookedMethod: clock not generated or disabled, trying to return to defaults");
                        try {
                            //calling updateColors does not fix wrong colors, so we just set it to white
                            //XposedHelpers.callMethod(param.thisObject, "updateColors", null);
                            textView.setTextColor(Color.WHITE);
                            textView.setText("");
                            //this call weirdly does nothing
                            textView.setCompoundDrawables(null, null, null, null);
                        } catch (Exception e) {
                            //Log.e("hangoverclock", "beforeHookedMethod: error resetting stuff", e);
                        }
                    }
                } else {
                    //since the background thread is not refreshing the result anymore, we need to check manually
                    if (getEnabled()) {
                        startThread();
                        enabled = true;
                    }
                    //removing the compound drawables in disable routine is not enough, so for now just remove them every iteration
                    textView.setCompoundDrawables(null, null, null, null);
                }
            }
        });
    }
    
    private static void startThread() {
        executorService.execute(updateRunnable);
        //new Thread(runnable).start();
    }
    
    private static Context getContext() {
        try {
            return AndroidAppHelper.currentApplication().createPackageContext("com.JJ.hangoverclock", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("hangoverclock", "getContext: could not get context", e);
            return null;
        }
    }
    
    private static SharedPreferences getSharedPreferences(Context context) {
        return new RemotePreferences(context, "com.JJ.hangoverclock.PreferencesProvider", "statusbar");
    }
    
    private static boolean getEnabled() {
        Context context = getContext();
        if (context == null) throw new NullPointerException();
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return getEnabled(sharedPreferences);
    }
    
    private static boolean getEnabled(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean("enabled", false);
    }
    
    private static Result calculateClock() {
        Context context = getContext();
        if (context == null) return null;
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        if (!getEnabled(sharedPreferences)) return null;
        int houroverhang = sharedPreferences.getInt("houroverhang", context.getResources().getInteger(R.integer.statusbardefaulthouroverhang));
        int minuteoverhang = sharedPreferences.getInt("minuteoverhang", context.getResources().getInteger(R.integer.statusbardefaultminuteoverhang));
        int secondoverhang = sharedPreferences.getInt("secondoverhang", context.getResources().getInteger(R.integer.statusbardefaultsecondoverhang));
        int dayoverhang = sharedPreferences.getInt("dayoverhang", context.getResources().getInteger(R.integer.statusbardefaultdayoverhang));
        int monthoverhang = sharedPreferences.getInt("monthoverhang", context.getResources().getInteger(R.integer.statusbardefaultmonthoverhang));
        boolean twelvehour = sharedPreferences.getBoolean("twelvehours", !DateFormat.is24HourFormat(context));
        boolean enableseconds = secondsAvailable && sharedPreferences.getBoolean("enableseconds", context.getResources().getBoolean(R.bool.statusbardefaultenableseconds));
        boolean enabledate = sharedPreferences.getBoolean("enabledate", context.getResources().getBoolean(R.bool.statusbardefaultenabledate));
        String font = sharedPreferences.getString("font", context.getResources().getString(R.string.defaultfonttext));
        float fontscale = sharedPreferences.getFloat("fontscale", context.getResources().getInteger(R.integer.statusbardefaultfontscale));
        int color = sharedPreferences.getInt("color", context.getResources().getColor(R.color.statusbardefaultcolor));
        float density = sharedPreferences.getFloat("density", 1);
        long timestamp = System.currentTimeMillis() + 1000;
        boolean imagebased = sharedPreferences.getBoolean("imagebased", false);
        Result result = new Result();
        result.imagebased = imagebased;
        result.color = color;
        result.text = ClockGenerator.calculatetime(timestamp, houroverhang, minuteoverhang, secondoverhang, twelvehour, enableseconds);
        if (imagebased) {
            Bitmap bitmap = ClockGenerator.generateClock(context, timestamp,
                    secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
                    twelvehour, enableseconds, enabledate, font, color, fontscale);
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
			/*Log.i("hangoverclock", "calculateClock: "
					+ " densitydpi=" + context.getResources().getDisplayMetrics().densityDpi
					+ " calculateddensity=" + context.getResources().getDisplayMetrics().densityDpi * 0.04 * density);*/
            //drawable.setTargetDensity((int) (context.getResources().getDisplayMetrics().scaledDensity * 10));
            drawable.setTargetDensity((int) (context.getResources().getDisplayMetrics().densityDpi * 0.04 * density));
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
