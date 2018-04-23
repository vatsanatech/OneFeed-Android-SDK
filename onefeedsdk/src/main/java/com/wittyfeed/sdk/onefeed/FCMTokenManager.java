package com.wittyfeed.sdk.onefeed;

/**
 *
 * Manages the FCM token of the host app for OneFeed SDK and
 * performs necessary operations on the following events -
 *      1) refresh Token: to set the current active FCM Token,
 *          then push FCM Token to server if required
 *      2) get FCM Token: fetches the current active FCM Token
 *      3) set Saved Fcm Token: saves the FCM token which has been successfully
 *          sent to the OneFeed server
 *      4) get Old FCM Token: fetches the saved FCM Token as the old FCM token
 *          from the shared preference
 *      5) push FCM Token If Required: use Network service manager to hitUpdateFcmTokenAPI if
 *          the new token is not equal to the saved fcm token
 *
 *
 */
public final class FCMTokenManager {

    FCMTokenManager(String fcm_token){
        currentFcmToken = fcm_token;
    }

    private String currentFcmToken;

    synchronized String getOldFcmToken() {
        return OneFeedMain.getInstance().ofSharedPreference.getSavedFcmToken();
    }

    synchronized String getCurrentFcmToken(){
        return currentFcmToken;
    }

    synchronized void setSavedFcmToken(String token){
        OneFeedMain.getInstance().ofSharedPreference.setSavedFcmToken(token);
    }

    void pushFcmTokenIfRequired() {
        if(currentFcmToken == null){
            OFLogger.log(OFLogger.ERROR, "FCM Token is null");
            return;
        }
        if( !currentFcmToken.equals(getOldFcmToken()) ){
            OneFeedMain.getInstance().networkServiceManager.hitUpdateFcmTokenAPI();
        }
    }

    public synchronized void refreshToken(String newFcmToken){
        currentFcmToken = newFcmToken;
        pushFcmTokenIfRequired();
    }
}
