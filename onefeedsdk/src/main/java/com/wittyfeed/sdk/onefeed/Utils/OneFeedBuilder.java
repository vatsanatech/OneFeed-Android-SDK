package com.wittyfeed.sdk.onefeed.Utils;

import com.wittyfeed.sdk.onefeed.Views.Fragment.HolderFragment;

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

    public boolean hasSearchFragmentOrientationChanged = false;
    public boolean hasInterestFragmentOrientationChanged = false;
    public boolean isNotifiedDataStoreLoaded = false;
    public FragmentFeedType openedFragmentFeedType;
    public OnBackClickInterface onBackClickInterface;
    private HolderFragment holderFragment;

    public OneFeedBuilder(){
        holderFragment = new HolderFragment();
    }

    /**
     *Returns object of HolderFragment
     *
     */
    public HolderFragment getHolderFragment() {
        if(isNotifiedDataStoreLoaded){
            notifyHolderFragmentDataStoreLoaded();
        }
        return holderFragment;
    }

    /**
     *Notifies about the data store load
     *
     */
    public void notifyDataStoreLoaded(){
        isNotifiedDataStoreLoaded = true;
        notifyHolderFragmentDataStoreLoaded();
    }

    /**
     *Notifies about the data store load and updates the UI accordingly
     *
     */
    private void notifyHolderFragmentDataStoreLoaded(){
        holderFragment.notifyDataStoreLoaded();
    }

    /**
     *Sets up an instance of OnBackClickLInterface
     *
     */
    public void setOnBackClickInterface(OnBackClickInterface arg) {
        onBackClickInterface = arg;
    }

    /**
     *Enum that Keeps the type of feeds in the sdk
     *
     */
    public enum FragmentFeedType{
        MAIN_FEED,
        SEARCH_FEED,
        INTEREST_FEED
    }

    /**
     *Interface to handle the back button
     *
     */
    public interface OnBackClickInterface{
        void onBackClick();
    }

}
