package com.JJ.hangoverclock.xposed;

import android.os.Build;

public class Compatibility {
    public static boolean xposedHooked = false;
    public static boolean isXposedCompatible() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && Build.VERSION.SDK_INT <= Build.VERSION_CODES.R;
    }
}
