package com.wittyfeed.sdk.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.iid.FirebaseInstanceId;
import com.wittyfeed.sdk.onefeed.WittyFeedSDKApiClient;
import com.wittyfeed.sdk.onefeed.WittyFeedSDKMain;
import com.wittyfeed.sdk.onefeed.WittyFeedSDKMainInterface;
import com.wittyfeed.sdk.onefeed.WittyFeedSDKNotificationManager;

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
    private WittyFeedSDKMainInterface wittyFeedSDKMainInterface;

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
         * OPTIONAL to provide basic user_meta.
         * By providing user_meta your app can receive targeted content which has an higher CPM then regular content.
         */
        HashMap<String, String> user_meta = new HashMap<>();

        /*
         * WittyFeedSDKGender has following options = "M" for Male, "F" for Female, "O" for Other, "N" for None
         */
        user_meta.put("client_gender", "M");

        /*
         * User Interests.
         * String with a max_length = 100
        */
        user_meta.put("client_interests", "love, funny, sad, politics, food, technology, DIY, friendship, hollywood, bollywood, NSFW"); // string max_length = 100

        /*
         * below code is only ***required*** for Initializing WittyFeed Android SDK API, -- providing 'user_meta' is optional --
        */
        wittyFeedSDKApiClient = new WittyFeedSDKApiClient(activity, APP_ID, API_KEY, FCM_TOKEN/*, user_meta*/);
        mSingleton.getInstance().witty_sdk = new WittyFeedSDKMain(activity, wittyFeedSDKApiClient);

        /*
         * Use this interface callback to do operations when SDK finished loading
        */
        wittyFeedSDKMainInterface = new WittyFeedSDKMainInterface() {
            @Override
            public void onOperationDidFinish() {
                // witty sdk did loaded completely successfully
                Log.d("Main App", "witty sdk did load successfully");

                progressBar.setVisibility(View.GONE);
                btns_ll.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                // if unexpected error
            }
        };

        /*
         * setting callback here
         */
        mSingleton.getInstance().witty_sdk.set_operationDidFinish_callback(wittyFeedSDKMainInterface);

        /*
         * Style the content view of OneFeed using the two methods below
         * NOTE - needs to be done before calling init_wittyfeed_sdk()
         */
        mSingleton.getInstance().witty_sdk.set_onefeed_base_color("#000000");
        //  mSingleton.getInstance().witty_sdk.set_onefeed_back_icon(getBitmapFromDrawable(activity, R.drawable.m_ic_back));

        /*
         * Initializing SDK here (mandatory)
         */
        mSingleton.getInstance().witty_sdk.init_wittyfeed_sdk();

        // ==================
        // SDK WORK ENDS HERE
        // ==================



        findViewById(R.id.goto_waterfall_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, OneFeedActivity.class));
            }
        });

        findViewById(R.id.goto_endless_feed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // will implement later
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


    private Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmapFromVectorDrawable((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("Unable to convert to bitmap");
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getBitmapFromVectorDrawable(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

}
