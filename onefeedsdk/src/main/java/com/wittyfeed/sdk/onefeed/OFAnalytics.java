package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.Utils.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 <p><span style="font-size: 13pt;"><strong>Provides an analytics end-point for the whole OneFeed SDK to use</strong></span></p>
 <p>currently utilises Google Analytics Measurement Protocol, read more at <a href="https://developers.google.com/analytics/devguides/collection/protocol/v1/">https://developers.google.com/analytics/devguides/collection/protocol/v1/</a></p>
 <p>Architecture is Singleton with Lazy-loading capability and has 1 Enum for {@link AnalyticsType}</p>
 * @see <a href="https://developers.google.com/analytics/devguides/collection/protocol/v1/">https://developers.google.com/analytics/devguides/collection/protocol/v1/</a>
 */

public final class OFAnalytics {

    private Map<String, String> mainPayload;
    private RequestQueue requestQueue;

    private OFAnalytics() {
    }

    /**
     * @return instance of OFAnalytics
     */
    public static OFAnalytics getInstance() {
        return LazyHolder.ourInstance;
    }

    /**
     * Initialises a {@link RequestQueue} and the  boiler plate payload data for using Google Analytics Measurment Protocol
     * @param applicationContext the application context
     */
    public synchronized void init(Context applicationContext){
        this.requestQueue = Volley.newRequestQueue(applicationContext);

        mainPayload = new HashMap<>();
        mainPayload.put("sdkvr","" + Constant.ONE_FEED_VERSION);
        mainPayload.put("lng", "" + Locale.getDefault().getISO3Language());
        mainPayload.put("cc", "" + Locale.getDefault().getISO3Country());
        mainPayload.put("pckg",  "" + Utils.getPackageName(applicationContext));
        mainPayload.put("device_id", "" + ApiClient.getInstance().getDeviceId());

    }

    /**
     * Receives the category of analytics to send and its label
     * @param typeArg   category type of Analytics
     * @param labelArg label for analytics
     */
    public final synchronized void sendAnalytics(@NonNull AnalyticsType typeArg, @NonNull String labelArg){
        if(labelArg.isEmpty()){
            OFLogger.log(OFLogger.ERROR, "Analytics Label is sent blank");
            labelArg = "null";
        }
        String[] args = labelArg.split(":");
        switch (typeArg) {
            case SDK:
                prepareSDKInitialisedTracking("SDK initialised", ApiClient.getInstance().getAppId());
                break;
            case NotificationReceived:
                prepareNotificationReceivedTracking("notification received", args[0], args[1], ApiClient.getInstance().getAppId());
                break;
            case NotificationOpened:
                prepareNotificationOpenedTracking("notification opened", args[0], args[1], ApiClient.getInstance().getAppId());
                break;
            case Story:
                prepareStoryOpenedTracking("story opened", args[0], args[1], ApiClient.getInstance().getAppId());
                break;
            case Search:
                prepareSearchExecutedTracking("search executed", args[0], ApiClient.getInstance().getAppId());
                break;
            case OneFeed:
                prepareOneFeedViewedTracking("OneFeed viewed", ApiClient.getInstance().getAppId());
                break;
        }
    }

    /**
     *
     * Used when the analytics is to be sent without initializing the OneFeedMain
         <ul style="list-style-type: disc;">
         <li>i.e. when notification is received by Notification Manager when App was in background receives context to create a new requestQueue with, AppID to send, analytics category and label for analytics</li>
         </ul>
     *
     * @param context   context to create newRequestQueue with
     * @param appId     appID to send with analytics
     * @param categoryArg   category type of Analytics
     * @param labelArg label for analytics
     */
    public final synchronized void sendAnalytics(@NonNull Context context, @NonNull String appId, @NonNull AnalyticsType categoryArg, @NonNull String labelArg){
        if(mainPayload == null || requestQueue == null){
            requestQueue = Volley.newRequestQueue(context);
            mainPayload = new HashMap<>();
            mainPayload.put("sdkvr",  "" + Constant.ONE_FEED_VERSION);
            mainPayload.put("lng", "" + Locale.getDefault().getISO3Language());
            mainPayload.put("cc", "" + Locale.getDefault().getISO3Country());
            mainPayload.put("pckg",  "" + Utils.getPackageName(context));

        }

        if(labelArg.isEmpty()){
            OFLogger.log(OFLogger.ERROR, "Analytics Label is sent blank");
            labelArg = "null";
        }

        String[] args = labelArg.split(":");
        switch (categoryArg) {
            case NotificationOpened:
                prepareNotificationOpenedTracking("notification opened", args[0], args[1], appId);
                break;
            case NotificationReceived:
                prepareNotificationReceivedTracking("notification received", args[0], args[1], appId);
                break;
            case Story:
                prepareStoryOpenedTracking("story opened", args[0], args[1], appId);
                break;

        }
    }

