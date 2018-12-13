package com.onefeedsdk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.onefeedsdk.R;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.event.Event;
import com.onefeedsdk.job.GetSearchFeedJob;
import com.onefeedsdk.job.PostUserTrackingJob;
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.ui.adapter.CategoryAdapter;
import com.onefeedsdk.ui.adapter.StoryFeedAdapter;
import com.onefeedsdk.util.Util;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 03-October-2018
 * Time: 10:12
 */
public class SearchFeedFragment extends Fragment {

    private EditText searchText;
    private RecyclerView categoryRecycler;
    private RecyclerView storyFeedRecycler;
    private TextView errorView;
    private TextView searchTitleView;
    private ImageView backArrowImage;
    private ImageView searchBtn;
    private ProgressBar progressBar;

    private FeedModel.FeedData feedModel;
    private StoryFeedAdapter storyFeedAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.getSerializable(Constant.SEARCH) != null) {
            feedModel = (FeedModel.FeedData) bundle.getSerializable(Constant.SEARCH);
            Log.e("SEARCH", feedModel.getBlocks().get(0).getCardList().get(0).toString());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.onefeed_fragment_search_feed, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
        errorView = view.findViewById(R.id.error_msg);
        searchTitleView = view.findViewById(R.id.view_search_title);
        searchBtn = view.findViewById(R.id.image_search);

        TextView searchView = view.findViewById(R.id.view_search);
        searchView.setVisibility(View.GONE);


        searchText = view.findViewById(R.id.text_search);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    StorySearch();
                    return true;
                }
                return false;
            }
        });
        searchText.setVisibility(View.VISIBLE);
        searchBtn.setVisibility(View.VISIBLE);

        categoryRecycler = view.findViewById(R.id.recycler_category);
        storyFeedRecycler = view.findViewById(R.id.recycler_search_feed);

        backArrowImage = view.findViewById(R.id.image_arrow_back);
        backArrowImage.setVisibility(View.VISIBLE);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StorySearch();
            }
        });
        backArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(SearchFeedFragment.this).commit();
                }catch (Exception e){}
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Category List
        LinearLayoutManager categoryManager = new LinearLayoutManager(getActivity());
        categoryManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecycler.setLayoutManager(categoryManager);

        CategoryAdapter categoryAdapter = new CategoryAdapter();
        categoryAdapter.setCardList(feedModel.getBlocks().get(0).getCardList());
        categoryRecycler.setAdapter(categoryAdapter);

        //Search Feed
        LinearLayoutManager searchManager = new LinearLayoutManager(getActivity());
        storyFeedRecycler.setLayoutManager(searchManager);

        storyFeedAdapter = new StoryFeedAdapter();
        storyFeedRecycler.setAdapter(storyFeedAdapter);

        categoryRecycler.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    private void StorySearch(){
        if(Util.checkNetworkConnection(getActivity())) {
            if (TextUtils.isEmpty(searchText.getText().toString())) {
                Util.showToastMsg(getActivity(), "Please enter some text!");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            OneFeedSdk.getInstance().getJobManager()
                    .addJobInBackground(new GetSearchFeedJob(searchText.getText().toString()));
            //Hide Keyboard
            Util.hideKeyboard(getActivity(), searchText);
        }else{
            Util.showToastMsg(getActivity(), getString(R.string.error_network_msg));
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
        //Hide Keyboard
        Util.hideKeyboard(getActivity(), searchText);
        OneFeedSdk.getInstance().getEventBus().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(Event.SearchFeedEvent event) {
        progressBar.setVisibility(View.GONE);
        OneFeedSdk.getInstance().getEventBus().removeStickyEvent(event);
        if (event.isSuccess()) {

            storyFeedAdapter.setCardList(event.getFeed().getFeedData().getBlocks().get(0).getCardList());
            storyFeedAdapter.notifyDataSetChanged();

            errorView.setVisibility(View.GONE);
            storyFeedRecycler.setVisibility(View.VISIBLE);
            searchTitleView.setVisibility(View.VISIBLE);

            //Tracking
            OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                    new PostUserTrackingJob(Constant.SEARCH_VIEWED, Constant.SEARCH_VIEWED_BY_FEED,
                            searchText.getText().toString()));

        } else {
            errorView.setVisibility(View.VISIBLE);
            storyFeedRecycler.setVisibility(View.GONE);
        }
    }
}
