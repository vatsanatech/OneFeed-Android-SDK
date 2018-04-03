package com.wittyfeed.sdk.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.wittyfeed.sdk.onefeed.WittyFeedSDKBackPressInterface;
import com.wittyfeed.sdk.onefeed.WittyFeedSDKOneFeedFragment;

/**
 * Created by aishwarydhare on 30/03/18.
 */

public class OneFeedActivity extends AppCompatActivity {

    FrameLayout onefeed_fl;
    private final String SIMPLE_FRAGMENT_TAG = "myfragmenttag";
    WittyFeedSDKOneFeedFragment wittyFeedSDKOneFeedFragment;
    WittyFeedSDKBackPressInterface wittyFeedSDKBackPressInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_onefeed);

        onefeed_fl = findViewById(R.id.onefeed_fl);

        if (savedInstanceState != null) { // saved instance state, fragment may exist
            // look up the instance that already exists by tag
            wittyFeedSDKOneFeedFragment = (WittyFeedSDKOneFeedFragment) getSupportFragmentManager().findFragmentByTag(SIMPLE_FRAGMENT_TAG);
        } else if (wittyFeedSDKOneFeedFragment == null) {
            // only create fragment if they haven't been instantiated already
            wittyFeedSDKOneFeedFragment = new WittyFeedSDKOneFeedFragment();
        }

        wittyFeedSDKBackPressInterface = new WittyFeedSDKBackPressInterface() {
            @Override
            public void perform_back() {
                finish();
            }
        };

        wittyFeedSDKOneFeedFragment.setWittyFeedSDKBackPressInterface(wittyFeedSDKBackPressInterface);


        getSupportFragmentManager().beginTransaction()
                .replace(onefeed_fl.getId(), wittyFeedSDKOneFeedFragment, SIMPLE_FRAGMENT_TAG)
                .commit();
    }


}
