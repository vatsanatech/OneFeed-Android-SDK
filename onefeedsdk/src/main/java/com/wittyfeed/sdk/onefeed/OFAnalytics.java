package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.Utils.OFSharedPreference;
import com.wittyfeed.sdk.onefeed.Utils.Utils;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * <p><span style="font-size: 13pt;"><strong>Provides an analytics end-point for the whole OneFeed SDK to use</strong></span></p>
 * <p>currently utilises Google Analytics Measurement Protocol, read more at <a href="https://developers.google.com/analytics/devguides/collection/protocol/v1/">https://developers.google.com/analytics/devguides/collection/protocol/v1/</a></p>
 * <p>Architecture is Singleton with Lazy-loading capability and has 1 Enum for {@link AnalyticsType}</p>
 *
 * @see <a href="https://developers.google.com/analytics/devguides/collection/protocol/v1/">https://developers.google.com/analytics/devguides/collection/protocol/v1/</a>
 */

public final class OFAnalytics {

    private static OFAnalytics ourInstance;
    private Map<String, String> mainPayload;
    private RequestQueue requestQueue;

    private OFAnalytics() {
    }

    /**
     * @return instance of OFAnalytics
     */
    public static OFAnalytics getInstance() {

        //Changed by yogesh soni
        if (ourInstance == null) {
            ourInstance = new OFAnalytics();
        }
        return ourInstance;
    }

    /**
     * Initialises a {@link RequestQueue} and the  boiler plate payload data for using Google Analytics Measurment Protocol
     *
     * @param applicationContext the application context
     */
    public synchronized void init(Context applicationContext) {
        this.requestQueue = Volley.newRequestQueue(applicationContext);

        mainPayload = new HashMap<>();
        mainPayload.put("sdkvr", "" + Constant.ONE_FEED_VERSION);
        mainPayload.put("lng", "" + Locale.getDefault().getISO3Language());
       // mainPayload.put("cc", "" + Locale.getDefault().getISO3Country());
        mainPayload.put("pckg", "" + Utils.getPackageName(applicationContext));
        mainPayload.put("device_id", "" + Utils.getAndroidId(applicationContext));
        mainPayload.put("ntype", Utils.getNetworkConnectionType(applicationContext));
    }

    /**
     * Receives the category of analytics to send and its label
     *
     * @param typeArg  category type of Analytics
     * @param labelArg label for analytics
     */
    public final synchronized void sendAnalytics(Context context, @NonNull AnalyticsType typeArg, @NonNull String labelArg) {
        if (labelArg.isEmpty()) {
            OFLogger.log(OFLogger.ERROR, "Analytics Label is sent blank");
            labelArg = "null";
        }
        String[] args = labelArg.split(":");

        OFSharedPreference preference = new OFSharedPreference(context);
        String userId = preference.getUserId();

        switch (typeArg) {
            case SDK:
                prepareSDKInitialisedTracking("SDK Initialised", ApiClient.getInstance().getAppId(), userId);
                break;
            case NotificationReceived:
                prepareNotificationReceivedTracking(context, "Notification Received", args[0], args[1], ApiClient.getInstance().getAppId(), userId);
                break;
            case NotificationOpened:
                prepareNotificationOpenedTracking(context, "Story Opened", args[0], args[1], ApiClient.getInstance().getAppId(), userId, userId);
                break;
            case Story:
                prepareStoryOpenedTracking(context, "Story Opened", args[0], args[1], ApiClient.getInstance().getAppId(), userId);
                break;
            case Search:
                prepareSearchExecutedTracking(context, "Search Executed", args[0], ApiClient.getInstance().getAppId(), userId);
                break;
            case OneFeed:
                prepareOneFeedViewedTracking(context, "OneFeed Viewed", args[0], ApiClient.getInstance().getAppId(), userId);
                break;
            case CARD:
                prepareCardViewTracking(context, "Card Viewed", args[0], args[1], ApiClient.getInstance().getAppId(), userId);
                break;
            case PowerIn:
                //prepareOneFeedViewedTracking(context, "OneFeed PlugIn", ApiClient.getInstance().getAppId());
                break;
            case PowerOut:
                //prepareOneFeedViewedTracking(context, "OneFeed PlugOut", ApiClient.getInstance().getAppId());
                break;
        }
    }

