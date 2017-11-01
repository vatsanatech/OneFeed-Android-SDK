package com.sdk.wittyfeed.debug;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by aishwarydhare on 26/10/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {


    private static final String TAG = "SDK_FCM";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

}
