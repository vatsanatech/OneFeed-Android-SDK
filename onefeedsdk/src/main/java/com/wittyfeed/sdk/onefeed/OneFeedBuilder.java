package com.wittyfeed.sdk.onefeed;

/**
 *
 * Manages the UI Views of MainFeed, SearchFeed, InterestFeed of OneFeed
 *
 * has 1 interface OnBackClickInterface, which provides endpoint for host app
 *  to set OnBackClickInterface to the OneFeed UI's back button
 *  it has following method declarations:
 *      1) onBackClick
 *
 * has 1 Enum FragmentFeedType corresponding to the Feed Fragments with values -
 *      1) MAIN_FEED
 *      2) SEARCH_FEED
 *      1) INTEREST_FEED
 *
 * has the following responsibilities -
 *      1) set interface OnBackClickInterface
 *      2) notify HolderFragment about DataStoreLoaded so that it can update UI
 *          its implemented lazily
 *          Read about: DataStoreManager, DataStore, HolderFragment
 *      3) future: will provide endpoint for host app to do customizations in feed's UI appearance
 *      4) provides accessible helper variables to whole UI of OneFeed
 *          such as hasSearchFragmentOrientationChanged, hasInterestFragmentOrientationChanged
 *
 */

public final class OneFeedBuilder {

    boolean hasSearchFragmentOrientationChanged = false;
    boolean hasInterestFragmentOrientationChanged = false;
    boolean isNotifiedDataStoreLoaded = false;
    FragmentFeedType openedFragmentFeedType;
    OnBackClickInterface onBackClickInterface;
    private HolderFragment holderFragment;

    OneFeedBuilder(){
        holderFragment = new HolderFragment();
    }

    HolderFragment getHolderFragment() {
        if(isNotifiedDataStoreLoaded){
            notifyHolderFragmentDataStoreLoaded();
        }
        return holderFragment;
    }

    void notifyDataStoreLoaded(){
        isNotifiedDataStoreLoaded = true;
        notifyHolderFragmentDataStoreLoaded();
    }

    private void notifyHolderFragmentDataStoreLoaded(){
        holderFragment.notifyDataStoreLoaded();
    }

    public void setOnBackClickInterface(OnBackClickInterface arg) {
        onBackClickInterface = arg;
    }

    enum FragmentFeedType{
        MAIN_FEED,
        SEARCH_FEED,
        INTEREST_FEED
    }

    public interface OnBackClickInterface{
        void onBackClick();
    }

}
