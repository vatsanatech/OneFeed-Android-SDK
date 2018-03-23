package com.sdk.wittyfeed.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdk.wittyfeed.demo.utils.CustomViewPager;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKMainInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKApiClient;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKMain;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity{

    private static String APP_ID = "108";
    private static String API_KEY = "963a1cf4d081b0b0bdc6e9e13de66dd3";
    private static String FCM_TOKEN = "";

    private Activity activity;
    private CustomViewPager viewPager;

    private ProgressBar progressBar;
    public int selected_frag_id = 0;
    private VPAdapter vpAdapter;
    private boolean wantToExit = false;
    private WittyFeedSDKApiClient wittyFeedSDKApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        FCM_TOKEN = FirebaseInstanceId.getInstance().getToken();

        viewPager = findViewById(R.id.viewPager);
        progressBar = findViewById(R.id.progressBar);

        viewPager.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (FCM_TOKEN == null) {
            FCM_TOKEN = "";
        }

        Log.d("FCM_CUSTOM", "Refreshed token: " + FCM_TOKEN);


        // ====================
        // SDK WORK STARTS HERE
        // ====================

        //
        // OPTIONAL to provide basic user_meta.
        // By providing user_meta your app can receive targeted content which has an higher CPM then regular content.
        //
        HashMap<String, String> user_meta = new HashMap<>();

        //
        // WittyFeedSDKGender has following options = "M" for Male, "F" for Female, "O" for Other, "N" for None
        //
        user_meta.put("client_gender", "M");

        //
        // User Interests.
        // String with a max_length = 100
        //
        user_meta.put("client_interests", "love, funny, sad, politics, food, technology, DIY, friendship, hollywood, bollywood, NSFW"); // string max_length = 100

        //
        // below code is only ***required*** for Initializing Wittyfeed Android SDK API, -- providing 'user_meta' is optional --
        //
        wittyFeedSDKApiClient = new WittyFeedSDKApiClient(activity, APP_ID, API_KEY, FCM_TOKEN/*, user_meta*/);
        mSingleton.getInstance().witty_sdk = new WittyFeedSDKMain(activity, wittyFeedSDKApiClient);

        //
        // Use this interface callback to do operations when SDK finished loading
        //
        WittyFeedSDKMainInterface wittyFeedSDKMainInterface = new WittyFeedSDKMainInterface() {
            @Override
            public void onOperationDidFinish() {
                // witty sdk did loaded completely successfully
                Log.d("Main App", "witty sdk did load successfully");
                String[] availableCats = mSingleton.getInstance().witty_sdk.get_all_categoies_available();

                progressBar.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        selected_frag_id = position;
                        if(selected_frag_id == 1){
                            viewPager.setIs_swipeable(false);
                        } else {
                            viewPager.setIs_swipeable(true);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

                vpAdapter = new VPAdapter(getSupportFragmentManager());
                viewPager.setAdapter(vpAdapter);
                viewPager.setCurrentItem(0);
                viewPager.setOffscreenPageLimit(3);
            }

            @Override
            public void onError(Exception e) {
                // if unexpected error
            }
        };

        //
        //setting callback here
        //
        mSingleton.getInstance().witty_sdk.set_operationDidFinish_callback(wittyFeedSDKMainInterface);

        //
        // Initializing SDK here (mandatory)
        //
        mSingleton.getInstance().witty_sdk.init_wittyfeed_sdk();

        //
        // Fetch fresh feeds from our servers with this method call.
        // It is not mandatory if only notification feature is desired from the SDK
        //
        mSingleton.getInstance().witty_sdk.prepare_feed();

        // ====================
        // SDK WORK ENDS HERE
        // ====================
    }

    @Override
    public void onBackPressed() {

        // ============
        // === NOTE ===
        // ============
        //
        // The OneFeed SDK Section utilises overridden functionality of `onBackPressed` button,
        // Hence to handle it well with the host activity.
        //
        // Please use the method WittyFeedSDKFeedSupportFragment.is_doing_onefeed_back()
        // as we have done in this sample app
        //

        if (selected_frag_id == 1) {
            try {
                if (vpAdapter.get_active_fragment() instanceof FeedFragment) {
                    //
                    // below code is *mandatory*
                    //
                    if (vpAdapter.get_active_fragment() != null) {
                        if(!((FeedFragment) vpAdapter.get_active_fragment()).performOnBack()){
                            if (selected_frag_id == 1) {
                                set_vp_pos(0);
                            }
                        }

                        if (wantToExit) {
                            try {
                                set_vp_pos(0);
                                return;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            wantToExit = true;
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                wantToExit = false;
                            }
                        }, 300);
                    }
                }
            } catch (Exception e) {
                super.onBackPressed();
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
        }
    }


    public void set_vp_pos(int i) {
        viewPager.setCurrentItem(i);
    }

}
