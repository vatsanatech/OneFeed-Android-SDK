package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by aishwarydhare on 21/10/17.
 */

public class WittyFeedSDKApiClient {

    private String APP_ID = "";
    private String API_KEY = "";
    private String PACKAGE_NAME = "";
    private String TAG = "WF_SDK";
    private String FCM_TOKEN = "";
    private Context context;
    private String user_meta;
    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static final String SDK_Version = "1.0.3";
    private String device_id = "";

    public WittyFeedSDKApiClient(Activity para_activity, String app_id , String api_key, String fcm_token){
        this.context = para_activity;
        this.APP_ID = app_id;
        this.API_KEY = api_key;
        this.PACKAGE_NAME = para_activity.getPackageName().toLowerCase();
        this.user_meta = get_user_meta(new HashMap<String, String>());

        this.FCM_TOKEN = fcm_token;
    }

    public WittyFeedSDKApiClient(Context para_context, String app_id , String api_key, String fcm_token){
        this.context = para_context;
        this.APP_ID = app_id;
        this.API_KEY = api_key;
        this.PACKAGE_NAME = this.context.getPackageName().toLowerCase();
        this.user_meta = get_user_meta(new HashMap<String, String>());

        this.FCM_TOKEN = fcm_token;
    }

    private String get_user_meta(HashMap<String, String> para_user_mata){
        HashMap<String, String> user_meta = new HashMap<>();

        user_meta.put("device_type", "android");

        user_meta.put("onefeed_sdk_version", SDK_Version);

        // default country / locale of user's device in ISO3 format
        String locale_country_iso3 = context.getResources().getConfiguration().locale.getISO3Country();
        user_meta.put("client_locale", locale_country_iso3);

        //It’s a 64-bit number that should remain constant for the lifetime of a device
        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        user_meta.put("android_id", android_id);
        user_meta.put("device_id", android_id);
        device_id = android_id;

        Log.i(TAG, "ANDROID ID: "+android_id);


        //UUID.randomUUID() method generates an unique identifier for a specific installation.
        String uuid = id(context);
        user_meta.put("uuid", uuid);
        Log.i(TAG, "UU ID: "+uuid);


        String serial_number = "";

        if(Build.VERSION.SDK_INT <26){
            serial_number = Build.SERIAL;
        }
        user_meta.put("serial_number", serial_number);
        Log.i(TAG, "SERIAL ID: "+serial_number);


        // default language of user's device
        String locale_language = Locale.getDefault().getLanguage();
        user_meta.put("client_locale_language", locale_language);

        // user's device Android_ID
        try {
            @SuppressLint("HardwareIds")
            String client_uuid = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            user_meta.put("client_uuid", client_uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        try {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            float height = displayMetrics.heightPixels;
            float width = displayMetrics.widthPixels;
            user_meta.put("screen_height", para_user_mata.get("" + height));
            user_meta.put("screen_width", para_user_mata.get("" + width));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject user_meta_json = new JSONObject(user_meta);
        return user_meta_json.toString();
    }

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
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

    String getDevice_id() {
        return device_id;
    }
}
