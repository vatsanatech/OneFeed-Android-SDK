package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 *
 * Manages Data fetching and storing in SharedPreferences when required by OneFeed
 * Currently performs actions when -
 *
 *      1) FCM Token which is sent successfully to OneFeed served needs to be saved in
 *          shared preferences
 *      2) FCM Token is required which is saved in shared preferences
 *
 */
final class OFSharedPreference {

    private final String SAVED_FCM_TOKEN = "wf_saved_fcm_token";
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    OFSharedPreference(Context context){
        this.mPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mEditor = mPref.edit();
    }

    public synchronized String getSavedFcmToken(){
        return mPref.getString(SAVED_FCM_TOKEN, "");
    }

    public final void setSavedFcmToken(String newFcmToken){
        mEditor.putString(SAVED_FCM_TOKEN, newFcmToken).apply();
    }
}
