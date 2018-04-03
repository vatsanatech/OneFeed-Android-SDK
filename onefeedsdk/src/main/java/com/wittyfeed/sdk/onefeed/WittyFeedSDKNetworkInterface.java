package com.wittyfeed.sdk.onefeed;

/**
 * Created by aishwarydhare on 16/10/17.
 */

interface WittyFeedSDKNetworkInterface {
    void onSuccess(String jsonString, boolean isLoadedMore, boolean isBackgroundRefresh);
    void onError(Exception e);
}