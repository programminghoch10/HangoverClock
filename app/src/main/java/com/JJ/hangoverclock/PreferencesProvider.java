package com.JJ.hangoverclock;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

public class PreferencesProvider extends RemotePreferenceProvider {
	private static final String[] files = {"statusbar", "lockscreen"};
	
	public PreferencesProvider() {
		super("com.JJ.hangoverclock.PreferencesProvider", files);
	}
}
