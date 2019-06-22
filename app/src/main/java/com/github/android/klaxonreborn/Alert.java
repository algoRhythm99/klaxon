package com.github.android.klaxonreborn;

import android.content.ContentValues;

/**
 * Class representing an Alert Object
 * abstraction used to get from a received message to an item in PagerProvider
 */
public class Alert {
    private ContentValues cv;

    public Alert() {
        cv = new ContentValues();
        if (!cv.containsKey(Pager.Pages.ACK_STATUS))
            cv.put(Pager.Pages.ACK_STATUS, 0); //default to no response.
    }

    //clone contentvalues, so we can start passing around Alerts instead.
    public Alert(ContentValues v) {
        cv = v;
    }

    // "raw" from address, for use in replying.
    public void setFrom(String from) {
        cv.put(Pager.Pages.SENDER, from);
    }

    public String getFrom() {
        return cv.getAsString(Pager.Pages.SENDER);
    }

    // "DisplayName" analog - phone number, or email addr.
    public void setDisplayFrom(String from) {
        cv.put(Pager.Pages.FROM_ADDR, from);
    }

    public String getDisplayFrom() {
        return cv.getAsString(Pager.Pages.FROM_ADDR);
    }

    // subject line of the alert
    public void setSubject(String from) {
        cv.put(Pager.Pages.SUBJECT, from);
    }

    public String getSubject() {
        return cv.getAsString(Pager.Pages.SUBJECT);
    }

    // body of the alert.
    public void setBody(String body) {
        cv.put(Pager.Pages.BODY, body);
    }

    public String getBody() {
        return cv.getAsString(Pager.Pages.BODY);
    }

    public void setTransport(String t) {
        cv.put(Pager.Pages.TRANSPORT, t);
    }

    // used for inserting this alert into our contentprovider
    public ContentValues asContentValues() {
        return cv;
    }
}
