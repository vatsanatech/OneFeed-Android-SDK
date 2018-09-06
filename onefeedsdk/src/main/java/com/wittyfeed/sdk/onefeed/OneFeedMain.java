package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.wittyfeed.sdk.onefeed.DataStoreManagement.DataStore;
import com.wittyfeed.sdk.onefeed.DataStoreManagement.DataStoreManager;
import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Utils.ContentViewMaker;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.Utils.OFSharedPreference;
import com.wittyfeed.sdk.onefeed.Utils.OneFeedBuilder;

/**
 <p><span style="font-size: 13pt;"><strong>Only point of contact between Host app and OneFeed</strong></span></p>
 <p>As per Single Responsibility Principle Architecture of OneFeed SDK,
 this class acts as the only point of contact between Host app and OneFeed, all other services of
 OneFeed are accessible from here and handled here only, no direct work is done</p>
 <p>Architecture is Singleton with Lazy-loading capability</p>
 <p>Has 1 interface {@link OnInitialized}, which is used as callback for host app to notify that OneFeed
 SDK has completely loaded. And it has following method declarations -</p>
    <ol>
    <li>onSuccess</li>
    <li>onError</li>
    </ol>
 */

public final class OneFeedMain {

    private static OneFeedMain ourInstance;

    private static boolean hideBackButton = false;

    public DataStore dataStore;
    public OFSharedPreference ofSharedPreference;
    public NetworkServiceManager networkServiceManager;
    public OneFeedBuilder oneFeedBuilder;
    private DataStoreManager dataStoreManager;
    public FCMTokenManager fcmTokenManager;
    private ContentViewMaker contentViewMaker;
    private OnInitialized onInitialized;
    public OFCardFetcher ofCardFetcher;

    private OneFeedMain() {}

    /**
     * @return singleton instance of {@link OneFeedMain}
     */
    public static OneFeedMain getInstance() {
        if(ourInstance == null) {
            ourInstance = new OneFeedMain();
        }
        return ourInstance;
    }

    /**
     *
     * Receives host app's credentials, fcm token of user and applicationContext<br>
     <p>Then initialises -</p>
         <ol>
         <li>{@link NetworkServiceManager} is responsible for all OneFeed Web Services</li>
         <li>{@link OFSharedPreference} handles all SharedPreferences operations of OneFeed</li>
         <li>{@link DataStore} is the end-point of Data-Model throughout the SDK</li>
         <li>{@link DataStoreManager} manages the DataStore</li>
         <li>{@link FCMTokenManager} handles and manages all FCM Token related operations</li>
         <li>{@link OFAnalytics} handles and all analytics of SDK Usage</li>
         <li>{@link ContentViewMaker} warms up content or story views read more in</li>
         <li>executes loadDataStore() for loading data of OneFeedSDK using {@link DataStoreManager}</li>
         </ol>
     * @param applicationContext    the application context
     * @param APP_ID                the APP_ID of this app as per registration on OneFeed Dashboard
     * @param API_KEY               the API_KEY id of this app as per registration on OneFeed Dashboard
     */
    public final synchronized void init(Context applicationContext, String APP_ID, String API_KEY, String FCM_TOKEN){

        ApiClient.getInstance().init(applicationContext, APP_ID, API_KEY);

        Constant.APP_ID = APP_ID;

        initNetworkServiceManager(applicationContext);

        initOFSharedPref(applicationContext);

        initDataStore();

        initOneFeedBuilder();

        initContentViewMaker(applicationContext);

        initDataStoreManager();

        initFcmTokenManager(applicationContext, FCM_TOKEN);

        initAnalyticsManager(applicationContext);

        loadDataStore(applicationContext);

        OFAnalytics.getInstance().sendAnalytics(applicationContext, OFAnalytics.AnalyticsType.SDK, "OneFeed Initialized");

        Log.i("init", "ONEFEED INITIALIZED: ");

        initOFCardFetcher(dataStore, applicationContext);


    }

    public static void setHideBackButtonFromMainFeed(boolean hideBackButton) {
        OneFeedMain.hideBackButton = hideBackButton;
    }

    public static boolean isHideBackButton() {
        return hideBackButton;
    }

    /**
     * init FCM token manager which when initialized will also check if the server has updated FCM token available or not
     * @param applicationContext the application context
     * @param FCM_TOKEN the current active FCM token of the host app's user
     */
    private void initFcmTokenManager(Context applicationContext, String FCM_TOKEN){
        fcmTokenManager = new FCMTokenManager(FCM_TOKEN);
        networkServiceManager.setConfigRequestQueue(applicationContext);
        fcmTokenManager.pushFcmTokenIfRequired();
    }

    /**
     * init DataStoreManager which will load OneFeed Data from cache if avaialble and not expired,<br>
     * Else will fetch fresh feed from server<br>
     * Also has interface to notify OneFeedMain and ultimately host app about initialization success or exception
     */
    private void initDataStoreManager(){
        dataStoreManager = new DataStoreManager(new DataStoreManager.OnStoreManagerDidFinishDataFetch() {
            @Override
            public void onSuccess() {
                // when data did finish loading
                //Changed by yogesh
//                OFLogger.log( OFLogger.DEBUG, "blocks count: " + OneFeedMain.getInstance().getInstanceDataStore().getMainFeedDataBlockArr().size() );
//                OFLogger.log( OFLogger.DEBUG, "default search blocks count: " + OneFeedMain.getInstance().getInstanceDataStore().getSearchDefaultDataBlockArr().size() );
                callOnInitializedSuccess();
            }

            @Override
            public void onError() {
                // if data is unable to load
                callOnInitializedError();
            }
        });
    }

