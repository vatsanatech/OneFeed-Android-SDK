package com.wittyfeed.sdk.onefeed.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * A Utility class that helps handle miscellaneous functions across the app
 * <p>
 * 1. Contains a function that returns the Android ID of the device.
 * 2. Function that returns height of the screen
 * 3. Function that returns width of the screen.
 * 4. Method that returns package name of the app.
 * 5. Method that returns internet connectivity status of the app.
 * 6. Method that converts Pixel length to DP.
 * 7. Method that returns search bar background width.
 * 8. Method that returns search bar width.
 */

public final class Utils {

    /**
     * Returns Android ID of the device
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @SuppressLint("HardwareIds")
    public static Map<String, String> getDeviceIdMap(Context context) {
        Map<String, String> androidIdMap = new HashMap<>();
        androidIdMap.put("android_id", getAndroidId(context));
        androidIdMap.put("device_id", getAndroidId(context));
        return androidIdMap;
    }

    /**
     * Returns a Screen height and width Map
     */
    public static Map<String, String> getScreenHeightWidth(Context context) {
        Map<String, String> screenHeightWidthMap = new HashMap<>();
        screenHeightWidthMap.put("screen_height", String.valueOf(getScreenHeight(context)));
        screenHeightWidthMap.put("screen_width", String.valueOf(getScreenWidth(context)));
        return screenHeightWidthMap;
    }

    /**
     * Returns screen height in pixels
     */
    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * Returns screen width in pixels
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * Returns package name of the app.
     */
    public static String getPackageName(Context context) {
        return context.getPackageName().toLowerCase();
    }

    /**
     * Converts a String Map to one String and returns it
     */
    public static String convertMapToString(Map<String, String> map) {
        return new JSONObject(map).toString();
    }

    /**
     * Returns internet connectivity status of the app.
     */
    public static boolean isConnected(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE);
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

    /**
     * Converts Pixel length to DP
     */
    public static int sizeInDp(Context context, int arg) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = arg / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) dp;
    }

    /**
     * Returns internet connectivity status of the app.
     */
    public static int getSearchBarBackgroundWidth(Context context) {
        return (int) (getScreenWidth(context) * 0.68);
    }

    /**
     * Returns internet connectivity status of the app.
     */
    public static int getSearchBarWidth(Context context) {
        return (int) (getScreenWidth(context) * 0.68) - (int) (getScreenWidth(context) * 0.1);
    }

    //Created by yogesh
    public static String getNetworkConnectionType(Context context) {

        ConnectivityManager connectivityManager;
        NetworkInfo networkInfo;
        NetworkInfo networkInfo2;

        connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        networkInfo2 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

        String networkType = "Unknown";
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                networkType = "2G";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                networkType = "3G";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                networkType = "4G";
                break;
        }

        if(networkInfo.isConnected()){
            return networkType;
        }else if(networkInfo2.isConnected()){
            return "WIFI";
        }else {
            return "Unknown";
        }
    }

}
