package com.github.android.klaxonreborn.tests.app;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.github.android.klaxonreborn.PageViewer;
import com.github.android.klaxonreborn.Pager.Pages;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PageViewerTest {
    private Uri testpageUri = null;

    @Rule
    public ActivityTestRule<PageViewer> mActivityRule = new ActivityTestRule(PageViewer.class);

    @BeforeClass
    public void setUp() {
        Log.d("KlaxonTest", "setting up..");
        //insert a dummy page, so we have one to view..
        ContentValues cv = new ContentValues();
        cv.put(Pages.SERVICE_CENTER, "00001");
        cv.put(Pages.SENDER, "someone@example.com");
        cv.put(Pages.SUBJECT, "something urgent");
        cv.put(Pages.ACK_STATUS, 0);
        cv.put(Pages.FROM_ADDR, "someone@example.com");
        cv.put(Pages.BODY, "the body of a message that is very important!");
        cv.put(Pages.TRANSPORT, "nonexistant");
        Log.d("KlaxonTest", "trying to insert some data...");
        this.testpageUri = getInstrumentation().getTargetContext().getContentResolver().insert(Pages.CONTENT_URI, cv);
        Log.d("KlaxonTest", "inserted: " + this.testpageUri.toString());
        Intent i = new Intent(Intent.ACTION_VIEW, this.testpageUri);
        setActivityIntent(i);

    }

    @AfterClass
    public void tearDown() {
        Log.d("KlaxonTest", "tearing down.");
        if (this.testpageUri != null) {
            getActivity().getContentResolver().delete(this.testpageUri, null, null);
        }
    }

    @Test
    public void testLaunchWithViewAction() {
        Log.d("XXXX", "attempting to launch with: " + this.testpageUri.toString());
        getActivity();
        assertTrue(true);
    }

}
