package com.wittyfeed.sdk.onefeed;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *
 * Holds whole UI of OneFeed, and manages it according to user-created events and OneFeedBuilder
 *
 * has 3 child fragments to manage -
 *      1) MainFeedFragment
 *          read more in MainFeedFragment
 *      2) SearchFeedFragment
 *          read more in SearchFeedFragment
 *      3) InterestFeedFragment
 *          read more in InterestFeedFragment
 *
 * has following responsibilities -
 *      1) show progressBar until data store is loaded
 *      2) updateUI accordingly when OneFeedBuilder notifies about DataStore loading completion
 *      3) switch feeds from MainFeed, SearchFeed, InterestFeed (interest selection)
 *          with respect to user-generated events
 *      4) execute method onBackClick() of callback interface OnBackClickClick from OneFeedBuilder
 *          when back is tapped, and if interface is set or non-null
 *      5) keep alive the proper fragments after screen orientation change or other onPause()
 *          events
 *
 * Read about: MainAdapter, OneFeedBuilder, HolderFragment
 */

public final class HolderFragment extends Fragment {

    private FrameLayout main_feed_holder_fl, search_feed_holder_fl, interest_feed_holder_fl;
    private TextView search_tv;
    private View search_v;
    private ImageView plus_iv;
    private ImageView back_iv;
    private RelativeLayout header_rl;
    private FragmentManager childFragmentManager;
    private View progress_rl;

    private boolean isAttached = false;

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_holder, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_tv = view.findViewById(R.id.search_tv);
        main_feed_holder_fl = view.findViewById(R.id.main_feed_holder_fl);
        search_feed_holder_fl = view.findViewById(R.id.search_feed_holder_fl);
        interest_feed_holder_fl = view.findViewById(R.id.interest_feed_holder_fl);
        plus_iv = view.findViewById(R.id.plus_iv);
        back_iv = view.findViewById(R.id.back_iv);
        header_rl = view.findViewById(R.id.header_rl);
        search_v = view.findViewById(R.id.search_v);
        progress_rl = view.findViewById(R.id.progress_rl);

        childFragmentManager = getChildFragmentManager();

        search_v.getLayoutParams().width = Constant.getSearchBarBackgroundWidth(getContext());
        search_tv.getLayoutParams().width = Constant.getSearchBarWidth(getContext());

        if(OneFeedMain.getInstance().oneFeedBuilder.isNotifiedDataStoreLoaded) {
            updateUI(false);
            showMainFragmentFeed();
        }
        else {
            updateUI(true);
        }

