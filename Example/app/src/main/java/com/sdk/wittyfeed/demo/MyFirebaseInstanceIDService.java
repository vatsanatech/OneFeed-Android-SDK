package com.sdk.wittyfeed.demo;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

/**
 * Created by aishwarydhare on 24/10/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_CUSTOM", "Refreshed token:- " + refreshedToken);

        //
        // * Mandatory for Using Notification Service by OneFeed*
        // To notify WittyFeedSDK about your updated fcm_token
        //
        if(WittyFeedSDKSingleton.getInstance().witty_sdk != null){
            WittyFeedSDKSingleton.getInstance().witty_sdk.update_fcm_token(refreshedToken);
        }

    }

}
