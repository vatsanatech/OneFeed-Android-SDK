package com.wittyfeed.sdk.demo;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.wittyfeed.sdk.onefeed.WittyFeedSDKNotificationManager;


/**
 * Created by aishwarydhare on 24/10/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String FCM_TAG = "FCM_CUSTOM";
    WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(getApplicationContext(), FirebaseInstanceId.getInstance().getToken());

        //Set Intent of the Activity you want to open on Back press from Story opens from Notification
        wittyFeedSDKNotificationManager.setHomeScreenIntent(new Intent(getApplicationContext(), MainActivity.class));

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
         * Compulsorily add the below snioppet of code so that the recieved notification
         *      from OneFeed can be generated and shown
         *
         * NOTE: optionally you can check that notification has arrived from WittyFeed by below line -
         *       if(remoteMessage.getData().get("notiff_agent").equals("wittyfeed_sdk")
        */
        wittyFeedSDKNotificationManager.handleNotification(remoteMessage.getData(), R.mipmap.ic_launcher);
    }

    @Override
    public void onDeletedMessages() {
        Log.d("FCM_CUSTOM", "messages deleted");
        super.onDeletedMessages();
    }


}
