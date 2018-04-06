package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by aishwarydhare on 02/04/18.
 */

public class WittyFeedSDKMainFeedFragment extends Fragment {

    Context activityContext;

    RecyclerView onefeed_rv;
    LinearLayoutManager main_feed_linearLayoutManager;
    OneFeedAdapter main_oneFeedAdapter;

    SwipeRefreshLayout main_feed_srl;

    WittyFeedSDKMainInterface fetch_more_main_callback;
    WittyFeedSDKMainInterface refresh_main_callback;

    private static final String TAG = "WF_SDK";
    boolean is_fetching_data = false;
    private int feed_loadmore_offset = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return inflater.inflate(R.layout.fragment_main_feed, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onefeed_rv = view.findViewById(R.id.onefeed_rv);
        main_feed_srl = view.findViewById(R.id.main_feed_srl);

        main_oneFeedAdapter = new OneFeedAdapter(activityContext, WittyFeedSDKSingleton.getInstance().blockArrayList, 1);
        main_feed_linearLayoutManager = new LinearLayoutManager(activityContext);
        onefeed_rv.setLayoutManager(main_feed_linearLayoutManager);
        onefeed_rv.setAdapter(main_oneFeedAdapter);
        init_main_feed();
    }


    private void init_main_feed(){

        fetch_more_main_callback = new WittyFeedSDKMainInterface() {
            @Override
            public void onOperationDidFinish() {
                if(main_oneFeedAdapter !=null)
                    main_oneFeedAdapter.notifyDataSetChanged();
                feed_loadmore_offset++;
                Log.d(TAG, "fetch more data :: END");
                is_fetching_data = false;
                int siz  = dpToPx(activityContext, 50);
                int negate = -siz;
                onefeed_rv.smoothScrollBy(0,negate);
            }

            @Override
            public void onError(Exception e) {
                // if unexpected error
                is_fetching_data = false;
                if (e != null) {
                    Log.e(TAG, "onError: fetch more data error", e);
                } else {
                    Log.e(TAG, "onError: fetch more data error");
                }
            }
        };

        onefeed_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1) && !WittyFeedSDKUtils.isConnected(activityContext)) {
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "No Internet, Please connect to a Network", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = main_feed_linearLayoutManager.getChildCount();
//                Log.d(TAG,"VisibleItemCount " +visibleItemCount );
                int totalItemCount = main_feed_linearLayoutManager.getItemCount();
//                Log.d(TAG,"TotalItemCount " +totalItemCount );
                int firstVisibleItemPosition = main_feed_linearLayoutManager.findFirstVisibleItemPosition();
//                Log.d(TAG,"FirstVisibleItemposition " +firstVisibleItemPosition );

                if (!is_fetching_data) {
                    if(firstVisibleItemPosition+visibleItemCount > totalItemCount-4){
                        if(WittyFeedSDKUtils.isConnected(activityContext)){
                            is_fetching_data = true;
                            WittyFeedSDKSingleton.getInstance().witty_sdk.fetch_more_data(fetch_more_main_callback, feed_loadmore_offset);
                            Log.d(TAG,"fetch more data :: START");
                        } else {
                            is_fetching_data = false;
                        }
                    }
                }
            }
        });

        refresh_main_callback = new WittyFeedSDKMainInterface() {
            @Override
            public void onOperationDidFinish() {
                if(main_oneFeedAdapter !=null)
                    main_oneFeedAdapter.notifyDataSetChanged();
                main_feed_srl.setRefreshing(false);
                int siz  = dpToPx(activityContext, 50);
                int negate = -siz;
                onefeed_rv.smoothScrollBy(0,negate);
                Log.d(TAG, "Feed Data refreshed");
            }

            @Override
            public void onError(Exception e) {
                // if unexpected error
                main_feed_srl.setRefreshing(false);
                if (e != null) {
                    Log.e(TAG, "onError: refresh data error", e);
                } else {
                    Log.e(TAG, "onError: refresh data error");
                }
            }
        };

        main_feed_srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                main_feed_srl.setRefreshing(true);
                if(!WittyFeedSDKUtils.isConnected(activityContext)){
                    main_feed_srl.setRefreshing(false);
                    Log.d(TAG, "onRefresh: internet is not active");
                }
                WittyFeedSDKSingleton.getInstance().witty_sdk.load_initial_data(refresh_main_callback, false);
            }
        });
    }
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
