package com.wittyfeed.sdk.onefeed;

import android.content.Context;

/**
 *
 * Manages the data-model which is used through the app, and performs following actions -
 *      1) Fetches the fresh data when required
 *          and parses the raw-data into Model-Data and stores in DataStore object
 *      2) Saves or updates the data in Cache
 *      3) loads raw-data from cache when available and updates the raw-data it in background
 *          and parses the raw-data into Model-Data and stores in DataStore object
 *      4) notifies by callback about task completion
 *          using the interface OnStoreManagerDidFinishDataFetch
 *
 * has 1 interface OnStoreManagerDidFinishDataFetch, which has 2 methods -
 *      1) onSuccess
 *      2) onError
 *
 * Read about: DataCacheManager, DataStore, DataParser
 */

final class DataStoreManager {

    // TODO: 23/04/18 handle crash if cache-loading fails

    private OnStoreManagerDidFinishDataFetch onStoreManagerDidFinishDataFetch;

    DataStoreManager(OnStoreManagerDidFinishDataFetch arg){
        this.onStoreManagerDidFinishDataFetch = arg;
    }

    void prepareOneFeedMainData(Context applicationContext) {
        if(DataStoreCacheManager.checkIfCacheAvailable(applicationContext)){

            String dataStr = DataStoreCacheManager.readCachedJSON(applicationContext);
            if(!dataStr.equalsIgnoreCase("")){
                // parse cache
                OneFeedMain.getInstance().dataStore.setMainFeedData( DataStoreParser.parseMainFeedString(dataStr) );
                OneFeedMain.getInstance().dataStore.setSearchDefaultData( DataStoreParser.parseSearchDefaultString(dataStr) );
                OneFeedMain.getInstance().dataStore.incrementMainFeedDataOffset();
                OFLogger.log(OFLogger.DEBUG, OFLogger.CacheReadSuccess);
                onStoreManagerDidFinishDataFetch.onSuccess();

            } else {
                // unable to parse cache, request fresh feed instead
                requestFreshMainFeedData(false, applicationContext);

            }
            // background refresh cache
            requestFreshMainFeedData(true, applicationContext);

        } else {
            // if cache not available
            OFLogger.log(OFLogger.DEBUG, "cache unavailable");
            requestFreshMainFeedData(false, applicationContext);

        }
    }

    private void requestFreshMainFeedData(final boolean isBackgroundRefresh, final Context applicationContext){
        OneFeedMain.getInstance().networkServiceManager.setMainFeedRequestQueue(applicationContext);

        int offsetToSend = 0;
        if(!isBackgroundRefresh) {
            OneFeedMain.getInstance().dataStore.resetMainFeedDataOffset();
            offsetToSend = OneFeedMain.getInstance().dataStore.getMainFeedDataOffset();
        }

        OneFeedMain.getInstance().networkServiceManager.hitMainFeedDataAPI(
                offsetToSend,
                new NetworkServiceManager.OnNetworkServiceDidRespond() {
                    @Override
                    public void onSuccessResponse(String response) {
                        DataStoreCacheManager.createCachedJSON(response, applicationContext);
                        if(!isBackgroundRefresh){
                            OneFeedMain.getInstance().dataStore.setMainFeedData( DataStoreParser.parseMainFeedString(response) );
                            OneFeedMain.getInstance().dataStore.incrementMainFeedDataOffset();
                            OneFeedMain.getInstance().dataStore.setSearchDefaultData( DataStoreParser.parseSearchDefaultString(response) );
                            onStoreManagerDidFinishDataFetch.onSuccess();
                        }
                        OFLogger.log(OFLogger.DEBUG, OFLogger.MainFeedFetchedSuccess);
                    }
                    @Override
                    public void onError() {
                        onStoreManagerDidFinishDataFetch.onError();
                        OFLogger.log(OFLogger.DEBUG, OFLogger.MainFeedFetchedError);
                    }
                });
    }

    interface OnStoreManagerDidFinishDataFetch{
        void onSuccess();
        void onError();
    }

}