        search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragmentFeed(OneFeedBuilder.FragmentFeedType.SEARCH_FEED);
            }
        });

        plus_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFragmentFeed(OneFeedBuilder.FragmentFeedType.INTEREST_FEED);
            }
        });

        if(OneFeedMain.getInstance().oneFeedBuilder.hasSearchFragmentOrientationChanged) {
            showSearchFragmentFeed();
        } else if(OneFeedMain.getInstance().oneFeedBuilder.hasInterestFragmentOrientationChanged) {
            showInterestFeedFragment();
        }

        isAttached = true;

        OFLogger.log(OFLogger.DEBUG, "child fragment manager stack count: " + getChildFragmentManager().getBackStackEntryCount());
    }

    void notifyDataStoreLoaded() {
        OFLogger.log(OFLogger.DEBUG, OFLogger.DataStoreLoaded);
        if(isAttached) {
            updateUI(false);
            showMainFragmentFeed();
        }
    }

    private void updateUI(boolean isLoading){
        if(isLoading){

            header_rl.setVisibility(View.GONE);
            main_feed_holder_fl.setVisibility(View.GONE);
            search_feed_holder_fl.setVisibility(View.GONE);
            interest_feed_holder_fl.setVisibility(View.GONE);

            progress_rl.setVisibility(View.VISIBLE);

        } else {

            header_rl.setVisibility(View.VISIBLE);
            main_feed_holder_fl.setVisibility(View.VISIBLE);
            search_feed_holder_fl.setVisibility(View.VISIBLE);
            interest_feed_holder_fl.setVisibility(View.VISIBLE);

            progress_rl.setVisibility(View.GONE);
        }
    }

    private void switchFragmentFeed(OneFeedBuilder.FragmentFeedType fragmentFeedType){
        switch (fragmentFeedType){
            case MAIN_FEED:
                updateUI(false);
                break;
            case SEARCH_FEED:
                OneFeedMain.getInstance().dataStore.clearSearchFeedDataArray();
                OneFeedMain.getInstance().dataStore.setSearchFeedData(OneFeedMain.getInstance().dataStore.getSearchDefaultDatum());
                OneFeedMain.getInstance().dataStore.resetLastStringSearched();
                showSearchFragmentFeed();
                break;
            case INTEREST_FEED:
                showInterestFeedFragment();
                break;
        }
    }

    private void showMainFragmentFeed(){
        getChildFragmentManager().executePendingTransactions();
        if(getChildFragmentManager().findFragmentByTag("MainFeed") != null){
            getChildFragmentManager().beginTransaction().remove(childFragmentManager.findFragmentByTag("MainFeed")).commit();
        }
        getChildFragmentManager().beginTransaction().add(main_feed_holder_fl.getId(), new MainFeedFragment(), "MainFeed").commit();
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeOnBackClickInterface();
            }
        });
    }

    private void showSearchFragmentFeed(){
        childFragmentManager.executePendingTransactions();
        if(childFragmentManager.findFragmentByTag("SearchFeed") != null){
            childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("SearchFeed")).commitNow();
        }
        childFragmentManager.beginTransaction().add(search_feed_holder_fl.getId(), new SearchFeedFragment(), "SearchFeed").commit();
        plus_iv.setVisibility(View.GONE);
        search_v.setVisibility(View.GONE);
        search_tv.setVisibility(View.GONE);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childFragmentManager.executePendingTransactions();
                if(childFragmentManager.findFragmentByTag("SearchFeed") != null){
                    childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("SearchFeed")).commitNow();
                }
                back_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        executeOnBackClickInterface();
                    }
                });
                plus_iv.setVisibility(View.VISIBLE);
                search_v.setVisibility(View.VISIBLE);
                search_tv.setVisibility(View.VISIBLE);
            }
        });
        OneFeedMain.getInstance().oneFeedBuilder.hasSearchFragmentOrientationChanged = false;
    }

    private void showInterestFeedFragment(){
        childFragmentManager.executePendingTransactions();
        if(childFragmentManager.findFragmentByTag("InterestsFeed") != null){
            childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("InterestsFeed")).commitNow();
        }
        childFragmentManager.beginTransaction().add(search_feed_holder_fl.getId(), new InterestsFeedFragment(), "InterestsFeed").commit();
        plus_iv.setVisibility(View.GONE);
        search_v.setVisibility(View.GONE);
        search_tv.setVisibility(View.GONE);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childFragmentManager.executePendingTransactions();
                if(childFragmentManager.findFragmentByTag("InterestsFeed") != null){
                    childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("InterestsFeed")).commitNow();
                }
                back_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        executeOnBackClickInterface();
                    }
                });
                plus_iv.setVisibility(View.VISIBLE);
                search_v.setVisibility(View.VISIBLE);
                search_tv.setVisibility(View.VISIBLE);
            }
        });
        OneFeedMain.getInstance().oneFeedBuilder.hasInterestFragmentOrientationChanged = false;
    }

    private void executeOnBackClickInterface(){
        if(OneFeedMain.getInstance().oneFeedBuilder.onBackClickInterface == null){
            OFLogger.log(OFLogger.ERROR, "onBackClickInterface is null, set it first.");
            return;
        }
        OneFeedMain.getInstance().oneFeedBuilder.onBackClickInterface.onBackClick();
    }

}