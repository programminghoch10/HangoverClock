package com.JJ.hangoverclock;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.dreams.DreamService;
import android.text.format.DateFormat;
import android.view.View;
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
			int houroverhang = sharedPreferences.getInt("houroverhang", context.getResources().getInteger(R.integer.daydreamdefaulthouroverhang));
			int minuteoverhang = sharedPreferences.getInt("minuteoverhang", context.getResources().getInteger(R.integer.daydreamdefaultminuteoverhang));
			int secondoverhang = sharedPreferences.getInt("secondoverhang", context.getResources().getInteger(R.integer.daydreamdefaultsecondoverhang));
			int dayoverhang = sharedPreferences.getInt("dayoverhang", context.getResources().getInteger(R.integer.daydreamdefaultdayoverhang));
			int monthoverhang = sharedPreferences.getInt("monthoverhang", context.getResources().getInteger(R.integer.daydreamdefaultmonthoverhang));
			boolean twelvehour = sharedPreferences.getBoolean("twelvehours", !DateFormat.is24HourFormat(context));
			boolean enableseconds = sharedPreferences.getBoolean("enableseconds", context.getResources().getBoolean(R.bool.daydreamdefaultenableseconds));
			boolean enabledate = sharedPreferences.getBoolean("enabledate", context.getResources().getBoolean(R.bool.daydreamdefaultenabledate));
			String font = sharedPreferences.getString("font", context.getResources().getString(R.string.defaultfonttext));
			float fontscale = sharedPreferences.getFloat("fontscale", context.getResources().getInteger(R.integer.daydreamdefaultfontscale));
			int color = sharedPreferences.getInt("color", context.getResources().getColor(R.color.daydreamdefaultcolor));
			((ImageView) findViewById(R.id.daydreamimageview)).setImageBitmap(
					ClockGenerator.generateWidget(
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
		setInteractive(false);    // Exit dream upon user touch
		//setFullscreen(true);	// Hide status bar
		int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
			flags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
		getWindow().getDecorView().setSystemUiVisibility(flags | getWindow().getDecorView().getSystemUiVisibility()); //hides nav bar
		//TODO: prevent status bar to go to light mode
		setScreenBright(false);    // wether to set screen brightness to 100%
		setContentView(R.layout.daydream);    // Set the dream layout
		sharedPreferences = getSharedPreferences(DaydreamProvider.this.getResources().getString(R.string.daydreampreferencesfilename), MODE_PRIVATE);
	}
	
	@Override
	public void onDreamingStarted() {
		super.onDreamingStarted();
		valueAnimator = new ValueAnimator();
		valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
		valueAnimator.setRepeatMode(ValueAnimator.RESTART);
		valueAnimator.setIntValues(1, 2);
		duration = sharedPreferences.getBoolean("enableseconds", DaydreamProvider.this.getResources().getBoolean(R.bool.daydreamdefaultenableseconds)) ? 1000 : 60000;
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
