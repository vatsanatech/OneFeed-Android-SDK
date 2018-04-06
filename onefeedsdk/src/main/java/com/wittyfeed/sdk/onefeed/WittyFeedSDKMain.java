package com.wittyfeed.sdk.onefeed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class WittyFeedSDKMain {

    private final String TAG = "WF_SDK";
    WittyFeedSDKApiClient wittyFeedSDKApiClient;
    private Context context;
    //SDK vars
    private WittyFeedSDKNetworking wittyFeedSdkNetworking;
    private WittyFeedSDKMainInterface wittyFeedSDKMainInterface;

    /*
    * Constructor
    *   - Context: Context of the Host Application, will be only used in whole app for Glide-RequestManager
    *              and Volley-RequestManager and SharedResources
    *   - WittyFeedSDKApiClient: contains all the credentials and meta about the host app, used for networking,
    *              and passing safe analytics data to server about the consumption of OneFeed by user
    *
    * */

    public WittyFeedSDKMain(Context applicationContext, WittyFeedSDKApiClient para_wittyFeedSDKApiClient){
        this.context = applicationContext;
        this.wittyFeedSDKApiClient = para_wittyFeedSDKApiClient;
        WittyFeedSDKSingleton.getInstance().witty_sdk = this;
    }


    /*
    * Package Private Methods
    *
    *   1. fetch_more_data: this method fetches more data from server for endless feed implementation
    *       - MainInterface: interface notifies the method executor when data is successfully fetched and loaded
    *       - int: load more offset
    *
    *   2. load_initial_data: this method fetches the very first data-set (i.e. load_more_offset is 0)
    *       - MainInterface: interface notifies the method executor when data is successfully fetched and loaded
    *       - boolean: this boolean if sent 'true' then the fetched data will be overwritten to OneFeedCache
    *
    * */

    void fetch_more_data(final WittyFeedSDKMainInterface fetch_more_main_callback, int loadmore_offset){
        WittyFeedSDKNetworkInterface fetch_more_networking_callback = new WittyFeedSDKNetworkInterface() {
            @Override
            public void onSuccess(String jsonString, boolean isLoadedMore, boolean isBackgroundRefresh) {
                String mainFeedString = "";

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    mainFeedString = jsonObject.optJSONObject("data").toString();
                } catch (JSONException e) {
                    Log.d(TAG, "onSuccess: error in getting fresh data");
                    fetch_more_main_callback.onError(null);
                }

                handle_feeds_result(mainFeedString, "", isLoadedMore, isBackgroundRefresh, false);
                fetch_more_main_callback.onOperationDidFinish();
            }

            @Override
            public void onError(Exception e) {
                if (e != null) {
                    Log.d(TAG, "error in fetching more data: "+ e.getMessage());
                    e.printStackTrace();
                    fetch_more_main_callback.onError(e);
                } else {
                    fetch_more_main_callback.onError(null);
                }
            }
        };
        wittyFeedSdkNetworking.getStoryFeedData(true, fetch_more_networking_callback, loadmore_offset, false);
    }

    void load_initial_data(final WittyFeedSDKMainInterface refresh_data_main_callback, final boolean isBackgroundCacheRefresh) {

        WittyFeedSDKNetworkInterface refresh_data_networking_callback = new WittyFeedSDKNetworkInterface() {
            @Override
            public void onSuccess(String jsonString, boolean isLoadedMore, boolean isBackgroundRefresh) {
                String mainFeedString = "";
                String searchBlockFeedString = "";

                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    mainFeedString = jsonObject.optJSONObject("data").toString();
                    searchBlockFeedString = jsonObject.optJSONObject("block_data").toString();
                } catch (JSONException e) {
                    Log.d(TAG, "onSuccess: error in getting fresh data");
                    refresh_data_main_callback.onError(null);
                }

                handle_feeds_result(mainFeedString, searchBlockFeedString, isLoadedMore, isBackgroundRefresh, false);
                refresh_data_main_callback.onOperationDidFinish();
            }

            @Override
            public void onError(Exception e) {
                if (e != null) {
                    Log.d(TAG, "error in getting fresh data (isBackgroundCacheRefresh:" + isBackgroundCacheRefresh +") : "+ e.getMessage());
                    e.printStackTrace();
                    refresh_data_main_callback.onError(e);
                } else {
                    refresh_data_main_callback.onError(null);
                }
            }
        };

        wittyFeedSdkNetworking.getStoryFeedData(false, refresh_data_networking_callback, 0, isBackgroundCacheRefresh);
    }

    void search_content(final WittyFeedSDKMainInterface search_callback, final String search_input_str, int loadmore_offset){

        WittyFeedSDKNetworkInterface search_results_networking_callback = new WittyFeedSDKNetworkInterface() {
            @Override
            public void onSuccess(String jsonString, boolean isLoadedMore, boolean isBackgroundRefresh) {
                handle_search_result(jsonString, isLoadedMore, isBackgroundRefresh, false);
                search_callback.onOperationDidFinish();
            }

            @Override
            public void onError(Exception e) {
                if (e != null) {
                    Log.d(TAG, "error in searching content for :" + search_input_str +" -" + e.getMessage());
                    e.printStackTrace();
                    search_callback.onError(e);
                } else {
                    search_callback.onError(null);
                }
            }
        };

        wittyFeedSdkNetworking.get_search_results(search_input_str, loadmore_offset, search_results_networking_callback);
    }

    void get_interests_list(final WittyFeedSDKMainInterface get_interests_content_callback) {

        WittyFeedSDKNetworkInterface interests_networking_callback = new WittyFeedSDKNetworkInterface() {
            @Override
            public void onSuccess(String jsonString, boolean isLoadedMore, boolean isBackgroundRefresh) {
                handle_interests_result(jsonString);
                get_interests_content_callback.onOperationDidFinish();
            }

            @Override
            public void onError(Exception e) {
                if (e != null) {
                    Log.d(TAG, "error in getting interests for -" + e.getMessage());
                    e.printStackTrace();
                    get_interests_content_callback.onError(e);
                } else {
                    get_interests_content_callback.onError(null);
                }
            }
        };

        wittyFeedSdkNetworking.fetch_interests(interests_networking_callback);
    }

    void set_interests_list(final WittyFeedSDKMainInterface get_interests_content_callback, String interest_id, boolean isSelected) {

        WittyFeedSDKNetworkInterface set_interests_networking_callback = new WittyFeedSDKNetworkInterface() {
            @Override
            public void onSuccess(String responseString, boolean isLoadedMore, boolean isBackgroundRefresh) {
                if (responseString.equalsIgnoreCase("true")) {
                    get_interests_content_callback.onOperationDidFinish();
                } else {
                    get_interests_content_callback.onError(null);
                }
            }

            @Override
            public void onError(Exception e) {
                if (e != null) {
                    Log.d(TAG, "error in getting interests for -" + e.getMessage());
                    e.printStackTrace();
                    get_interests_content_callback.onError(e);
                } else {
                    get_interests_content_callback.onError(null);
                }
            }
        };

        wittyFeedSdkNetworking.set_interest(set_interests_networking_callback, interest_id, isSelected);
    }


    /*
    * Private Methods
    *
    *   1. handle_feeds_result: parses the data that is received from web_service response or from either OneFeedCache
    *       - String: jsonData that will be parsed in raw-string
    *       - boolean: isLoadedMore
    *                  if passed true then the fetched data will not be cached and also it will be parsed and will be appended to existing data-array
    *                  if passed false then the fetched data will overwrite existing data (Refresh Functionality)
    *
    *       - boolean: isBackgroundRefresh
    *                  if passed true then the fetched data will be cached into OneFeedCached without parsing
    *                  if passed false then the data will not be cached
    *
    *   2. prepare_feed_data: checks if cache is available,
    *       - if cache is available and not expired then will load it immediately and pass the data into handle_feeds_result();
    *         and then will update the cache in background from server
    *       - if cache is not available then it will request server for data and then after fetching it will pass the data to handle_feeds_result();
    *         and then will also cache the data
    *
    *   3. create_cached_JSON: caches the data into OneFeedCache
    *       - String: fetched_data which is the response of received JsonObject in raw-string
    *
    *   4. read_cached_JSON: reads the cached data from OneFeedCache
    *
    *   5. get_screen_dimensions: utility method which sets screen's height and width into variables in Singleton class, this variables are used for rendering later
    *
    *   6. handle_search_result: parses the data that is received from web_service response or from either OneFeedCache
    *       - NOTE: will parse data for fetched search content
    *       - Parameters: same as handle_feeds_result
    *
    *   6. handle_interests_result: parses the data that is received from web_service response or from either OneFeedCache
    *       - NOTE: will parse data for fetched interests and their active_inactive status
    *       - Parameters: same as handle_feeds_result
    *
    * */

    private void handle_feeds_result(String mainFeedString, String searchFeedString, boolean isLoadedMore, boolean isBackgroundRefresh, boolean is_cached) {

        MainDatum temp_mainDatum = null;
        try{
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            temp_mainDatum = gson.fromJson(mainFeedString, MainDatum.class);
            WittyFeedSDKSingleton.getInstance().oneFeedConfig = temp_mainDatum.getConfig();
        }catch (Exception e){
            e.printStackTrace();
        }


        MainDatum temp_searchBlockDatum = null;
        if (!searchFeedString.equalsIgnoreCase("")) {
            try{
                Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                temp_searchBlockDatum = gson.fromJson(searchFeedString, MainDatum.class);

                WittyFeedSDKSingleton.getInstance().default_search_block_arr.clear();
                WittyFeedSDKSingleton.getInstance().default_search_block_arr = (ArrayList<Block>) temp_searchBlockDatum.getBlocks();
            }catch (Exception e){
                e.printStackTrace();
            }
        }


        if(!isLoadedMore && !isBackgroundRefresh){
            // clear the existing blocks array
            WittyFeedSDKSingleton.getInstance().blockArrayList.clear();
        }

        if (!isBackgroundRefresh) {
            // append new loaded data to existing data array
            if (temp_mainDatum != null) {
                WittyFeedSDKSingleton.getInstance().blockArrayList.addAll(temp_mainDatum.getBlocks());
            }

            if(!isLoadedMore){
                // create cached json
                if (temp_mainDatum != null && !is_cached) {
                    create_cached_JSON(mainFeedString);
                }
            }
            // call function to sort data by order and do other operations

        } else {
            // cache the data in background without disturbing model array
            create_cached_JSON(mainFeedString);
        }
    }

    private void handle_search_result(String jsonString, boolean isLoadedMore, boolean isBackgroundRefresh, boolean is_cached) {
        MainDatum temp_search_mainDatum = null;
        try{
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            temp_search_mainDatum = gson.fromJson(jsonString, MainDatum.class);

            if(!isLoadedMore){
                WittyFeedSDKSingleton.getInstance().search_blocks_arr.clear();
            }

            WittyFeedSDKSingleton.getInstance().search_blocks_arr.addAll(temp_search_mainDatum.getBlocks());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handle_interests_result(String jsonString) {
        MainDatum temp_search_mainDatum = null;
        try{
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            temp_search_mainDatum = gson.fromJson(jsonString, MainDatum.class);

            WittyFeedSDKSingleton.getInstance().interests_block_arr.clear();
            WittyFeedSDKSingleton.getInstance().interests_block_arr.addAll(temp_search_mainDatum.getBlocks());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void prepare_feed_data() {
        Log.d(TAG,"preparing OneFeed Data");
        // Get file from file name
        File fileSize = new File(context.getCacheDir(), "OneFeedCache.json");
        // Get length of file in bytes
        long fileSizeInBytes = fileSize.length();
        Log.d(TAG,"fileSizeInBytes: "+ fileSizeInBytes);
        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        long fileSizeInKB = fileSizeInBytes / 1024;
        Log.d(TAG,"fileSizeInKB: "+ fileSizeInKB);
        // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
        long fileSizeInMB = fileSizeInKB / 1024;
        Log.d(TAG,"fileSizeInMB: "+ fileSizeInMB);

        if (fileSizeInKB > 5) {
            // Parsing cached data at startup, and Will update the feed in background
            read_cached_JSON();

            Log.d(TAG,"refreshing SDK Data in background");
            WittyFeedSDKMainInterface refresh_background_cache_main_callback = new WittyFeedSDKMainInterface() {
                @Override
                public void onOperationDidFinish() {
                    Log.d(TAG, "cache in background refreshed successfully");
                }

                @Override
                public void onError(Exception e) {
                    // if unexpected error
                    if (e != null) {
                        Log.e(TAG, "onError: refresh data error", e);
                    } else {
                        Log.e(TAG, "onError: refresh data error");
                    }
                }
            };
            load_initial_data(refresh_background_cache_main_callback, true);
        } else {
            if (WittyFeedSDKUtils.isConnected(context)) {
                // when no cached data is available
                WittyFeedSDKNetworkInterface get_feed_callback = new WittyFeedSDKNetworkInterface() {
                    @Override
                    public void onSuccess(String jObject, boolean isLoadedMore, boolean isBackgroundRefresh) {
                        try {
                            String mainFeedString = "";

                            try {
                                JSONObject jsonObject = new JSONObject(jObject);
                                mainFeedString = jsonObject.optJSONObject("data").toString();
                            } catch (JSONException e) {
                                Log.d(TAG, "onSuccess: error in getting fresh data");
                                wittyFeedSDKMainInterface.onError(null);
                            }

                            handle_feeds_result(mainFeedString, "", isLoadedMore, isBackgroundRefresh, false);
                            wittyFeedSDKMainInterface.onOperationDidFinish();
                        } catch (Exception e) {
                            wittyFeedSDKMainInterface.onError(e);
                            if (e != null) {
                                e.printStackTrace();
                                Log.e("witty", "data error", e);
                            } else {
                                Log.e("witty", "data error");
                            }
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("WF_SDK", "error in get data request " + e.getMessage());
                        wittyFeedSDKMainInterface.onError(null);
                    }
                };
                wittyFeedSdkNetworking.getStoryFeedData(false, get_feed_callback, 0, false);

            } else {
                // notify user that internet is down
                Toast.makeText(context, "Internet Lost", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void create_cached_JSON(String feed_data_str) {
        /*http://codetheory.in/android-saving-files-on-internal-and-external-storage/*/
        File file;
        FileOutputStream outputStream;
        try {
            file = new File(context.getCacheDir(), "OneFeedCache.json");
            outputStream = new FileOutputStream(file);
            outputStream.write(feed_data_str.getBytes());
            outputStream.close();
            Log.d(TAG, "refreshed SDK Data in background");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read_cached_JSON() {
        /*http://codetheory.in/android-saving-files-on-internal-and-external-storage/*/

        File fileRead = null;
        BufferedReader input = null;

        try {
            fileRead = new File(context.getCacheDir(), "OneFeedCache.json"); // Pass getFilesDir() and "MyFile" to read file

            input = new BufferedReader(new InputStreamReader(new FileInputStream(fileRead)));
            String line;
            StringBuilder buffer = new StringBuilder();

            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }

            try {
                WittyFeedSDKSingleton.getInstance().isDataUpdated = false;
                handle_feeds_result(buffer.toString(), "", false, false, true);
                wittyFeedSDKMainInterface.onOperationDidFinish();
                Log.d(TAG,"previous data read from cache successfully");
            } catch (Exception e) {
                wittyFeedSDKMainInterface.onError(e);
                Log.d(TAG,"previous data couldn't be read from cache");
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void get_screen_dimensions(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float height = displayMetrics.heightPixels;
        float width = displayMetrics.widthPixels;
        WittyFeedSDKSingleton.getInstance().screenHeight = (int)height;
        WittyFeedSDKSingleton.getInstance().screenWidth = (int)width;
    }


    /*
    * Public Methods
    *
    *   1. init_wittyfeed_sdk: Starts all initializations for the OneFeed SDK
    *       - reads cached configurations from SharedPreferences for the app
    *       - prepares data by method prepare_feed_data
    *       - warms-up Custom-ChromeTab which OneFeed SDK uses for content consumption
    *       - sends GA Event for OneFeed initialized
    *
    *   2. set_operationDidFinish_callback:
    *       - MainInterface: set MainInterface through this method to set Callback which will be called when data loading is finished
    *                        onOperationDidFinish(); when data is successfully loaded
    *                        onError(Exception e): return Exception if data couldn't be loaded
    *
    *   3. update_fcm_token:
    *       - String: pass new fcm_token that needs to be sent to server, it is called from host app's mFirebaseInstanceIdService class when their FCM Token Refreshes
    *
    *   4. get_one_feed_support_fragment: *** DISABLED RIGHT NOW ***
    *       - fetches OneFeedFragment for the host app to use
    * */

    public void init_wittyfeed_sdk(){

        WittyFeedSDKSingleton.getInstance().wittySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        WittyFeedSDKSingleton.getInstance().editor_sharedPref = WittyFeedSDKSingleton.getInstance().wittySharedPreferences.edit();


        WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics = new WittyFeedSDKGoogleAnalytics(context, WittyFeedSDKSingleton.getInstance().getGA_TRACKING_ID(), wittyFeedSDKApiClient.getFCM_TOKEN());
        // TODO: 26/10/17 GA send WF STATUS event
        try {
            String eventCat = "WF SDK";
            String eventAction = "" + WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKApiClient.getAPP_ID();
            String eventVal = "1" ;
            String eventLabel = "WF SDK initialized";
            WittyFeedSDKSingleton.getInstance().wittyFeedSDKGoogleAnalytics.send_event_tracking_GA_request(eventCat, eventAction, eventVal, eventLabel);
            Log.d(TAG, "For " + eventCat);
        } catch (Exception e) {
            e.printStackTrace();
        }

        wittyFeedSdkNetworking = new WittyFeedSDKNetworking(this.context, wittyFeedSDKApiClient);

        get_screen_dimensions();

        try {
            WittyFeedSDKSingleton.getInstance().loader_iv_url = WittyFeedSDKSingleton.getInstance().wittySharedPreferences.getString("loader_iv_url","");
            Log.d(TAG, "init_wittyfeed_sdk: cached loader_iv_url: " + WittyFeedSDKSingleton.getInstance().loader_iv_url);

            WittyFeedSDKSingleton.getInstance().is_load_cache_else_network = WittyFeedSDKSingleton.getInstance().wittySharedPreferences.getBoolean("is_load_cache_else_network",true);
            Log.d(TAG, "init_wittyfeed_sdk: cached is_load_cache_else_network: " + WittyFeedSDKSingleton.getInstance().is_load_cache_else_network);
        } catch (Exception e) {
            e.printStackTrace();
        }

        prepare_feed_data();
    }

    public void set_operationDidFinish_callback(WittyFeedSDKMainInterface para_wittyFeedSDKMainInterface){
        this.wittyFeedSDKMainInterface = para_wittyFeedSDKMainInterface;
    }

    public void update_fcm_token(String new_fcm_token){
        try {

            WittyFeedSDKNetworkInterface update_fcm_token_callback = new WittyFeedSDKNetworkInterface() {
                @Override
                public void onSuccess(String jsonString, boolean isLoadedMore, boolean isBackgroundRefresh) {
                    Log.d(TAG,"FCM Token updated to OneFeed Server");
                }

                @Override
                public void onError(Exception e) {
                    if (e != null) {
                        Log.e("WF_SDK", "error in udpate fcm request", e);
                    } else {
                        Log.e("WF_SDK", "error in udpate fcm request");
                    }
                }
            };

            if(wittyFeedSDKApiClient != null ){
                wittyFeedSDKApiClient.setFCM_TOKEN(new_fcm_token);
            } else {
                Log.e(TAG, "update_fcm_token: api client is null. Please verify that OneFeed SDK is initializing properly when app loads");
                return;
            }

            if(wittyFeedSdkNetworking == null){
                wittyFeedSdkNetworking = new WittyFeedSDKNetworking(this.context, wittyFeedSDKApiClient);
            } else {
                wittyFeedSdkNetworking.setFcm_token(new_fcm_token);
            }

            wittyFeedSdkNetworking.update_fcm_token(update_fcm_token_callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_onefeed_base_color(String para_color) {
        try {
            if(!para_color.contains("#") || para_color.charAt(0) != '#'){
                para_color = "#" + para_color;
            }
            WittyFeedSDKSingleton.getInstance().onefeed_bg_color_string = para_color;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set_onefeed_back_icon(Bitmap para_back_icon_bitmap) {
        try {
            WittyFeedSDKSingleton.getInstance().onefeed_back_icon_bitmap = para_back_icon_bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*public WittyFeedSDKOneFeedFragment get_one_feed_support_fragment(){
        return new WittyFeedSDKOneFeedFragment();
    }*/

}
