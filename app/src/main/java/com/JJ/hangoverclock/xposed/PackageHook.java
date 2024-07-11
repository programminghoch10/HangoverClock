package com.JJ.hangoverclock.xposed;

import static com.JJ.hangoverclock.xposed.Compatibility.isXposedCompatible;

import android.os.Build;

import com.JJ.hangoverclock.BuildConfig;
import com.JJ.hangoverclock.SettingsActivity;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageHook implements IXposedHookLoadPackage {
    
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!isXposedCompatible()) {
            XposedBridge.log("HangoverClock: Android SDK " + Build.VERSION.SDK_INT + " is not supported!");
            return;
        }
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)) {
            //XposedBridge.log("HangoverClock: Hooking own package");
            Class<?> settingsActivity = XposedHelpers.findClass(SettingsActivity.class.getName(), lpparam.classLoader);
            XposedHelpers.setStaticBooleanField(Compatibility.class, "xposedHooked", true);
            return;
        }
        if (!lpparam.packageName.equals("com.android.systemui")) return;
        XposedBridge.log("HangoverClock: Hooking SystemUI Clock");
        StatusbarClockHook.hook(lpparam);
        LockscreenClockHook.hook(lpparam);
    }
}
