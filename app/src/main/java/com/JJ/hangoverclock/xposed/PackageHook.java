package com.JJ.hangoverclock.xposed;

import de.robv.android.xposed.IXposedHookLoadPackage;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackageHook implements IXposedHookLoadPackage {
	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.systemui")) return;
		XposedBridge.log("Hangoverclock: Hooking SystemUI Clock");
		findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new StatusbarClockHook());
	}
}
