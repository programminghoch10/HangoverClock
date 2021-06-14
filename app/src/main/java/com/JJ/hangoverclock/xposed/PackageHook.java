package com.JJ.hangoverclock.xposed;

import android.os.Build;

import androidx.annotation.RequiresApi;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageHook implements IXposedHookLoadPackage {
	
	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.systemui")) return;
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.Q) {
			XposedBridge.log("HangoverClock: Android Version is smaller than required!");
			return;
		}
		XposedBridge.log("HangoverClock: Hooking SystemUI Clock");
		StatusbarClockHook.hook(lpparam);
		LockscreenClockHook.hook(lpparam);
	}
}
