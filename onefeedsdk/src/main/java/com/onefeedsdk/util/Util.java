package com.onefeedsdk.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsIntent;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.onefeedsdk.R;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.job.PostUserTrackingJob;
import com.onefeedsdk.listener.AddResponseListener;

import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 17-July-2018
 * Time: 11:46
 */
public class Util {

    private static long lastClickTime;
    private static String TAG = "Util";

    @SuppressLint("HardwareIds")
    public static String getAndroidUniqueId() {
        Context context = OneFeedSdk.getInstance().getContext();
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static void changedBackgroundColorToRes(View view, int color) {
        GradientDrawable bgShape = (GradientDrawable) view.getBackground();
        bgShape.setColor(color);
    }

    public static void showCustomTabBrowserByNotification(Context context, int color, String title, String url, String storyId) {
        openCustomTab(context, color, title, url, storyId);
    }

    public static void showCustomTabBrowser(Context context, int color, String title, String url, String storyId) {
        openCustomTab(context, color, title, url, storyId);
        //Tracking OneFeed View
        OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                new PostUserTrackingJob(Constant.STORY_OPENED, Constant.STORY_OPENED_BY_ONE_FEED, storyId));
    }

    public static void showCustomTabBrowserByCard(Context context, int color, String title, String url, String storyId) {
        openCustomTab(context, color, title, url, storyId);
        //Tracking OneFeed Card View
        OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                new PostUserTrackingJob(Constant.STORY_OPENED, Constant.STORY_OPENED_BY_CARD, storyId));
    }

    private static void openCustomTab(Context context, int color, String title, String url, String storyId) {
        if (isAppInstalled(context, "com.android.chrome")) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                    .addDefaultShareMenuItem()
                    .setToolbarColor(color)
                    .setShowTitle(true)
                    // Setting a custom back button
                    .setCloseButtonIcon(BitmapFactory.decodeResource(
                            context.getResources(), R.drawable.arrow))
                    .addMenuItem(title, null);

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.intent.setPackage("com.android.chrome");
            customTabsIntent.intent.putExtra(Intent.EXTRA_REFERRER, Uri.parse("android-app://" + context.getPackageName()));
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        }
    }

    public static void showToastMsg(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void setPrefValue(String key, String value) {
        SharedPreferences.Editor editor = OneFeedSdk.getInstance().getDefaultAppSharedPreferences()
                .edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }


    public static boolean preventMultipleClick() {
        // preventing double, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return true;
        }

        lastClickTime = SystemClock.elapsedRealtime();
        return false;
    }

    public static void hideKeyboard(Context context, View view) {
        try {
            // hide virtual keyboard
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
    }

    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
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

    public static String getNetworkConnectionType(Context context) {
        try {
            ConnectivityManager connectivityManager;
            NetworkInfo networkInfo;
            NetworkInfo networkInfo2;

            connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            networkInfo2 = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            String nOperator = "";
            String simOperator = "";
            try {
                nOperator = telephonyManager.getNetworkOperatorName();
                simOperator = telephonyManager.getSimOperatorName();
            } catch (NullPointerException e) {
                Log.e("Exception:", e.getMessage());
            } catch (Exception e) {
                Log.e("Exception:", e.getMessage());
            }
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

            if (networkInfo != null && networkInfo.isConnected()) {
                return networkType;
            } else if (networkInfo2 != null && networkInfo2.isConnected()) {
                return "WIFI";
            } else {
                return "Unknown";
            }
        } catch (NullPointerException e) {
            Log.e("Exception:", e.getMessage());
        } catch (Exception e) {
            Log.e("Exception:", e.getMessage());
        }
        return "Unknown";
    }

    public static void getPhoneDetail(Context context) {

        String simInfo = "";
        try {
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            simInfo = telephonyManager.getSimOperatorName();
        } catch (NullPointerException e) {
            Log.e("Exception:", e.getMessage());
        } catch (Exception e) {
            Log.e("Exception:", e.getMessage());
        }

        String oldSim = OneFeedSdk.getInstance().getDefaultAppSharedPreferences()
                .getString(Constant.OLD_SIM, "");

        if (!oldSim.equalsIgnoreCase(simInfo)) {
            final String finalSimInfo = simInfo;
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                    new PostUserTrackingJob(Constant.SIM_CHANGE, oldSim, simInfo, 1, new AddResponseListener() {
                        @Override
                        public void success() {
                            SharedPreferences.Editor editor = OneFeedSdk.getInstance().getDefaultAppSharedPreferences()
                                    .edit();
                            editor.putString(Constant.OLD_SIM, finalSimInfo).apply();
                            editor.commit();
                        }

                        @Override
                        public void error() {

                        }
                    })
            );
        }
    }


}
