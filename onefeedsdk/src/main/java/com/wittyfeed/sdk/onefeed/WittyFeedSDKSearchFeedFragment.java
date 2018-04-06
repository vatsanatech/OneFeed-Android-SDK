package com.wittyfeed.sdk.onefeed;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by aishwarydhare on 02/04/18.
 */

public class WittyFeedSDKSearchFeedFragment extends Fragment {

    Context activityContext;
    int currentOrientation;

    RecyclerView search_feed_rv;
    LinearLayoutManager search_linearLayoutManager;
    OneFeedAdapter search_oneFeedAdapter;

    ProgressBar pb;
    EditText search_et;
    ImageView search_iv;

    private static final String TAG = "WF_SDK";
    private String last_string_searched = "";
    private int search_loadmore_offset = 0;
    private String string_to_search = "";
    private View root_view;

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
        root_view = inflater.inflate(R.layout.fragment_search_feed, null, false);
        currentOrientation = getResources().getConfiguration().orientation;
        return root_view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_feed_rv = view.findViewById(R.id.feed_rv);
        pb = view.findViewById(R.id.pb);
        search_et = view.findViewById(R.id.search_et);
        search_iv = view.findViewById(R.id.search_iv);

        last_string_searched = WittyFeedSDKSingleton.getInstance().last_search_for_str;
        search_et.setText(""+last_string_searched);

        search_oneFeedAdapter = new OneFeedAdapter(activityContext, WittyFeedSDKSingleton.getInstance().search_blocks_arr, 2);
        search_linearLayoutManager = new LinearLayoutManager(activityContext);
        search_feed_rv.setLayoutManager(search_linearLayoutManager);
        search_feed_rv.setAdapter(search_oneFeedAdapter);

        pb.setVisibility(View.GONE);

        search_et.setFocusable(true);
        search_et.requestFocus();

        InputMethodManager imm = (InputMethodManager) (activityContext).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(search_et, InputMethodManager.SHOW_IMPLICIT);
        }

        init_search_feed();
    }


    private void init_search_feed() {

        final WittyFeedSDKMainInterface search_content_callback = new WittyFeedSDKMainInterface() {
            @Override
            public void onOperationDidFinish() {
                Log.d(TAG, "onOperationDidFinish: successfully searched for: " + last_string_searched);
                WittyFeedSDKSingleton.getInstance().last_search_for_str = last_string_searched;
                Log.d(TAG, "onOperationDidFinish: new search arr_list.size: " + WittyFeedSDKSingleton.getInstance().search_blocks_arr);
                search_oneFeedAdapter.notifyDataSetChanged();
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                pb.setVisibility(View.GONE);
                // TODO: 03/04/18 set search failed placeholder image
                if (e != null) {
                    Log.e(TAG, "onError: couldn't fetch search data", e);
                } else {
                    Log.e(TAG, "onError: couldn't fetch search data");
                }
            }
        };

        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                string_to_search = charSequence.toString();
            }
        });

        search_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(string_to_search.length() >= 0){
                    if (!string_to_search.equalsIgnoreCase(last_string_searched)) {
                        last_string_searched = string_to_search;
                        Log.d(TAG, "searching for: " + last_string_searched);
                        WittyFeedSDKSingleton.getInstance().witty_sdk.search_content(search_content_callback , string_to_search, search_loadmore_offset);
                        pb.setVisibility(View.VISIBLE);
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
                    if(string_to_search.length() >= 0){
                        if (!string_to_search.equalsIgnoreCase(last_string_searched)) {
                            last_string_searched = string_to_search;
                            Log.d(TAG, "searching for: " + last_string_searched);
                            WittyFeedSDKSingleton.getInstance().witty_sdk.search_content(search_content_callback , string_to_search, search_loadmore_offset);
                            pb.setVisibility(View.VISIBLE);
                        }
                    }
                    return true;
                }
                return false;
            }
        });


        // TODO: 02/04/18 implement endless search results
        /*fetch_more_main_callback = new WittyFeedSDKMainInterface() {
            @Override
            public void onOperationDidFinish() {
                pb.setVisibility(View.GONE);
                if(search_oneFeedAdapter !=null)
                    search_oneFeedAdapter.notifyDataSetChanged();
//                search_loadmore_offset++;
                Log.d(TAG, "fetch more data :: END");
                is_fetching_data = false;
            }

            @Override
            public void onError(Exception e) {
                // if unexpected error
                pb.setVisibility(View.GONE);
                is_fetching_data = false;
                Log.e(TAG, "onError: fetch more data error", e);
            }
        };*/

        /*search_feed_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                int visibleItemCount = search_linearLayoutManager.getChildCount();
                int totalItemCount = search_linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = search_linearLayoutManager.findFirstVisibleItemPosition();

                if (!is_fetching_data) {
//                    boolean msc = firstVisibleItemPosition+visibleItemCount > totalItemCount-4;
                    if(firstVisibleItemPosition+visibleItemCount > totalItemCount-4){
                        if(WittyFeedSDKUtils.isConnected(activityContext)){
//                            pb.setVisibility(View.VISIBLE);
                            is_fetching_data = true;
//                            WittyFeedSDKSingleton.getInstance().witty_sdk.fetch_more_data(fetch_more_main_callback, search_loadmore_offset);
                            Log.d(TAG,"fetch more data :: START");
                        } else {
                            is_fetching_data = false;
                            pb.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });*/

    }

    @Override
    public void onDestroyView() {
        InputMethodManager imm = (InputMethodManager) (activityContext).getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(search_et.getWindowToken(), 0);
        }
        int config = getResources().getConfiguration().orientation;
        int prev_config  = currentOrientation;
        if(prev_config!=config)
            WittyFeedSDKSingleton.getInstance().hasSearchFragmentOrientationChanged = true;
        super.onDestroyView();
    }

}
