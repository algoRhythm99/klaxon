package com.github.android.klaxonreborn;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.github.android.klaxonreborn.pageparser.Go2Mobile;
import com.github.android.klaxonreborn.pageparser.LabeledFields;
import com.github.android.klaxonreborn.pageparser.Standard;

import java.util.ArrayList;
import java.util.List;


public class SmsPageReceiver extends BroadcastReceiver {
    public static String TAG = "SmsPageReceiver";
    private static String MY_TRANSPORT = "sms";

    public void queryAndLog(Context context, Uri u) {
        Log.d(TAG, "querying: " + u.toString());
        Cursor c = context.getContentResolver().query(u, null, null, null, null);
        c.moveToFirst();
        for (int i = 0; i < c.getColumnCount(); i++) {
            Log.d(TAG, c.getColumnName(i) + " : " + c.getString(i));
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //check to see if we want to intercept.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (!prefs.getBoolean("is_oncall", true)) {
            Log.d(TAG, "not oncall. not bothering with incoming sms.");
            return;
        }

        Bundle extras = intent.getExtras();
        String action = intent.getAction();
        if (Pager.REPLY_ACTION.equals(action)) {
            //replying to a received page.
            Uri data = intent.getData();
            if (null != extras) {
                String response = extras.getString("response");
                Integer new_ack_status = extras.getInt("new_ack_status");
                if (canReply(context, data)) {
                    replyTo(context, data, response, new_ack_status);
                    return;
                } else {
                    Log.d(TAG, "cannot reply to this message.");
                }
            }
        }

        Log.d(TAG, "fetching messages...");
        //SmsMessage[] msgs = {};
        List<SmsMessage> msgs = new ArrayList<>();
        try {
            /*
            //assemble messages from raw pdus.
            if (!extras.isEmpty()) {
                Object[] pduObjs = (Object[]) intent.getExtras().get("pdus");
                msgs = new SmsMessage[pduObjs.length];
                for (int i = 0; i < pduObjs.length; i++) {
                    //msgs[i] = SmsMessage.createFromPdu((byte[]) pduObjs[i]);
                    msgs.add( SmsMessage.createFromPdu((byte[]) pduObjs[i]) );
                }
            }
            */

            for (SmsMessage message : Telephony.Sms.Intents.
                    getMessagesFromIntent(intent)) {
                if (message == null) {
                    Log.e(TAG, "SMS message is null -- ABORT");
                    break;
                }
                msgs.add(message);
            }
        }
        //XXX: this probably shouldnt throw an NPE.
        catch (NullPointerException e) {
            Log.e(TAG, "No data associated with new sms intent!");
        }

        Alert incoming = null;
        String parser = prefs.getString("pageparser", "Standard");
        SmsMessage[] msgsArray = (SmsMessage[]) msgs.toArray();
        switch (parser) {
            case "Standard":
                Log.d(TAG, "using Standard pageparser");
                incoming = (new Standard()).parse(msgsArray);
                break;
            case "Go2Mobile":
                Log.d(TAG, "using go2mobile pageparser");
                incoming = (new Go2Mobile()).parse(msgsArray);
                break;
            case "Labeled Fields":
                Log.d(TAG, "using labeled pageparser");
                incoming = (new LabeledFields()).parse(msgsArray);
                break;
            default:
                Log.e(TAG, "unknown page parser:" + parser);
                break;
        }

        // note that this page was received via sms.
        if (null != incoming) {
            incoming.setTransport(MY_TRANSPORT);

            //Log some bits.
            Log.d(TAG, "from: " + incoming.getFrom());
            Log.d(TAG, "display from: " + incoming.getDisplayFrom());
            Log.d(TAG, "subject: " + incoming.getSubject());
            Log.d(TAG, "body: " + incoming.getBody());

            if (!isPage(incoming.asContentValues(), context)) {
                Log.d(TAG, "message doesn't appear to be a page. skipping");
                return;
            }

            Uri newpage = context.getContentResolver().insert(Pager.Pages.CONTENT_URI, incoming.asContentValues());
            Log.d(TAG, "new message inserted.");
            Intent annoy = new Intent(Pager.PAGE_RECEIVED);
            annoy.setData(newpage);
            context.sendBroadcast(annoy);
            Log.d(TAG, "sent intent " + annoy.toString());
            //NOTE: as of 1.6, this broadcast can be aborted.
            if (prefs.getBoolean("consume_sms_message", false)) {
                abortBroadcast();
                Log.d(TAG, "sms broadcast aborted.");
            }
        }

    }

