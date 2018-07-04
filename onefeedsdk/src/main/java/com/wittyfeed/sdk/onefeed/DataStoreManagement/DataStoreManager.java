package com.wittyfeed.sdk.onefeed.DataStoreManagement;

import android.content.Context;

import com.wittyfeed.sdk.onefeed.NetworkServiceManager;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.OneFeedMain;
import com.wittyfeed.sdk.onefeed.Models.MainDatum;

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

public final class DataStoreManager {

    private OnStoreManagerDidFinishDataFetch onStoreManagerDidFinishDataFetch;

    public DataStoreManager(OnStoreManagerDidFinishDataFetch arg){
        this.onStoreManagerDidFinishDataFetch = arg;
    }

    /**
     * Prepares Main data: Checks if cache is available, if yes reads from it else requests for fresh feed data
     */
    public void prepareOneFeedMainData(Context applicationContext) {
        if(DataStoreCacheManager.checkIfCacheAvailable(applicationContext)){

            String dataStr = DataStoreCacheManager.readCachedJSON(applicationContext);
            if(!dataStr.isEmpty()){
                // parse cache
                MainDatum mainDatum = DataStoreParser.parseMainFeedString(dataStr);
                MainDatum searchDefaultDatum = DataStoreParser.parseSearchDefaultString(dataStr);
                MainDatum nonRepeatingDatum  =  DataStoreParser.parseNonRepeatingDataString(dataStr);

                if (mainDatum == null || searchDefaultDatum == null || nonRepeatingDatum == null) {
                    // cache loaded is invalid or deprecated, requesting fresh feed instead
                    OFLogger.log(OFLogger.ERROR, OFLogger.CacheIsDeprecated);
                    requestFreshMainFeedData(false, applicationContext);

                } else {
                    OneFeedMain.getInstance().dataStore.setMainFeedData(mainDatum);
                    OneFeedMain.getInstance().dataStore.setSearchDefaultData(searchDefaultDatum);
                    OneFeedMain.getInstance().dataStore.setNonRepeatingDatum(nonRepeatingDatum);
                    OneFeedMain.getInstance().dataStore.incrementMainFeedDataOffset();
                    OFLogger.log(OFLogger.DEBUG, OFLogger.CacheReadSuccess);
                    OFLogger.log(OFLogger.DEBUG, OFLogger.CacheLoadSuccessful);
                    onStoreManagerDidFinishDataFetch.onSuccess();
                }

            } else {
                // unable to parse cache, request fresh feed instead
                OFLogger.log(OFLogger.ERROR, OFLogger.CacheParsingFailed);
                requestFreshMainFeedData(false, applicationContext);

            }
            // background refresh cache
            requestFreshMainFeedData(true, applicationContext);

        } else {
            // if cache not available
            OFLogger.log(OFLogger.DEBUG, OFLogger.CacheUnavailable);
            requestFreshMainFeedData(false, applicationContext);

        }
    }

    /**
     * Requests Main Fresh Feed Data
     */
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
                            OneFeedMain.getInstance().dataStore.setMainFeedData( DataStoreParser.parseMainFeedString(response));
                            OneFeedMain.getInstance().dataStore.incrementMainFeedDataOffset();
                            OneFeedMain.getInstance().dataStore.setSearchDefaultData( DataStoreParser.parseSearchDefaultString(response));
                            OneFeedMain.getInstance().dataStore.setNonRepeatingDatum(DataStoreParser.parseNonRepeatingDataString(response));
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

    /**
     * Interface to get notified about the data fetching status, Uses Two methods: OnSuccess(), OnError()
     */
    public interface OnStoreManagerDidFinishDataFetch{
        void onSuccess();
        void onError();
    }

}
