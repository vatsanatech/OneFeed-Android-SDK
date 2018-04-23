package com.wittyfeed.sdk.onefeed;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * All web services for OneFeed are written here
 *
 * has 1 Interface: OnNetworkServiceDidRespond which has 2 method declarations
 *      1) onSuccess
 *      2) onError
 *
 * has 4 Volley RequestQueues, distinguished with different contexts in each
 *      1) mainFeedRequestQueue - receives application context,
 *          hence feed updation events are promised and independent
 *      2) searchRequestQueue - receives context of the activity,
 *          hence cancels and destroys with it
 *      3) interestRequestQueue - receives context of the activity,
 *          hence cancels and destroys with it, and also works independently of other requestQueues
 *      4) configRequestQueue - receives application context,
 *          hence all config requests are promised and independent
 *
 * has 5 WebService APIs
 *      1) hitMainFeedAPI: to request data for main OneFeed from server
 *          receives offset and callback through the interface object
 *      2) hitSearchFeedDataAPI: to request data for search initiated by user from server
 *          receives stringToSearch, offset & callback through interface object
 *      3) hitGetInterestAPI: to request data for fetching available to select interest to server
 *          receives callback through interface object
 *      4) hitSetInterestSelectionAPI: to request for setting the interest as selected by user to server
 *          receives ID of interest selected, boolean for isSelected and callback through interface object
 *      5) hitUpdateFcmTokenAPI: to request for setting the Fcm Token to server
 *
 */
final class NetworkServiceManager {

    private final String basePrefix = "https://api.wittyfeed.com";
    private RequestQueue mainFeedRequestQueue;
    private RequestQueue searchRequestQueue;
    private RequestQueue interestRequestQueue;
    private RequestQueue configRequestQueue;

    void setMainFeedRequestQueue(Context applicationContext) {
        if(mainFeedRequestQueue != null){
            return;
        }
        this.mainFeedRequestQueue = Volley.newRequestQueue(applicationContext);
    }

    void setSearchRequestQueue(Context localContext) {
        if(searchRequestQueue != null){
            searchRequestQueue.cancelAll("SearchFeedRequest");
            return;
        }
        this.searchRequestQueue = Volley.newRequestQueue(localContext);
    }

    void setInterestRequestQueue(Context localContext) {
        if(interestRequestQueue != null){
            return;
        }
        this.interestRequestQueue = Volley.newRequestQueue(localContext);
    }

    void setConfigRequestQueue(Context applicationContext) {
        if(configRequestQueue != null){
            return;
        }
        this.configRequestQueue = Volley.newRequestQueue(applicationContext);
    }

    void hitMainFeedDataAPI(final int offset, final OnNetworkServiceDidRespond onNetworkServiceDidRespond){
        if(mainFeedRequestQueue == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.MainFeedRequestQueueIsNull);
            return;
        }

        OFLogger.log(OFLogger.DEBUG, OFLogger.OffsetCount + offset);

        String url_api = basePrefix + "/Sdk/home_feed_v5";

        url_api += "?";
        url_api += "&unique_identifier=" + ApiClient.getInstance().getUniqueIdentifier();
        url_api += "&offset=" + offset;
        url_api += "&app_id=" + ApiClient.getInstance().getAppId();
        url_api += "&api_key=" + ApiClient.getInstance().getApiKey();
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
                                OFLogger.log(OFLogger.ERROR, "Fetch Main Feed Data Failed");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            OFLogger.log(OFLogger.ERROR, "Fetch Main Feed Data Failed", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, OFLogger.MainFeedFetchError, e);
                        onNetworkServiceDidRespond.onError();
                    }
                }
        );
        mainFeedRequestQueue.add(request);
    }

    void hitSearchFeedDataAPI(String stringToSearch, int searchLoadmoreOffset, final OnNetworkServiceDidRespond onNetworkServiceDidRespond) {
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
                                OFLogger.log(OFLogger.ERROR, "Search Failed");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            OFLogger.log(OFLogger.ERROR, "Search Failed", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, "Search Feed Data Request Failed", e);
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

    void hitGetInterestsAPI(final OnNetworkServiceDidRespond onNetworkServiceDidRespond) {
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
                                OFLogger.log(OFLogger.ERROR, "Fetch Interests Failed");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            OFLogger.log(OFLogger.ERROR, "Fetch Interests Failed", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        OFLogger.log(OFLogger.ERROR, "Fetch Interests Failed", e);
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

    void hitSetInterestSelectionAPI(final String interestId, final boolean isSelected, final OnNetworkServiceDidRespond onNetworkServiceDidRespond){
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
                                OFLogger.log(OFLogger.ERROR, "Save Interest Failed ");
                                onNetworkServiceDidRespond.onError();
                            }
                        } catch (JSONException e) {
                            // if error received
                            OFLogger.log(OFLogger.ERROR, "Save Interest Failed ", e);
                            onNetworkServiceDidRespond.onError();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        OFLogger.log(OFLogger.ERROR, "Save Interest Failed ", e);
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

    void hitUpdateFcmTokenAPI() {
        if(configRequestQueue == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.ConfigRequestQueueIsNull);
            return;
        }
        String url_api = basePrefix + "/Sdk/updateToken";

        final String fcmTokenToSend = OneFeedMain.getInstance().getFcmTokenManager().getCurrentFcmToken();
        final String oldFcmToken = OneFeedMain.getInstance().getFcmTokenManager().getOldFcmToken();

        OFLogger.log(OFLogger.DEBUG, "old token: " + oldFcmToken);
        OFLogger.log(OFLogger.DEBUG, "new token: " + fcmTokenToSend);

        StringRequest request = new StringRequest(Request.Method.POST, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            OFLogger.log(OFLogger.VERBOSE, "Update FCM Resposne: " + response);
                            JSONObject result = new JSONObject(response);
                            if(result.optBoolean("status")) {
                                OneFeedMain.getInstance().getFcmTokenManager().setSavedFcmToken(fcmTokenToSend);
                                OFLogger.log(OFLogger.DEBUG, "Force Updated FCM Token");
                            } else {
                                OFLogger.log(OFLogger.ERROR, "Force Update FCM Token Failed");
                            }
                        } catch (JSONException e) {
                            // if error received
                            OFLogger.log(OFLogger.ERROR, "Force Update FCM Token Failed", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        OFLogger.log(OFLogger.ERROR, "Force Update FCM Token Failed", e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> payload = new HashMap<>();

                payload.put("app_id", ApiClient.getInstance().getAppId());
                payload.put("firebase_token", "" + fcmTokenToSend);
                payload.put("old_firebase_token", "" + oldFcmToken);
                payload.put("device_meta", "" + ApiClient.getInstance().getDeviceMeta());
                return payload;
            }
        };
        configRequestQueue.add(request);
    }

    interface OnNetworkServiceDidRespond {
        void onSuccessResponse(String response);
        void onError();
    }

}
