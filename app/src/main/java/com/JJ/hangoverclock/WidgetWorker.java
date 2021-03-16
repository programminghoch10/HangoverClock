package com.JJ.hangoverclock;

import android.app.PendingIntent;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.POWER_SERVICE;

public class WidgetWorker extends Worker {
	
	static final String DATA_KEY_SCHEDULE = "schedule";
	static final String DATA_KEY_SECONDS = "seconds";
	private static final String TAG = "WidgetWorker";
	private static final String SCHEDULE_WORK_UNIQUE_ID = "scheduleTask";
	private static final String SCHEDULE_WORK_TAG = "scheduleWorker";
	
	public WidgetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
		super(context, workerParams);
	}
	
	@NonNull
	@Override
	public Result doWork() {
		Log.d(TAG, "doWork: Worker running");
		Data data = getInputData();
		boolean scheduleWork = data.getBoolean(DATA_KEY_SCHEDULE, false);
		if (!scheduleWork) {
			int interval = data.getInt(DATA_KEY_SECONDS, 60);
			int count = (5 * 60) / interval;
			Log.d(TAG, "doWork: interval=" + interval + " count=" + count);
			for (int i = 0; i < count; i++) {
				try {
					//TODO: switch to dynamic sleep interval for more precise update times
					Thread.sleep(interval * 1000);
				} catch (InterruptedException ignored) {
					Log.d(TAG, "doWork: interrupted sleep");
				}
				if (!isScreenOn()) {
					//Log.d(TAG, "doWork: ignoring clock update due to disabled screen");
					continue;
				}
				try {
					//Log.d(TAG, "doWork: updating widget, " + i + "/" + count);
					WidgetProvider.createClockTickIntent(getApplicationContext()).send();
				} catch (PendingIntent.CanceledException e) {
					Log.i(TAG, "doWork: pending intent cancelled!");
				}
			}
			Log.d(TAG, "doWork: worker done");
			return Result.success();
		}
		
		Log.d(TAG, "doWork: scheduling updates");
		int interval = 5 * 60;
		int count = 3;
		int seconds = data.getInt(DATA_KEY_SECONDS, 60);
		WorkManager.getInstance(getApplicationContext()).cancelUniqueWork(SCHEDULE_WORK_UNIQUE_ID);
		for (int i = 0; i < count; i++) {
			OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(WidgetWorker.class)
					.setInputData(
							new Data.Builder()
									.putInt(DATA_KEY_SECONDS, seconds)
									.build()
					)
					.addTag(SCHEDULE_WORK_TAG)
					.build();
			WorkManager.getInstance(getApplicationContext()).enqueueUniqueWork(SCHEDULE_WORK_UNIQUE_ID, ExistingWorkPolicy.APPEND_OR_REPLACE, request);
			Log.d(TAG, "doWork: enqueued " + request.getId() + " with " + seconds + "s interval");
		}
		Log.d(TAG, "doWork: finished scheduling " + count + " sequential updates");
		
		return Result.success();
	}
	
	@Override
	public void onStopped() {
		super.onStopped();
		if (this.getTags().contains(SCHEDULE_WORK_TAG)) {
			Log.d(TAG, "onStopped: stopped update worker " + this.getId());
			return;
		}
		Log.d(TAG, "onStopped: stopping scheduled update workers");
		WorkManager workManager = WorkManager.getInstance(getApplicationContext());
		workManager.cancelUniqueWork(SCHEDULE_WORK_UNIQUE_ID);
	}
	
	private boolean isScreenOn() {
		Context context = getApplicationContext();
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
			for (Display display : dm.getDisplays()) {
				if (display.getState() == Display.STATE_ON) {
					return true;
				}
			}
			return false;
		} else {
			PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
			return powerManager.isScreenOn();
		}
	}
}
