package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 *
 * As per Single Responsibility Principle Architecture of OneFeed SDK
 *  this class acts as the only point of contact between Host app and OneFeed,
 *  all other services of OneFeed are accessible from here and handled here only
 *  no direct work is done
 *
 * Architecture is Singleton with Lazy-loading capability
 *
 * has 1 method init(); which -
 *      1) receives host app's credentials, fcm token of user and applicationContext for initial
 *          configurations of all services
 *      2) initialises
 *          (i) NetworkServiceManager, which is responsible for all OneFeed Web Services
 *              read more in NetworkServiceManager
 *         (ii) OFSharedPreference, which handles all SharedPreferences operations of OneFeed
 *              read more in OFSharedPreference
 *        (iii) DataStore, which is the end-point of Data-Model throughout the SDK
 *              read more in DataStore
 *         (iv) DataStoreManager, which manages the DataStore
 *              read more in DataStoreManager
 *          (v) FCMTokenManager, which handles and manages all FCM Token related operations
 *              read more in FCMTokenManager
 *         (vi) OFAnalytics, which handles and all analytics of SDK Usage
 *              read more in OFAnalytics
 *        (vii) ContentViewMaker, which warms up content or story views
 *              read more in ContentViewMaker
 *      3) executes loadDataStore() for loading data of OneFeedSDK using DataStoreManager
 *          read more in DataStoreManager
 *
 * has 1 interface OnInitialized, which is used as callback for host app to notify that OneFeed
 *  SDK has completely loaded. And it has following method declarations -
 *      1) onSuccess
 *      2) onError
 *
 */

public final class OneFeedMain {

    DataStore dataStore;
    private DataStoreManager dataStoreManager;
    OFSharedPreference ofSharedPreference;
    NetworkServiceManager networkServiceManager;
    FCMTokenManager fcmTokenManager;
    private ContentViewMaker contentViewMaker;

    private OnInitialized onInitialized;

    private OneFeedMain() {}

    public OneFeedBuilder oneFeedBuilder;

    public static OneFeedMain getInstance() {
        return LazyHolder.ourInstance;
    }

    /**
     *
     * @param applicationContext    the application context
     * @param APP_ID                the APP_ID of this app as per registration on OneFeed Dashboard
     * @param API_KEY               the API_KEY id of this app as per registration on OneFeed Dashboard
     */
    public final synchronized void init(Context applicationContext, String APP_ID, String API_KEY, String FCM_TOKEN){

        ApiClient.getInstance().init(applicationContext, APP_ID, API_KEY);

        initNetworkServiceManager(applicationContext);

        initOFSharedPref(applicationContext);

        initDataStore();

        initOneFeedBuilder();

        initContentViewMaker(applicationContext);

        initDataStoreManager();

        initFcmTokenManager(applicationContext, FCM_TOKEN);

        initAnalyticsManager(applicationContext);

        loadDataStore(applicationContext);

        OFAnalytics.getInstance().sendAnalytics(OFAnalytics.AnalyticsCat.WF_SDK, "OneFeed Initialized");
    }

    /**
     * init FCM token manager which when initialized will also check if the server has updated FCM token available or not
     * @param applicationContext the application context
     * @param FCM_TOKEN
     */
    private void initFcmTokenManager(Context applicationContext, String FCM_TOKEN){
        fcmTokenManager = new FCMTokenManager(FCM_TOKEN);
        networkServiceManager.setConfigRequestQueue(applicationContext);
        fcmTokenManager.pushFcmTokenIfRequired();
    }

    /**
     * init DataStoreManager which will load OneFeed Data from cache if avaialble and not expired,
     * Else will fetch fresh feed from server
     * Also has interface to notify OneFeedMain and ultimately host app about initialization success or exception
     */
    private void initDataStoreManager(){
        dataStoreManager = new DataStoreManager(new DataStoreManager.OnStoreManagerDidFinishDataFetch() {
            @Override
            public void onSuccess() {
                // when data did finish loading
                OFLogger.log( OFLogger.DEBUG, "blocks count: " + OneFeedMain.getInstance().dataStore.getMainFeedDataBlockArr().size() );
                OFLogger.log( OFLogger.DEBUG, "default search blocks count: " + OneFeedMain.getInstance().dataStore.getSearchDefaultDataBlockArr().size() );
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
     * initialized ContentViewMaker of OneFeed, if chrome available then executes its warmUp();
     * else does WebView optimisation configurations
     * @param applicationContext    the application context
     */
    private void initContentViewMaker(Context applicationContext){
        contentViewMaker = new ContentViewMaker(applicationContext);
    }

    /**
     * initializes OneFeedBuilder which provides availability of design customisations of OneFeed to HostApp
     * Host App shall explore numerous possibilities availed by getter setter of this object
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

    /**
     * initialises SharedPref that will be used by OneFeed
     * @param applicationContext    the appliation context
     */
    private void initOFSharedPref(Context applicationContext){
        ofSharedPreference = new OFSharedPreference(applicationContext);
    }

    /**
     * initializes service manager which is available through the SDK package, it handles the
     * networking requests.
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
     * Executes if DataStore loads successfully
     * Notifies host app about OneFeed Initialization Success if OnInitialized Interface is not null
     */
    private void callOnInitializedSuccess(){
        if (onInitialized != null) {
            oneFeedBuilder.notifyDataStoreLoaded();
            onInitialized.onSuccess();
        } else {
            OFLogger.log(OFLogger.ERROR, OFLogger.SDKMainInterFaceIsNull);
        }
    }

    /**
     * Executes if DataStore receives error or exception in loading
     * Notifies host app about OneFeed Initialization Error if OnInitialized Interface is not null
     */
    private void callOnInitializedError(){
        if (onInitialized != null) {
            onInitialized.onError();
        } else {
            OFLogger.log(OFLogger.ERROR, OFLogger.SDKMainInterFaceIsNull);
        }
    }

    /**
     *
     * @param arg   interface which will inform back the host app when OneFeed initialises successfully
     */
    public final synchronized void setOneFeedDidInitialisedCallback(OnInitialized arg){
        this.onInitialized = arg;
    }

    /**
     *
     * @return Ready to deploy OneFeedFragment
     */
    public final synchronized Fragment getOneFeedFragment(){
        Fragment fragmetToReturn = null;
        if(oneFeedBuilder == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.OneFeedMainIsNotInitialized);
        } else {
            fragmetToReturn = oneFeedBuilder.getHolderFragment();
        }

        OFAnalytics.getInstance().sendAnalytics(OFAnalytics.AnalyticsCat.WF_OneFeed, "OneFeed Initialized");

        return fragmetToReturn;
    }

    ContentViewMaker getContentViewMaker(Context applicationContext) {
        if(contentViewMaker == null){
            initContentViewMaker(applicationContext);
        }
        return contentViewMaker;
    }

    public FCMTokenManager getFcmTokenManager() {
        return fcmTokenManager;
    }

    /**
     * interface to notify host app about OneFeed initialization
     */
    public interface OnInitialized {
        void onSuccess();
        void onError();
    }

    /**
     *
     * the static object of DeviceMeta will initialise when its required to do so
     */
    private static class LazyHolder {
        private static final OneFeedMain ourInstance = new OneFeedMain();
    }

}
