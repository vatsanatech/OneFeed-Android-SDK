package com.wittyfeed.sdk.onefeed.Utils;

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
public final class OFSharedPreference {

    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    public OFSharedPreference(Context context){
        this.mPref = PreferenceManager.getDefaultSharedPreferences(context);
        this.mEditor = mPref.edit();
    }

    /**
     *Returns the FCM token saved locally in default Shared Preferences
     *
     */
    public synchronized String getSavedFcmToken(){
        return mPref.getString(Constant.SAVED_FCM_TOKEN, "");
    }


    /**
     *Saves the FCM token locally into default Shared Preferences
     *
     */
    public final void setSavedFcmToken(String newFcmToken){
        mEditor.putString(Constant.SAVED_FCM_TOKEN, newFcmToken).apply();
    }

    public void setSavedHomeScreenIntent(String classPassedInIntent) {
        mEditor.putString(Constant.SAVED_HOME_SCREEN_CLASS, classPassedInIntent).commit();
    }

    public synchronized String getSavedHomeScreenIntent(){
        return mPref.getString(Constant.SAVED_HOME_SCREEN_CLASS, "");
    }

    public void setSDKVersion(String version){
        mEditor.putString(Constant.SAVED_ONE_FEED_VERSION, Constant.ONE_FEED_VERSION).commit();
    }

    public String getSDKVersion(){
        return mPref.getString(Constant.SAVED_ONE_FEED_VERSION,"");
    }
}
