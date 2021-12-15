package com.JJ.hangoverclock.xposed;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.JJ.hangoverclock.BuildConfig;
import com.JJ.hangoverclock.SettingsActivity;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageHook implements IXposedHookLoadPackage {
    
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
            XposedBridge.log("HangoverClock: Android version " + Build.VERSION.SDK_INT + " is not supported!");
            return;
        }
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            //XposedBridge.log("HangoverClock: Hooking own package");
            Class<?> settingsActivity = XposedHelpers.findClass(SettingsActivity.class.getName(), lpparam.classLoader);
            XposedHelpers.setStaticBooleanField(settingsActivity, "xposedHooked", true);
            return;
        }
        if (!lpparam.packageName.equals("com.android.systemui")) return;
        XposedBridge.log("HangoverClock: Hooking SystemUI Clock");
        StatusbarClockHook.hook(lpparam);
        LockscreenClockHook.hook(lpparam);
    }
}
