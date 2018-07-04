package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 <p><span style="font-size: 13pt;"><strong>All web services for OneFeed are written here</strong></span></p>
 */
public final class NetworkServiceManager {

    public int getRepeatingDataOffset() {
        return repeatingDataOffset;
    }

    public void incrementRepeatingDataOffset(){
        repeatingDataOffset++;
    }

    public void resetRepeatingDataOffset(){
        repeatingDataOffset = 0;
    }


    private int repeatingDataOffset = 0;


    /**
     * base url to OneFeed Server
     */
    private final String basePrefix = "https://api.wittyfeed.com";

    /**
     * separate request queue for independent feed updating events <br>
     * works with application context
     */
    private RequestQueue mainFeedRequestQueue;

    /**
     * separate request queue for independent search operations in OneFeed <br>
     * works with activity context
     *
     */
    private RequestQueue searchRequestQueue;

    /**
     * separate request queue for independent setting or un-setting of interests in OneFeed <br>
     * works with activity context
     */
    private RequestQueue interestRequestQueue;

    private RequestQueue repeatingDataRequestQueue;

    /**
     * separate request queue for independent configurations changes such as registering new FCM Token <br>
     * works with application context
     */
    private RequestQueue configRequestQueue;

    /**
     * Initialises new {@link RequestQueue} for MainFeed operations if null
     * @param applicationContext the application context
     */
    public void setMainFeedRequestQueue(Context applicationContext) {
        if(mainFeedRequestQueue != null){
            return;
        }
        this.mainFeedRequestQueue = Volley.newRequestQueue(applicationContext);
        repeatingDataRequestQueue = Volley.newRequestQueue(applicationContext);
    }

    /**
     * Initialises new {@link RequestQueue} for Search operations if null
     * @param localContext activity context
     */
    public void setSearchRequestQueue(Context localContext) {
        if(searchRequestQueue != null){
            searchRequestQueue.cancelAll("SearchFeedRequest");
            return;
        }
        this.searchRequestQueue = Volley.newRequestQueue(localContext);
    }

    /**
     * Initialises new {@link RequestQueue} for Interest fetch, set or unset and other operations if null
     * @param localContext activity context
     */
    public void setInterestRequestQueue(Context localContext) {
        if(interestRequestQueue != null){
            return;
        }
        this.interestRequestQueue = Volley.newRequestQueue(localContext);
    }

    /**
     * Initialises new {@link RequestQueue} for Interest fetch, set or unset and other operations if null
     * @param applicationContext the application context
     */
    public void setConfigRequestQueue(Context applicationContext) {
        if(configRequestQueue != null){
            return;
        }
        this.configRequestQueue = Volley.newRequestQueue(applicationContext);
    }

    /**
     * To request data for main OneFeed from server receives offset and callback through the interface object
     * @param offset integer offset to send in payload, implemented for fetch more
     * @param onNetworkServiceDidRespond callback of {@link OnNetworkServiceDidRespond}
     */
    public void hitMainFeedDataAPI(final int offset, final OnNetworkServiceDidRespond onNetworkServiceDidRespond){
        if(mainFeedRequestQueue == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.MainFeedRequestQueueIsNull);
            return;
        }

        String fcmTokenToSend = OneFeedMain.getInstance().getFcmTokenManager().getCurrentFcmToken();

        OFLogger.log(OFLogger.DEBUG, OFLogger.OffsetCount + offset);

        String url_api = basePrefix + "/Sdk/home_feed_v5";

        url_api += "?";
        url_api += "&unique_identifier=" + ApiClient.getInstance().getUniqueIdentifier();
        url_api += "&offset=" + offset;
        url_api += "&app_id=" + ApiClient.getInstance().getAppId();
        url_api += "&api_key=" + ApiClient.getInstance().getApiKey();
        url_api += "&firebase_token=" + fcmTokenToSend;
        url_api += "&onefeed_sdk_version=" + Constant.ONE_FEED_VERSION;
        url_api += "&device_id=" + ApiClient.getInstance().getDeviceId();
        url_api += "&user_meta=" + ApiClient.getInstance().getUserMeta();
        url_api += "&device_meta=" + ApiClient.getInstance().getDeviceMeta();