    /**
     * Determine if we care about a particular SMS message.
     */
    boolean isPage(ContentValues cv, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String trigger_string = prefs.getString("sender_match", "").toLowerCase();
        String sender = cv.getAsString(Pager.Pages.SENDER);
        String fromAddr = cv.getAsString(Pager.Pages.FROM_ADDR);
        String messageBody = cv.getAsString(Pager.Pages.BODY);
        Log.d(TAG, "Trigger: " + trigger_string);
        Log.d(TAG, "sender: " + sender);
        Log.d(TAG, "from_addr: " + fromAddr);

        ContentResolver contentResolver = context.getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(sender));

        String[] projection = new String[]{PhoneLookup.DISPLAY_NAME, PhoneLookup._ID};

        String contactId = null;
        String contactName = null;
        // try to look up by phone number
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup._ID));
                contactName = cursor.getString(cursor.getColumnIndexOrThrow(PhoneLookup.DISPLAY_NAME));
            }
            cursor.close();
        }
        if (null == contactId) {
            // try to look up by email address
            projection = new String[]{ContactsContract.RawContacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Email.DATA};
            String order = String.format("CASE WHEN %s NOT LIKE '%@%' THEN 1 ELSE 2 END, %s, %s COLLATE NOCASE"
                    , ContactsContract.Contacts.DISPLAY_NAME
                    , ContactsContract.Contacts.DISPLAY_NAME
                    , ContactsContract.CommonDataKinds.Email.DATA
            );
            String filter = String.format("%s NOT LIKE ''", ContactsContract.CommonDataKinds.Email.DATA);
            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, filter, null, order);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    do {
                        String emailAddr = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DATA));
                        if (null == emailAddr) {
                            emailAddr = "";
                        }
                        if (emailAddr.contains(fromAddr)) {
                            contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.RawContacts._ID));
                            contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                            break;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }

        for (String trigger : trigger_string.split(",")) {

            if (null != contactName) {
                String lowerContactName = contactName.toLowerCase();
                if (lowerContactName.contains(trigger)) {
                    return true;
                }
            }
            if (sender.toLowerCase().contains(trigger)) {
                return true;
            }
            if (fromAddr.toLowerCase().contains(trigger)) {
                return true;
            }
            if (prefs.getBoolean("also_match_body", false)) {
                if (messageBody.toLowerCase().contains(trigger)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * check if we can reply to this page.
     */
    boolean canReply(Context context, Uri data) {
        Log.d(TAG, "attempting to reply to: " + data);
        Cursor cursor = context.getContentResolver().query(data,
                new String[]{Pager.Pages.TRANSPORT, Pager.Pages._ID},
                null,
                null,
                null);
        cursor.moveToFirst();

        String transport = cursor.getString(cursor.getColumnIndex(Pager.Pages.TRANSPORT));
        if (transport.equals(MY_TRANSPORT)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * replyTo: Uri, string, int
     * replies to a particular message, specified by Uri.
     */
    void replyTo(Context context, Uri data, String reply, int ack_status) {
        Log.d(TAG, "replying from smspagereceiver!");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SmsManager sm = SmsManager.getDefault();

        Cursor cursor = context.getContentResolver().query(data,
                new String[]{Pager.Pages.SENDER, Pager.Pages.SERVICE_CENTER, Pager.Pages._ID, Pager.Pages.FROM_ADDR, Pager.Pages.SUBJECT},
                null,
                null,
                null);
        cursor.moveToFirst();

        String sc = null;
        if (prefs.getBoolean("use_received_service_center", false)) {
            sc = cursor.getString(cursor.getColumnIndex(Pager.Pages.SERVICE_CENTER));
        }
        if (prefs.getBoolean("include_subject", false)) {
            reply = "(" + cursor.getString(cursor.getColumnIndex(Pager.Pages.SUBJECT)) + ") " + reply;
        }
        if (prefs.getBoolean("include_dest_address", false)) {
            //send the destination address, and subject, with the reply.
            String email_addr = cursor.getString(cursor.getColumnIndex(Pager.Pages.FROM_ADDR));
            reply = email_addr + " " + reply;
            Log.d(TAG, "reply text: " + reply);
        }
        String dest = cursor.getString(cursor.getColumnIndex(Pager.Pages.SENDER));
        Intent successIntent = new Intent("com.github.android.klaxon.REPLY_SENT", data);
        Log.d(TAG, "new ack status should be: " + ack_status);
        successIntent.putExtra(Pager.EXTRA_NEW_ACK_STATUS, ack_status); //note what our new status should be set to.
        sm.sendTextMessage(dest, sc, reply,
                PendingIntent.getBroadcast(context, 0, successIntent, PendingIntent.FLAG_UPDATE_CURRENT),
                null
        );
        Log.d(TAG, "Message sent.");
    }

}


