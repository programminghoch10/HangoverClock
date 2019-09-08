package com.JJ.hangoverclock;

import android.app.Activity;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<RowItem> {
    
    int spinner = R.id.spinnerview;
    String TAG = "CustomSpinnerAdapter";
    LayoutInflater flater;
    
    CustomSpinnerAdapter(Activity context, int resouceId, int textviewId, List<RowItem> list) {
        
        super(context, resouceId, textviewId, list);
        flater = context.getLayoutInflater();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        RowItem rowItem = getItem(position);
        
        View rowview = flater.inflate(R.layout.listitems_layout, null, true);
        
        TextView txtTitle = (TextView) rowview.findViewById(spinner);
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
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = flater.inflate(R.layout.listitems_layout, parent, false);
        }
        RowItem rowItem = getItem(position);
        TextView txtTitle = (TextView) convertView.findViewById(spinner);
        txtTitle.setText(rowItem.getTitle());
        txtTitle.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        if (position == 0) return convertView;
        try {
            //Log.d(TAG, "getView: tf string is " + rowItem.getTitleFont(position));
            //Log.d(TAG, "getView: identifier is " + getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName()));
            //Log.d(TAG, "getView: typefont is " + ResourcesCompat.getFont(getContext(), getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName())));
            txtTitle.setTypeface(ResourcesCompat.getFont(getContext(), getContext().getResources().getIdentifier(rowItem.getTitleFont(position), "font", getContext().getPackageName())));
        } catch (Exception e) {
            Log.e(TAG, "getView: error occured while determiting font", e);
            txtTitle.setVisibility(View.GONE);
        }
        return convertView;
    }
}


class RowItem {
    
    private String Title;
    
    public RowItem(String Title) {
        this.Title = Title;
    }
    
    public String getTitle() {
        return Title;
    }
    
    public void setTitle(String Title) {
        
        this.Title = Title;
    }
    
    public String getTitleFont(int position) {
        return ClockWidgetProvider.fonts.get(position).replace(" ", "_");
    }
    
    @Override
    public String toString() {
        return Title;
    }
}
