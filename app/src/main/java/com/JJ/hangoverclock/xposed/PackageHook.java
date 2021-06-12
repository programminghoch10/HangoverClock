package com.JJ.hangoverclock.xposed;

import android.os.Build;

import androidx.annotation.RequiresApi;

import de.robv.android.xposed.IXposedHookLoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageHook implements IXposedHookLoadPackage {
	@RequiresApi(api = Build.VERSION_CODES.Q)
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.systemui")) return;
		XposedBridge.log("Hangoverclock: Hooking SystemUI Clock");
		StatusbarClockHook.hook(lpparam);
		LockscreenClockHook.hook(lpparam);
	}
}
