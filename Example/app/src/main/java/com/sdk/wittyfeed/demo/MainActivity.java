package com.sdk.wittyfeed.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKApiClient;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKMain;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKMainInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKNotificationManager;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private static String APP_ID = "108";
    private static String API_KEY = "963a1cf4d081b0b0bdc6e9e13de66dd3";
    private static String FCM_TOKEN = "";

    private Activity activity;

    private ProgressBar progressBar;
    private WittyFeedSDKApiClient wittyFeedSDKApiClient;
    private LinearLayout btns_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        FCM_TOKEN = FirebaseInstanceId.getInstance().getToken();

        progressBar = findViewById(R.id.progressBar);
        btns_ll = findViewById(R.id.btns_ll);

        progressBar.setVisibility(View.VISIBLE);
        btns_ll.setVisibility(View.GONE);

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

                //
                // to fetch the number of categories stories are available in
                //
                String[] availableCats = mSingleton.getInstance().witty_sdk.get_all_categoies_available();

                progressBar.setVisibility(View.GONE);
                btns_ll.setVisibility(View.VISIBLE);
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

        // ==================
        // SDK WORK ENDS HERE
        // ==================

        findViewById(R.id.goto_waterfall_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSingleton.getInstance().witty_sdk.open_onefeed(activity);
            }
        });

        findViewById(R.id.goto_endless_feed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EndlessFeedActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.simulate_notiff_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_demo_fcm(); // For Directly going to the WebPage Story of WittyFeed
            }
        });
    }

    private void send_demo_fcm() {
        WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(activity, FCM_TOKEN);
        int preferred_notiff_icon = R.mipmap.ic_launcher;
        Map<String, String> dummy_notiff_data = new HashMap<>();
        try {
            dummy_notiff_data.put("story_id", "60496");
            dummy_notiff_data.put("story_title", "10 Things Every Girl Should Put On Her List");
            dummy_notiff_data.put("cover_image", "https://cdn.wittyfeed.com/41441/ilik0kqmr2hpv1i4l8ya.jpeg?imwidth=960");
            dummy_notiff_data.put("story_url","https://www.wittyfeed.me/story/41441/things-every-girl-should-put-in-her-list?utm_hash=ArD51&nohead=1");

            dummy_notiff_data.put("id", "400");
            dummy_notiff_data.put("body", "Hey, Here's a new amazing story for you!");
            dummy_notiff_data.put("title", "10 Things Every Girl Should Put On Her List");
            dummy_notiff_data.put("notiff_agent", "wittyfeed_sdk");
            dummy_notiff_data.put("app_id" , "108");

            dummy_notiff_data.put("action", "" + "WittyFeedSDKContentViewActivity");

        } catch (Exception e) {
            e.printStackTrace();
        }

        wittyFeedSDKNotificationManager.handleNotification(dummy_notiff_data, preferred_notiff_icon);
    }

}
