package com.sdk.wittyfeed.debug.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.sdk.wittyfeed.debug.utils.CustomFixedRecyclerView;
import com.sdk.wittyfeed.debug.R;
import com.sdk.wittyfeed.debug.adapter.CategoryFeedRVAdapter;
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
    int cat_pos;
    LinearLayoutManager linearLayoutManager;
    View root_view;


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
        wittyFeedSDKCardFetcher = new WittyFeedSDKCardFetcher(getActivity());

        if(getArguments() != null){
            Bundle bundle = getArguments();
            try {
                cat_name = bundle.getString("cat_name");
                cat_pos = bundle.getInt("cat_pos");
            } catch (Exception e) {
                Log.e(TAG, "INVALID CAT_NAME", e);
            }
        } else {
            Log.e(TAG, "onCreateView: AT_NAME NOT FOUND");
        }

        feed_rv = root_view.findViewById(R.id.feed_rv);
        categoryFeedRVAdapter = new CategoryFeedRVAdapter(witty_cards , getActivity());

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
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        categoryFeedRVAdapter.notifyDataSetChanged();
                    }
                };
                handler.post(runnable);
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "onError: "+ e.getMessage(), e);
            }
        };

        wittyFeedSDKCardFetcher.set_to_open_content_view_directly(true);
        wittyFeedSDKCardFetcher.setWittyFeedSDKCardFetcherInterface(wittyFeedSDKCardFetcherInterface);

        for (int i = 0; i <= 2; i++) {
            wittyFeedSDKCardFetcher.fetch_a_card("category_card", 0.8f, cat_name);
        }

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
