package com.wittyfeed.sdk.demo;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onesignal.OneSignal;
import com.wittyfeed.sdk.onefeed.ApiClient;
import com.wittyfeed.sdk.onefeed.OFNotificationManager;
import com.wittyfeed.sdk.onefeed.OneFeedMain;
import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Utils.OneFeedBuilder;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String APP_ID = "108";
    private String API_KEY = "963a1cf4d081b0b0bdc6e9e13de66dd3";
    private String FCM_TOKEN = "";
    private Activity activity;
    public ViewPager viewPager;

    public int selected_frag_id = 0;
    private VPAdapter vpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        viewPager = findViewById(R.id.viewPager);


        // ====================
        // SDK WORK STARTS HERE
        // ====================

        /*
         * OPTIONAL to provide basic mUserMeta.
         * By providing mUserMeta your app can receive targeted content which has an higher CPM then regular content.
         */
        HashMap<String, String> mUserMeta = new HashMap<>();

        /*
         * Send Gender of User:- "M" for Male, "F" for Female, "O" for Other, "N" for None
         */
        mUserMeta.put("client_gender", "M");

        /*
         * User Interests.
         * String with a max_length = 100
         */
        mUserMeta.put("client_interests", "love, funny, sad, politics, food, technology, DIY, friendship, hollywood, bollywood, NSFW"); // string max_length = 100

        /*
         * -- passing 'mUserMeta' is OPTIONAL --
         */
        ApiClient.getInstance().appendCustomUserMetaToUserMeta(mUserMeta);

        /*
         * setting callback here
         * Use this interface callback to do operations when SDK finished loading
         */


        OneFeedMain.getInstance().setOneFeedDidInitialisedCallback(new OneFeedMain.OnInitialized() {
            @Override
            public void onSuccess() {
                Log.d("Main App", "witty sdk did load successfully");
                viewPager.setVisibility(View.VISIBLE);

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        selected_frag_id = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });

                Fragment oneFeedFragment = OneFeedMain.getInstance().getOneFeedFragment();

                OneFeedMain.getInstance().oneFeedBuilder.setOnBackClickInterface(new OneFeedBuilder.OnBackClickInterface() {
                    @Override
                    public void onBackClick() {
                        viewPager.setCurrentItem(0);
                    }
                });

                vpAdapter = new VPAdapter(getSupportFragmentManager(), oneFeedFragment);
                viewPager.setAdapter(vpAdapter);
                viewPager.setCurrentItem(0);
                viewPager.setOffscreenPageLimit(3);
            }

            @Override
            public void onError() {
                Toast.makeText(activity, "OneFeed data couldn't be loaded", Toast.LENGTH_SHORT).show();
                Log.e("mAPP", "onError: OneFeed data couldn't be loaded");
            }
        });

        /*
         * below code is ***required*** for Initializing WittyFeed Android SDK API,
         */
        OneFeedMain.setHideBackButtonFromMainFeed(false);

        if(Root.notificationProvider.equalsIgnoreCase("F")){
            FCM_TOKEN = FirebaseInstanceId.getInstance().getToken();
            if (FCM_TOKEN == null) {
                FCM_TOKEN = "";
            }
            Log.e("FCM Token:", FCM_TOKEN);

            String topicName = "OneFeed_" + APP_ID + "_" + Constant.ONE_FEED_VERSION;
            FirebaseMessaging.getInstance().subscribeToTopic(topicName);

            OneFeedMain.getInstance().init(getBaseContext(), APP_ID, API_KEY, FCM_TOKEN);

            /*
             * Below code is ***required*** for consistent unsubscription and token update on sdk version change
             */
            if (!(OneFeedMain.getInstance().ofSharedPreference.getSDKVersion().equals(""))) {
                if (!OneFeedMain.getInstance().ofSharedPreference.getSDKVersion().equals(Constant.ONE_FEED_VERSION)) {
                    OneFeedMain.getInstance().fcmTokenManager.updateTokenForVersionChange();
                    String oldTopic = "OneFeed_" + APP_ID + "_" + OneFeedMain.getInstance().ofSharedPreference.getSDKVersion();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(oldTopic);
                }
            }
            OneFeedMain.getInstance().ofSharedPreference.setSDKVersion(Constant.ONE_FEED_VERSION);
        }else{

            OneSignal.getTags(new OneSignal.GetTagsHandler() {
                @Override
                public void tagsAvailable(JSONObject tags) {
                    if (tags != null) {
                        try {
                            String tag = tags.getString("onefeed");
                            if (!tag.equalsIgnoreCase("app_id_" + Constant.ONE_FEED_VERSION)) {
                                OneSignal.sendTag("onefeed", "app_id_" + Constant.ONE_FEED_VERSION);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else{
                        OneSignal.sendTag("onefeed", "app_id_" + Constant.ONE_FEED_VERSION);
                    }
                }
            });

            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                @Override
                public void idsAvailable(String userId, String registrationId) {
                    Log.e("debug", "User:" + userId);
                    OneFeedMain.getInstance().init(getBaseContext(), APP_ID, API_KEY, userId);


                    if (registrationId != null)
                        Log.e("debug", "registrationId:" + registrationId);

                }
            });
        }



        /*
         * Set Intent of the Activity you want to open on Back press from Story opens from Notification
         */
        OFNotificationManager.getInstance().setHomeScreenIntent(this, new Intent(this.getApplicationContext(), MainActivity.class));


        // ==================
        // SDK WORK ENDS HERE
        // ==================

    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 1) {
            viewPager.setCurrentItem(0);
        } else
            super.onBackPressed();
    }
}