        StringRequest request = new StringRequest(Request.Method.GET, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject resultJson = null;
                        try {
                            resultJson = new JSONObject(response);
                            if(resultJson.optBoolean("status")) {
                                onNetworkServiceDidRespond.onSuccessResponse(response);
                            } else {
                                OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "MainFeed");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "MainFeed", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "MainFeed", e);
                        onNetworkServiceDidRespond.onError();
                    }
                }
        );
        mainFeedRequestQueue.add(request);
    }


    public void hitRepeatingDataAPI(final int offset, final OnNetworkServiceDidRespond onNetworkServiceDidRespond, int card_id){
        if(repeatingDataRequestQueue == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.repeatingDataRequestQueueIsNull);
            return;
        }

        String fcmTokenToSend = OneFeedMain.getInstance().getFcmTokenManager().getCurrentFcmToken();

        OFLogger.log(OFLogger.DEBUG, OFLogger.OffsetCount + offset);

        String url_api = basePrefix + "/Sdk/home_feed_v5";

        url_api += "?";
        url_api += "&unique_identifier=" + ApiClient.getInstance().getUniqueIdentifier();
        url_api += "&offset=" + offset;
        url_api += "&app_id=" + ApiClient.getInstance().getAppId();
        url_api += "&api_key=" + ApiClient.getInstance().getApiKey();
        url_api += "&firebase_token=" + fcmTokenToSend;
        url_api += "&onefeed_sdk_version=" + Constant.ONE_FEED_VERSION;
        url_api += "&device_id=" + ApiClient.getInstance().getDeviceId();
        url_api += "&user_meta=" + ApiClient.getInstance().getUserMeta();
        url_api += "&device_meta=" + ApiClient.getInstance().getDeviceMeta();
        url_api += "&repeatingCard=" + 1;
        url_api += "&card_id=" + card_id+"";

        StringRequest request = new StringRequest(Request.Method.GET, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject resultJson = null;
                        try {
                            resultJson = new JSONObject(response);
                            if(resultJson.optBoolean("status")) {
                                onNetworkServiceDidRespond.onSuccessResponse(response);
                            } else {
                                OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "MainFeed");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "MainFeed", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "MainFeed", e);
                        onNetworkServiceDidRespond.onError();
                    }
                }
        );
        repeatingDataRequestQueue.add(request);
    }

    /**
     * To request data for search initiated by user from server receives stringToSearch, offset &amp; callback through interface object
     * @param stringToSearch keyword to search for from OneFeed Content Database, will be passed as payload
     * @param searchLoadmoreOffset integer offset to send in payload, implemented for fetch more
     * @param onNetworkServiceDidRespond callback of {@link OnNetworkServiceDidRespond}
     */
    public void hitSearchFeedDataAPI(String stringToSearch, int searchLoadmoreOffset, final OnNetworkServiceDidRespond onNetworkServiceDidRespond) {
        if(searchRequestQueue == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.SearchFeedRequestQueueIsNull);
            return;
        }

        String url_api = basePrefix + "/Sdk/search_v5";

        url_api += "?";
        url_api += "&keyword=" + stringToSearch;
        url_api += "&offset=" + (searchLoadmoreOffset*10);
        url_api += "&user_id=" + OneFeedMain.getInstance().dataStore.getUserIdFromConfig();
        url_api += "&app_id=" + ApiClient.getInstance().getAppId();

        StringRequest request = new StringRequest(Request.Method.GET, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject resultJson = null;
                        try {
                            OFLogger.log(OFLogger.VERBOSE, "Search Response: " + response);
                            resultJson = new JSONObject(response);
                            if(resultJson.optBoolean("status")) {
                                onNetworkServiceDidRespond.onSuccessResponse(response);
                            } else {
                                OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "Search");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "Search", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "Search", e);
                        onNetworkServiceDidRespond.onError();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };
        searchRequestQueue.cancelAll("SearchFeedRequest");
        searchRequestQueue.add(request).setTag("SearchFeedRequest");
    }

    /**
     * To request data for fetching available to select interest to server receives callback through interface object
     * @param onNetworkServiceDidRespond callback of {@link OnNetworkServiceDidRespond}
     */
    public void hitGetInterestsAPI(final OnNetworkServiceDidRespond onNetworkServiceDidRespond) {
        if(interestRequestQueue == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.InterestRequestQueueIsNull);
            return;
        }
        String url_api = basePrefix + "/Sdk/getCategories_v5";

        url_api += "?";
        url_api += "app_id=" + ApiClient.getInstance().getAppId();
        url_api += "&device_id=" + ApiClient.getInstance().getDeviceId();

        StringRequest request = new StringRequest(Request.Method.GET, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject resultJson = null;
                        try {
                            OFLogger.log(OFLogger.VERBOSE, "Fetch Interests Response: " + response);
                            resultJson = new JSONObject(response);
                            if(resultJson.optBoolean("status")) {
                                onNetworkServiceDidRespond.onSuccessResponse(response);
                            } else {
                                OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "Interests");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "Interests", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "Interests", e);
                        onNetworkServiceDidRespond.onError();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };
        interestRequestQueue.cancelAll("getInterest");
        interestRequestQueue.add(request).setTag("getInterest");
    }

    /**
     * To request for setting the interest as selected by user to server receives ID of interest selected, boolean for isSelected and callback through interface object
     * @param interestId id of the interest selected or un-selected
     * @param isSelected selected status of interest, if true then 1 will be send in payload, else 0
     * @param onNetworkServiceDidRespond callback of {@link OnNetworkServiceDidRespond}
     */
    public void hitSetInterestSelectionAPI(final String interestId, final boolean isSelected, final OnNetworkServiceDidRespond onNetworkServiceDidRespond){
        if(interestRequestQueue == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.InterestRequestQueueIsNull);
            return;
        }
        String url_api = basePrefix + "/Sdk/saveUserInterest";

        StringRequest request = new StringRequest(Request.Method.POST, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            OFLogger.log(OFLogger.VERBOSE, "Save Interest Response: " + response);
                            JSONObject resultJson = new JSONObject(response);
                            if(resultJson.optBoolean("status")) {
                                onNetworkServiceDidRespond.onSuccessResponse(response);
                            } else {
                                OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "SaveInterest");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            // if error received
                            OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "SaveInterest", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "SaveInterest", e);
                        onNetworkServiceDidRespond.onError();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> payload = new HashMap<>();
                payload.put("app_id", ApiClient.getInstance().getAppId());
                payload.put("selected_cat_id", ""+interestId);

                int isInterestSelected = 0;
                if(isSelected) {
                    isInterestSelected = 1;
                }
                payload.put("is_selected", ""+isInterestSelected);
                payload.put("device_id", "" + ApiClient.getInstance().getDeviceId());
                return payload;
            }
        };
        interestRequestQueue.add(request).setTag("saveInterest");
    }

    /**
     * To request for setting the Fcm Token to server
     */
    public void hitUpdateFcmTokenAPI() {
        if(configRequestQueue == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.ConfigRequestQueueIsNull);
            return;
        }
        String url_api = basePrefix + "/Sdk/fetchFcmToken";

        final String fcmTokenToSend = OneFeedMain.getInstance().getFcmTokenManager().getCurrentFcmToken();
        final String oldFcmToken = OneFeedMain.getInstance().getFcmTokenManager().getOldFcmToken();

        OFLogger.log(OFLogger.DEBUG, "old token: " + oldFcmToken);
        OFLogger.log(OFLogger.DEBUG, "new token: " + fcmTokenToSend);

        StringRequest request = new StringRequest(Request.Method.POST, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            OFLogger.log(OFLogger.VERBOSE, "Update FCM Response: " + response);
                            JSONObject result = new JSONObject(response);
                            if(result.optBoolean("status")) {
                                OneFeedMain.getInstance().getFcmTokenManager().setSavedFcmToken(fcmTokenToSend);
                                OFLogger.log(OFLogger.DEBUG, OFLogger.ForceUpdatedFCMToken);
                            } else {
                                OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "FCMTokenUpdate");
                            }
                        } catch (JSONException e) {
                            // if error received
                            OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "FCMTokenUpdate", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchData + "FCMTokenUpdate", e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> payload = new HashMap<>();

                payload.put("app_id", ApiClient.getInstance().getAppId());
                payload.put("firebase_token", "" + fcmTokenToSend);
                payload.put("old_firebase_token", "" + oldFcmToken);
                payload.put("onefeed_sdk_version", ""+ Constant.ONE_FEED_VERSION);
                payload.put("device_id", ""+ApiClient.getInstance().getDeviceId());
                return payload;
            }
        };
        configRequestQueue.add(request);
    }

    /**
     * Interface used as Callback which notifies about the success or failure of web service request<br>
     */
    public interface OnNetworkServiceDidRespond {
        /**
         * On successful web service request
         * @param response raw response as received by request from server
         */
        void onSuccessResponse(String response);

        /**
         * On web service request failure
         */
        void onError();
    }

}
