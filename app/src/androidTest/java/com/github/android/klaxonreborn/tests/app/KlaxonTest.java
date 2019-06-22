package com.github.android.klaxonreborn.tests.app;


import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.github.android.klaxonreborn.KlaxonList;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class KlaxonTest {

    @Rule
    public ActivityTestRule<KlaxonList> mActivityRule = new ActivityTestRule(KlaxonList.class);

    @Test
    public void testLaunch(){
        mActivityRule.launchActivity();
        Assert.assertTrue(true);
    }

}
