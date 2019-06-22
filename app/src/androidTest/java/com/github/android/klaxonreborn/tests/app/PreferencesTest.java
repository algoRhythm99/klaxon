package com.github.android.klaxonreborn.tests.app;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.github.android.klaxonreborn.Preferences;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PreferencesTest {

    @Rule
    public ActivityTestRule<Preferences> mActivityRule = new ActivityTestRule(Preferences.class);

    @Test
    public void testLaunch(){
        mActivityRule.getActivity();
        Assert.assertTrue(true);
    }

}
