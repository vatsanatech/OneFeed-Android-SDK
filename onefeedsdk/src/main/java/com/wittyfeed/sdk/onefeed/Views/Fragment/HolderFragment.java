package com.wittyfeed.sdk.onefeed.Views.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wittyfeed.sdk.onefeed.OFAnalytics;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.Utils.OneFeedBuilder;
import com.wittyfeed.sdk.onefeed.OneFeedMain;
import com.wittyfeed.sdk.onefeed.R;
import com.wittyfeed.sdk.onefeed.Utils.Utils;

/**
 * Holds whole UI of OneFeed, and manages it according to user-created events and OneFeedBuilder
 * <p>
 * has 3 child fragments to manage -
 * 1) MainFeedFragment
 * read more in MainFeedFragment
 * 2) SearchFeedFragment
 * read more in SearchFeedFragment
 * 3) InterestFeedFragment
 * read more in InterestFeedFragment
 * <p>
 * has following responsibilities -
 * 1) show progressBar until data store is loaded
 * 2) updateUI accordingly when OneFeedBuilder notifies about DataStore loading completion
 * 3) switch feeds from MainFeed, SearchFeed, InterestFeed (interest selection)
 * with respect to user-generated events
 * 4) execute method onBackClick() of callback interface OnBackClickClick from OneFeedBuilder
 * when back is tapped, and if interface is set or non-null
 * 5) keep alive the proper fragments after screen orientation change or other onPause()
 * events
 * <p>
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
    private int backButtonWidth = 50;

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

        hideBackButton(0);

        if (OneFeedMain.getInstance().oneFeedBuilder.isNotifiedDataStoreLoaded) {
            updateUI(false);
            showMainFragmentFeed();
        } else {
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

        if (OneFeedMain.getInstance().oneFeedBuilder.hasSearchFragmentOrientationChanged) {
            showSearchFragmentFeed();
        } else if (OneFeedMain.getInstance().oneFeedBuilder.hasInterestFragmentOrientationChanged) {
            showInterestFeedFragment();
        }

        isAttached = true;

        OFLogger.log(OFLogger.DEBUG, OFLogger.ChildFragmentManagerStackCount + getChildFragmentManager().getBackStackEntryCount());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            OFAnalytics.getInstance().sendAnalytics(OFAnalytics.AnalyticsType.OneFeed, "OneFeed Opened");
        }
    }

    private void hideBackButton(int id){
//        search_v.getLayoutParams().width = Utils.getSearchBarBackgroundWidth(getContext());
//        search_tv.getLayoutParams().width = Utils.getSearchBarWidth(getContext());

        if(id == 0 && OneFeedMain.isHideBackButton()){
            back_iv.setImageResource(0);
            back_iv.setPadding((int) getResources().getDimension(R.dimen.margin_15), 0, 0, 0);
        }else{
            back_iv.setImageResource(R.drawable.ic_back);
            back_iv.setPadding((int) getResources().getDimension(R.dimen.margin_15), 0,
                    (int) getResources().getDimension(R.dimen.margin_15), 0);
        }
    }

    public void notifyDataStoreLoaded() {
        OFLogger.log(OFLogger.DEBUG, OFLogger.DataStoreLoaded);
        if (isAttached) {

            updateUI(false);
            showMainFragmentFeed();
        }
    }

    private void updateUI(boolean isLoading) {
        if (isLoading) {

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

    private void switchFragmentFeed(OneFeedBuilder.FragmentFeedType fragmentFeedType) {
        switch (fragmentFeedType) {
            case MAIN_FEED:
                hideBackButton(0);
                updateUI(false);
                break;
            case SEARCH_FEED:
                hideBackButton(1);
                OneFeedMain.getInstance().getInstanceDataStore().clearSearchFeedDataArray();
                OneFeedMain.getInstance().getInstanceDataStore().setSearchFeedData(OneFeedMain.getInstance().getInstanceDataStore().getSearchDefaultDatum());
                OneFeedMain.getInstance().getInstanceDataStore().resetLastStringSearched();
                showSearchFragmentFeed();
                break;
            case INTEREST_FEED:

                hideBackButton(1);
                showInterestFeedFragment();
                break;
        }
    }

    private void showMainFragmentFeed() {
        getChildFragmentManager().executePendingTransactions();
        if (getChildFragmentManager().findFragmentByTag("MainFeed") != null) {
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

    private void showSearchFragmentFeed() {
        childFragmentManager.executePendingTransactions();
        if (childFragmentManager.findFragmentByTag("SearchFeed") != null) {
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
                if (childFragmentManager.findFragmentByTag("SearchFeed") != null) {
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

                hideBackButton(0);
            }
        });
        OneFeedMain.getInstance().oneFeedBuilder.hasSearchFragmentOrientationChanged = false;
    }

    private void showInterestFeedFragment() {
        childFragmentManager.executePendingTransactions();
        if (childFragmentManager.findFragmentByTag("InterestsFeed") != null) {
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
                if (childFragmentManager.findFragmentByTag("InterestsFeed") != null) {
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
                hideBackButton(0);
            }
        });
        OneFeedMain.getInstance().oneFeedBuilder.hasInterestFragmentOrientationChanged = false;
    }

    private void executeOnBackClickInterface() {
        if (OneFeedMain.getInstance().oneFeedBuilder.onBackClickInterface == null) {
            OFLogger.log(OFLogger.ERROR, OFLogger.BackInterfaceIsNull);
            return;
        }
        OneFeedMain.getInstance().oneFeedBuilder.onBackClickInterface.onBackClick();
    }

}