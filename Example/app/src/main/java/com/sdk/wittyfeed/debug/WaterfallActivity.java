package com.sdk.wittyfeed.debug;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

/**
 * Created by aishwarydhare on 08/11/17.
 */

public class WaterfallActivity extends AppCompatActivity {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterfall);

        activity = this;

        // initializing waterfall fragment. Note- Make sure you have initialized the SDK in previous steps
        Fragment fragment = WittyFeedSDKSingleton.getInstance().witty_sdk.get_waterfall_fragment(this);

        // using our WittyFeedSDKWaterfallFragment

        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(findViewById(R.
                id.fragmentHolder_fl).getId(), fragment, "WittyFeed_SDK_Waterfall").commit();
    }

}
