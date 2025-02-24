package com.github.android.klaxonreborn.tests.pageparser;

import com.github.android.klaxonreborn.Alert;
import com.github.android.klaxonreborn.pageparser.LabeledFields;

import junit.framework.TestCase;

public class LabeledFieldTest extends TestCase {

    public void testMessage() {
        String message = "Msg:alert body";
        Alert expected = new Alert();
        expected.setFrom("1234");
        expected.setDisplayFrom("test@example.com");
        expected.setSubject("subject");
        expected.setBody("alert body");
        Alert observed = (new LabeledFields()).parse("1234", "subject", message);
        assertEquals(expected.getBody(), observed.getBody());
    }

    public void testSubject() {
        String message = "Subj:subject\nMsg:alert body";
        Alert expected = new Alert();
        expected.setFrom("1234");
        expected.setDisplayFrom("test@example.com");
        expected.setSubject("subject");
        expected.setBody("alert body");
        Alert observed = (new LabeledFields()).parse("1234", "", message);
        assertEquals(expected.getBody(), observed.getBody());
        assertEquals(expected.getSubject(), observed.getSubject());
    }

    public void testFrom() {
        String message = "Frm:test@example.com\nSubj:subject\nMsg:alert body";
        Alert expected = new Alert();
        expected.setFrom("1234");
        expected.setDisplayFrom("test@example.com");
        expected.setSubject("subject");
        expected.setBody("alert body");
        Alert observed = (new LabeledFields()).parse("1234", "", message);
        assertEquals(expected.getDisplayFrom(), observed.getDisplayFrom());
        assertEquals(expected.getBody(), observed.getBody());
        assertEquals(expected.getSubject(), observed.getSubject());
    }

}
