package com.github.android.klaxonreborn;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class ReplyAdapter extends ResourceCursorAdapter
{
    @SuppressWarnings("unused")
    private String TAG = "ReplyAdapter";

    public ReplyAdapter(Context context, int layout, Cursor c){
        super(context, layout, c);
    }

    public void bindView(View view, Context context, Cursor cursor){
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        TextView subject = (TextView) view.findViewById(R.id.subject);
        TextView body = (TextView) view.findViewById(R.id.body);
        subject.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));

        subject.setText(cursor.getString(cursor.getColumnIndex(Pager.Replies.NAME)));
        body.setText(cursor.getString(cursor.getColumnIndex(Pager.Replies.BODY)));
        int status = cursor.getInt(cursor.getColumnIndex(Pager.Replies.ACK_STATUS));
        icon.setImageResource(Pager.getStatusResId(status));
    }

    

}

