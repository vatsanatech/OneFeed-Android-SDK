package com.wittyfeed.sdk.onefeed;

import android.util.Log;

final class OFLogger {

    // DataStoreCacheManager
    public static final String CacheReadSuccess = "Did Load MainFeedData Cache";
    public static final String CacheRefreshSuccess = "Did Refresh MainFeedData Cache";
    public static final String CacheLoadError = "Error in MainFeedData Cache Load";
    public static final String CacheRefreshERROR = "Error in MainFeedData Cache Refresh";

    // NetworkServiceManager
    public static final String MainFeedFetchedSuccess = "Did Load MainFeedData Cache";
    public static final String MainFeedFetchedError = "Did Refresh MainFeedData Cache";

    // Content View Maker
    public static final String URLToOpen = "url_to_open: ";
    public static final String UnableToConvertToBitmap = "Unable to convert to bitmap";

    // NetworkServiceManager
    public static final String MainFeedRequestQueueIsNull = "mainFeedRequestQueue is null, initialize it first";
    public static final String SearchFeedRequestQueueIsNull = "searchFeedRequestQueue is null, initialize it first";
    public static final String InterestRequestQueueIsNull = "interestFeedRequestQueue is null, initialize it first";
    public static final String ConfigRequestQueueIsNull = "configFeedRequestQueue is null, initialize it first";
    public static final String MainFeedFetchError = "main feed fetch error";
    public static final String OffsetCount = "Last Offset Sent: ";

    // OneFeedSDKMain
    public static final String SDKMainInterFaceIsNull = "SDK Main callback interface OnInitialized is null";
    public static final String OneFeedMainIsNotInitialized = "OneFeedMain is null, initialize OneFeedMain before using OneFeed Fragment";

    // DataStoreParser
    public static final String DataParseError = "Data couldn't be parser";

    // HolderFragment
    public static final String DataStoreLoaded = "HolderFragment notified about DataStore loaded";

    // MainFeedFragment
    public static final String FetchMoreSTART = "Fetch More Data :: START";
    public static final String FetchMoreEnd = "Fetch More Data :: END";
    public static final String FetchMoreError = "Fetch More Data :: ERROR";
    public static final String RefreshDataStart = "Refresh Data :: START";
    public static final String RefreshDataEnd = "Fetch More Data :: END";
    public static final String RefreshDataError = "Fetch More Data :: ERROR";

    // MainAdapter
    public static final String InOnCreate = "OnCreate: ";
    public static final String InOnBind = "OnBind: ";

    // SearchFeedFragment
    public static final String SearchingFor = "Searching for: ";
    public static final String CouldNotFetchSearchFeedData = "Couldn't Fetch Search Feed Data";
    public static final String SuccessfullySearchedFor = "Successfully Searched For: ";
    public static final String SearchFeedArraySize = "New Search Feed Array Size: ";

    // Misc / Common
    public static final String NoInternet = "No Internet";

    static final int VERBOSE = 1;
    static final int DEBUG = 2;
    static final int ERROR = 3;

    private static final String OneFeed_TAG = "OF_SDK";

    private static final boolean to_show_logs = true;

    public static final void log(int type, String msg){
        if (to_show_logs) {
            if(type == VERBOSE){
                Log.v(OneFeed_TAG, msg);
            } else if(type == DEBUG) {
                Log.d(OneFeed_TAG, msg);
            } else if(type == ERROR) {
                Log.e(OneFeed_TAG, msg);
            }
        }
    }

    public static final void log(int type, String msg, Exception e){
        if (to_show_logs) {
            if(type == VERBOSE){
                Log.v(OneFeed_TAG, msg, e);
            } else if(type == DEBUG) {
                Log.d(OneFeed_TAG, msg, e);
            } else if(type == ERROR) {
                Log.e(OneFeed_TAG, msg, e);
            }
        }
    }

}