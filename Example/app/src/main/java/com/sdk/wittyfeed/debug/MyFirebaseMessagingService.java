package com.sdk.wittyfeed.debug;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sdk.wittyfeed.wittynativesdk.utils.fcm.WittyFeedSDKNotificationManager;

/**
 * Created by aishwarydhare on 26/10/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "SDK_FCM";

    // should be initialised at the class level
    WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // This line is required to be just after the onMessageReceived block starts
        wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(getApplicationContext());

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        int your_preferred_icon_for_notifications = R.mipmap.ic_launcher;
        wittyFeedSDKNotificationManager.handleNotification(remoteMessage.getData(), your_preferred_icon_for_notifications);

    }


}
