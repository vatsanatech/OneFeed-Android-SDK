package com.wittyfeed.sdk.demo;

import com.wittyfeed.sdk.onefeed.WittyFeedSDKMain;

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
