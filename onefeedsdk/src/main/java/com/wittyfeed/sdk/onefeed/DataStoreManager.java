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

    private OnStoreManagerDidFinishDataFetch onStoreManagerDidFinishDataFetch;

    DataStoreManager(OnStoreManagerDidFinishDataFetch arg){
        this.onStoreManagerDidFinishDataFetch = arg;
    }

    void prepareOneFeedMainData(Context applicationContext) {
        if(DataStoreCacheManager.checkIfCacheAvailable(applicationContext)){

            String dataStr = DataStoreCacheManager.readCachedJSON(applicationContext);
            if(!dataStr.equalsIgnoreCase("")){
                // parse cache
                MainDatum mainDatum = DataStoreParser.parseMainFeedString(dataStr);
                MainDatum searchDefaultDatum = DataStoreParser.parseSearchDefaultString(dataStr);

                if (mainDatum == null || searchDefaultDatum == null) {
                    // cache loaded is invalid or deprecated, requesting fresh feed instead
                    OFLogger.log(OFLogger.ERROR, "cache parsing failed, cache is either invalid or deprecated, requesting fresh feed instead");
                    requestFreshMainFeedData(false, applicationContext);

                } else {
                    OneFeedMain.getInstance().dataStore.setMainFeedData(mainDatum);
                    OneFeedMain.getInstance().dataStore.setSearchDefaultData(searchDefaultDatum);
                    OneFeedMain.getInstance().dataStore.incrementMainFeedDataOffset();
                    OFLogger.log(OFLogger.DEBUG, OFLogger.CacheReadSuccess);
                    OFLogger.log(OFLogger.DEBUG, "cache loaded successfully");
                    onStoreManagerDidFinishDataFetch.onSuccess();
                }

            } else {
                // unable to parse cache, request fresh feed instead
                OFLogger.log(OFLogger.ERROR, "unable to parse cache, requesting fresh feed instead");
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
