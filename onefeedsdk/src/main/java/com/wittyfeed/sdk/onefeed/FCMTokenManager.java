package com.wittyfeed.sdk.onefeed;

import com.wittyfeed.sdk.onefeed.Utils.OFLogger;

/**
 <p><span style="font-size: 13pt;"><strong>Manages the FCM token of the host app for OneFeed SDK</strong></span> and has following responsibilities - -</p>
     <ol>
     <li>refresh Token: to set the current active FCM Token, then push FCM Token to server if required</li>
     <li>get FCM Token: fetches the current active FCM Token</li>
     <li>set Saved Fcm Token: saves the FCM token which has been successfully sent to the OneFeed server</li>
     <li>get Old FCM Token: fetches the saved FCM Token as the old FCM token from the shared preference</li>
     <li>push FCM Token If Required: use Network service manager to hitUpdateFcmTokenAPI if the new token is not equal to the saved fcm token</li>
     </ol>
 */
public final class FCMTokenManager {

    FCMTokenManager(String fcm_token){
        currentFcmToken = fcm_token;
    }

    private String currentFcmToken;

    /**
     * @return fetches the saved token from cache which was the last fcm_token that had been successfully sent to OneFeed Server
     */
    synchronized String getOldFcmToken() {
        return OneFeedMain.getInstance().ofSharedPreference.getSavedFcmToken();
    }

    /**
     * @return the current FCM token of the host app's user
     */
    synchronized String getCurrentFcmToken(){
        return currentFcmToken;
    }

    /**
     * @param token the FCM token to be saved in cache which had been successfully sent to OneFeed Server
     */
    synchronized void setSavedFcmToken(String token){
        OneFeedMain.getInstance().ofSharedPreference.setSavedFcmToken(token);
    }

    /**
     * registers/pushes the new FCM token to OneFeed Server
     */
    void pushFcmTokenIfRequired() {

        if(currentFcmToken == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.FcmTokenIsNull);
            return;
        }
        if( !currentFcmToken.equals(getOldFcmToken())){
            OneFeedMain.getInstance().networkServiceManager.hitUpdateFcmTokenAPI();
        }
    }

    /**
     * Checks if the new FCM token is not equal to the previously saved,
     * if false then pushes the new FCM token to the OneFeed Server
     * @param newFcmToken the new FCM token
     */
    public synchronized void refreshToken(String newFcmToken){
        currentFcmToken = newFcmToken;
        pushFcmTokenIfRequired();
    }

    public synchronized void updateTokenForVersionChange(){
        OneFeedMain.getInstance().networkServiceManager.hitUpdateFcmTokenAPI();
    }
}
