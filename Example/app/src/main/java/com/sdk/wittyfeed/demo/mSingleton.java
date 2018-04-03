package com.sdk.wittyfeed.demo;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKMain;

/**
 * Created by aishwarydhare on 23/03/18.
 */

public class mSingleton {
    private static final mSingleton ourInstance = new mSingleton();
    WittyFeedSDKMain witty_sdk;

    public static mSingleton getInstance() {
        return ourInstance;
    }

    private mSingleton() {
    }
}
