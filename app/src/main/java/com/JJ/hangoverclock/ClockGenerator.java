package com.JJ.hangoverclock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.core.content.res.ResourcesCompat;

import java.util.Calendar;
import java.util.Locale;

public class ClockGenerator {
	
	public static Bitmap generateClock(Context context, long timestamp, ClockConfig config) {
		return ClockGenerator.generateClock(
				context, timestamp,
				config.secondoverhang, config.minuteoverhang, config.houroverhang,
				config.dayoverhang, config.monthoverhang,
				config.twelvehours, config.enableseconds, config.enabledate,
				config.font, config.color, config.fontscale
		);
	}
	
	public static Bitmap generateClock(Context context, long timestamp,
									   int secondoverhang, int minuteoverhang, int houroverhang,
									   int dayoverhang, int monthoverhang,
									   boolean twelvehours, boolean withseconds, boolean withdate,
									   String font, int color, float fontscale) {
		if (!withdate) {
			return generateBitmap(context,
					calculatetime(timestamp, houroverhang, minuteoverhang, secondoverhang, twelvehours, withseconds),
					font, color);
		} else {
			String[] hangovertext = combinedcalculate(timestamp,
					monthoverhang, dayoverhang,
					houroverhang, minuteoverhang, secondoverhang,
					withseconds, twelvehours);
			return generateBitmap(context,
					hangovertext[0], hangovertext[1],
					font, color, fontscale);
		}
	}
	
	private static Bitmap generateBitmap(Context context, String time, String date, String font, int color, float datefontscale) {
		if (date == null) {
			return generateBitmap(context, time, font, color);
		} else {
			return generateBitmap(context, time, font, color, date, font, color, datefontscale);
		}
	}
	
	private static Bitmap generateBitmap(Context context, String time, String timefont, int timecolor) {
		return generateBitmap(context, false, time, timefont, timecolor, null, null, 0, 0);
	}
	
	private static Bitmap generateBitmap(Context context, String time, String timefont, int timecolor,
										 String date, String datefont, int datecolor, float datefontscale) {
		return generateBitmap(context, true, time, timefont, timecolor, date, datefont, datecolor, datefontscale);
	}
	
	private static Bitmap generateBitmap(Context context, boolean withdate,
										 String time, String timefont, int timecolor,
										 String date, String datefont, int datecolor,
										 float fontscale) {
		//ah shit .settypeface doesnt exist in remoteviews wth do I do now? guess ill be rendering a bitmap
		//solution: https://stackoverflow.com/questions/4318572/how-to-use-a-custom-typeface-in-a-widget
		//but i added the date myself
		//int fontSizePX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, fontresolution, context.getResources().getDisplayMetrics());
		int fontSizePX = calculatefontsize(context, time, timefont);
		
		//get font scaling
		TypedValue outValue = new TypedValue();
		context.getResources().getValue(R.fraction.maxfontscale, outValue, true);
		float maxfontscale = outValue.getFloat();
		context.getResources().getValue(R.fraction.minfontscale, outValue, true);
		float minfontscale = outValue.getFloat();
		fontscale = maxfontscale + (1 - fontscale) * (minfontscale - maxfontscale);
		
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
		if (withdate) canvas.drawText(date, (float) (bitmap.getWidth() / 2) + pad, fontSizePX + (fontSizePX / fontscale), datepaint);
		return bitmap;
	}
	
	public static String calculatetime(long timestamp, int houroverhang, int minuteoverhang, int secondoverhang, boolean twelvehours, boolean withseconds) {
		/*inputs: long timestamp in millis
		 *        int overhang of hours
		 *        int overhang of minutes
		 *        int overhang of seconds
		 *        boolean wether clock is using 12 hour format
		 *        boolean wether seconds should be shown
		 */
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		int twelvehoursnumber = twelvehours ? 12 : 24;
		long h = twelvehours ? calendar.get(Calendar.HOUR) : calendar.get(Calendar.HOUR_OF_DAY);
		long m = calendar.get(Calendar.MINUTE);
		long s = withseconds ? calendar.get(Calendar.SECOND) : 0;
		if (withseconds) {
			if (secondoverhang % 60 >= s) s += 60;
			s += (secondoverhang / 60) * 60;
			m -= (s / 60) % 60;
		}
		if (minuteoverhang % 60 > m) m += 60;
		m += (minuteoverhang / 60) * 60;
		h -= (m / 60) % twelvehoursnumber;
		if (houroverhang % twelvehoursnumber > h) h += twelvehoursnumber;
		h += (houroverhang / twelvehoursnumber) * twelvehoursnumber;
		if (withseconds)
			return String.format(Locale.GERMANY, "%02d", h) + ":" + String.format(Locale.GERMANY, "%02d", m) + ":" + String.format(Locale.GERMANY, "%02d", s);
		return String.format(Locale.GERMANY, "%02d", h) + ":" + String.format(Locale.GERMANY, "%02d", m);
	}
 
