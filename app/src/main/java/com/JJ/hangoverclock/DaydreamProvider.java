package com.JJ.hangoverclock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.dreams.DreamService;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class DaydreamProvider extends DreamService {

    private int houroverhang;
    private int minuteoverhang;
    private int secondoverhang;
    private int dayoverhang;
    private int monthoverhang;
    private boolean twelvehour;
    private boolean enableseconds;
    private boolean enabledate;
    private String font;
    private float fontscale;
    private int color;
    private int updateseconds;
    private boolean runUpdateThread = false;
    private static final String TAG = DaydreamProvider.class.getName();
    private final Thread updateThread = new Thread() {
        @Override
        public void run() {
            super.run();
            while (runUpdateThread) {
                try {
                    updateDisplay();
                } catch (RuntimeException e) {
                    Log.i(TAG, "run: got RuntimeException during updateDisplay, ignoring for now");
                }
                try {
                    Thread.sleep(updateseconds * 1000L);
                } catch (InterruptedException ignored) {
                }
            }
        }
    };

    private void updateDisplay() {
        ((ImageView) findViewById(R.id.daydreamimageview)).setImageBitmap(
                ClockGenerator.generateClock(
                        DaydreamProvider.this, Calendar.getInstance().getTimeInMillis(),
                        secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
                        twelvehour, enableseconds, enabledate, font, color, fontscale
                )
        );
    }

    private void updateConfiguration() {
        Context context = DaydreamProvider.this;
        SharedPreferences sharedPreferences = getSharedPreferences(DaydreamProvider.this);
        houroverhang = sharedPreferences.getInt("houroverhang", context.getResources().getInteger(R.integer.daydreamdefaulthouroverhang));
        minuteoverhang = sharedPreferences.getInt("minuteoverhang", context.getResources().getInteger(R.integer.daydreamdefaultminuteoverhang));
        secondoverhang = sharedPreferences.getInt("secondoverhang", context.getResources().getInteger(R.integer.daydreamdefaultsecondoverhang));
        dayoverhang = sharedPreferences.getInt("dayoverhang", context.getResources().getInteger(R.integer.daydreamdefaultdayoverhang));
        monthoverhang = sharedPreferences.getInt("monthoverhang", context.getResources().getInteger(R.integer.daydreamdefaultmonthoverhang));
        twelvehour = sharedPreferences.getBoolean("twelvehour", !DateFormat.is24HourFormat(context));
        enableseconds = sharedPreferences.getBoolean("enableseconds", context.getResources().getBoolean(R.bool.daydreamdefaultenableseconds));
        enabledate = sharedPreferences.getBoolean("enabledate", context.getResources().getBoolean(R.bool.daydreamdefaultenabledate));
        font = sharedPreferences.getString("font", context.getResources().getString(R.string.defaultfonttext));
        fontscale = sharedPreferences.getFloat("fontscale", context.getResources().getInteger(R.integer.daydreamdefaultfontscale));
        color = sharedPreferences.getInt("color", context.getResources().getColor(R.color.daydreamdefaultcolor));
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setInteractive(false);    // Exit dream upon user touch
        //setFullscreen(true);	// Hide status bar
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().getDecorView().setSystemUiVisibility(flags | getWindow().getDecorView().getSystemUiVisibility()); //hides nav bar
        //TODO: prevent status bar to go to light mode
        setScreenBright(false);    // wether to set screen brightness to 100%
        setContentView(R.layout.daydream);    // Set the dream layout
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return getSharedPreferences(context.getResources().getString(R.string.daydreampreferencesfilename), MODE_PRIVATE);
    }

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        SharedPreferences sharedPreferences = getSharedPreferences(DaydreamProvider.this);
        updateseconds = sharedPreferences.getBoolean("enableseconds", DaydreamProvider.this.getResources().getBoolean(R.bool.daydreamdefaultenableseconds))
                ? 1 : 60;
        updateConfiguration();
        runUpdateThread = true;
        updateThread.start();
    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        runUpdateThread = false;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        runUpdateThread = false;
    }

}
