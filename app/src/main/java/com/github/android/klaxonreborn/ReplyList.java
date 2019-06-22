package com.github.android.klaxonreborn;


import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import org.nerdcircus.android.klaxonreborn.Pager.Replies;


public class ReplyList extends ListActivity
{
    private String TAG = "ReplyList";

    //menu constants.
    private int MENU_ACTIONS_GROUP = Menu.FIRST;
    private int MENU_ALWAYS_GROUP = Menu.FIRST + 1;
    private int MENU_ADD = Menu.FIRST + 2;

    private Cursor mCursor;

    @Override
    public void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);

        setContentView(R.layout.replylist);
        
        String[] cols = new String[] {Pager.Replies._ID, Pager.Replies.NAME, Pager.Replies.BODY, Pager.Replies.ACK_STATUS };
        mCursor = Pager.Replies.query(this.getContentResolver(), cols);
        startManagingCursor(mCursor);
        ListAdapter adapter = new ReplyAdapter(this, 
                                               R.layout.replylist_item,
                                               mCursor);
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView parent, View v, int position, long id){
        Log.d(TAG, "Item clicked!");
        Uri uri = Uri.withAppendedPath(Pager.Replies.CONTENT_URI, ""+id);
        Log.d(TAG, "intent that started us: " + this.getIntent().getAction());
        if( this.getIntent().getAction().equals(Intent.ACTION_PICK) ){
            //we're picking responses, not editing them.
            Log.d(TAG, "pick action. returning result.");
            //Note: this reuses the sent Intent, so we dont lose the 'page_uri' data, if included.
            setResult(RESULT_OK, new Intent(this.getIntent()).setData(uri));
            finish();
        }
        else {
            Log.d(TAG, "not picking, edit.");
            Intent i = new Intent(Intent.ACTION_EDIT, uri);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem mi;
        mi = menu.add(MENU_ACTIONS_GROUP, MENU_ADD, Menu.NONE, R.string.add_reply);
        Intent i = new Intent(Intent.ACTION_INSERT,
                              Pager.Replies.CONTENT_URI);
        mi.setIntent(i);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        Log.d(TAG, "preparing options menu");
        super.onPrepareOptionsMenu(menu);
        final boolean haveItems = mCursor.getCount() > 0;
        menu.setGroupVisible(MENU_ACTIONS_GROUP, haveItems);
        menu.setGroupVisible(MENU_ALWAYS_GROUP, true);
        return true;
    }

}

