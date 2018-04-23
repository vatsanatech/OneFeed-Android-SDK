package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

final class Constant {

    public static final String ONE_FEED_VERSION = "1.1.0";

    public static final String ANALYTICS_TRACKING_ID = "UA-40875502-17";

    public static final String DEVICE_TYPE = "android";

    public static final String CHROME_PACKAGE_NAME = "com.android.chrome";

    public static final String  POSTER_SOLO = "poster_solo",
                                POSTER_RV = "poster_rv",
                                VIDEO_SOLO = "video_solo",
                                VIDEO_RV = "video_rv",
                                STORY_LIST = "story_list",
                                COLLECTION_1_4 = "collection_1_4";

    public static final int PROGRESS_BAR = -1,
                            POSTER_SOLO_NUM = 1,
                            POSTER_RV_NUM = 2,
                            VIDEO_SOLO_NUM = 3,
                            VIDEO_RV_NUM = 4,
                            STORY_LIST_NUM = 5,
                            COLLECTION_1_4_NUM = 6,
                            VIDEO_SMALL_SOLO_NUM = 7,
                            STORY_LIST_ITEM_NUM = 8,
                            COLLECTION_ITEM_NUM = 9;

    public static final double  TextSizeRatioLarge = 1.0,
                                TextSizeRatioMedium = 0.7,
                                TextSizeRatioSmall = 0.6;

    public static final int STORY_LIST_ROOT_LL_ID = 0x3af04;
    public static int loaderThresholdInt = 85;
    public static boolean hasChromeCustomTabLoaded = false;


    @SuppressLint("HardwareIds")
    public static final String getAndroidId(Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @SuppressLint("HardwareIds")
    public static final Map<String, String> getDeviceIdMap(Context context){
        Map<String, String> androidIdMap = new HashMap<>();
        androidIdMap.put("android_id", getAndroidId(context));
        androidIdMap.put("device_id", getAndroidId(context));
        return androidIdMap;
    }

    public static final Map<String, String> getScreenHeightWidth(Context context){
        Map<String, String> screenHeightWidthMap = new HashMap<>();
        screenHeightWidthMap.put("screen_height", String.valueOf(getScreenHeight(context)));
        screenHeightWidthMap.put("screen_width", String.valueOf(getScreenWidth(context)));
        return screenHeightWidthMap;
    }

    public static final int getScreenHeight(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static final int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static final String getPackageName(Context context){
        return context.getPackageName().toLowerCase();
    }

    public static final String convertMapToString(Map<String, String> map){
        return new JSONObject(map).toString();
    }

    public static final boolean isConnected(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static final int sizeInDp(Context context, int arg){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = arg / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }

    public static final int getSearchBarBackgroundWidth(Context context) {
        return (int) (getScreenWidth(context)*0.68);
    }

    public static final int getSearchBarWidth(Context context) {
        return (int) (getScreenWidth(context)*0.68) - (int) (getScreenWidth(context)*0.1);
    }


}
