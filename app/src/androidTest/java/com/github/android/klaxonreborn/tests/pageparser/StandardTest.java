package com.github.android.klaxonreborn.tests.pageparser;

import com.github.android.klaxonreborn.Alert;
import com.github.android.klaxonreborn.pageparser.Standard;

import junit.framework.TestCase;

public class StandardTest extends TestCase {

    public void testNoop(){
        assertTrue(true);
    }

    // standard page. the magic is handled inside of the Android Framework,
    // so this test is pretty straightforward.
    public void testSimple(){
        String message = "alert body";
        Alert expected = new Alert();
        expected.setFrom("1234");
        expected.setDisplayFrom("test@example.com");
        expected.setSubject("subject");
        expected.setBody("alert body");
        Alert observed = (new Standard()).parse("1234", "subject", message);
        assertEquals(expected.getFrom(), observed.getFrom());
        assertEquals(expected.getSubject(), observed.getSubject());
        assertEquals(expected.getBody(), observed.getBody());
    }

    public void testSubjectSnippet(){
        String message = "alert body with no alert subject. it has a large body so we only pull the beginning.";
        Alert expected = new Alert();
        expected.setFrom("1234");
        expected.setDisplayFrom("test@example.com");
        expected.setSubject(message.substring(0,40));
        Alert observed = (new Standard()).parse("1234", "", message);
        assertEquals(expected.getFrom(), observed.getFrom());
        assertEquals(expected.getSubject(), observed.getSubject());
    }

}
