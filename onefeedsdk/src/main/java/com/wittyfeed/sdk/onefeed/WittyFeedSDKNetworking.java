package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

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
 * Created by aishwarydhare on 16/10/17.
 */

class WittyFeedSDKNetworking {

    private static final String TAG = "WF_SDK";
    private WittyFeedSDKApiClient wittyFeedSDKApiClient;
    private Context activity;
    private RequestQueue search_requestQueue;
    private RequestQueue interest_requestQueue;
    private final String base_prefix = "https://api.wittyfeed.com";


    WittyFeedSDKNetworking(Context activity, WittyFeedSDKApiClient para_wittyFeedSDKApiClient){
        this.activity= activity;
        this.wittyFeedSDKApiClient = para_wittyFeedSDKApiClient;
        search_requestQueue = Volley.newRequestQueue(activity);
        interest_requestQueue = Volley.newRequestQueue(activity);
    }


    void getStoryFeedData(final boolean isLoadedMore, final WittyFeedSDKNetworkInterface callback, final int loadmore_offset, final boolean isBackgroundCacheRefresh) {
        hitApiToVerifyCredentials_and_FetchData(callback, isLoadedMore, loadmore_offset, isBackgroundCacheRefresh);
    }


    private void hitApiToVerifyCredentials_and_FetchData(final WittyFeedSDKNetworkInterface callback, final boolean isLoadedMore, final int loadmore_offset, final boolean isBackgroundCacheRefresh) {

        String url_api = base_prefix + "/Sdk/home_feed_v5";

        final WittyFeedSDKApiClient final_wittyFeedSDKApiClient = this.wittyFeedSDKApiClient;

        url_api += "?";
        url_api += "&unique_identifier=" + final_wittyFeedSDKApiClient.getPACKAGE_NAME();
        if(isLoadedMore)
            url_api += "&offset=" + loadmore_offset;
        url_api += "&api_key=" + final_wittyFeedSDKApiClient.getAPI_KEY();
        url_api += "&app_id=" + final_wittyFeedSDKApiClient.getAPP_ID();
        url_api += "&user_meta=" + final_wittyFeedSDKApiClient.getUser_meta();




        StringRequest postRequest = new StringRequest(Request.Method.GET, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);

                            if(result.optBoolean("status")) {
                                String feed_loader_iv_url = result.optString("feed_loader_iv_url");
                                int feed_loader_threshold = result.optInt("feed_loader_threshold");
                                boolean is_load_cache_else_network = result.optBoolean("is_load_cache_else_network", true);

                                if (!final_wittyFeedSDKApiClient.getFCM_TOKEN().equalsIgnoreCase("")) {
                                    WittyFeedSDKSingleton.getInstance().editor_sharedPref.putString("wf_saved_fcm_token", final_wittyFeedSDKApiClient.getFCM_TOKEN()).commit();
                                }

                                if(is_load_cache_else_network){
                                    try {
                                        WittyFeedSDKSingleton.getInstance().is_load_cache_else_network = is_load_cache_else_network;
                                        WittyFeedSDKSingleton.getInstance().editor_sharedPref.putBoolean("is_load_cache_else_network", WittyFeedSDKSingleton.getInstance().is_load_cache_else_network).commit();
                                        Log.d(TAG, "is_load_cache_else_network: " + is_load_cache_else_network);
                                    } catch (Exception e) {
                                        Log.d(TAG, "unable to parse is_load_cache_else_network");
                                    }
                                }
                                if(!feed_loader_iv_url.equalsIgnoreCase("")){
                                    try {
                                        WittyFeedSDKSingleton.getInstance().loader_iv_url = feed_loader_iv_url;
                                        WittyFeedSDKSingleton.getInstance().editor_sharedPref.putString("loader_iv_url", WittyFeedSDKSingleton.getInstance().loader_iv_url).commit();
                                    } catch (Exception e) {
                                        Log.d(TAG, "onResponse: invalid loader_iv_url", e);
                                        WittyFeedSDKSingleton.getInstance().loader_iv_url = "";
                                    }
                                }

                                WittyFeedSDKSingleton.getInstance().loader_iv_url = "http://gifimage.net/wp-content/uploads/2017/08/small-gif.gif";

                                if(feed_loader_threshold > 0 && feed_loader_threshold < 100){
                                    try {
                                        WittyFeedSDKSingleton.getInstance().loader_threshold_int = feed_loader_threshold;
                                    } catch (Exception e) {
                                        Log.d(TAG, "onResponse: invalid loader threshold", e);
                                        WittyFeedSDKSingleton.getInstance().loader_threshold_int = 85;
                                    }
                                } else {
                                    WittyFeedSDKSingleton.getInstance().loader_threshold_int = 85;
                                }

                                Log.d(TAG, "credentials verified successfully");
                                callback.onSuccess(response, isLoadedMore, isBackgroundCacheRefresh);
                            } else {
                                Log.d(TAG, "onResponse: invalid response - " + response);
                                callback.onError(null);
                            }
                        } catch (JSONException e) {
                            // if error received
                            Log.d(TAG, "onResponse: invalid response - " + response, e);
                            callback.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        Log.d(TAG, "onResponse: request failure", e);
                        callback.onError(e);
                    }
                }
        );
        Volley.newRequestQueue(activity).add(postRequest);
    }


    void update_fcm_token(final WittyFeedSDKNetworkInterface callback) {

        String url_api = base_prefix + "/Sdk/updateToken";
        final WittyFeedSDKApiClient final_wittyFeedSDKApiClient = this.wittyFeedSDKApiClient;

        StringRequest postRequest = new StringRequest(Request.Method.POST, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject result = new JSONObject(response);
                            if(result.optBoolean("status")) {
                                Log.d(TAG, "onResponse: git response - " + response);
                                if (!final_wittyFeedSDKApiClient.getFCM_TOKEN().equalsIgnoreCase("")) {
                                    PreferenceManager.getDefaultSharedPreferences(activity).edit().putString("wf_saved_fcm_token", final_wittyFeedSDKApiClient.getFCM_TOKEN()).apply();
                                }
                            } else {
                                Log.d(TAG, "onResponse: invalid response - " + response);
                            }
                        } catch (JSONException e) {
                            // if error received
                            Log.d(TAG, "onResponse: invalid response - " + response, e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        Log.d(TAG, "onResponse: request failure", e);
                        callback.onError(e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> payload = new HashMap<>();
                payload.put("app_id", final_wittyFeedSDKApiClient.getAPP_ID());
                String fcm_token_to_send = final_wittyFeedSDKApiClient.getFCM_TOKEN();
                String old_fcm_token = "";
                try {
                    if (!PreferenceManager.getDefaultSharedPreferences(activity).getString("wf_saved_fcm_token", "").equalsIgnoreCase("")) {
                        old_fcm_token = PreferenceManager.getDefaultSharedPreferences(activity).getString("wf_saved_fcm_token", "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "old token: " + old_fcm_token);
                Log.d(TAG, "new token: " + final_wittyFeedSDKApiClient.getFCM_TOKEN());
                Log.d(TAG, "Force Update New FCM Token");
                payload.put("firebase_token", "" + fcm_token_to_send);
                payload.put("old_firebase_token", "" + old_fcm_token);
                payload.put("device_meta", "" + final_wittyFeedSDKApiClient.getDevice_meta());
                return payload;
            }
        };
        Volley.newRequestQueue(activity).add(postRequest);
    }


    void get_search_results(final String search_input_str, final int loadmore_offset, final WittyFeedSDKNetworkInterface callback) {

        String url_api = base_prefix + "/Sdk/search_v5";

        final WittyFeedSDKApiClient final_wittyFeedSDKApiClient = this.wittyFeedSDKApiClient;

        url_api += "?";
        url_api += "&keyword=" + search_input_str;
        url_api += "&offset=" + (loadmore_offset*10);
        url_api += "&user_id=" + WittyFeedSDKSingleton.getInstance().oneFeedConfig.getUser_id();
        url_api += "&app_id=" + final_wittyFeedSDKApiClient.getAPP_ID();

        StringRequest search_stringRequest = new StringRequest(Request.Method.GET, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resultJson = new JSONObject(response);
                            if(resultJson.optBoolean("status")) {
                                boolean isLoadedMore = false;
                                if(loadmore_offset > 0) {
                                    isLoadedMore = true;
                                }
                                callback.onSuccess(resultJson.optJSONObject("feed_data").toString(), isLoadedMore, false);
                            } else {
                                Log.d(TAG, "onResponse: invalid response - " + response);
                                callback.onError(null);
                            }
                        } catch (JSONException e) {
                            // if error received
                            Log.d(TAG, "onResponse: invalid response - " + response, e);
                            callback.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        Log.d(TAG, "onResponse: request failure", e);
                        callback.onError(e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };
        search_requestQueue.cancelAll("search");
        search_requestQueue.add(search_stringRequest).setTag("search");
    }


    void fetch_interests(final WittyFeedSDKNetworkInterface callback) {

        String url_api = base_prefix + "/Sdk/getCategories_v5";

        final WittyFeedSDKApiClient final_wittyFeedSDKApiClient = this.wittyFeedSDKApiClient;

        url_api += "?";
        url_api += "app_id=" + final_wittyFeedSDKApiClient.getAPP_ID();
        url_api += "&device_id=" + final_wittyFeedSDKApiClient.getDevice_id();

        StringRequest search_stringRequest = new StringRequest(Request.Method.GET, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resultJson = new JSONObject(response);
                            Log.d(TAG, "onResponse: search result received: " + response);
                            if(resultJson.optBoolean("status")) {
                                callback.onSuccess(resultJson.optJSONObject("feed_data").toString(), false, false);
                            } else {
                                Log.d(TAG, "onResponse: invalid response - " + response);
                                callback.onError(null);
                            }
                        } catch (JSONException e) {
                            // if error received
                            Log.d(TAG, "onResponse: invalid response - " + response, e);
                            callback.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        Log.d(TAG, "onResponse: request failure", e);
                        callback.onError(e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return new HashMap<>();
            }
        };
        interest_requestQueue.cancelAll("get_interest");
        interest_requestQueue.add(search_stringRequest).setTag("get_interest");
    }


    void set_interest(final WittyFeedSDKNetworkInterface callback, final String interest_id, final boolean isSelected) {

        String url_api = base_prefix + "/Sdk/saveUserInterest";

        final WittyFeedSDKApiClient final_wittyFeedSDKApiClient = this.wittyFeedSDKApiClient;

        StringRequest search_stringRequest = new StringRequest(Request.Method.POST, url_api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resultJson = new JSONObject(response);
                            Log.d(TAG, "onResponse: search result received: " + response);
                            if(resultJson.optBoolean("status")) {
                                callback.onSuccess("true", false, false);
                            } else {
                                Log.d(TAG, "onResponse: invalid response - " + response);
                                callback.onError(null);
                            }
                        } catch (JSONException e) {
                            // if error received
                            Log.d(TAG, "onResponse: invalid response - " + response, e);
                            callback.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError e) {
                        // if error received
                        Log.d(TAG, "onResponse: request failure", e);
                        callback.onError(e);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> payload = new HashMap<>();
                payload.put("app_id", final_wittyFeedSDKApiClient.getAPP_ID());
                payload.put("selected_cat_id", ""+interest_id);

                int is_cat_active = 0;
                if(isSelected) {
                    is_cat_active = 1;
                }
                payload.put("is_selected", ""+is_cat_active);
                payload.put("device_id", "" + final_wittyFeedSDKApiClient.getDevice_id());
                return payload;
            }
        };
        interest_requestQueue.cancelAll("get_interest");
        interest_requestQueue.add(search_stringRequest).setTag("get_interest");
    }


    void setFcm_token(String new_fcm_token) {
        this.wittyFeedSDKApiClient.setFCM_TOKEN(new_fcm_token);
    }

}
