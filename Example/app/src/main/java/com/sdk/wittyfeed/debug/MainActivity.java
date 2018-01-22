package com.sdk.wittyfeed.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sdk.wittyfeed.wittynativesdk.Interfaces.WittyFeedSDKMainInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKApiClient;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKMain;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;
import com.sdk.wittyfeed.wittynativesdk.utils.fcm.WittyFeedSDKNotificationManager;
import com.sdk.wittyfeed.wittynativesdk.utils.wittyenum.WittyFeedSDKGender;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static String APP_ID = "61";
    private static String API_KEY = "f168529b71aedcbf628fae0bcedb84d4";
    private static String FCM_TOKEN = "";

    Activity activity;

    private String ACTION_BAR_BG_COLOR = "";
    private String ACTION_BAR_TEXT_COLOR = "";
    LinearLayout btns_ll;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        FCM_TOKEN = FirebaseInstanceId.getInstance().getToken();

        btns_ll = (LinearLayout) findViewById(R.id.btns_ll);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btns_ll.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        if (FCM_TOKEN == null) {
            FCM_TOKEN = "";
        }

        Log.d("FCM_CUSTOM", "Refreshed token: " + FCM_TOKEN);


        // OPTIONAL to provide basic user_meta.
        // By providing basic user_meta your app can receive targeted content which has an higher CPM then regular content.
        HashMap<String, String> user_meta = new HashMap<>();

        // WittyFeedSDKGender has following options = MALE, FEMALE, OTHER, NONE
        user_meta.put("client_gender", WittyFeedSDKGender.MALE);

        // user Interests. String with a max_length = 100
        user_meta.put("client_interests", "love, funny, sad, politics, food, technology, DIY, friendship, hollywood, bollywood, NSFW"); // string max_length = 100



        // below code is only ***required*** for Initializing Wittyfeed Android SDK API, -- providing 'user_meta' is optional --
        WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient = new WittyFeedSDKApiClient(activity, APP_ID, API_KEY, FCM_TOKEN, user_meta);
        WittyFeedSDKSingleton.getInstance().witty_sdk = new WittyFeedSDKMain(activity, WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient);

        // use this interface callback to do operations when SDK finished loading
        WittyFeedSDKMainInterface wittyFeedSDKMainInterface = new WittyFeedSDKMainInterface() {
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


        //setting callback here
        WittyFeedSDKSingleton.getInstance().witty_sdk.set_operationDidFinish_callback(wittyFeedSDKMainInterface);

        // initializing SDK here
        WittyFeedSDKSingleton.getInstance().witty_sdk.init_wittyfeed_sdk();
        WittyFeedSDKSingleton.getInstance().witty_sdk.prepare_feed();


        findViewById(R.id.goto_waterfall_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go_to_wf_screen = new Intent(MainActivity.this,
                        WaterfallActivity.class);
                startActivity(go_to_wf_screen);
            }
        });

        findViewById(R.id.goto_endless_feed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, EndlessFeedActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.goto_carousel_demo_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CarouselDemoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.goto_category_wise_feed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, CategoryWiseFeedActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.simulate_detail_notiff_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_demo_fcm("WittyFeedSDKDetailCardActivity");
            }
        });
    }


    private void send_demo_fcm(String notiff_type) {
        WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(activity, FCM_TOKEN);
        int preferred_notiff_icon = R.mipmap.ic_launcher;
        Map<String, String> dummy_notiff_data = get_dummy_json_object();

        dummy_notiff_data.put("action", "" + notiff_type);

        wittyFeedSDKNotificationManager.handleNotification(dummy_notiff_data, preferred_notiff_icon);
    }


    private Map<String, String> get_dummy_json_object() {

        Map<String, String> object = new HashMap<>();

        try {
            object.put("story_id", "60496");
            object.put("story_title", "This Adorable Baby Copied His Model Uncle's Poses And The Result Is Amazing");
            object.put("cover_image", "https://cdn.wittyfeed.com/60496/xqivpjxv76pt44q269cm.png?impolicy=pqlow&imwidth=960");
            object.put("user_id", "143026");
            object.put("user_full_name", "Richa");
            object.put("doc", "2017-10-24 09:06:57");
            object.put("cat_name", "Lifestyle");
            object.put("cat_id", "47");
            object.put("cat_image", "lifestyle.png");
            object.put("card_type", "card_type_7");
            object.put("doodle", "https://cdn.wittyfeed.com/Mobile-App/doodles/doodle_1.png");
            object.put("audio", "https://cdn.wittyfeed.com//Mobile-App/stories_sound/1.mp3");
            object.put("animation_type", "slideInUp");
            object.put("cat_color", "#f37022");
            object.put("story_url", "https://www.wittyfeed.com/story/60496");
            object.put("detail_view", "");
            String[] empty_string_arr = {""};
            object.put("image_url", empty_string_arr.toString());

            object.put("id", "400");
            object.put("body", "Hey, Here's a new amazing story for you!");
            object.put("title", "Whow, you wouldn't belive how good this SDK works!");
            object.put("notiff_agent" , "wittyfeed_sdk");
            object.put("app_id" , "61");

            object.put("notiff_agent", "wittyfeed_sdk");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }

}
