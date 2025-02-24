package com.github.android.klaxonreborn;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.github.android.klaxonreborn.Pager.Replies;

import java.util.ArrayList;


public class ReplyEditor extends Activity {
    private String TAG = "ReplyEditor";

    private Uri mContentURI;
    private Cursor mCursor;
    private EditText mSubjectView;
    private EditText mBodyView;
    private ImageView mIconView;
    private CheckBox mCheckBox;
    private Spinner mAckStatusSpinner;

    private ArrayList<Integer> mAckStatusList = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.replyeditor);


        /* stash our statuses in the ack status list*/
        // TODO: Make this an enum?
        mAckStatusList.add(Integer.valueOf(0));
        mAckStatusList.add(Integer.valueOf(1));
        mAckStatusList.add(Integer.valueOf(2));

        mSubjectView = (EditText) findViewById(R.id.subject);
        //mSubjectView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        //mSubjectView.setTextSize((float)(mSubjectView.getTextSize() * 1.25));

        mBodyView = (EditText) findViewById(R.id.body);
        mIconView = (ImageView) findViewById(R.id.icon);
        mCheckBox = (CheckBox) findViewById(R.id.show_in_menu);
        mAckStatusSpinner = (Spinner) findViewById(R.id.ack_status_spinner);
        mAckStatusSpinner.setAdapter(new AckStatusAdapter(this, R.layout.ackstatusspinner, mAckStatusList));

        Intent i = getIntent();
        mContentURI = i.getData();

        /* add onclick listeners for cancel and delete. */
        Button button = (Button) findViewById(R.id.delete_button);
        if (i.getAction().equals(Intent.ACTION_EDIT)) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    doDelete();
                    finish();
                }
            });
        } else {
            button.setVisibility(View.GONE);
        } //dont show the button if not editing.

        button = (Button) findViewById(R.id.cancel_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        button = (Button) findViewById(R.id.save_button);
        if (i.getAction().equals(Intent.ACTION_EDIT)) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    doSave();
                    finish();
                }
            });
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    doInsert();
                    finish();
                }
            });
        }

        if (i.getAction().equals(Intent.ACTION_EDIT)) {
            Log.d(TAG, "displaying: " + mContentURI.toString());
            mCursor = managedQuery(mContentURI,
                    new String[]{Replies._ID, Replies.NAME, Replies.BODY, Replies.ACK_STATUS, Replies.SHOW_IN_MENU},
                    null, null, null);
            mCursor.moveToFirst();

            mSubjectView.setText(mCursor.getString(mCursor.getColumnIndex(Replies.NAME)));
            mBodyView.setText(mCursor.getString(mCursor.getColumnIndex(Replies.BODY)));
            if (mCursor.getShort(mCursor.getColumnIndex(Replies.SHOW_IN_MENU)) == 1) {
                mCheckBox.setChecked(true);
            } else {
                mCheckBox.setChecked(false);
            }

            int status = mCursor.getInt(mCursor.getColumnIndex(Replies.ACK_STATUS));
            mAckStatusSpinner.setSelection(status);
            mIconView.setImageResource(Pager.getStatusResId(status));

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        //TODO: add a "save" and "discard" menu.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    /* this function actually does the saving of various bits..*/
    private void doInsert() {
        ContentValues cv = new ContentValues();
        cv.put(Replies.NAME, mSubjectView.getText().toString());
        cv.put(Replies.BODY, mBodyView.getText().toString());
        cv.put(Replies.ACK_STATUS, mAckStatusSpinner.getSelectedItemPosition());
        if (mCheckBox.isChecked()) {
            cv.put(Replies.SHOW_IN_MENU, 1);
        } else {
            cv.put(Replies.SHOW_IN_MENU, 0);
        }
        this.getContentResolver().insert(Replies.CONTENT_URI, cv);
    }

    private void doSave() {
        ContentValues cv = new ContentValues();
        cv.put(Replies.NAME, mSubjectView.getText().toString());
        cv.put(Replies.BODY, mBodyView.getText().toString());
        cv.put(Replies.ACK_STATUS, mAckStatusSpinner.getSelectedItemPosition());
        if (mCheckBox.isChecked()) {
            cv.put(Replies.SHOW_IN_MENU, 1);
        } else {
            cv.put(Replies.SHOW_IN_MENU, 0);
        }
        this.getContentResolver().update(mContentURI, cv, null, null);
    }

    private void doDelete() {
        //FIXME: ensure that the content uri is a specific item.
        this.getContentResolver().delete(mContentURI, null, null);
    }

}