    /**
     *
     * initialized ContentViewMaker of OneFeed, if chrome available then executes its warmUp();<br>
     * else does WebView optimisation configurations
     * @param applicationContext    the application context
     */
    private void initContentViewMaker(Context applicationContext){
        contentViewMaker = new ContentViewMaker(applicationContext);
    }

    /**
     * initializes OneFeedBuilder which provides availability of design customisations of OneFeed to HostApp <br>
     * The Host App shall explore numerous possibilities availed by getter setter of this object
     */
    private void initOneFeedBuilder(){
        oneFeedBuilder = new OneFeedBuilder();
    }

    /**
     * DataStore holds the data of OneFeed
     */
    private void initDataStore(){
            dataStore = new DataStore();
    }

    //Changed by Yogesh
    public DataStore getInstanceDataStore(){
        if(dataStore == null){
            dataStore = new DataStore();
        }
        return dataStore;
    }
    /**
     * initialises SharedPref that will be used by OneFeed
     * @param applicationContext    the appliation context
     */
    private void initOFSharedPref(Context applicationContext){
        ofSharedPreference = new OFSharedPreference(applicationContext);
    }

    /**
     * initializes service manager which is available through the SDK package,<br>
     * object of {@link NetworkServiceManager} handles the networking requests
     * @param applicationContext    the application context
     */
    private void initNetworkServiceManager(Context applicationContext){
        networkServiceManager = new NetworkServiceManager();
        networkServiceManager.setMainFeedRequestQueue(applicationContext);
    }

    /**
     * init OfAnalytics of SDK which when initialized will prepare requestQueues for analytics and other boiler plate initializations
     * @param applicationContext the application context
     */
    private void initAnalyticsManager(Context applicationContext) {
        OFAnalytics.getInstance().init(applicationContext);
    }

    /**
     * Asks DataStoreManager to prepare data for OneFeed
     * @param applicationContext    the application context
     */
    private void loadDataStore(Context applicationContext){
        dataStoreManager.prepareOneFeedMainData(applicationContext);
    }

    /**
     * Executes if DataStore loads successfully<br>
     * And, notifies host app about OneFeed Initialization Success if OnInitialized Interface is not null
     */
    private void callOnInitializedSuccess(){
        if (onInitialized != null) {
            oneFeedBuilder.notifyDataStoreLoaded();
            onInitialized.onSuccess();
        } else {
            OFLogger.log(OFLogger.ERROR, OFLogger.SDKMainInterFaceIsNull);
        }
    }

    private void initOFCardFetcher(DataStore dataStore,Context applicationContext){
        ofCardFetcher = new OFCardFetcher();
        ofCardFetcher.setDataStore(dataStore, applicationContext);
    }

    /**
     * Executes if DataStore receives error or exception in loading<br>
     * And, notifies host app about OneFeed Initialization Error if OnInitialized Interface is not null
     */
    private void callOnInitializedError(){
        if (onInitialized != null) {
            onInitialized.onError();
        } else {
            OFLogger.log(OFLogger.ERROR, OFLogger.SDKMainInterFaceIsNull);
        }
    }

    /**
     * sets the interface which will inform back the host app when OneFeed initialises successfully
     * @param arg   the object of {@link OnInitialized }interface
     */
    public final synchronized void setOneFeedDidInitialisedCallback(OnInitialized arg){
        this.onInitialized = arg;
    }

    /**
     * returns Ready to deploy OneFeedFragment
     * @return an instance of {@link Fragment} as OneFeedFragment
     */
    public final synchronized Fragment getOneFeedFragment(){
        Fragment fragmentToReturn = null;
        if(oneFeedBuilder == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.OneFeedMainIsNotInitialized);
        } else {
            fragmentToReturn = oneFeedBuilder.getHolderFragment();
        }


        return fragmentToReturn;
    }

    /**
     *
     * @param applicationContext the application context, required by the {@link ContentViewMaker} to do its initial configurations
     * @return instance of {@link ContentViewMaker}
     */
    public ContentViewMaker getContentViewMaker(Context applicationContext) {
        if(contentViewMaker == null){
            initContentViewMaker(applicationContext);
        }
        return contentViewMaker;
    }

    /**
     * @return instance of {@link FCMTokenManager}
     */
    public FCMTokenManager getFcmTokenManager() {
        return fcmTokenManager;
    }

    /**
     <p>It is used as callback for host app to notify that OneFeed
     SDK has completely loaded. And it has following method declarations -</p>
         <ol>
         <li>onSuccess</li>
         <li>onError</li>
         </ol>
     */
    public interface OnInitialized {
        void onSuccess();
        void onError();
    }

    /**
     * Lazy loads the instance of OFAnalytics when required <br>
     * i.e. instance creates when the first time getInstance methods is called
     */
    private static class LazyHolder {
        private static final OneFeedMain ourInstance = new OneFeedMain();
    }

}
