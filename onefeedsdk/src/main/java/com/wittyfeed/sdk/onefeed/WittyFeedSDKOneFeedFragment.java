package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by aishwarydhare on 30/03/18.
 */

@SuppressLint("ValidFragment")
public class WittyFeedSDKOneFeedFragment extends Fragment {

    Context activityContext;
    FrameLayout main_feed_holder_fl, search_feed_holder_fl, interest_feed_holder_fl;
    TextView search_tv;
    View search_v;
    ImageView plus_iv;
    ImageView back_iv;
    RelativeLayout header_rl;
    WittyFeedSDKMainFeedFragment mainFeedFragment;
    WittyFeedSDKBackPressInterface wittyFeedSDKBackPressInterface;

    private static final String TAG = "WF_SDK";

    private FragmentManager childFragmentManager;
    private View.OnClickListener back_onClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activityContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return inflater.inflate(R.layout.fragment_onefeed, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_tv = view.findViewById(R.id.search_tv);
        main_feed_holder_fl = view.findViewById(R.id.main_feed_holder_fl);
        search_feed_holder_fl = view.findViewById(R.id.search_feed_holder_fl);
        interest_feed_holder_fl = view.findViewById(R.id.interest_feed_holder_fl);
        plus_iv = view.findViewById(R.id.plus_iv);
        back_iv = view.findViewById(R.id.back_iv);
        header_rl = view.findViewById(R.id.header_rl);
        search_v = view.findViewById(R.id.search_v);

        childFragmentManager = getChildFragmentManager();

        mainFeedFragment = new WittyFeedSDKMainFeedFragment();

        back_onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do nothing
                if (wittyFeedSDKBackPressInterface != null) {
                    wittyFeedSDKBackPressInterface.perform_back();
                }
            }
        };
        back_iv.setOnClickListener(back_onClickListener);

        init_main_feed();

        init_header();

        if(WittyFeedSDKSingleton.getInstance().hasInterestFragmentOrientationChanged) {
                back_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        childFragmentManager.executePendingTransactions();
                        if(childFragmentManager.findFragmentByTag("InterestsFeed") != null){
                            childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("InterestsFeed")).commitNow();
                        }
                        back_iv.setOnClickListener(back_onClickListener);
                        plus_iv.setVisibility(View.VISIBLE);
                        search_v.setVisibility(View.VISIBLE);
                        search_tv.setVisibility(View.VISIBLE);
                    }
                });
                search_v.setVisibility(View.GONE);
                plus_iv.setVisibility(View.GONE);
                search_tv.setVisibility(View.GONE);
                WittyFeedSDKSingleton.getInstance().hasInterestFragmentOrientationChanged = false;
        }

        if(WittyFeedSDKSingleton.getInstance().hasSearchFragmentOrientationChanged) {
            back_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    childFragmentManager.executePendingTransactions();
                    if(childFragmentManager.findFragmentByTag("SearchFeed") != null){
                        childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("SearchFeed")).commitNow();
                    }
                    back_iv.setOnClickListener(back_onClickListener);
                    plus_iv.setVisibility(View.VISIBLE);
                    search_v.setVisibility(View.VISIBLE);
                    search_tv.setVisibility(View.VISIBLE);
                }
            });
            plus_iv.setVisibility(View.GONE);
            WittyFeedSDKSingleton.getInstance().hasSearchFragmentOrientationChanged = false;
        }
    }


    private void init_main_feed(){
        childFragmentManager.executePendingTransactions();
        if(childFragmentManager.findFragmentByTag("MainFeed") != null){
            childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("MainFeed")).commit();
        }
        childFragmentManager.beginTransaction().add(main_feed_holder_fl.getId(), mainFeedFragment, "MainFeed").commit();
    }


    private void init_header() {
        search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSearchFeedFragment wittyFeedSDKSearchFeedFragment = new WittyFeedSDKSearchFeedFragment();

                WittyFeedSDKSingleton.getInstance().search_blocks_arr.clear();
                WittyFeedSDKSingleton.getInstance().search_blocks_arr.addAll(WittyFeedSDKSingleton.getInstance().default_search_block_arr);
                WittyFeedSDKSingleton.getInstance().last_search_for_str = "";

                childFragmentManager.executePendingTransactions();
                if(childFragmentManager.findFragmentByTag("SearchFeed") != null){
                    childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("SearchFeed")).commitNow();
                }
                childFragmentManager.beginTransaction().add(search_feed_holder_fl.getId(), wittyFeedSDKSearchFeedFragment, "SearchFeed").commit();
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
                        back_iv.setOnClickListener(back_onClickListener);
                        plus_iv.setVisibility(View.VISIBLE);
                        search_v.setVisibility(View.VISIBLE);
                        search_tv.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        plus_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKInterestsFeedFragment wittyFeedSDKInterestsFeedFragment = new WittyFeedSDKInterestsFeedFragment();

                childFragmentManager.executePendingTransactions();
                if(childFragmentManager.findFragmentByTag("InterestsFeed") != null){
                    childFragmentManager.beginTransaction().remove(childFragmentManager.findFragmentByTag("InterestsFeed")).commitNow();
                }
                childFragmentManager.beginTransaction().add(search_feed_holder_fl.getId(), wittyFeedSDKInterestsFeedFragment, "InterestsFeed").commit();
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
                        back_iv.setOnClickListener(back_onClickListener);
                        plus_iv.setVisibility(View.VISIBLE);
                        search_v.setVisibility(View.VISIBLE);
                        search_tv.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }


    public WittyFeedSDKBackPressInterface getWittyFeedSDKBackPressInterface() {
        return wittyFeedSDKBackPressInterface;
    }


    public void setWittyFeedSDKBackPressInterface(WittyFeedSDKBackPressInterface wittyFeedSDKBackPressInterface) {
        this.wittyFeedSDKBackPressInterface = wittyFeedSDKBackPressInterface;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
