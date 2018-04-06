package com.wittyfeed.sdk.demo;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.wittyfeed.sdk.onefeed.WittyFeedSDKApiClient;
import com.wittyfeed.sdk.onefeed.WittyFeedSDKMain;

/**
 * Created by aishwarydhare on 24/10/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private String APP_ID = "108";
    private String API_KEY = "963a1cf4d081b0b0bdc6e9e13de66dd3";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_CUSTOM", "Refreshed token:- " + refreshedToken);

        /*
         * Compulsory for Using Notification Service by OneFeed
         *      To notify WittyFeedSDK about your updated fcm_token
        */
        WittyFeedSDKMain witty_sdk_main = new WittyFeedSDKMain(getApplicationContext(), new WittyFeedSDKApiClient(getApplicationContext(),APP_ID,API_KEY, refreshedToken));
        witty_sdk_main.update_fcm_token(refreshedToken);
    }

}
