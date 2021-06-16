package com.JJ.hangoverclock;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

@RequiresApi(api = Build.VERSION_CODES.Q)
public class PreferencesProvider extends RemotePreferenceProvider {
	private static final String[] files = {"statusbar", "lockscreen"};
	
	public PreferencesProvider() {
		super("com.JJ.hangoverclock.PreferencesProvider", files);
	}
	
	@Override
	protected boolean checkAccess(String prefFileName, String prefKey, boolean write) {
		if (write) return false;
		if ("com.android.systemui".equals(getCallingPackage())) return true;
		return "com.JJ.hangoverclock".equals(getCallingPackage());
	}
}
