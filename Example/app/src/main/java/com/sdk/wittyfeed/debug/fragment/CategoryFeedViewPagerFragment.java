package com.sdk.wittyfeed.debug.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdk.wittyfeed.debug.R;
import com.sdk.wittyfeed.debug.adapter.CategoryFeedRVAdapter;
import com.sdk.wittyfeed.debug.utils.CustomFixedRecyclerView;
import com.sdk.wittyfeed.wittynativesdk.Interfaces.WittyFeedSDKCardFetcherInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKCardFetcher;
import com.sdk.wittyfeed.wittynativesdk.utils.WittyFeedSDKUtils;

import java.util.ArrayList;

/**
 * Created by aishwarydhare on 12/02/18.
 */

public class CategoryFeedViewPagerFragment extends Fragment {

    Activity activity;
    CustomFixedRecyclerView feed_rv;
    WittyFeedSDKCardFetcher wittyFeedSDKCardFetcher;
    WittyFeedSDKCardFetcherInterface wittyFeedSDKCardFetcherInterface;
    CategoryFeedRVAdapter categoryFeedRVAdapter;
    private String TAG = "WF_SDK";
    boolean is_fetching_data = false;
    ArrayList<View> witty_cards;
    String cat_name;
    String cat_pos;
    LinearLayoutManager linearLayoutManager;
    View root_view;
    private View rv_view;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root_view = inflater.inflate(R.layout.item_view_pager, container, false);

        witty_cards = new ArrayList<>();

        //=================================
        //======== NATIVE CARD DOC ========
        //=================================

        // Total Steps 3
            // First Step: Create an interface of type WittyFeedSDKCardFetcherInterface in which four methods will be there as demonstrated below

        // Second Step: Initialize an object of WittyFeedSDKCardFetcher to fetch cards, NOTE- use same object from WittyFeedSDKSingleton as demonstrated below
            // if you don't want to see any repeated card anywhere in the app. Otherwise you can initialize different object of WittyFeedSDKCardFetcher

        // Third Step: Use fetch_a_card() method of WittyFeedSDKCardFetcher to place a WittyFeed SDK Card in one your ViewGroups (i.e. views, layouts etc)
            // fetch_a_card() has two overload methods,
                // First overloaded fetch_a_card() method fetches a random card from any cateogry and,
                // Second overloaded fetch_a_card() method fetches a card of specific category which will passed as the third argument of String type
                    // First argument: is of String TYPE and is used to define your own custom tag that you will later recieve in onCardReceived (its purpose is similar to itemType parameter in OnCreateViewHolder of RecyclerView)
                    // Second argument: is FLOAT TYPE for adjusting font_size_ratio of cards which should be between 0.0f to 1.0f (example: if your layout covers full screen then pass 1.0f)
                    // Third argument: is of STRING TYPE for the specific category, it may return null if category is sent wrong

        // Other Available Methods by WittyFeedSDKCardFetcher:
            // clearCardFetchedHistory(): Clears history that keep tracks what card have been used and what not,
            // clearing this will fetch again the very first card, that was fetched.

        //
        // First Step is this
        //
        wittyFeedSDKCardFetcherInterface = new WittyFeedSDKCardFetcherInterface() {
            @Override
            public void onWillStartFetchingMoreData() {
                Log.d(TAG, "onWillStartFetchingMoreData: fetching more cards from server");
                is_fetching_data = true;
            }

            @Override
            public void onMoreDataFetched() {
                Log.d(TAG, "onMoreDataFetched: more cards fetched");
                is_fetching_data = false;
            }

            @Override
            public void onCardReceived(String customTag, View cardViewFromWittyFeed) {
                witty_cards.add(cardViewFromWittyFeed);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        categoryFeedRVAdapter.notifyDataSetChanged();
                    }
                }).run();
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError: "+ e.getMessage(), e);
            }
        };


        //
        // Second Step is this
        //
        // NOTE: If you don't want cards to repeat anywhere in your app then always use a static singleton object (create it in the class that extends Application)
        //
        // NOTE: either pass the 'wittyFeedSDKCardFetcherInterface' object in the constructor of 'WittyFeedSDKCardFetcher'
            // or else use the method 'setWittyFeedSDKCardFetcherInterface' to set it later
        //
        wittyFeedSDKCardFetcher = new WittyFeedSDKCardFetcher(getActivity());
        wittyFeedSDKCardFetcher.setWittyFeedSDKCardFetcherInterface(wittyFeedSDKCardFetcherInterface);

        if(getArguments() != null){
            Bundle bundle = getArguments();
            try {
                cat_name = bundle.getString("cat_name");
                cat_pos = bundle.getString("cat_pos");
            } catch (Exception e) {
                Log.e(TAG, "INVALID CAT_NAME", e);
            }
        } else {
            Log.e(TAG, "onCreateView: AT_NAME NOT FOUND");
        }

        feed_rv = root_view.findViewById(R.id.feed_rv);
        categoryFeedRVAdapter = new CategoryFeedRVAdapter(witty_cards , getActivity());

        for (int i = 0; i <= 2; i++) {
            //
            // Third and Last Step is this
            //
            wittyFeedSDKCardFetcher.fetch_a_card("category_card", 0.8f, cat_name);
        }

        //=====================================
        //======== NATIVE CARD DOC END ========
        //=====================================

        container.addView(root_view);

        return root_view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        feed_rv.setLayoutManager(linearLayoutManager);
        feed_rv.setAdapter(categoryFeedRVAdapter);

        final LinearLayoutManager final_linearLayoutManager = linearLayoutManager;

        feed_rv.addOnScrollListener(new CustomFixedRecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!feed_rv.canScrollVertically(1) && !WittyFeedSDKUtils.isConnected(activity)) {
                    Snackbar.make(view, "No Internet, Please connect to a Network", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = final_linearLayoutManager.getChildCount();
                int totalItemCount = final_linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = final_linearLayoutManager.findFirstVisibleItemPosition();
                if(WittyFeedSDKUtils.isConnected(activity)){
                    if(firstVisibleItemPosition+visibleItemCount > totalItemCount- 1  && !is_fetching_data){
                        for (int j = 0; j <= 2; j++) {
                            if(!is_fetching_data){
                                wittyFeedSDKCardFetcher.fetch_a_card("category_card", 0.8f, cat_name);
                            }
                        }
                    }
                }else {
                    if(firstVisibleItemPosition+visibleItemCount>=totalItemCount) {
                        Snackbar.make(view, "No Internet, Please connect to a Network", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
