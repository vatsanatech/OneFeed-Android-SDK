package com.onefeed.sdk.sample.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.onefeed.sdk.sample.FeedActivity;
import com.onefeed.sdk.sample.R;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.notification.NotificationHelper;


/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 03-Oct-2018
 * Time: 16:00
 */
public class OnefeedFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData().get("notiff_agent").equalsIgnoreCase("wittyfeed_sdk")) {
            NotificationHelper.sendNotification(getApplicationContext(), FeedActivity.class, remoteMessage.getData(), R.mipmap.ic_launcher);
        } else {
            //Handle by user
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        //Update Token
        OneFeedSdk.getInstance().setToken(s);
        Log.e("Token", s);
    }
}
