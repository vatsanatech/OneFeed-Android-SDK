package com.onefeed.sdk.sample.app;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.onefeedsdk.app.OneFeedSdk;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 13-September-2018
 * Time: 16:17
 */
public class Root extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OneFeedSdk.getInstance().init(getApplicationContext());
    }
}
