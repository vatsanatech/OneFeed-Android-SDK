package com.onefeedsdk.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.onefeedsdk.R;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.event.Event;
import com.onefeedsdk.job.GetHomeFeedJob;
import com.onefeedsdk.job.PostUserTrackingJob;
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.ui.adapter.MainFeedAdapter;
import com.onefeedsdk.util.Util;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 13-September-2018
 * Time: 17:36
 */
public class MainFeedFragment extends Fragment implements View.OnClickListener {

    private LinearLayout toolbar;
    private RecyclerView feedRecycler;
    private TextView errorMsgView;
    private TextView searchText;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeContainer;

    private LinearLayoutManager layoutManager;

    private MainFeedAdapter feedAdapter;
    private List<FeedModel.Blocks> feedDataList;
    private FeedModel feedModel;

    private int offset = 0;
    private boolean isSdkInitialize = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.onefeed_fragment_main_feed, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        feedDataList = new ArrayList<>();

        toolbar = getView().findViewById(R.id.toolbar);
        progressBar = getView().findViewById(R.id.progress_bar);
        errorMsgView = getView().findViewById(R.id.error_msg);
        feedRecycler = getView().findViewById(R.id.recycler_feed);
        searchText = getView().findViewById(R.id.view_search);
        swipeContainer = getView().findViewById(R.id.swipe_container);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeed(false, 0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        searchText.setOnClickListener(this);
        layoutManager = new LinearLayoutManager(getContext());
        feedRecycler.setLayoutManager(layoutManager);
        feedAdapter = new MainFeedAdapter(feedRecycler);
        //Load more story
        feedAdapter.addListener(new MainFeedAdapter.AddListener() {
            @Override
            public void loadMoreFeed() {

                offset += 1;
                fetchFeed(true, offset);
            }
        });
        feedAdapter.setBlocks(feedDataList);
        feedRecycler.setAdapter(feedAdapter);
        defaultSetFeed(false);

    }

    private void fetchFeed(boolean loadMoreFeed, int offset){
        if(Util.checkNetworkConnection(getActivity())) {
            this.offset = offset;
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(new GetHomeFeedJob(loadMoreFeed, offset));
        }else {
            swipeContainer.setRefreshing(false);
            Util.showToastMsg(getActivity(), getString(R.string.error_network_msg));
        }
    }

    //If available in local storage
    private void defaultSetFeed(boolean loadMoreFeed){
        String feedString = OneFeedSdk.getInstance().getDefaultAppSharedPreferences().getString(Constant.FEED_TEMP, "");
       if(!TextUtils.isEmpty(feedString)) {
           if (!TextUtils.isEmpty(feedString) && !loadMoreFeed) {
               Event.FeedEvent event = new GsonBuilder().create().fromJson(feedString, Event.FeedEvent.class);
               OneFeedSdk.getInstance().getEventBus().postSticky(event);
           }
           feedAdapter.setLoaded();
       }else {
           fetchFeed(false, 0);
       }
    }

    @Override
    public void onStart() {
        super.onStart();
        OneFeedSdk.getInstance().getEventBus().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        OneFeedSdk.getInstance().getEventBus().unregister(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && isResumed()) {
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                    new PostUserTrackingJob(Constant.ONE_FEED, Constant.ONE_FEED_BY_SWIPE));
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(final Event.FeedEvent event) {
        feedAdapter.setLoaded();
        swipeContainer.setRefreshing(false);
        OneFeedSdk.getInstance().getEventBus().removeStickyEvent(event);

        if (event.getFeed() != null && event.isSuccess() && !event.isLoadMoreFeed()) {

            feedModel = event.getFeed();
            feedDataList.clear();
            feedDataList.addAll(event.getFeed().getFeedData().getBlocks());
            feedAdapter.notifyDataSetChanged();
            toolbar.setVisibility(View.VISIBLE);
            errorMsgView.setVisibility(View.GONE);
            feedRecycler.setVisibility(View.VISIBLE);

            //SDK INITIALIZE PROPERLY
            if (!isSdkInitialize) {

                isSdkInitialize = true;
                if (getUserVisibleHint()) {
                    //Tracking OneFeed View
                    OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                            new PostUserTrackingJob(Constant.ONE_FEED, Constant.ONE_FEED_BY_CLICK));
                }
            }
        } else if(event.getFeed() != null && event.isLoadMoreFeed()) {

            feedModel = event.getFeed();
            feedDataList.addAll(event.getFeed().getFeedData().getBlocks());
            feedAdapter.setLoaded();
            feedAdapter.notifyDataSetChanged();
        } else {
            if (feedDataList != null && !feedDataList.isEmpty()) {
                //errorMsgView.setVisibility(View.VISIBLE);
                // feedRecycler.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.view_search) {
            try {
                SearchFeedFragment searchFeedFragment = new SearchFeedFragment();
                if (feedModel != null && feedModel.getSearchData() != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constant.SEARCH, feedModel.getSearchData());
                    searchFeedFragment.setArguments(bundle);
                }
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_container, searchFeedFragment)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
