package com.wittyfeed.sdk.onefeed.Utils;

import android.util.Log;

/**
 *
 * Contains all the required values to be used in logging
 *
 *
 *Keeps all the log messages that is used across the app
 *
 * Also contains type of log values
 *
 */

public final class OFLogger {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int ERROR = 3;

    private static final String OneFeed_TAG = "OF_SDK";

    private static final boolean to_show_logs = true;

    public static void log(int type, String msg){
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

    public static void log(int type, String msg, Exception e){
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

    // DataStoreCacheManager
    public static final String CacheReadSuccess = "Did Load MainFeedData Cache";
    public static final String CacheRefreshSuccess = "Did Refresh MainFeedData Cache";
    public static final String CacheLoadError = "Error in MainFeedData Cache Load";
    public static final String CacheRefreshERROR = "Error in MainFeedData Cache Refresh";

    // DataStoreManager
    public static final String CacheIsDeprecated = "cache parsing failed, cache is either invalid or deprecated, requesting fresh feed instead";
    public static final String CacheLoadSuccessful = "cache loaded successfully";
    public static final String CacheParsingFailed = "cache parsing failed, requesting fresh feed instead";
    public static final String CacheUnavailable = "cache unavailable";

    // NetworkServiceManager
    public static final String MainFeedFetchedSuccess = "Did Load MainFeedData Cache";
    public static final String MainFeedFetchedError = "Did Refresh MainFeedData Cache";

    // FCMTokenManager
    public static final String FcmTokenIsNull = "FCM Token is null";

    // ContentViewMaker
    public static final String URLToOpen = "url_to_open: ";
    public static final String UnableToConvertToBitmap = "Unable to convert to bitmap";

    // OFContentViewActivity
    public static final String invalidClassInHomeActivityIntent = "Invalid class in HomeActivityIntent";

    // NetworkServiceManager
    public static final String MainFeedRequestQueueIsNull = "mainFeedRequestQueue is null, initialize it first";
    public static final String SearchFeedRequestQueueIsNull = "searchFeedRequestQueue is null, initialize it first";
    public static final String InterestRequestQueueIsNull = "interestFeedRequestQueue is null, initialize it first";
    public static final String ConfigRequestQueueIsNull = "configFeedRequestQueue is null, initialize it first";
    public static final String CouldNotFetchData = "couldn't fetch data: ";
    public static final String OffsetCount = "Last Offset Sent: ";
    public static final String ForceUpdatedFCMToken = "Force Updated FCM Token";

    // OneFeedSDKMain
    public static final String SDKMainInterFaceIsNull = "SDK Main callback interface OnInitialized is null";
    public static final String OneFeedMainIsNotInitialized = "OneFeedMain is null, initialize OneFeedMain before using OneFeed Fragment";

    // DataStoreParser
    public static final String DataParseError = "Data couldn't be parser";

    // HolderFragment
    public static final String DataStoreLoaded = "HolderFragment notified about DataStore loaded";
    public static final String BackInterfaceIsNull = "onBackClickInterface is null, set it first.";
    public static final String ChildFragmentManagerStackCount = "child fragment manager stack count: ";

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

    // CardDataViewHolderBinder
    public static final String OnLoadFailedImgCover = "onLoadFailed: imgCover:";

    // CardViewHolderFactory
    public static final String InflaterIsNull = "Inflater is null, set inflater via method setInflater() first";

    // InterestsFeedFragment
    public static final String CouldNotFetchInterestsData = "couldn't fetch Interests Data";
    public static final String InterestUnSelected = "Interest un-selection Succeed";
    public static final String InterestUnSelectedFailed = "Interest un-selection Failed";
    public static final String InterestSelected = "Interest selection Succeed";
    public static final String InterestSelectedFailed = "Interest selection Failed";



}