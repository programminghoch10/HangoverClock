package com.JJ.hangoverclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import android.util.TypedValue;

import java.util.Calendar;
import java.util.Locale;

class WidgetGenerator {
    
    static Bitmap generateWidget(Context context, long timestamp,
                                 int secondoverhang, int minuteoverhang, int houroverhang,
                                 int dayoverhang, int monthoverhang,
                                 boolean twelvehours, boolean withseconds, boolean withdate,
                                 String font, int color, float fontscale, int fontresolution) {
        if (!withdate) {
            return generateBitmap(context,
                    calculatetime(timestamp, houroverhang, minuteoverhang, secondoverhang, twelvehours, withseconds),
                    font, color, fontresolution);
        } else {
            String[] hangovertext = combinedcalculate(timestamp,
                    monthoverhang, dayoverhang,
                    houroverhang, minuteoverhang, secondoverhang,
                    withseconds, twelvehours);
            return generateBitmap(context,
                    hangovertext[0], hangovertext[1],
                    font, color, fontscale, fontresolution);
        }
    }
    
    private static Bitmap generateBitmap(Context context, String time, String date, String font, int color, float datefontscale, int fontresolution) {
        if (date == null) {
            return generateBitmap(context, time, font, color, fontresolution);
        } else {
            return generateBitmap(context, time, font, color, date, font, color, datefontscale, fontresolution);
        }
    }
    
    private static Bitmap generateBitmap(Context context, String time, String timefont, int timecolor, int fontresolution) {
        return generateBitmap(context, false, time, timefont, timecolor, null, null, 0,
                0, fontresolution);
    }
    
    private static Bitmap generateBitmap(Context context, String time, String timefont, int timecolor,
                                         String date, String datefont, int datecolor, float datefontscale, int fontresolution) {
        return generateBitmap(context, true, time, timefont, timecolor, date, datefont, datecolor, datefontscale, fontresolution);
    }
    
    private static Bitmap generateBitmap(Context context, boolean withdate,
                                         String time, String timefont, int timecolor,
                                         String date, String datefont, int datecolor,
                                         float fontscale, int fontresolution) {
        //ah shit .settypeface doesnt exist in remoteviews wth do I do now? guess ill be rendering a bitmap
        //solution: https://stackoverflow.com/questions/4318572/how-to-use-a-custom-typeface-in-a-widget
        //but i added the date myself
        int fontSizePX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, fontresolution, context.getResources().getDisplayMetrics());
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
        int height = (int) (fontSizePX / 0.70);
        Bitmap bitmap = Bitmap.createBitmap(textWidth, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(time, (float) pad, fontSizePX, timepaint);
        if (withdate)
            canvas.drawText(date, (float) (bitmap.getWidth() / 2) + pad, fontSizePX + (fontSizePX / fontscale), datepaint);
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
        while (m < minuteoverhang | h < houroverhang | (withseconds & s < secondoverhang)) {
            if (m < minuteoverhang) {
                m += 60;
                h--;
            }
            if (h < houroverhang) {
                h += 24;
                if (twelvehours) h -= 12;
            }
            if (withseconds & s < secondoverhang) {
                s += 60;
                m--;
            }
        }
        if (h < houroverhang) {
            h += 24;
            if (twelvehours) h -= 12;
        }
        if (withseconds)
            return String.format(Locale.GERMANY, "%02d", h) + ":" + String.format(Locale.GERMANY, "%02d", m) + ":" + String.format(Locale.GERMANY, "%02d", s);
        return String.format(Locale.GERMANY, "%02d", h) + ":" + String.format(Locale.GERMANY, "%02d", m);
    }
    /*
    private static String calculatedate(long timestamp, int dayoverhang, int monthoverhang) {
        // i guess this function is redundant now, meh still gonna leave it here, i dont wanna scrap all that effort
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        while (day <= dayoverhang | month <= monthoverhang) {
            if (day <= dayoverhang) {
                calendar.add(Calendar.MONTH, -1);
                day += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                month -= 1;
            }
            if (month <= monthoverhang + 1) {
                month += calendar.getMaximum(Calendar.MONTH) + 1;
                year -= 1;
                calendar.add(Calendar.YEAR, -1);
            }
        }
        return day + "." + month + "." + year;
    }
    */
    private static String[] combinedcalculate(long timestamp,
                                              int monthoverhang, int dayoverhang,
                                              int houroverhang, int minuteoverhang, int secondoverhang,
                                              boolean withseconds, boolean twelvehours) {
        String[] returnstring = new String[2];
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);
        int s = calendar.get(Calendar.SECOND);
        while (day <= dayoverhang | month <= monthoverhang | m < minuteoverhang | h < houroverhang | (withseconds & s < secondoverhang)) {
            if (withseconds & s < secondoverhang) {
                s += 60;
                m--;
                calendar.add(Calendar.MINUTE, -1);
            }
            if (m < minuteoverhang) {
                m += 60;
                h--;
                calendar.add(Calendar.HOUR_OF_DAY, -1);
            }
            if (h < houroverhang) {
                h += 24;
                day--;
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
            if (day <= dayoverhang) {
                calendar.add(Calendar.MONTH, -1);
                day += calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                month -= 1;
            }
            if (month <= monthoverhang + 1) {
                month += calendar.getMaximum(Calendar.MONTH) + 1;
                year -= 1;
                calendar.add(Calendar.YEAR, -1);
            }
        }
        if (twelvehours & (h >= 12+houroverhang & h <= 24)) h -= 12;
        if (withseconds)
            returnstring[0] = String.format(Locale.GERMANY, "%02d", h) + ":" + String.format(Locale.GERMANY, "%02d", m) + ":" + String.format(Locale.GERMANY, "%02d", s);
        returnstring[0] = String.format(Locale.GERMANY, "%02d", h) + ":" + String.format(Locale.GERMANY, "%02d", m);
        returnstring[1] = day + "." + month + "." + year;
        return returnstring;
    }
}
