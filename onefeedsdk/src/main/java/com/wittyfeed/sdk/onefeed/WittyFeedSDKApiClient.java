package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.provider.Settings;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by aishwarydhare on 21/10/17.
 */

public class WittyFeedSDKApiClient {

    private String APP_ID = "";
    private String API_KEY = "";
    private String PACKAGE_NAME = "";
    private String TAG = "WF_SDK";
    private String FCM_TOKEN = "";
    private Activity activity;
    private String user_meta;

    public WittyFeedSDKApiClient(Activity activity, String app_id , String api_key, String fcm_token){
        this.activity = activity;
        this.APP_ID = app_id;
        this.API_KEY = api_key;
        this.activity = activity;
        this.PACKAGE_NAME = activity.getPackageName().toLowerCase();
        this.user_meta = get_user_meta(new HashMap<String, String>());

        this.FCM_TOKEN = fcm_token;
    }

    public WittyFeedSDKApiClient(Activity activity, String app_id , String api_key, String fcm_token, HashMap<String, String> para_user_meta){
        this.activity = activity;
        this.APP_ID = app_id;
        this.API_KEY = api_key;
        this.activity = activity;
        this.FCM_TOKEN = fcm_token;
        this.PACKAGE_NAME = activity.getPackageName().toLowerCase();
        this.user_meta = get_user_meta(para_user_meta);
    }

    private String get_user_meta(HashMap<String, String> para_user_mata){
        HashMap<String, String> user_meta = new HashMap<>();

        // default country / locale of user's device in ISO3 format
        String locale_country_iso3 = activity.getResources().getConfiguration().locale.getISO3Country();
        user_meta.put("client_locale", locale_country_iso3);

        // default language of user's device
        String locale_language = Locale.getDefault().getLanguage();
        user_meta.put("client_locale_language", locale_language);

        // user's device Android_ID
        try {
            @SuppressLint("HardwareIds")
            String client_uuid = Settings.Secure.getString(activity.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            user_meta.put("client_uuid", client_uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //user's device Device_ID
//        try {
//            TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//            Log.d("ID", "Device ID : " + tm.getDeviceId());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if(para_user_mata.containsKey("client_gender")){
            if(para_user_mata.get("client_gender").equalsIgnoreCase("")){
                user_meta.put("client_gender", "N");
            } else {
                user_meta.put("client_gender", para_user_mata.get("client_gender"));
            }
        }

        if(para_user_mata.containsKey("client_interests")){
            if(para_user_mata.get("client_interests").equalsIgnoreCase("")){
                user_meta.put("client_interests", "");
            } else {
                user_meta.put("client_interests", para_user_mata.get("client_interests"));
            }
        }

        JSONObject user_meta_json = new JSONObject(user_meta);
        return user_meta_json.toString();
    }

    String getAPP_ID() {
        return APP_ID;
    }

    String getAPI_KEY() {
        return API_KEY;
    }

    String getPACKAGE_NAME() {
        return PACKAGE_NAME;
    }

    String getFCM_TOKEN() {
        return FCM_TOKEN;
    }

    String getUser_meta() {
        return user_meta;
    }

    void setFCM_TOKEN(String FCM_TOKEN) {
        this.FCM_TOKEN = FCM_TOKEN;
    }
}
