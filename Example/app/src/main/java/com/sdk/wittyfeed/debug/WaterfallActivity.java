package com.sdk.wittyfeed.debug;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.sdk.wittyfeed.wittynativesdk.Interfaces.WittyFeedRecyclerViewCallback;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;
import com.sdk.wittyfeed.wittynativesdk.fragment.WittyFeedSDKWaterfallFragment;

/**
 * Created by aishwarydhare on 08/11/17.
 */

// Implement WittyFeedRecyclerViewCallback only if you need hold of the Recyclerview powering the views

public class WaterfallActivity extends AppCompatActivity implements WittyFeedRecyclerViewCallback {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waterfall);

        activity = this;

        // initializing waterfall fragment. Note- Make sure you have initialized the SDK in previous steps
        WittyFeedSDKWaterfallFragment fragment = WittyFeedSDKSingleton.getInstance().witty_sdk.get_waterfall_fragment(this);


        // using our WittyFeedSDKWaterfallFragment

        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(findViewById(R.
                id.fragmentHolder_fl).getId(), fragment, "WittyFeed_SDK_Waterfall").commit();
    }

    @Override
    public void onRecyclerView(RecyclerView recyclerView) {
        RecyclerView recyclerView1 = recyclerView;

    }
}