    /**
     * Used when the analytics is to be sent without initializing the OneFeedMain
     * <ul style="list-style-type: disc;">
     * <li>i.e. when notification is received by Notification Manager when App was in background receives context to create a new requestQueue with, AppID to send, analytics category and label for analytics</li>
     * </ul>
     *
     * @param context     context to create newRequestQueue with
     * @param appId       appID to send with analytics
     * @param categoryArg category type of Analytics
     * @param labelArg    label for analytics
     */
    public final synchronized void sendAnalytics(@NonNull Context context, @NonNull String appId, @NonNull AnalyticsType categoryArg, @NonNull String labelArg) {
        if (mainPayload == null || requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
            mainPayload = new HashMap<>();
            mainPayload.put("sdkvr", "" + Constant.ONE_FEED_VERSION);
            mainPayload.put("lng", "" + Locale.getDefault().getISO3Language());
          //  mainPayload.put("cc", "" + Locale.getDefault().getISO3Country());
            mainPayload.put("pckg", "" + Utils.getPackageName(context));
            mainPayload.put("ntype", Utils.getNetworkConnectionType(context));

        }

        if (labelArg.isEmpty()) {
            OFLogger.log(OFLogger.ERROR, "Analytics Label is sent blank");
            labelArg = "null";
        }

        OFSharedPreference preference = new OFSharedPreference(context);
        String userId = preference.getUserId();

        String[] args = labelArg.split(":");
        String noId= "0";
        try{
            noId  = args[1];
        }catch (Exception e){
            noId = "0";
        }
        switch (categoryArg) {
            case NotificationOpened:
                prepareNotificationOpenedTracking(context, "Notification Opened", "Story Opened", args[0], noId, appId, userId);
                break;
            case NotificationReceived:
                prepareNotificationReceivedTracking(context, "Notification Received", args[0], noId, appId, userId);
                break;
            case Story:
                //Change by Yogesh
                prepareStoryOpenedTracking(context, "Story Opened", args[0], "Notification", appId, userId);
                break;

        }
    }

    /**
     * prepares payload for Notification Received tracking
     *
     * @param context
     * @param eventType      the event category to be passed to Google Analytics
     * @param storyId        the id of the story
     * @param notificationId the id of the notification
     * @param appId          the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     * @param userId
     */
    private void prepareNotificationReceivedTracking(Context context, String eventType, String storyId, String notificationId, String appId, String userId) {
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", "" + eventType);
        payload.put("appid", "" + appId);
        payload.put("sid", "" + storyId);
        payload.put("rsrc", "Notification");
        payload.put("noid", "" + notificationId);
        payload.put("device_id", "" + Utils.getAndroidId(context));
        //Changes by yogesh soni
        payload.put("appuid", "" + userId);

        sendRequest(payload);
    }

    /**
     * prepares payload for Notification Opened tracking
     *  @param notification_opened
     * @param eventType           the event category to be passed to Google Analytics
     * @param storyId             the id of the story
     * @param notificationId      the id of the notification
     * @param appId               the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     * @param userId
     */
    private void prepareNotificationOpenedTracking(Context context, String notification_opened, String eventType, String storyId, String notificationId, String appId, String userId) {
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", "" + eventType);
        payload.put("appid", "" + appId);
        payload.put("sid", "" + storyId);
        payload.put("rsrc", "Notification");
        payload.put("noid", "" + notificationId);
        payload.put("device_id", "" + Utils.getAndroidId(context));
        //Changes by yogesh soni
        payload.put("appuid", "" + userId);

        sendRequest(payload);
    }

    /**
     * prepares payload for SDK initialised tracking
     *
     * @param eventType
     * @param appId       the event category to be passed to Google Analytics
     * @param userId           the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     */
    private void prepareSDKInitialisedTracking(String eventType, String appId, String userId) {
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", "" + eventType);
        payload.put("appid", "" + appId);
        payload.put("rsrc", "App-Init");
//        if(OneFeedMain.getInstance().getInstanceDataStore().getMainFeedData()!=null)
//            payload.put("appuid",  "" + OneFeedMain.getInstance().getInstanceDataStore().getUserIdFromConfig());

        payload.put("appuid", userId);
        sendRequest(payload);
    }

