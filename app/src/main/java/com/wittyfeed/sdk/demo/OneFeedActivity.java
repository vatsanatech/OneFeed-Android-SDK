package com.wittyfeed.sdk.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.wittyfeed.sdk.onefeed.OneFeedBuilder;
import com.wittyfeed.sdk.onefeed.OneFeedMain;

public class OneFeedActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_onefeed);

         /*
          * pass the object of WittyFeedSDKBackPressInterface to fragment so that when
          * user taps on back button of onefeed, perform_back() function of interface will call
          */
        OneFeedMain.getInstance().oneFeedBuilder.setOnBackClickInterface(new OneFeedBuilder.OnBackClickInterface() {
            @Override
            public void onBackClick() {
                finish();
            }
        });

        /*
         * initializing OneFeed Support Fragment. Note- Make sure you have initialized the SDK in previous steps
         */
        Fragment fragment = OneFeedMain.getInstance().getOneFeedFragment();

        /*
         * using the OneFeed Fragment
         */
        getSupportFragmentManager().executePendingTransactions();
        if(getSupportFragmentManager().findFragmentByTag("mOneFeedFragment") == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(
                            R.id.onefeed_fl,
                            fragment,
                            "mOneFeedFragment"
                    )
                    .commit();
        }
    }
}
