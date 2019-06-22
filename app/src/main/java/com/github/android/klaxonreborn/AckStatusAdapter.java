package com.github.android.klaxonreborn;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/* class to represent the various ack statuses to be show in a spinner.*/
public class AckStatusAdapter extends ArrayAdapter<Integer> {

    private static int ICON_RESOURCE_ID = R.id.icon;


    public AckStatusAdapter(Context context, int textViewResourceId, List<Integer> items) {
        super(context, textViewResourceId, items);
    }

    public ImageView getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(getContext());
        }
        ((ImageView) convertView).setImageResource(Pager.getStatusResId(position));
        return (ImageView) convertView;
    }

    public ImageView getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ImageView(getContext());
        }
        ((ImageView) convertView).setImageResource(Pager.getStatusResId(position));
        return (ImageView) convertView;
    }

}