    /**
     * prepares payload for Story opened tracking
     *
     * @param context
     * @param eventType the event category to be passed to Google Analytics
     * @param storyId   the id of the story opened
     * @param source    the source of the story opened from
     * @param appId     the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     * @param userId
     */
    private void prepareStoryOpenedTracking(Context context, String eventType, String storyId, String source, String appId, String userId) {
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", "" + eventType);
        payload.put("appid", "" + appId);
        payload.put("sid", "" + storyId);
        payload.put("rsrc", "" + source);
        payload.put("device_id", "" + Utils.getAndroidId(context));
        //Changes by yogesh soni
        payload.put("appuid", "" + userId);

        sendRequest(payload);
    }

    /**
     * prepares payload for Story opened tracking
     *
     * @param context
     * @param eventType the event category to be passed to Google Analytics
     * @param storyId   the id of the story opened
     * @param source    the source of the story opened from
     * @param appId     the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     * @param userId
     */
    private void prepareCardViewTracking(Context context, String eventType, String storyId, String source, String appId, String userId) {
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", "" + eventType);
        payload.put("appid", "" + appId);
        payload.put("sid", "" + storyId);
        payload.put("rsrc", "" + source);
        payload.put("device_id", "" + Utils.getAndroidId(context));
        //Changes by yogesh soni
        payload.put("appuid", "" + userId);

        sendRequest(payload);
    }

    /**
     * prepares payload for Search executed tracking in OneFeed
     *
     * @param eventType the event category to be passed to Google Analytics
     * @param searchStr the string which is searched by user
     * @param appId     the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     * @param userId
     */
    private void prepareSearchExecutedTracking(Context context, String eventType, String searchStr, String appId, String userId) {
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", "" + eventType);
        payload.put("appid", "" + appId);
        payload.put("srchstr", "" + searchStr);
        payload.put("rsrc", "OneFeed");
        payload.put("device_id", "" + Utils.getAndroidId(context));
        //Changes by yogesh soni
        payload.put("appuid", "" + userId);

        sendRequest(payload);
    }

    /**
     * prepares payload for OneFeed viewed tracking in OneFeed
     *
     * @param eventType the event category to be passed to Google Analytics
     * @param appId     the App_ID as per registration on OneFeed Dashboard, will be used as Event Action on Google Analytics
     * @param userId
     */
    private void prepareOneFeedViewedTracking(Context context, String eventType, String ref, String appId, String userId) {
        final Map<String, String> payload = new HashMap<>(mainPayload);
        payload.put("etype", "" + eventType);
        payload.put("appid", "" + appId);
        payload.put("device_id", "" + Utils.getAndroidId(context));
        payload.put("rsrc", ref);
        //Changes by yogesh soni
        payload.put("appuid", "" + userId);

        sendRequest(payload);
    }


    private void sendRequest(final Map<String, String> payload) {
        final JSONObject jsonBody = new JSONObject(payload);
        StringRequest request = new StringRequest(Request.Method.POST, Constant.AnalyticsURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                OFLogger.log(OFLogger.VERBOSE, "response: " + response);
                OFLogger.log(OFLogger.DEBUG, "Tracking Sent: " + response + payload.get("etype"));
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
        if (requestQueue != null)
            requestQueue.add(request);
    }


    /**
     * Enum for types of categories available to send GA event of -
     * <ol>
     * <li>SDK: when SDK is initialised</li>
     * <li>OneFeed: when OneFeed opens in host app</li>
     * <li>Search: when search initiated in search feed</li>
     * <li>NotificationReceived: when notification is received</li>
     * <li>NotificationOpened: when notification is opened</li>
     * <li>Story: when a content or story is opened</li>
     * </ol>
     */

    public enum AnalyticsType {
        SDK,
        NotificationReceived,
        NotificationOpened,
        Story,
        Search,
        OneFeed,
        CARD,
        PowerIn,
        PowerOut,
    }

    /**
     * Lazy loads the instance of OFAnalytics when required <br>
     * i.e. instance creates when the first time getInstance methods is called
     */
    private static class LazyHolder {
        private static final OFAnalytics ourInstance = new OFAnalytics();
    }
}
