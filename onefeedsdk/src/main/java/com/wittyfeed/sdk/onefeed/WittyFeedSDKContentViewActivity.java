package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aishwarydhare on 27/03/18.
 */

public class WittyFeedSDKContentViewActivity extends AppCompatActivity{

    // TODO: 30/03/18 clean up content view activity

    private static final String TAG = "WF_SDK";
    WebView web_view;
    String story_id;
    String url_to_open;
    ProgressBar determinateBar;
    Activity activity;
    String story_url = "";
    private RelativeLayout loaderView_rl;
    private ImageView loader_iv;
    private TextView loader_tv;
    private boolean is_loaded_once = false;
    private boolean is_from_notification = false;
    private WittyFeedSDKOneFeedInterface wittyFeedSDKOneFeedInterface;
    private boolean has_fallbacked = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_content_view_wfsdk);

        try {
            if (!WittyFeedSDKSingleton.getInstance().onefeed_bg_color_string.equalsIgnoreCase("")) {
                findViewById(R.id.root_fl).setBackgroundColor(Color.parseColor(WittyFeedSDKSingleton.getInstance().onefeed_bg_color_string));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Tag","Invalid color code provided for OneFeed Background");
        }

        activity = this;

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey("is_loaded_notification")) {
            is_from_notification = true;
        }

        try {
            this.getActionBar().hide();
        } catch (Exception e) {
        }
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
            // do nothing
        }

        web_view = findViewById(R.id.wv_data);
        determinateBar = findViewById(R.id.determinateBar);
        loaderView_rl = findViewById(R.id.loaderView_rl);
        loader_iv = findViewById(R.id.loader_iv);
        loader_tv = findViewById(R.id.loader_tv);

        try {
            WittyFeedSDKSingleton.getInstance().loader_iv_url = WittyFeedSDKSingleton.getInstance().wittySharedPreferences.getString("loader_iv_url","");
            Log.d(TAG, "init_wittyfeed_sdk: cached loader_iv_url: " + WittyFeedSDKSingleton.getInstance().loader_iv_url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        init_loader_view();
        initWebViewContent();
    }

    private void init_loader_view() {
        try {
            if (!WittyFeedSDKSingleton.getInstance().loader_iv_url.equalsIgnoreCase("")) {
                WittyGlide
                        .with(activity) // replace with 'this' if it's in activity
                        .load(WittyFeedSDKSingleton.getInstance().loader_iv_url)
                        .error(R.drawable.one_feed_loader) // show error drawable if the image is not a gif
                        .into(loader_iv);
            } else {
                WittyGlide
                        .with(activity) // replace with 'this' if it's in activity
                        .load(R.drawable.one_feed_loader)
                        .error(R.drawable.one_feed_loader) // show error drawable if the image is not a gif
                        .into(loader_iv);
            }
        } catch (Exception e) {
            Log.d(TAG, "onViewCreated: couldn't load loader_iv", e);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    void initWebViewContent(){

        try {
            if(!url_to_open.equalsIgnoreCase("")){
                story_url = url_to_open;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        web_view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress)
            {
                super.onProgressChanged(view, progress);
                Log.d(TAG, ""+progress);
                determinateBar.setProgress(progress);

                if(progress >= 100){
                    determinateBar.setVisibility(View.GONE);
                }

                if (!is_loaded_once) {
                    if(progress >= WittyFeedSDKSingleton.getInstance().loader_threshold_int){
                        web_view.setVisibility(View.VISIBLE);
                        loaderView_rl.setVisibility(View.GONE);
                        is_loaded_once = true;
                    } else {
                        web_view.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        Bundle bundle = getIntent().getExtras();
        String url_to_open = bundle.getString("","");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("app_id",""+ bundle.getString("app_id",""));
            jsonObject.putOpt("story_id",""+ bundle.getString("story_id",""));
            jsonObject.putOpt("story_title",""+ bundle.getString("story_title",""));
            jsonObject.putOpt("fcm_token",""+ bundle.getString("fcm_token",""));
            jsonObject.putOpt("url_to_open",""+ bundle.getString("url_to_open",""));

            url_to_open = bundle.getString("url_to_open","");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        WittyFeedSDKWebView wittyFeedSDKWebView = new WittyFeedSDKWebView();
        wittyFeedSDKWebView.setUpWebView(url_to_open,web_view,activity);
        build_native_story_GA(jsonObject);

        wittyFeedSDKOneFeedInterface = new WittyFeedSDKOneFeedInterface() {
            @Override
            public void goBackToHostApp() {
                custom_finish_app();
            }
        };
    }

    private void custom_finish_app(){
        if(WittyFeedSDKSingleton.getInstance().homeActivityIntent != null) {
            this.finish();
            this.activity.startActivity(WittyFeedSDKSingleton.getInstance().homeActivityIntent);
        }else {
            finish();
        }
    }

    private void build_native_story_GA(JSONObject jsonObject) {
        String eventCat = "WF Story";
        String eventAction = "";
        String eventLabel = "";
        String fcm_token = "";
        try {
            eventAction = "" + jsonObject.getString("app_id");
            fcm_token = WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKApiClient.getFCM_TOKEN();
            eventLabel = ""
                    + jsonObject.getString("story_id")
                    + " : "
                    + jsonObject.getString("story_title");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        send_GA(fcm_token, eventCat, eventLabel, eventAction, activity);
    }

    private void send_GA(String fcm_token, String eventCat, String eventAction, String eventLabel, Context context) {

        String eventVal = "1";

        try{
            if (WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics == null) {
                WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics = new WittyFeedSDKGoogleAnalytics(context, WittyFeedSDKSingleton.getInstance().getGA_TRACKING_ID(), fcm_token);
            }

            WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics.send_event_tracking_GA_request(eventCat, eventAction, eventVal, eventLabel);
            Log.d(TAG, "For " + eventCat + " " + eventLabel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
