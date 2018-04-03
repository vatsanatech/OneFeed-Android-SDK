package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by aishwarydhare on 23/10/17.
 */

class WittyFeedSDKSingleton {
    @SuppressLint("StaticFieldLeak")
    private static final WittyFeedSDKSingleton ourInstance = new WittyFeedSDKSingleton();
    final String TAG = "WF_SDK";

    // cached data that's used throughout the app
    String loader_iv_url = "";
    int loader_threshold_int = 85;
    String onefeed_bg_color_string = "";
    boolean is_load_cache_else_network = true;
    Bitmap onefeed_back_icon_bitmap;
    String last_search_for_str = "";
    ArrayList<Block> search_blocks_arr = new ArrayList<>();
    ArrayList<Block> interests_block_arr = new ArrayList<>();
    ArrayList<Block> default_search_block_arr = new ArrayList<>();
    //
    // utils
    //
    SharedPreferences wittySharedPreferences;
    SharedPreferences.Editor editor_sharedPref;
    int screenHeight;
    int screenWidth;
    double SMALL_TSR = 0.5;
    double MEDIUM_TSR = 0.7;
    double LARGE_TSR = 1;
    View root_activity_view;
    //
    // main
    //
    @SuppressLint("StaticFieldLeak")
    WittyFeedSDKGoogleAnalytics wittyFeedSDKGoogleAnalytics;
    WittyFeedSDKMain witty_sdk;
    Intent homeActivityIntent; // used for notification exit-callback intent
    //
    // model
    //
    ArrayList<Block> blockArrayList = new ArrayList<>();
    OneFeedConfig oneFeedConfig;
    //
    // other
    //
    boolean isDataUpdated = false;

    static WittyFeedSDKSingleton getInstance() {
        return ourInstance;
    }

    String getGA_TRACKING_ID() {
        // TODO: 26/12/17 enable live GA
        String GA_TRACKING_ID = "UA-40875502-17"; // sdk main - tech@wittyfeed.com , sdk.wittyfeed.com
        return GA_TRACKING_ID;
    }
}