	private static String[] combinedcalculate(long timestamp,
											  int monthoverhang, int dayoverhang,
											  int houroverhang, int minuteoverhang, int secondoverhang,
											  boolean withseconds, boolean twelvehours) {
		/*inputs: long timestamp in millis
		 *        int overhang of months
		 *        int overhanf of days
		 *        int overhang of hours
		 *        int overhang of minutes
		 *        int overhang of seconds
		 *        boolean wether seconds should be showns
		 *        boolean wether clock is using 12 hour format
		 */
		String[] returnstring = new String[2];
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp);
		long day = calendar.get(Calendar.DAY_OF_MONTH);
		long month = calendar.get(Calendar.MONTH) + 1;
		long year = calendar.get(Calendar.YEAR);
		long h = calendar.get(Calendar.HOUR_OF_DAY);
		long m = calendar.get(Calendar.MINUTE);
		long s = calendar.get(Calendar.SECOND);
		//FIXME: ANR when going up to integer limit, don't know if it even is fixable, would need to overhaul entire calcutlation
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
			if (month <= monthoverhang) {
				month += calendar.getMaximum(Calendar.MONTH) + 1;
				year -= 1;
				calendar.add(Calendar.YEAR, -1);
			}
		}
		if (twelvehours & (h >= 12+houroverhang & h <= 24)) h -= 12;
		returnstring[0] = String.format(Locale.GERMANY, "%02d", h) + ":" + String.format(Locale.GERMANY, "%02d", m);
		if (withseconds)
			returnstring[0] = String.format(Locale.GERMANY, "%02d", h) + ":" + String.format(Locale.GERMANY, "%02d", m) + ":" + String.format(Locale.GERMANY, "%02d", s);
		returnstring[1] = day + "." + month + "." + year;
		return returnstring;
	}
	
	private static int calculatefontsize(Context context, String text, String font) {
		/*calculates the maximum possible textsize which fits into a widget
		 * inputs: context
		 *         String input text, which should fit into widget
		 *         String font name used for the text
		 */
		//String TAG = "calculatefontsize";
		int cap = 5000; //max iterations cap to preventto crash rather then ANR
		Typeface typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
		font = font.replace(" ", "_");
		if (!context.getString(R.string.defaultfonttext).equals(font)) {
			try {
				typeface = ResourcesCompat.getFont(context, context.getResources().getIdentifier(font, "font", context.getPackageName()));
			} catch (Resources.NotFoundException ignored) {}	//expected if no font was specified
		}
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Point size = new Point();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			display.getRealSize(size);
		} else {
			display.getSize(size);
		}
		int screenwidth = size.x;
		int screenheight = size.y;
		int maxbytes = (int) (screenwidth * screenheight * 4 * 1.5);
		//Log.d(TAG, "generateBitmap: max bytes is " + maxbytes);
		//Log.d(TAG, "generateBitmap: width is " + screenwidth + ", height is " + screenheight);
		int fontsize = 0;
		int currentbytes = 0;
		Paint paint = new Paint();
		paint.setTextAlign(Paint.Align.LEFT);
		paint.setAntiAlias(true);
		paint.setTypeface(typeface);
		int count = 0;
		while (currentbytes < maxbytes) {
			fontsize++;
			count++;
			//i wasted so many days just to find out that my first formula works best :(
			if ((float)currentbytes/maxbytes<1) fontsize*=(1-(float)currentbytes/maxbytes)/2+1;
			paint.setTextSize(fontsize);
			int pad = (fontsize / 9);
			int textWidth = (int) (paint.measureText(text) + pad * 2);
			int height = (int) (fontsize / 0.7);
			currentbytes = (textWidth * height * 4);
			//Log.d(TAG, "calculatefontsize: itaration "+count+", fontsize "+fontsize+", size is "+currentbytes+", that is "+((float)currentbytes/maxbytes));
			if (count>cap) break;
		}
		fontsize--;
		paint.setTextSize(fontsize);
		{
			int pad = (fontsize / 9);
			int textWidth = (int) (paint.measureText(text) + pad * 2);
			int height = (int) (fontsize / 0.7);
			currentbytes = (textWidth * height * 4);
		}
		while (currentbytes > maxbytes) {
			fontsize--;
			count++;
			paint.setTextSize(fontsize);
			int pad = (fontsize / 9);
			int textWidth = (int) (paint.measureText(text) + pad * 2);
			int height = (int) (fontsize / 0.7);
			currentbytes = (textWidth * height * 4);
			//Log.d(TAG, "calculatefontsize: subtratcting itaration "+count+", fontsize "+fontsize+", size is "+currentbytes+", that is "+((float)currentbytes/maxbytes));
			if (count>cap*2) break;
		}
		//Log.d(TAG, "calculatefontsize: using "+currentbytes+" of "+maxbytes+", that is "+((float)currentbytes/maxbytes));
		//Log.d(TAG, "calculatefontsize: calculated font size is "+fontsize);
		return fontsize;
	}
}
