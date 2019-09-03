package com.JJ.hangoverclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.AlarmManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

public class ClockWidgetProvider extends AppWidgetProvider {

    static final String TAG = "ClockWidgetProvider";
    public static ArrayList<String> fonts = new ArrayList<String>() {{
        add("default");
    }};
    //public static final String[] fonts = new String[] { "default", "faster one", "magneto", "nosifer", "orbitron", "oswald", "permanent marker", "press start 2p" };


    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        setAlarmManager(context);
    }

    public static int overhang;
    public static String CLOCK_WIDGET_UPDATE = "com.JJ.hangoverclock.widgetupdate";
    public static String controlbutton = "controlbuttonclicklistener";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        //Log.d(TAG, "onReceive: got intent " + intent.getAction());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (intent.getAction().split("#").length != 1) {
            overhang = sharedPreferences.getInt("overhang" + intent.getAction().split("#")[1], overhang);
        }
        if (controlbutton.equals(intent.getAction().split("#")[0])) {
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            for (int appWidgetID : ids) {
                updateAppWidget(context, appWidgetManager, appWidgetID);
            }
            setAlarmManager(context);
        }
        if (CLOCK_WIDGET_UPDATE.equals(intent.getAction())) {
            //Log.d(TAG, "onReceive: Recieved Clock Update");
            // Get the widget manager and ids for this widget provider, then call the shared clock update method.
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] ids = appWidgetManager.getAppWidgetIds(thisAppWidget);
            boolean increaserefreshrate = false;
            for (int appWidgetID: ids) {
                if (sharedPreferences.getBoolean("seconds"+appWidgetID, false)) increaserefreshrate = true;
                updateAppWidget(context, appWidgetManager, appWidgetID);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("increaserefreshrate", increaserefreshrate);
            editor.apply();
            setAlarmManager(context);
        }
    }

    private PendingIntent createClockTickIntent(Context context) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(CLOCK_WIDGET_UPDATE);
        return PendingIntent.getBroadcast(context, 23, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createClockTickIntent(context));
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.clear();
        editor.commit(); //commit is intentional, i want to wait until cleanup is done
        Log.i(TAG, "onDisabled: Final cleanup done, Goodbye!");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.i(TAG, "onEnabled: Hello World!");
        setAlarmManager(context);
        collectfonts(context);
    }

    public static void collectfonts(Context context) {
        Field[] fontfields = R.font.class.getFields();
        fonts.set(0, context.getString(R.string.defaultfonttext));
        for (int i = 1; i < fontfields.length + 1; i++) {
            Field fontfield = fontfields[i - 1];
            String fontname;
            try {
                fontname = fontfield.getName().replace("_", " ");
            } catch (Exception e) {
                fontname = null;
            }
            //Log.d(TAG, "collectfonts: fontname " + i + " is \"" + fontname + "\"");
            if (fontname != null) {
                boolean indexexists;
                try {
                    fonts.get(i);
                    indexexists = true;
                } catch (IndexOutOfBoundsException indexerr) {
                    indexexists = false;
                }
                if (!indexexists) {
                    fonts.add(i, fontname);
                } else {
                    fonts.set(i, fontname);
                }
            }
        }
    }

    private void setAlarmManager(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("increaserefreshrate", false)) {
            calendar.add(Calendar.SECOND, 1);
        } else {
            calendar.add(Calendar.SECOND, (60 - calendar.get(Calendar.SECOND)));
        }
        AlarmManagerCompat.setExact(alarmManager, AlarmManager.RTC, calendar.getTimeInMillis(), createClockTickIntent(context));
    }

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        overhang = sharedPreferences.getInt("overhang" + appWidgetId, overhang);
        boolean twelvehour = sharedPreferences.getBoolean("twelvehour" + appWidgetId, false);
        int hour;
        if (twelvehour) {
            hour = Calendar.getInstance().get(Calendar.HOUR);
        } else {
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        }
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        int seconds = Calendar.getInstance().get(Calendar.SECOND);
        int houroverhang = context.getResources().getInteger(R.integer.houroverhang);
        boolean withseconds = sharedPreferences.getBoolean("seconds" + appWidgetId, false);
        String time = calculatetime((double)hour*60*60+minutes*60+seconds, overhang, houroverhang, twelvehour, withseconds);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);
        /*remoteViews.setTextViewText(R.id.clock, time);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("time" + appWidgetId, time);
        editor.apply();
        remoteViews.setOnClickPendingIntent(R.id.controlbutton, getPendingSelfIntent(context, controlbutton + "#" + appWidgetId));
        remoteViews.setTextColor(R.id.clock, sharedPreferences.getInt("color" + appWidgetId, context.getResources().getColor(R.color.defaultWidgetColor)));
        */
        String font = sharedPreferences.getString("font" + appWidgetId, "default");
        //ah shit .settypeface doesnt exist in remoteviews wth do I do now? guess ill be rendering a bitmap
        //solution: https://stackoverflow.com/questions/4318572/how-to-use-a-custom-typeface-in-a-widget
        int fontSizePX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, context.getResources().getInteger(R.integer.widgetfontsize), context.getResources().getDisplayMetrics());
        int pad = (fontSizePX / 9);
        Paint paint = new Paint();
        Typeface typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        if (!font.equals(context.getString(R.string.defaultfonttext))) {
            try {
                typeface = ResourcesCompat.getFont(context, context.getResources().getIdentifier(font, "font", context.getPackageName()));
            } catch (Resources.NotFoundException notfounderr) {
                //expected if no font was specified
            }
        }
        paint.setAntiAlias(true);
        paint.setTypeface(typeface);
        paint.setColor(sharedPreferences.getInt("color" + appWidgetId, context.getResources().getColor(R.color.defaultWidgetColor)));
        paint.setTextSize(fontSizePX);
        int textWidth = (int) (paint.measureText(time) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(time, (float) pad, fontSizePX, paint);
        remoteViews.setImageViewBitmap(R.id.clock, bitmap);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    public static String calculatetime(double time, int overhang, int houroverhang, boolean twelvehours, boolean withseconds) {
        //inputs: double time in seconds
        //        int overhang in seconds
        //        int overhang of hours
        //        boolean if clock is using 12 hour format
        //        boolean if seconds shall be shown
        int h = (int) Math.floor(time / 60 / 60);
        int m = (int) Math.floor(time / 60) - (h*60);
        int s = 0;
        if (withseconds) s = (int) Math.floor(time) - (m*60) - (h*60*60);
        while (m<overhang) {
            m = m+60;
            if(m>=60) h--;
            if(h<houroverhang) {
                h+=24;
                if (twelvehours) h-=12;
            }
            if (withseconds & s<overhang) {
                s = s+60;
                if(s>=60) m--;
            }
        }
        if(h<houroverhang) {
            h+=24;
            if (twelvehours) h-=12;
        }
        if (withseconds) return String.format("%02d", h)+":"+String.format("%02d", m)+":"+String.format("%02d", s);
        return String.format("%02d", h)+":"+String.format("%02d", m);
    }

}
