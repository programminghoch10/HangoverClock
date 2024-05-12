package com.JJ.hangoverclock;

import android.annotation.TargetApi;
import android.os.Build;

import com.crossbowffs.remotepreferences.RemotePreferenceProvider;

public class PreferencesProvider extends RemotePreferenceProvider {
    private static final String[] files = {"statusbar", "lockscreen"};
    
    public PreferencesProvider() {
        super("com.JJ.hangoverclock.PreferencesProvider", files);
    }
    
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean checkAccess(String prefFileName, String prefKey, boolean write) {
        if (write) return false;
        if ("com.android.systemui".equals(getCallingPackage())) return true;
        return "com.JJ.hangoverclock".equals(getCallingPackage());
    }
}
