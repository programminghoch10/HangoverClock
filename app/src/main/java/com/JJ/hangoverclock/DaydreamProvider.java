package com.JJ.hangoverclock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.dreams.DreamService;
import android.text.format.DateFormat;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class DaydreamProvider extends DreamService {
	
	SharedPreferences sharedPreferences;
	ValueAnimator valueAnimator;
	private long lastupdate;
	private int duration;
	ValueAnimator.AnimatorUpdateListener updatelistener = new ValueAnimator.AnimatorUpdateListener() {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			Context context = DaydreamProvider.this;
			if (animation.getCurrentPlayTime() < lastupdate + duration) return;
			lastupdate = animation.getCurrentPlayTime();
			int houroverhang = sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeyhouroverhang), context.getResources().getInteger(R.integer.daydreamdefaulthouroverhang));
			int minuteoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeyminuteoverhang), context.getResources().getInteger(R.integer.daydreamdefaultminuteoverhang));
			int secondoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeysecondoverhang), context.getResources().getInteger(R.integer.daydreamdefaultsecondoverhang));
			int dayoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeydayoverhang), context.getResources().getInteger(R.integer.daydreamdefaultdayoverhang));
			int monthoverhang = sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeymonthoverhang), context.getResources().getInteger(R.integer.daydreamdefaultmonthoverhang));
			boolean twelvehour = sharedPreferences.getBoolean(context.getResources().getString(R.string.daydreamkeytwelvehour), !DateFormat.is24HourFormat(context));
			boolean enableseconds = sharedPreferences.getBoolean(context.getResources().getString(R.string.daydreamkeyenableseconds), context.getResources().getBoolean(R.bool.daydreamdefaultenableseconds));
			boolean enabledate = sharedPreferences.getBoolean(context.getResources().getString(R.string.daydreamkeyenabledate), context.getResources().getBoolean(R.bool.daydreamdefaultenabledate));
			String font = sharedPreferences.getString(context.getResources().getString(R.string.daydreamkeyfont), context.getResources().getString(R.string.defaultfonttext));
			float fontscale = sharedPreferences.getFloat(context.getResources().getString(R.string.daydreamkeyfontscale), context.getResources().getInteger(R.integer.daydreamdefaultdatefontscale));
			int color = sharedPreferences.getInt(context.getResources().getString(R.string.daydreamkeycolor), context.getResources().getColor(R.color.daydreamdefaultWidgetColor));
			((ImageView) findViewById(R.id.daydreamimageview)).setImageBitmap(
					WidgetGenerator.generateWidget(
							DaydreamProvider.this, Calendar.getInstance().getTimeInMillis(),
							secondoverhang, minuteoverhang, houroverhang, dayoverhang, monthoverhang,
							twelvehour, enableseconds, enabledate, font, color, fontscale
					)
			);
			//Log.d("DaydreamProvider", "onAnimationUpdate: updated bitmap, animation playtime is " + animation.getCurrentPlayTime());
		}
	};
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		// Exit dream upon user touch
		setInteractive(false);
		// Hide system UI
		setFullscreen(true);
		// Set screen brightness
		setScreenBright(false);
		// Set the dream layout
		setContentView(R.layout.daydream);
		sharedPreferences = getSharedPreferences(DaydreamProvider.this.getResources().getString(R.string.daydreampreferencesfilename), MODE_PRIVATE);
		
	}
	
	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		valueAnimator = new ValueAnimator();
		valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
		valueAnimator.setRepeatMode(ValueAnimator.RESTART);
		valueAnimator.setIntValues(1, 2);
		duration = sharedPreferences.getBoolean(DaydreamProvider.this.getResources().getString(R.string.daydreamkeyenableseconds), DaydreamProvider.this.getResources().getBoolean(R.bool.daydreamdefaultenableseconds)) ? 1000 : 60000;
		//valueAnimator.setDuration(duration);
		lastupdate = -duration;
		valueAnimator.addUpdateListener(updatelistener);
		valueAnimator.start();
	}
	
	@Override
	public void onDreamingStopped() {
		super.onDreamingStopped();
		valueAnimator.end();
	}
	
	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		valueAnimator.end();
		valueAnimator = null;
	}
	
}
