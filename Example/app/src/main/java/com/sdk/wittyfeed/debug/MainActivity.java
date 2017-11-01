package com.sdk.wittyfeed.debug;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdk.wittyfeed.debug.tindercard.TinderCardActivity;
import java.util.HashMap;
import java.util.Map;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedOldSDKWaterfallActivity;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKApiClient;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKMain;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKMainInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;
import com.sdk.wittyfeed.wittynativesdk.utils.fcm.WittyFeedSDKNotificationManager;

public class MainActivity extends AppCompatActivity {

    private static String APP_ID = "61"; // SET YOUR APP ID HERE
    private static String API_KEY = "f168529b71aedcbf628fae0bcedb84d4"; // SET YOUR API_KEY HERE
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
        if(FCM_TOKEN == null){
            FCM_TOKEN = "";
        }
        Log.d("FCM_CUSTOM", "Refreshed token: " + FCM_TOKEN);

        btns_ll = (LinearLayout) findViewById(R.id.btns_ll);
        btns_ll.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        if(APP_ID.equals("") || API_KEY.equals("")){
            Toast.makeText(activity, "APP_ID and API_KEY is not set", Toast.LENGTH_SHORT).show();
            return;
        }

        findViewById(R.id.goto_waterfall_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // waterfall layout code doesn't require anything else, just below lines of code fulfil everything
                Intent go_to_wf_screen = new Intent(activity,
                        WittyFeedOldSDKWaterfallActivity.class);
                go_to_wf_screen.putExtra("FCM_TOKEN", FCM_TOKEN);
                go_to_wf_screen.putExtra("APP_ID", APP_ID);
                go_to_wf_screen.putExtra("API_KEY", API_KEY);
                go_to_wf_screen.putExtra("ACTION_BAR_BG_COLOR", "");
                go_to_wf_screen.putExtra("ACTION_BAR_TEXT_COLOR", "");
                startActivity(go_to_wf_screen);
            }
        });


        // below code is only required for Native SDK CARDS support
        WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient = new WittyFeedSDKApiClient(activity, APP_ID, API_KEY, FCM_TOKEN);
        WittyFeedSDKSingleton.getInstance().witty_sdk = new WittyFeedSDKMain(activity, WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient);

        // use this interface callback to do operations when SDK finished loading
        WittyFeedSDKSingleton.getInstance().wittyFeedSDKMainInterface = new WittyFeedSDKMainInterface() {
            @Override
            public void onOperationDidFinish() {
                // witty sdk did loaded completely successfully
                Log.d("Main App", "witty sdk did load successfully");
                progressBar.setVisibility(View.GONE);
                btns_ll.setVisibility(View.VISIBLE);
            }
        };

        //setting callback here
        WittyFeedSDKSingleton.getInstance().witty_sdk.set_operationDidFinish_callback(WittyFeedSDKSingleton.getInstance().wittyFeedSDKMainInterface);

        // initializing SDK here
        WittyFeedSDKSingleton.getInstance().witty_sdk.init_wittyfeed_sdk();


        findViewById(R.id.goto_four_card_demo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, FourCardActivity.class);
                startActivity(intent);
            }
        });


        // TODO: 30/10/17 need to develop endless feed screens
        findViewById(R.id.goto_endless_feed_btn).setVisibility(View.GONE);
        findViewById(R.id.goto_endless_feed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        findViewById(R.id.goto_tinder_card_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, TinderCardActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.simulate_detail_notiff_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_demo_fcm("WittyFeedSDKDetailCardActivity");
            }
        });

        findViewById(R.id.simulate_web_notiff_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_demo_fcm("WittyFeedSDKWebViewActivity");
            }
        });
    }


    private void send_demo_fcm(String notiff_type) {
        WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(activity);
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
            object.put("cover_image", "https://cdn.wittyfeed.com/60496/300x0/xqivpjxv76pt44q269cm.png");
            object.put("user_id", "143026");
            object.put("user_full_name", "Richa");
            object.put("doc", "2017-10-24 09:06:57");
            object.put("cat_name", "Lifestyle");
            object.put("cat_id", "47");
            object.put("cat_image", "lifestyle.png");
            object.put("card_type", "card_type_2");
            object.put("doodle", "https://cdn.wittyfeed.com/Mobile-App/doodles/doodle_1.png");
            object.put("audio", "https://cdn.wittyfeed.com//Mobile-App/stories_sound/1.mp3");
            object.put("animation_type", "slideInUp");
            object.put("cat_color", "#f37022");
            object.put("story_url", "https://www.wittyfeed.com/story/60496");
            object.put("detail_view", "");
            object.put("image_url", "");

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
