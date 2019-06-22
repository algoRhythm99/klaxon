package com.github.android.klaxonreborn;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class EscAdapter extends ResourceCursorAdapter {
    private String TAG = "EscAdapter";

    public EscAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c);
    }

    public void bindView(View view, Context context, Cursor cursor) {
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView subject = (TextView) view.findViewById(R.id.subject);
        //subject.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        subject.setText(cursor.getString(cursor.getColumnIndex(Pager.Pages.SUBJECT)));
        int status = cursor.getInt(cursor.getColumnIndex(Pager.Pages.ACK_STATUS));
        icon.setImageResource(Pager.getStatusResId(status));
    }


}

