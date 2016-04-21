/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sym.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.R;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;

import jGit.sym.src.GeneralUtil;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    public static final String GCM_JSON_KEY_GREETING = "Greeting";
    public static final String GCM_JSON_KEY_OPERATION = "Operation";
    public static final String GCM_JSON_KEY_MESSAGE = "Message";
    public static final String GCM_JSON_KEY_SENTBY = "Sentby";
    public static final String GCM_JSON_DATA = "data";
    public static final String GCM_JSON_TO = "to";
    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    //[START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d(TAG,data.toString());
        String greeting = data.getString(GCM_JSON_KEY_GREETING);
        int operation = Integer.parseInt(data.getString(GCM_JSON_KEY_OPERATION));
        String message = data.getString(GCM_JSON_KEY_MESSAGE);
        String sentBy = data.getString(GCM_JSON_KEY_SENTBY);
        Log.d(TAG, GCM_JSON_KEY_SENTBY    + " " + sentBy);
        Log.d(TAG, GCM_JSON_KEY_MESSAGE   + " " + message);
        Log.d(TAG, GCM_JSON_KEY_OPERATION + " " + operation);
        Log.d(TAG, GCM_JSON_KEY_GREETING  + " " + greeting);

        if (from.startsWith("/topics/")) {
            System.out.println("Message Recieved in topic: "+message);
            // message received from some topic.
        } else {
            System.out.println("Share Your Moments: "+message);
            // normal downstream message.
        }
        MetaData mData = null;
        try {
            mData = new MetaData(data.getString(DatabaseHandler.KEY_GROUPNAME));
            mData.setUserList(new ArrayList<String>(Arrays.asList(data.getString(DatabaseHandler.KEY_USERLIST))));
            mData.setGroupDetails(new ArrayList<String>(Arrays.asList((data.getString(DatabaseHandler.KEY_FILENAME)))),
                    new ArrayList<String>(Arrays.asList((data.getString(DatabaseHandler.KEY_SIZE)))));
            mData.setCreatedDate(data.getString(DatabaseHandler.KEY_CREATEDDATE));
            DatabaseHandler db = new DatabaseHandler(this);
            Log.d("GCM Metadata","Metadata "+DatabaseHandler.JSONStringFromMetaData(mData));

            switch (operation){

                case DatabaseHandler.DB_OP_DELETE:
                    db.deleteMetaData(mData);
                    break;
                case DatabaseHandler.DB_OP_ADD:
                    db.addMetaData(mData);
                case DatabaseHandler.DB_OP_SYNC:
                    GeneralUtil.pullPhotos(mData);
                    break;
                case DatabaseHandler.DB_OP_UPDATE:
                    db.updateMetaData(mData);
                    GeneralUtil.pullPhotos(mData);
            }
            if(operation== DatabaseHandler.DB_OP_ADD||operation== DatabaseHandler.DB_OP_SYNC||operation== DatabaseHandler.DB_OP_UPDATE) {
               // GeneralUtil.pullPhotos(mData);

              db.addMetaData(mData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"Fatal error not synced.");
        }

        //[START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        sendNotification(greeting);
        //[END_EXCLUDE]
    }
    //[END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification_overlay)
                .setContentTitle("Share Your Moments")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }


}
