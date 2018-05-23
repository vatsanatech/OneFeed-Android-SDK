package com.wittyfeed.sdk.onefeed.Views.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wittyfeed.sdk.onefeed.DataStoreManagement.DataStoreParser;
import com.wittyfeed.sdk.onefeed.Models.Block;
import com.wittyfeed.sdk.onefeed.NetworkServiceManager;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.Utils.OneFeedBuilder;
import com.wittyfeed.sdk.onefeed.OneFeedMain;
import com.wittyfeed.sdk.onefeed.R;
import com.wittyfeed.sdk.onefeed.Utils.Utils;
import com.wittyfeed.sdk.onefeed.Views.Adapter.MainAdapter;

import java.util.ArrayList;

/**
 *
 * Holds the MainFeed of OneFeed, and is managed by Holder Fragment
 *  Read about: HolderFragment, OneFeedBuilder
 *
 * it has following responsibilities -
 *      1) prepare recycler view to populate the Model-Data from DataStore
 *          Read about: DataStore, DataStoreManager, OneFeedMain
 *      2) prepare MainAdapter to set with the recycler view
 *          Read about: MainAdapter
 *      3) prepare LinearLayoutManager to set with the recycler view
 *      4) use linearLayoutManager to track user scroll and set setFetchMoreToRecyclerView
 *          to implement endless feed,
 *          Read about: NetworkServiceManager
 *      5) implement and initialise swipe-to-refresh layout, to refresh MainFeed DataStore on
 *          pulling down gesture
 *          Read about: NetworkServiceManager
 *      6) notify OneFeedBuilder on screen-orientation change and other onPause() events
 *
 * Read about: MainAdapter, OneFeedBuilder, HolderFragment
 */

public class MainFeedFragment extends Fragment {

    private SwipeRefreshLayout mainFeed_srl;
    private boolean isFetchingData = false;
    private MainAdapter mainAdapter;
    private RecyclerView onefeed_rv;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(OneFeedMain.getInstance().oneFeedBuilder!=null)
            OneFeedMain.getInstance().oneFeedBuilder.openedFragmentFeedType = OneFeedBuilder.FragmentFeedType.MAIN_FEED;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_main_feed, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onefeed_rv = view.findViewById(R.id.onefeed_rv);
        mainFeed_srl = view.findViewById(R.id.mainFeed_srl);

        mainAdapter = new MainAdapter((ArrayList<Block>) OneFeedMain.getInstance().dataStore.getMainFeedDataBlockArr(), 1);

        linearLayoutManager = new LinearLayoutManager(getContext());
        onefeed_rv.setLayoutManager(linearLayoutManager);
        onefeed_rv.setAdapter(mainAdapter);

        setFetchMoreToRecyclerView();
        setRefreshFeedToRecyclerView();
    }

    private void setFetchMoreToRecyclerView(){
        onefeed_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && !Utils.isConnected(getContext())) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "No Internet, Please connect to a Network", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if (!isFetchingData) {
                    if(firstVisibleItemPosition + visibleItemCount > totalItemCount - 4){
                        if(Utils.isConnected(getContext())){

                            isFetchingData = true;

                            OneFeedMain.getInstance().networkServiceManager.hitMainFeedDataAPI(
                                    OneFeedMain.getInstance().dataStore.getMainFeedDataOffset(),
                                    new NetworkServiceManager.OnNetworkServiceDidRespond() {
                                        @Override
                                        public void onSuccessResponse(String response) {
                                            OneFeedMain.getInstance().dataStore.appendInMainFeedDataArray( DataStoreParser.parseMainFeedString(response) );

                                            mainAdapter.notifyDataSetChanged();
                                            OneFeedMain.getInstance().dataStore.incrementMainFeedDataOffset();
                                            OFLogger.log(OFLogger.DEBUG, OFLogger.FetchMoreEnd);
                                            isFetchingData = false;
                                        }

                                        @Override
                                        public void onError() {
                                            isFetchingData = false;
                                            OFLogger.log(OFLogger.ERROR, OFLogger.FetchMoreError);
                                        }
                                    });
                            OFLogger.log(OFLogger.DEBUG, OFLogger.FetchMoreSTART);

                        } else {
                            isFetchingData = false;
                        }
                    }
                }
            }
        });
    }

    private void setRefreshFeedToRecyclerView(){
        mainFeed_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainFeed_srl.setRefreshing(true);
                if(!Utils.isConnected(getContext())){
                    mainFeed_srl.setRefreshing(false);
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "No Internet, Please connect to a Network", Snackbar.LENGTH_LONG).show();
                    OFLogger.log(OFLogger.DEBUG, OFLogger.NoInternet);
                }

                OFLogger.log(OFLogger.DEBUG, OFLogger.RefreshDataStart);
                OneFeedMain.getInstance().dataStore.resetMainFeedDataOffset();
                OneFeedMain.getInstance().networkServiceManager.hitMainFeedDataAPI(
                        OneFeedMain.getInstance().dataStore.getMainFeedDataOffset(),
                        new NetworkServiceManager.OnNetworkServiceDidRespond() {
                            @Override
                            public void onSuccessResponse(String response) {
                                OneFeedMain.getInstance().dataStore.clearMainFeedDataArray();
                                OneFeedMain.getInstance().dataStore.setMainFeedData( DataStoreParser.parseMainFeedString(response) );
                                mainAdapter.setBlock_arr((ArrayList<Block>) OneFeedMain.getInstance().dataStore.getMainFeedDataBlockArr());
                                mainAdapter.notifyDataSetChanged();
                                mainFeed_srl.setRefreshing(false);
                                OneFeedMain.getInstance().dataStore.incrementMainFeedDataOffset();
                                OFLogger.log(OFLogger.DEBUG, OFLogger.RefreshDataEnd);
                            }

                            @Override
                            public void onError() {
                                OFLogger.log(OFLogger.DEBUG, OFLogger.RefreshDataError);
                            }
                        });
            }
        });
    }
}
