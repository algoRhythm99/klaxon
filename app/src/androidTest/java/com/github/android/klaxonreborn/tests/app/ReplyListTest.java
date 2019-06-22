package com.github.android.klaxonreborn.tests.app;


import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.github.android.klaxonreborn.ReplyList;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class ReplyListTest {

    @Rule
    public ActivityTestRule<ReplyList> mActivityRule = new ActivityTestRule(ReplyList.class);

    @Test
    public void testLaunch(){
        mActivityRule.launchActivity();
        Assert.assertTrue(true);
    }

}