    /**
     * prepares payload for Notification Received tracking
     * @param eventType the event category to be passed to Google Analytics
     * @param storyId the id of the story
     * @param notificationId the id of the notification
     * @param appId the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     */
    private void prepareNotificationReceivedTracking(String eventType, String storyId, String notificationId, String appId){
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", ""+ eventType);
        payload.put("appid", ""+ appId);
        payload.put("sid", ""+storyId);
        payload.put("rsrc", "notification");
        payload.put("noid", ""+notificationId);
        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        sendRequest(payload);
    }

    /**
     * prepares payload for Notification Opened tracking
     * @param eventType the event category to be passed to Google Analytics
     * @param storyId the id of the story
     * @param notificationId the id of the notification
     * @param appId the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     */
    private void prepareNotificationOpenedTracking(String eventType, String storyId, String notificationId, String appId){
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", ""+ eventType);
        payload.put("appid", ""+ appId);
        payload.put("sid", ""+storyId);
        payload.put("rsrc", "notification");
        payload.put("noid", ""+notificationId);
        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        sendRequest(payload);
    }

    /**
     * prepares payload for SDK initialised tracking
     * @param eventType the event category to be passed to Google Analytics
     * @param appId the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     */
    private void prepareSDKInitialisedTracking(String eventType, String appId){
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", ""+ eventType);
        payload.put("appid", ""+ appId);
        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        sendRequest(payload);
    }

    /**
     * prepares payload for Story opened tracking
     * @param eventType the event category to be passed to Google Analytics
     * @param storyId the id of the story opened
     * @param source the source of the story opened from
     * @param appId the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     */
    private void prepareStoryOpenedTracking(String eventType, String storyId, String source, String appId){
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", ""+ eventType);
        payload.put("appid", ""+ appId);
        payload.put("sid", ""+storyId);
        payload.put("rsrc", ""+source);
        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        sendRequest(payload);
    }

    /**
     * prepares payload for Search executed tracking in OneFeed
     * @param eventType the event category to be passed to Google Analytics
     * @param searchStr the string which is searched by user
     * @param appId the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     */
    private void prepareSearchExecutedTracking(String eventType, String searchStr, String appId){
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", ""+ eventType);
        payload.put("appid", ""+ appId);
        payload.put("srchstr", ""+ searchStr);
        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        sendRequest(payload);
    }

    /**
     * prepares payload for OneFeed viewed tracking in OneFeed
     * @param eventType the event category to be passed to Google Analytics
     * @param appId the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     */
    private void prepareOneFeedViewedTracking(String eventType, String appId){
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", ""+ eventType);
        payload.put("appid", ""+ appId);
        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        if(OneFeedMain.getInstance().dataStore.getMainFeedData()!=null)
            payload.put("appuid",  "" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig());

        sendRequest(payload);
    }


    private void sendRequest(final Map<String, String> payload){
        final JSONObject jsonBody = new JSONObject(payload);
        StringRequest request = new StringRequest(Request.Method.POST, Constant.AnalyticsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                OFLogger.log(OFLogger.VERBOSE, "response: " + response);
                OFLogger.log(OFLogger.DEBUG, "Tracking Sent: " + response+  payload.get("etype"));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                OFLogger.log(OFLogger.ERROR, "Tracking Error: " + payload.get("etype"), error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return jsonBody.toString() == null ? null : jsonBody.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    OFLogger.log(OFLogger.ERROR, "Unsupported Encoding" + payload.get("etype"), uee);
                    return null;
                }
            }
        };
        if(requestQueue!=null)
            requestQueue.add(request);
    }

    /**
     Enum for types of categories available to send GA event of -
         <ol>
         <li>SDK: when SDK is initialised</li>
         <li>OneFeed: when OneFeed opens in host app</li>
         <li>Search: when search initiated in search feed</li>
         <li>NotificationReceived: when notification is received</li>
         <li>NotificationOpened: when notification is opened</li>
         <li>Story: when a content or story is opened</li>
         </ol>
     */

    public enum AnalyticsType {
        SDK,
        NotificationReceived,
        NotificationOpened,
        Story,
        Search,
        OneFeed,
    }

    /**
     * Lazy loads the instance of OFAnalytics when required <br>
     * i.e. instance creates when the first time getInstance methods is called
     */
    private static class LazyHolder {
        private static final OFAnalytics ourInstance = new OFAnalytics();
    }
}
