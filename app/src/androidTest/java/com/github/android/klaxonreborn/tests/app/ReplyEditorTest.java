package com.github.android.klaxonreborn.tests.app;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.github.android.klaxonreborn.ReplyEditor;
import com.github.android.klaxonreborn.Pager.Replies;


@RunWith(AndroidJUnit4.class)
public class ReplyEditorTest {

    @Rule
    public ActivityTestRule<ReplyEditor> mActivityRule = new ActivityTestRule(ReplyEditor.class);

    @BeforeClass
    public void setUp(){
        Log.d("KlaxonTest", "setting up..");
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(Replies.CONTENT_URI + "/1"));
        setActivityIntent(i);

    }

    @AfterClass
    public void tearDown(){
        Log.d("KlaxonTest", "tearing down.");
    }

    @Test
    public void testLaunch(){
        mActivityRule.getActivity();
        Assert.assertTrue(true);
    }

}
