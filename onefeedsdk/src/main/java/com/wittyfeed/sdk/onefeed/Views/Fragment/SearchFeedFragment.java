package com.wittyfeed.sdk.onefeed.Views.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.wittyfeed.sdk.onefeed.DataStoreManagement.DataStoreParser;
import com.wittyfeed.sdk.onefeed.Models.Block;
import com.wittyfeed.sdk.onefeed.NetworkServiceManager;
import com.wittyfeed.sdk.onefeed.OFAnalytics;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.Utils.OneFeedBuilder;
import com.wittyfeed.sdk.onefeed.OneFeedMain;
import com.wittyfeed.sdk.onefeed.R;
import com.wittyfeed.sdk.onefeed.Utils.Utils;
import com.wittyfeed.sdk.onefeed.Views.Adapter.MainAdapter;

import java.util.ArrayList;

/**
 *
 * Holds the SearchFeed of OneFeed, and is managed by HolderFragment
 *  Read about: HolderFragment, OneFeedBuilder, MainFeedFragment
 *
 * it has following responsibilities -
 *      1) prepare recycler view to populate search specific Model-Data from DataStore
 *          Read about: DataStore, DataStoreManager, OneFeedMain
 *      2) use MainFeedAdapter to set with the recycler view
 *          Read about: MainAdapter
 *      3) prepare LinearLayoutManager to set with the recycler view
 *      4) future: use linearLayoutManager to track user scroll and set setFetchMoreToRecyclerView
 *          to implement endless feed,
 *          Read about: NetworkServiceManager
 *      5) track search-string as per entered by user and use networkServiceManager to fetch search
 *          results
 *      6) notify OneFeedBuilder on screen-orientation change and other onPause() events
 *
 * Read about: MainAdapter, OneFeedBuilder, HolderFragment
 */

public final class SearchFeedFragment extends Fragment {

    private int currentOrientation;

    private ProgressBar pb;
    private EditText search_et;
    private ImageView search_iv;

    private String lastStringSearched = "";
    private String stringToSearch = "";

    private RecyclerView searchFeedRv;
    private MainAdapter mainAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        OneFeedMain.getInstance().oneFeedBuilder.openedFragmentFeedType = OneFeedBuilder.FragmentFeedType.SEARCH_FEED;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root_view = inflater.inflate(R.layout.fragment_search_feed, null, false);
        currentOrientation = getResources().getConfiguration().orientation;
        return root_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchFeedRv = view.findViewById(R.id.feed_rv);
        pb = view.findViewById(R.id.pb);
        search_et = view.findViewById(R.id.search_et);
        search_iv = view.findViewById(R.id.search_iv);
        View search_v = view.findViewById(R.id.search_v);

        search_v.getLayoutParams().width = Utils.getSearchBarBackgroundWidth(getContext());
        search_et.getLayoutParams().width = Utils.getSearchBarWidth(getContext());

        lastStringSearched = OneFeedMain.getInstance().getInstanceDataStore().getLastStringSearched();
        search_et.setText(""+ lastStringSearched);

        mainAdapter = new MainAdapter((ArrayList<Block>) OneFeedMain.getInstance().getInstanceDataStore().getSearchFeedDataBlockArr(), 2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        searchFeedRv.setLayoutManager(linearLayoutManager);
        searchFeedRv.setAdapter(mainAdapter);

        pb.setVisibility(View.GONE);

        search_et.setFocusable(true);
        search_et.requestFocus();

        InputMethodManager imm = (InputMethodManager) (getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(search_et, InputMethodManager.SHOW_IMPLICIT);
        }

        init();
    }

    private void init() {
        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                stringToSearch = charSequence.toString();
            }
        });

        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(stringToSearch.length() >= 0){
                    if (!stringToSearch.equalsIgnoreCase(lastStringSearched)) {
                        executeSearch();
                    }
                }
            }
        });

        search_et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    // Perform action on key press
                    if(stringToSearch.length() >= 0){
                        if (!stringToSearch.equalsIgnoreCase(lastStringSearched)) {
                            executeSearch();
                        }
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void executeSearch(){
        lastStringSearched = stringToSearch;
        OneFeedMain.getInstance().networkServiceManager.setSearchRequestQueue(getContext());
        OFLogger.log(OFLogger.DEBUG, OFLogger.SearchingFor + stringToSearch);

        OneFeedMain.getInstance().networkServiceManager.hitSearchFeedDataAPI(lastStringSearched, 0
                , new NetworkServiceManager.OnNetworkServiceDidRespond() {
                    @Override
                    public void onSuccessResponse(String response) {
                        OFLogger.log(OFLogger.DEBUG, OFLogger.SuccessfullySearchedFor + lastStringSearched);
                        OneFeedMain.getInstance().getInstanceDataStore().setLastStringSearched(lastStringSearched);

                        OneFeedMain.getInstance().getInstanceDataStore().setSearchFeedData( DataStoreParser.parseGenericFeedString(response) );

                        OFLogger.log(OFLogger.DEBUG, OFLogger.SearchFeedArraySize +
                                OneFeedMain.getInstance().getInstanceDataStore().getSearchFeedDataBlockArr().size());

                        OFAnalytics.getInstance().sendAnalytics(getActivity(), OFAnalytics.AnalyticsType.Search, stringToSearch);

                        mainAdapter = new MainAdapter((ArrayList<Block>) OneFeedMain.getInstance().getInstanceDataStore().getSearchFeedDataBlockArr(), 2);
                        searchFeedRv.setAdapter(mainAdapter);

                        pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        pb.setVisibility(View.GONE);
                        OFLogger.log(OFLogger.ERROR, OFLogger.CouldNotFetchSearchFeedData);
                    }
                });
        pb.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        InputMethodManager imm = (InputMethodManager) (getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
        }
        int config = getResources().getConfiguration().orientation;
        int prev_config  = currentOrientation;
        if(prev_config!=config) {
            OneFeedMain.getInstance().oneFeedBuilder.hasSearchFragmentOrientationChanged = true;
        }
        super.onDestroyView();
    }

}
