package com.github.android.klaxonreborn;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private static final String[] PAGES_COLUMNS = new String[]{
            Pager.Pages._ID
          , Pager.Pages.SUBJECT
          , Pager.Pages.SENDER
          , Pager.Pages.SERVICE_CENTER
          , Pager.Pages.ACK_STATUS
    };

    //menu constants.
    private int MENU_ACTIONS_GROUP = Menu.FIRST;
    private int MENU_ALWAYS_GROUP = Menu.FIRST + 1;

    private int DIALOG_DELETE_ALL_CONFIRMATION = 1;

    private int REQUEST_PICK_REPLY = 1;

    private Cursor mCursor;
    private ListView pagesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (null == getPagesList()) {
            Log.d(TAG, "pagest list null!");
        }
        Log.d(TAG, "oncreate done.");
    }

    protected ListView getPagesList(boolean refresh)
    {
        if (null == pagesList)
        {
            pagesList = findViewById(R.id.pagesList);
            TextView emptyText = findViewById(android.R.id.empty);
            if (null != pagesList) {
                pagesList.setEmptyView(emptyText);
                registerForContextMenu(pagesList);
            }
            refresh = true;
        }
        if (refresh)
        {
            if (null != getPagesCursor(refresh)) {
                Log.d(TAG, "found rows:" + getPagesCursor().getCount());
                Log.d(TAG, "setting adapter");
                ListAdapter adapter = new EscAdapter(this,
                            R.layout.esclist_item,
                            getPagesCursor());
                if ((null != adapter) && (null != pagesList)) {
                    pagesList.setAdapter(adapter);
                }
            }
        }
        return pagesList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem mi = menu.add(MENU_ALWAYS_GROUP, Menu.NONE, Menu.NONE, R.string.prefs_activity);
        mi.setIcon(android.R.drawable.ic_menu_preferences);
        Intent i = new Intent(this, SettingsActivity.class);
        mi.setIntent(i);
        return true;
    }

    protected ListView getPagesList()
    {
        return getPagesList(false);
    }

    protected Cursor getPagesCursor(boolean refresh)
    {
        if (refresh || (null == mCursor))
        {
            Log.d(TAG, "querying");
            mCursor = Pager.Pages.query(this.getContentResolver(), PAGES_COLUMNS);
        }
        return mCursor;
    }

    protected Cursor getPagesCursor()
    {
        return getPagesCursor(false);
    }
}
