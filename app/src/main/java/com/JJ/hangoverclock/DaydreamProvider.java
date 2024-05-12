package com.JJ.hangoverclock;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Calendar;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class DaydreamProvider extends DreamService {
    
    private static final String TAG = DaydreamProvider.class.getName();
    private ClockConfig config;
    private int updateseconds;
    private boolean runUpdateThread = false;
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
                ClockGenerator.generateClock(DaydreamProvider.this, Calendar.getInstance().getTimeInMillis(), config)
        );
    }
    
    private void updateConfiguration() {
        Context context = DaydreamProvider.this;
        SharedPreferences sharedPreferences = getSharedPreferences(DaydreamProvider.this);
        config = new ClockConfig(sharedPreferences, ClockConfig.getDefaultsFromResources(context.getResources(), "daydream"));
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
