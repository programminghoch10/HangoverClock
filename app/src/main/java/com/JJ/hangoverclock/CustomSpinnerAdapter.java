package com.JJ.hangoverclock;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<RowItem> {
	
	private int spinner = R.id.spinnerview;
	private String TAG = "CustomSpinnerAdapter";
	private LayoutInflater flater;
	
	CustomSpinnerAdapter(Activity activity, int resouceId, int textviewId, List<RowItem> list) {
		
		super(activity, resouceId, textviewId, list);
		flater = activity.getLayoutInflater();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RowItem rowItem = getItem(position);
		
		View rowview = flater.inflate(R.layout.listitems_layout, null, true);
		
		TextView txtTitle = rowview.findViewById(spinner);
		txtTitle.setText(rowItem.getTitle());
		txtTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
		if (position == 0) return rowview;
		try {
			//Log.d(TAG, "getView: tf string is " + rowItem.getTitleFont(position));
			//Log.d(TAG, "getView: identifier is " + getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName()));
			//Log.d(TAG, "getView: typefont is " + ResourcesCompat.getFont(getContext(), getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName())));
			txtTitle.setTypeface(ResourcesCompat.getFont(getContext(), getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName())));
		} catch (Exception e) {
			Log.e(TAG, "getView: error occured while determiting font", e);
		}
		return rowview;
	}
	
	@Override
	public View getDropDownView(int position, View view, ViewGroup parent) {
		if (view == null) {
			view = flater.inflate(R.layout.listitems_layout, parent, false);
		}
		RowItem rowItem = getItem(position);
		TextView txtTitle = view.findViewById(spinner);
		txtTitle.setText(rowItem.getTitle());
		txtTitle.setTypeface(rowItem.getTypeface());
		return view;
	}
}


class RowItem {
	
	private final static String TAG = "rowitem";
	private String title;
	private Typeface typeface;
	private int visibility;
	
	RowItem(Context context, String title, int position) {
		this.title = title;
		if (position == 0) {
			typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
			return;
		}
		try {
			//Log.d(TAG, "getView: tf string is " + rowItem.getTitleFont(position));
			//Log.d(TAG, "getView: identifier is " + getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName()));
			//Log.d(TAG, "getView: typefont is " + ResourcesCompat.getFont(getContext(), getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName())));
			typeface = ResourcesCompat.getFont(context, context.getResources().getIdentifier(getTitleFont(position), "font", context.getPackageName()));
		} catch (Exception e) {
			Log.e(TAG, "getView: error occured while determiting font " + title, e);
			//visibility = View.GONE;
			visibility = View.INVISIBLE;
		}
	}
	
	String getTitle() {
		return title;
	}
	
	String getTitleFont(int position) {
		return FontsProvider.getFonts().get(position).replace(" ", "_");
	}
	
	Typeface getTypeface() {
		return typeface;
	}
	
	int getVisibility() {
		return visibility;
	}
	
	@Override
	public String toString() {
		return title;
	}
}
