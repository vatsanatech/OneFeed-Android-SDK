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

    private static Tracker sTracker;
    public GoogleAnalytics sAnalytics;

    public static mSingleton getInstance() {
        return ourInstance;
    }

    private mSingleton() {
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
        if (sTracker == null) {
            sTracker = sAnalytics.newTracker(R.xml.global_tracker);
        }

        return sTracker;
    }
}
