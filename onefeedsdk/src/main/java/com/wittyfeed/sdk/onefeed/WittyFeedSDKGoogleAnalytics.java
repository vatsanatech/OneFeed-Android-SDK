package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by aishwarydhare on 23/10/17.
 */

class WittyFeedSDKGoogleAnalytics {

    private final String TAG = "WF_SDK";
    private Map<String, String> main_payload;
    private Map<String, String> screen_payload;
    private Map<String, String> event_payload;
    private String GA_URL = "https://www.google-analytics.com/collect?";
    private RequestQueue requestQueue;

    @Nullable private String CLIENT_UUID = null;

    WittyFeedSDKGoogleAnalytics(Context context, String TRACKING_ID, String CLIENT_FCM_TOKEN){
        this.requestQueue = Volley.newRequestQueue(context);

        try {
            if (CLIENT_UUID == null) {
                CLIENT_UUID = UUID.randomUUID().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Invalid UUID Passed", e);
        }

        try {
            if(CLIENT_FCM_TOKEN.equalsIgnoreCase("")) {
                CLIENT_FCM_TOKEN = CLIENT_UUID;
            }
        } catch (Exception e) {
            Log.e(TAG, "Invalid FCM Token Passed", e);
        }

        main_payload = new HashMap<>();
        main_payload.put("v", "1");
        main_payload.put("tid", ""+TRACKING_ID);
        main_payload.put("ds", "Android SDK");
        main_payload.put("cid", "" + CLIENT_UUID);
        main_payload.put("uid", "" + CLIENT_FCM_TOKEN);
        // TODO: 19/11/17 SDK VERSION UPDATES HERE
        main_payload.put("av", "2.0.0");
        main_payload.put("an", ""+context.getPackageName());
        main_payload.put("aid", ""+context.getPackageName());
    }


    void send_screen_tracking_GA_request(final String screen_name){

        screen_payload = main_payload;
        screen_payload.put("t", "screenview");
        screen_payload.put("cd", screen_name);

        StringRequest postRequest = new StringRequest(Request.Method.POST, GA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "GA Sent: Screen : " + screen_name);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e(TAG, "GA Error: ScreenTrack : ", e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return screen_payload;
            }
        };
        requestQueue.add(postRequest);
    }


    private void send_page_tracking_GA_request(final String title, String event_action){

        event_payload = main_payload;
        event_payload.put("t", "pageview");
        event_payload.put("dh", event_action);
        event_payload.put("dt", title);
        event_payload.put("dp", title);


        StringRequest postRequest = new StringRequest(Request.Method.POST, GA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "GA Sent: pageview : " + title);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e(TAG, "GA Error: PageView : ", e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return event_payload;
            }
        };
        requestQueue.add(postRequest);
    }


    void send_event_tracking_GA_request(final String event_category, final String event_action, final String event_value, final String event_label){

        event_payload = main_payload;
        event_payload.put("t", "event");
        event_payload.put("ec", ""+event_category);
        event_payload.put("ea", ""+event_action);
        event_payload.put("el", ""+event_label);
        event_payload.put("ev", ""+event_value);


        StringRequest postRequest = new StringRequest(Request.Method.POST, GA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "GA Sent: " + event_category + " : " + event_label);
                        if(event_category.equalsIgnoreCase("WF Story")){
                            send_page_tracking_GA_request("WF Story", event_action);
                        } else if(event_category.equalsIgnoreCase("WF SDK") && event_label.startsWith("Opened") ) {
                            send_page_tracking_GA_request("WF Detail Card Activity", event_action);
                        } else if(event_category.equalsIgnoreCase("WF SDK") && event_label.equalsIgnoreCase("WF Waterfall SDK Initialized")){
                            send_page_tracking_GA_request("WF Waterfall", event_action);
                        } else if(event_category.equalsIgnoreCase("WF SDK") && event_label.equalsIgnoreCase("WF SDK initialized")){
                            send_page_tracking_GA_request("WF SDK Initialized", event_action);
                        } else if(event_category.equalsIgnoreCase("WF Notification")){
                            send_page_tracking_GA_request("WF Notification", event_action);
                        } else if(event_category.equalsIgnoreCase("WF OneFeed")){
                            send_page_tracking_GA_request("WF OneFeed", event_action);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e(TAG, "GA Error: EventTrack : ", e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return event_payload;
            }
        };
        requestQueue.add(postRequest);
    }


}
