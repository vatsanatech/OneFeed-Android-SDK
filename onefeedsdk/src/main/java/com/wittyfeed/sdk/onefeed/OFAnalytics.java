package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Provides an analytics end-point for the whole OneFeed SDK to use
 *  currently utilises Google Analytics Measurment Protocol
 *  read more: https://developers.google.com/analytics/devguides/collection/protocol/v1/
 *
 * Architecture is Singleton with Lazy-loading capability
 *
 * has 1 Enum for AnalyticsCategory with options:
 *      1) WF_SDK: when SDK is initialised
 *      2) WF_OneFeed: when OneFeed opens in host app
 *      3) WF_Search: when search initiated in search feed
 *      4) WF_Notification: when notification is received or opened
 *      5) WF_Story: when a content or story is opened
 *
 *
 * has the following methods-
 *      1) Send Analytics
 *          (i) sendAnalytics: receives the category of analytics to send and its label
 *         (ii) sendAnalytics: to be used when the analytics is to be sent without
 *                  initializing the OneFeedMain i.e. when notification is received by
 *                  Notification Manager when App was in background
 *                  receives context to create a new requestQueue with,
 *                  AppID to send, analytics category and label for analytics
 *      2) send Event Tracking: send event tracking measurement to Google Analytics
 *      3) send Page Tracking: send page tracking measurement to Google Analytics
 *      4) send screen Tracking: send screen tracking measurement to Google Analytics
 *
 */

final class OFAnalytics {

    private final String GA_URL = "https://www.google-analytics.com/collect?";
    private Map<String, String> main_payload;
    private RequestQueue requestQueue;

    private OFAnalytics() {
    }

    static OFAnalytics getInstance() {
        return LazyHolder.ourInstance;
    }

    synchronized void init(Context applicationContext){
        this.requestQueue = Volley.newRequestQueue(applicationContext);

        main_payload = new HashMap<>();
        main_payload.put("v",   "1");
        main_payload.put("tid", "" + Constant.ANALYTICS_TRACKING_ID);
        main_payload.put("ds",  "Android SDK");
        main_payload.put("cid", "" + Constant.getAndroidId(applicationContext));
        main_payload.put("uid", "" + Constant.getAndroidId(applicationContext));
        main_payload.put("av",  "" + Constant.ONE_FEED_VERSION);
        main_payload.put("an",  "" + Constant.getPackageName(applicationContext));
        main_payload.put("aid", "" + Constant.getPackageName(applicationContext));
    }

    /**
     *
     * @param categoryArg   category type of Analytics
     * @param eventLabelArg label for analytics
     */
    final synchronized void sendAnalytics(@NonNull AnalyticsCat categoryArg, @NonNull String eventLabelArg){
        if(eventLabelArg.equalsIgnoreCase("")){
            OFLogger.log(OFLogger.ERROR, "Analytics Label is sent black");
            eventLabelArg = "null";
        }
        switch (categoryArg) {
            case WF_SDK:
                sendEventTrackingRequest("WF SDK", eventLabelArg, ApiClient.getInstance().getAppId());
                break;
            case WF_Notification:
                sendEventTrackingRequest("WF Notification", eventLabelArg, ApiClient.getInstance().getAppId());
                break;
            case WF_Story:
                sendEventTrackingRequest("WF Story", eventLabelArg, ApiClient.getInstance().getAppId());
                break;
            case WF_Search:
                sendEventTrackingRequest("WF Search", eventLabelArg, ApiClient.getInstance().getAppId());
                break;
            case WF_OneFeed:
                sendEventTrackingRequest("WF OneFeed", eventLabelArg, ApiClient.getInstance().getAppId());
                break;
        }
    }

    /**
     *
     * Used when the analytics is to be sent without initializing the OneFeedMain
     *  i.e. when notification is received by Notification Manager when App was in background
     *
     * @param context   context to create newRequestQueue with
     * @param appId     appID to send with analytics
     * @param categoryArg   category type of Analytics
     * @param eventLabelArg label for analytics
     */
    final synchronized void sendAnalytics(@NonNull Context context, @NonNull String appId, @NonNull AnalyticsCat categoryArg, @NonNull String eventLabelArg){
        if(main_payload == null || requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
            main_payload = new HashMap<>();
            main_payload.put("v",   "1");
            main_payload.put("tid", "" +Constant.ANALYTICS_TRACKING_ID);
            main_payload.put("ds",  "Android SDK");
            main_payload.put("cid", "" + Constant.getAndroidId(context));
            main_payload.put("uid", "" + Constant.getAndroidId(context));
            main_payload.put("av",  "" + Constant.ONE_FEED_VERSION);
            main_payload.put("an",  "" + Constant.getPackageName(context));
            main_payload.put("aid", "" + Constant.getPackageName(context));
        }

        if(eventLabelArg.equalsIgnoreCase("")){
            OFLogger.log(OFLogger.ERROR, "Analytics Label is sent blank");
            eventLabelArg = "null";
        }

        switch (categoryArg) {
            case WF_SDK:
                sendEventTrackingRequest("WF SDK", eventLabelArg, appId);
                break;
            case WF_Notification:
                sendEventTrackingRequest("WF Notification", eventLabelArg, appId);
                break;
            case WF_Story:
                sendEventTrackingRequest("WF Story", eventLabelArg, appId);
                break;
            case WF_Search:
                sendEventTrackingRequest("WF Search", eventLabelArg, appId);
                break;
            case WF_OneFeed:
                sendEventTrackingRequest("WF OneFeed", eventLabelArg, appId);
                break;
        }
    }

    private void sendScreenTrackingRequest(final String screenName){
        final Map<String, String> screen_payload = new HashMap<>(main_payload);
        screen_payload.put("t", "screenview");
        screen_payload.put("cd", screenName);

        StringRequest request = new StringRequest(Request.Method.POST, GA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        OFLogger.log(OFLogger.DEBUG, "GA Sent: Screen : " + screenName);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, "GA Error: ScreenTrack : ", e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return screen_payload;
            }
        };
        requestQueue.add(request);
    }

    private void sendPageTrackingRequest(final String title, final String appId){
        final Map<String, String> event_payload = new HashMap<>(main_payload);
        event_payload.put("t", "pageview");
        event_payload.put("dh", appId);
        event_payload.put("dt", title);
        event_payload.put("dp", title);


        StringRequest request = new StringRequest(Request.Method.POST, GA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        OFLogger.log(OFLogger.DEBUG, "GA Sent: pageview : " + title);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, "GA Error: PageView : ", e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return event_payload;
            }
        };
        requestQueue.add(request);
    }

    private void sendEventTrackingRequest(final String eventCategory, final String eventLabel, final String appId){
        final Map<String, String> event_payload = new HashMap<>(main_payload);
        event_payload.put("t", "event");
        event_payload.put("ec", ""+ eventCategory);
        event_payload.put("ea", ""+ appId);
        event_payload.put("el", ""+eventLabel);
        event_payload.put("ev", "1");


        StringRequest request = new StringRequest(Request.Method.POST, GA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        OFLogger.log(OFLogger.DEBUG, "GA Sent: " + eventCategory + " : " + eventLabel);
                        sendScreenTrackingRequest(eventCategory);
                        sendPageTrackingRequest(eventLabel, appId);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, "GA Error: EventTrack : ", e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return event_payload;
            }
        };
        requestQueue.add(request);
    }

    enum AnalyticsCat {
        WF_SDK,
        WF_Notification,
        WF_Story,
        WF_Search,
        WF_OneFeed,
    }

    private static class LazyHolder {
        private static final OFAnalytics ourInstance = new OFAnalytics();
    }
}
