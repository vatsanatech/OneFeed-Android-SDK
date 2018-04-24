package com.wittyfeed.sdk.demo;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wittyfeed.sdk.onefeed.OFNotificationManager;


/**
 * Created by aishwarydhare on 24/10/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String FCM_TAG = "FCM_CUSTOM";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(FCM_TAG, "From: " + remoteMessage.getFrom());
        // Check if message contains a data payload.
        if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().size() > 0) {
                Log.d(FCM_TAG, "Message data payload: " + remoteMessage.getData());
            }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(FCM_TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }


        /*
         * Set Intent of the Activity you want to open on Back press from Story opens from Notification
         */
        OFNotificationManager.getInstance().setHomeScreenIntent(new Intent(getApplicationContext(), MainActivity.class));

        /*
         * NOTE: optionally you can check that notification has arrived from WittyFeed by below line -
         *       if(remoteMessage.getData().get("notiff_agent").equals("wittyfeed_sdk")
         */
        int your_preferred_icon_for_notifications = R.mipmap.ic_launcher; // <YOUR_PREFERRED_ICON_FOR_NOTIFICATION>
        OFNotificationManager.getInstance().handleNotification(
                getApplicationContext(),
                FirebaseInstanceId.getInstance().getToken(),
                remoteMessage.getData(),
                your_preferred_icon_for_notifications
        );

    }

    @Override
    public void onDeletedMessages() {
        Log.d("FCM_CUSTOM", "messages deleted");
        super.onDeletedMessages();
    }


}
