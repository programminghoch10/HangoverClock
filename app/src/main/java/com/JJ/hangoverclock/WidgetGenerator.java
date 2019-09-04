package com.JJ.hangoverclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.TypedValue;

import java.util.Calendar;

class WidgetGenerator {

    static Bitmap generateWidget(Context context, long timestamp,
                                 int secondoverhang, int minuteoverhang, int houroverhang,
                                 int dayoverhang, int monthoverhang, int yearoverhang,
                                 boolean twelvehours, boolean withseconds, boolean withdate,
                                 String font, int color, float fontscale) {
        //Calendar calendar = Calendar.getInstance();
        //calendar.setTimeInMillis(timestamp);
        if (!withdate) {
            return generateBitmap(context,
                    calculatetime(timestamp, houroverhang, minuteoverhang, secondoverhang, twelvehours, withseconds),
                    font, color);
        } else {
            return generateBitmap(context,
                    calculatetime(timestamp, houroverhang, minuteoverhang, secondoverhang, twelvehours, withseconds),
                    calculatedate(timestamp, dayoverhang, monthoverhang, yearoverhang),
                    font, color, fontscale);
        }
    }

    //constructors
    private static Bitmap generateBitmap(Context context, String time, String date, String font, int color, float datefontscale) {
        if (date == null) {
            return generateBitmap(context, time, font, color);
        } else {
            return generateBitmap(context, time, font, color, date, font, color, datefontscale);
        }
    }
    private static Bitmap generateBitmap(Context context, String time, String timefont, int timecolor) {
        return generateBitmap(context, false, time, timefont, timecolor, null, null, 0,
                context.getResources().getInteger(R.integer.defaultdatefontscale));
    }
    private static Bitmap generateBitmap(Context context, String time, String timefont, int timecolor,
                                         String date, String datefont, int datecolor, float datefontscale) {
        return generateBitmap(context, true, time, timefont, timecolor, date, datefont, datecolor, datefontscale);
    }
    private static Bitmap generateBitmap(Context context, boolean withdate,
                                         String time, String timefont, int timecolor,
                                         String date, String datefont, int datecolor, float fontscale) {
        //ah shit .settypeface doesnt exist in remoteviews wth do I do now? guess ill be rendering a bitmap
        //solution: https://stackoverflow.com/questions/4318572/how-to-use-a-custom-typeface-in-a-widget
        //but i added the date myself
        int fontSizePX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, context.getResources().getInteger(R.integer.widgetfontsize), context.getResources().getDisplayMetrics());
        int pad = (fontSizePX / 9);
        Typeface timetypeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        timefont = timefont.replace(" ", "_");
        if (!context.getString(R.string.defaultfonttext).equals(timefont)) {
            try {
                timetypeface = ResourcesCompat.getFont(context, context.getResources().getIdentifier(timefont, "font", context.getPackageName()));
            } catch (Resources.NotFoundException notfounderr) {
                //expected if no font was specified
            }
        }
        Paint timepaint = new Paint();
        timepaint.setTextAlign(Paint.Align.LEFT);
        timepaint.setAntiAlias(true);
        timepaint.setTypeface(timetypeface);
        timepaint.setColor(timecolor);
        timepaint.setTextSize(fontSizePX);
        Paint datepaint = new Paint();
        if (withdate) {
            Typeface datetypeface = Typeface.defaultFromStyle(Typeface.NORMAL);
            datefont = datefont.replace(" ", "_");
            if (!context.getString(R.string.defaultfonttext).equals(datefont)) {
                try {
                    datetypeface = ResourcesCompat.getFont(context, context.getResources().getIdentifier(datefont, "font", context.getPackageName()));
                } catch (Resources.NotFoundException notfounderr) {
                    //expected if no font was specified
                }
            }
            datepaint.setTextAlign(Paint.Align.CENTER);
            datepaint.setAntiAlias(true);
            datepaint.setTypeface(datetypeface);
            datepaint.setColor(datecolor);
            datepaint.setTextSize(fontSizePX / fontscale);
        }
        int textWidth = (int) (timepaint.measureText(time) + pad * 2);
        int height = (int) (fontSizePX / 0.75);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(time, (float) pad, fontSizePX, timepaint);
        if (withdate) canvas.drawText(date, (float) (bitmap.getWidth()/2) + pad, fontSizePX + (fontSizePX / fontscale), datepaint);
        return bitmap;
    }

    private static String calculatetime(long timestamp, int houroverhang, int minuteoverhang, int secondoverhang, boolean twelvehours, boolean withseconds) {
        //inputs: long timestamp in millis
        //        int overhang of minutes(/seconds)
        //        int overhang of hours
        //        boolean if clock is using 12 hour format
        //        boolean if seconds shall be shown
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int h;
        if (twelvehours) {
            h = calendar.get(Calendar.HOUR);
        } else {
            h = calendar.get(Calendar.HOUR_OF_DAY);
        }
        int m = calendar.get(Calendar.MINUTE);
        int s = 0;
        if (withseconds) s = calendar.get(Calendar.SECOND);
        while (m<minuteoverhang) {
            m = m+60;
            if(m>=60) h--;
            if(h<houroverhang) {
                h+=24;
                if (twelvehours) h-=12;
            }
            if (withseconds & s<secondoverhang) {
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

    private static String calculatedate(long timestamp, int dayoverhang, int monthoverhang, int yearoverhang) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        String datestring =
                calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + "." + calendar.get(Calendar.YEAR);
        return datestring;
    }
}
