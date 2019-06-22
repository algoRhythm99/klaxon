package com.github.android.klaxonreborn;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Pager {
    public static final int[] statusIcons = {
            android.R.drawable.presence_offline,
            android.R.drawable.presence_online,
            android.R.drawable.presence_busy
    };

    public static final String PREFS_FILE = "klaxon_prefs";

    public static final String REPLY_ACTION = "com.github.android.klaxon.REPLY";
    public static final String ACK_ACTION = "com.github.android.klaxon.ACK";
    public static final String NACK_ACTION = "com.github.android.klaxon.NACK";
    public static final String PAGE_RECEIVED = "com.github.android.klaxon.PAGE_RECEIVED";
    public static final String ANNOY_ACTION = "com.github.android.klaxon.ANNOY";
    public static final String SILENCE_ACTION = "com.github.android.klaxon.PAGES_VIEWED";

    public static final String EXTRA_NEW_ACK_STATUS = "com.github.android.klaxon.NEW_ACK_STATUS";

    public static final int STATUS_NONE = 0;
    public static final int STATUS_ACK = 1;
    public static final int STATUS_NACK = 2;

    /**
     * get the icon to display for the given ack_status value.
     */
    public static int getStatusResId(int status) {
        return statusIcons[status];
    }

    /**
     * Pages database
     */
    public static final class Pages implements BaseColumns {
        public static final String TABLE_NAME = "pages";

        public static final Cursor query(ContentResolver cr, String[] projection) {
            return cr.query(CONTENT_URI,
                    projection,
                    null,
                    null,
                    DEFAULT_SORT_ORDER);
        }

        public static final Cursor query(ContentResolver cr, String[] projection,
                                         String where, String orderBy) {
            return cr.query(CONTENT_URI, projection, where, null,
                    orderBy == null ? DEFAULT_SORT_ORDER : orderBy);
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =
                Uri.parse("content://com.github.android.klaxon/pages");

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "created DESC";

        /**
         * The subject of the page
         * <P>Type: TEXT</P>
         */
        public static final String SUBJECT = "subject";

        /**
         * The text of the page
         * <P>Type: TEXT</P>
         */
        public static final String BODY = "body";

        /**
         * The timestamp for when the page was created
         * <P>Type: INTEGER (long)</P>
         */
        public static final String CREATED_DATE = "created";

        /**
         * The service center address from which the page was received.
         * <P>Type: TEXT</P>
         */
        public static final String SERVICE_CENTER = "sc_addr";

        /**
         * The originating address
         * <P>Type: TEXT</P>
         */
        public static final String SENDER = "sender_addr";

        /**
         * Acknowledgement status
         * <P>Type: INTEGER</P>
         */
        public static final String ACK_STATUS = "ack_status";

        /**
         * Email From address
         * <P>Type: TEXT</P>
         */
        public static final String FROM_ADDR = "email_from_addr";

        /**
         * String to identify the transport over which this page was received.
         * <P>Type: TEXT</P>
         */
        public static final String TRANSPORT = "transport";
    }

    /**
     * Replies table.
     * stores our snappy comebacks.
     */
    public static final class Replies implements BaseColumns {
        public static final String TABLE_NAME = "replies";

        public static final Cursor query(ContentResolver cr, String[] projection) {
            return cr.query(CONTENT_URI,
                    projection,
                    null,
                    null,
                    DEFAULT_SORT_ORDER);
        }

        public static final Cursor query(ContentResolver cr, String[] projection,
                                         String where, String orderBy) {
            return cr.query(CONTENT_URI, projection, where, null,
                    orderBy == null ? DEFAULT_SORT_ORDER : orderBy);
        }

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =
                Uri.parse("content://com.github.android.klaxon/reply");

        /**
         * Short name of our reply.
         */
        public static final String NAME = "name";

        /**
         * content of the reply.
         */
        public static final String BODY = "body";
        /**
         * new "ack status". integer.
         */
        public static final String ACK_STATUS = "ack_status";
        /**
         * whether this item should be shown in the menu.
         */
        public static final String SHOW_IN_MENU = "show_in_menu";

        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "name ASC";
    }
}

