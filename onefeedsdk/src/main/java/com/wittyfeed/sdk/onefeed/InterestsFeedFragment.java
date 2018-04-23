package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 *
 * Holds the InterestFeed (interest selection UI) of OneFeed, and is managed by Holder Fragment
 *  Read about: HolderFragment, OneFeedBuilder
 *
 * has 1 inner class InterestsRVAdapter, which us used as adapter by recycler view
 *      InterestsRVAdapter has following responsibilities -
 *          1) populate available interests and bind to views
 *          2) track user-generated events to update UI of check-marks as selected or unselected
 *          3) send networkServiceManager API hit to send user's selection of interests to OneFeed
 *              server
 *          4) update in local Data-Model of DataStore about interest as selected or unselected
 *          5) on failing of sending selection status via networkServiceManager,
 *              revert UI accordingly and revert changes in Data-Model of DataStore accordingly
 *      refer to: R.layout.item_interest
 *
 * it has following responsibilities -
 *      1) prepare recycler view to populate the Model-Data from DataStore
 *          Read about: DataStore, DataStoreManager, OneFeedMain
 *      2) prepare inner to set with the recycler view
 *          Read about: MainAdapter
 *      3) prepare LinearLayoutManager to set with the recycler view
 *      6) notify OneFeedBuilder on screen-orientation change and other onPause() events
 *
 * Read about: MainAdapter, OneFeedBuilder, HolderFragment
 */

public final class InterestsFeedFragment extends Fragment {

    private int currentOrientation;
    private RecyclerView feed_rv;
    private ProgressBar pb;
    private InterestsRVAdapter interestsRVAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        OneFeedMain.getInstance().oneFeedBuilder.openedFragmentFeedType = OneFeedBuilder.FragmentFeedType.INTEREST_FEED;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        currentOrientation = getResources().getConfiguration().orientation;
        return inflater.inflate(R.layout.fragment_interests_feed, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feed_rv = view.findViewById(R.id.feed_rv);
        pb = view.findViewById(R.id.pb);

        pb.setVisibility(View.GONE);

        if (OneFeedMain.getInstance().dataStore.getInterestsDataBlockArray() != null){
            if(OneFeedMain.getInstance().dataStore.getInterestsDataBlockArray().size() > 0) {
                interestsRVAdapter = new InterestsRVAdapter();
                feed_rv.setLayoutManager(new LinearLayoutManager(getContext()));
                feed_rv.setAdapter(interestsRVAdapter);
                pb.setVisibility(View.GONE);
            }
        } else {
            OneFeedMain.getInstance().networkServiceManager.setInterestRequestQueue(getContext());
            OneFeedMain.getInstance().networkServiceManager.hitGetInterestsAPI(new NetworkServiceManager.OnNetworkServiceDidRespond() {
                @Override
                public void onSuccessResponse(String response) {
                    OneFeedMain.getInstance().dataStore.setInterestsDataDatum( DataStoreParser.parseGenericFeedString(response) );
                    interestsRVAdapter = new InterestsRVAdapter();
                    feed_rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    feed_rv.setAdapter(interestsRVAdapter);
                    pb.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    pb.setVisibility(View.GONE);
                    OFLogger.log(OFLogger.ERROR, "onError: couldn't fetch search data");
                }
            });
            pb.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        InputMethodManager imm = (InputMethodManager) (getContext()).getSystemService(Context.INPUT_METHOD_SERVICE);
        int config = getResources().getConfiguration().orientation;
        int prev_config = currentOrientation;
        if(config!=prev_config)
            OneFeedMain.getInstance().oneFeedBuilder.hasInterestFragmentOrientationChanged = true;
        super.onDestroyView();
    }

    class InterestsRVAdapter extends RecyclerView.Adapter<InterestsRVAdapter.ViewHolder> {

        InterestsRVAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_interest_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            final CheckedTextView checkedTextView = holder.checkedTextView;

            final Card card = OneFeedMain.getInstance().dataStore.getInterestsDataBlockArray().get(0).getCards().get(holder.getAdapterPosition());
            checkedTextView.setText(card.getStoryTitle());

            if(card.getBadgeText().equalsIgnoreCase("selected")){
                checkedTextView.setChecked(true);
            } else {
                checkedTextView.setChecked(false);
            }

            if (!checkedTextView.isChecked()) {
                checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_grey_20dp);
            } else {
                checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_20dp);
            }

            checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkedTextView.isChecked()) {
                        checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_grey_20dp);
                        checkedTextView.setChecked(false);
                        card.setBadgeText("");
                        OneFeedMain.getInstance().networkServiceManager.hitSetInterestSelectionAPI(String.valueOf(card.getId()),
                                false,
                                new NetworkServiceManager.OnNetworkServiceDidRespond() {
                                    @Override
                                    public void onSuccessResponse(String response) {
                                        OFLogger.log(OFLogger.DEBUG, "Interest un-selection Succeed");
                                    }

                                    @Override
                                    public void onError() {
                                        OFLogger.log(OFLogger.ERROR, "Interest un-selection failed");
                                        checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_20dp);
                                        checkedTextView.setChecked(false);
                                        card.setBadgeText("selected");
                                    }
                                });

                    } else {
                        checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_20dp);
                        checkedTextView.setChecked(true);
                        card.setBadgeText("selected");
                        OneFeedMain.getInstance().networkServiceManager.hitSetInterestSelectionAPI(String.valueOf(card.getId()),
                                true,
                                new NetworkServiceManager.OnNetworkServiceDidRespond() {
                                    @Override
                                    public void onSuccessResponse(String response) {
                                        OFLogger.log(OFLogger.DEBUG, "Interest selection Succeed");
                                    }

                                    @Override
                                    public void onError() {
                                        OFLogger.log(OFLogger.ERROR, "Interest selection failed");
                                        checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_grey_20dp);
                                        checkedTextView.setChecked(false);
                                        card.setBadgeText("");
                                    }
                                });
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return OneFeedMain.getInstance().dataStore.getInterestsDataBlockArray().get(0).getCards().size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout container_view_rl;
            CheckedTextView checkedTextView;
            ViewHolder(View itemView) {
                super(itemView);
                container_view_rl = itemView.findViewById(R.id.container_view_rl);
                checkedTextView = container_view_rl.findViewById(R.id.checked_tv);
            }
        }
    }


}
