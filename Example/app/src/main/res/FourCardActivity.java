package com.sdk.wittyfeed.debug;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.sdk.wittyfeed.wittynativesdk.Interfaces.WittyFeedSDKCardFetcherInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKCardFetcher;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

public class FourCardActivity extends AppCompatActivity {

    private static final String TAG = "WF_SDK";
    Activity activity;


    RelativeLayout content1_rl, content2_rl, content3_rl, content4_rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_card);

        activity = this;

        content1_rl = (RelativeLayout) findViewById(R.id.content1_rl);
        content2_rl = (RelativeLayout) findViewById(R.id.content2_rl);
        content3_rl = (RelativeLayout) findViewById(R.id.content3_rl);
        content4_rl = (RelativeLayout) findViewById(R.id.content4_rl);

        // First: create an interface of type WittyFeedSDKCardFetcherInterface in which four methods will be there as demonstrated below

        // Second: initialize an object of WittyFeedSDKCardFetcher to fetch cards, NOTE- use same object from WittyFeedSDKSingleton as demonstrated below
        // if you don't want to see any repeated card anywhere in the app. Otherwise you can initialize different object of WittyFeedSDKCardFetcher

        // Third: Use fetch_a_card() method of WittyFeedSDKCardFetcher to place a WittyFeed SDK Card in one your ViewGroups (i.e. views, layouts etc)
        // fetch_a_card() accepts two arguments
        // First argument: is of String TYPE and is used to define your own custom tag that you will later recieve in onCardReceived (its purpose is similar to itemType parameter in OnCreateViewHolder of RecyclerView)
        // Second argument: is FLOAT TYPE for adjusting font_size_ratio of cards which should be between 0.0f to 1.0f (example: if your layout covers full screen then pass 1.0f)

        // Other Available Methods by WittyFeedSDKCardFetcher:
        // clearCardFetchedHistory(): clears history that keep tracks what card have been used and what not,
        // clearing this will fetch again the very first card, that was fetched.

        // First
        WittyFeedSDKCardFetcherInterface wittyFeedSDKCardFetcherInterface = new WittyFeedSDKCardFetcherInterface() {
            @Override
            public void onWillStartFetchingMoreData() {
                // fetching more data, do necessary UI updates here. onMoreDataFetched will be called when the data will be fetched
                Log.d(TAG, "onWillStartFetchingMoreData: ");
                findViewById(R.id.change_ll).setVisibility(View.INVISIBLE);
                findViewById(R.id.pb_ll).setVisibility(View.VISIBLE);
            }


            @Override
            public void onMoreDataFetched() {
                // after fetching more data. onMoreDataFetched will be called when the data will be fetched
                Log.d(TAG, "onMoreDataFetched: ");
                findViewById(R.id.pb_ll).setVisibility(View.GONE);
                findViewById(R.id.change_ll).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCardReceived(String customTag, View cardViewFromWittyFeed) {
                // when a cardView is made, onCardReceived will return WittyFeedCard of type (View)
                switch (customTag){
                    case "content1_rl":
                        content1_rl.removeAllViews();
                        content1_rl.addView(cardViewFromWittyFeed);
                        break;
                    case "content2_rl":
                        content2_rl.removeAllViews();
                        content2_rl.addView(cardViewFromWittyFeed);
                        break;
                    case "content3_rl":
                        content3_rl.removeAllViews();
                        content3_rl.addView(cardViewFromWittyFeed);
                        break;
                    case "content4_rl":
                        content4_rl.removeAllViews();
                        content4_rl.addView(cardViewFromWittyFeed);
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                // unexpected happens here
                Log.d(TAG, "onError: "+ e.getMessage(), e);
            }
        };

        // Second
            // Tip: use the object of WittyFeedSDKCardFetcher from Singleton class if you want to use cards in difference activities
            // and also don't want to repeat cards that have been loaded previously in previous screens
            // otherwise just create an object local to current activity only and use that, see TinderCardActivity for such implementation
        WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher = new WittyFeedSDKCardFetcher(activity, wittyFeedSDKCardFetcherInterface);

        // Third
        WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content1_rl", 0.5f);
        WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content2_rl", 0.5f);
        WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content3_rl", 0.5f);
        WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content4_rl", 0.5f);


        findViewById(R.id.change1_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content1_rl", 0.5f);
            }
        });

        findViewById(R.id.change2_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content2_rl", 0.5f);
            }
        });

        findViewById(R.id.change3_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content3_rl", 0.5f);
            }
        });

        findViewById(R.id.change4_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content4_rl", 0.5f);
            }
        });

    }

}
