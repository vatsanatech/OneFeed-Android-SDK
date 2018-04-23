package com.wittyfeed.sdk.demo;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.wittyfeed.sdk.onefeed.ApiClient;
import com.wittyfeed.sdk.onefeed.OFNotificationManager;
import com.wittyfeed.sdk.onefeed.OneFeedMain;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity{

    private String APP_ID = "108";
    private String API_KEY = "963a1cf4d081b0b0bdc6e9e13de66dd3";
    private String FCM_TOKEN = "";

    private Activity activity;

    private ProgressBar progressBar;
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

        Log.d("FCM_CUSTOM", "FCM Token: " + FCM_TOKEN);


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
                progressBar.setVisibility(View.GONE);
                btns_ll.setVisibility(View.VISIBLE);
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
        OneFeedMain.getInstance().init(getApplicationContext(), APP_ID, API_KEY, FCM_TOKEN);

        // ==================
        // SDK WORK ENDS HERE
        // ==================



        findViewById(R.id.goto_waterfall_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, OneFeedActivity.class));
            }
        });

        findViewById(R.id.simulate_notiff_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_demo_fcm(); // For Directly going to the WebPage Story of WittyFeed
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("",""+FirebaseInstanceId.getInstance().getToken());
                clipboard.setPrimaryClip(clip);
            }
        });
    }


    private void send_demo_fcm() {
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
        OFNotificationManager.getInstance().setHomeScreenIntent(new Intent(getApplicationContext(), OneFeedActivity.class));

        OFNotificationManager.getInstance().handleNotification(getApplicationContext(),"", dummy_notiff_data, preferred_notiff_icon);
    }

}
