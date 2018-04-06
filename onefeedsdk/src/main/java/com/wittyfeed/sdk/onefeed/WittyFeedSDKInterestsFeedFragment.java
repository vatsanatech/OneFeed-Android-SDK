package com.wittyfeed.sdk.onefeed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by aishwarydhare on 02/04/18.
 */

public class WittyFeedSDKInterestsFeedFragment extends Fragment{

    int currentOrientation;
    Context activityContext;
    RecyclerView feed_rv;
    ProgressBar pb;
    InterestsRVAdapter interestsRVAdapter;
    LinearLayoutManager interest_linearLayoutManager;

    private static final String TAG = "WF_SDK";

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
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        currentOrientation = getResources().getConfiguration().orientation;
        return inflater.inflate(R.layout.fragment_interests_feed, null, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feed_rv = view.findViewById(R.id.feed_rv);
        pb = view.findViewById(R.id.pb);

        pb.setVisibility(View.GONE);

        final WittyFeedSDKMainInterface get_interests_content_callback = new WittyFeedSDKMainInterface() {
            @Override
            public void onOperationDidFinish() {
                interestsRVAdapter = new InterestsRVAdapter();
                interest_linearLayoutManager = new LinearLayoutManager(activityContext);
                feed_rv.setLayoutManager(interest_linearLayoutManager);
                feed_rv.setAdapter(interestsRVAdapter);
                pb.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                // TODO: 03/04/18 on interest list fetching failed placeholder
                pb.setVisibility(View.GONE);
                if (e != null) {
                    Log.e(TAG, "onError: couldn't fetch search data", e);
                } else {
                    Log.e(TAG, "onError: couldn't fetch search data");
                }
            }
        };

        if (WittyFeedSDKSingleton.getInstance().interests_block_arr.size() > 0) {
            interestsRVAdapter = new InterestsRVAdapter();
            interest_linearLayoutManager = new LinearLayoutManager(activityContext);
            feed_rv.setLayoutManager(interest_linearLayoutManager);
            feed_rv.setAdapter(interestsRVAdapter);
            pb.setVisibility(View.GONE);
        } else {
            WittyFeedSDKSingleton.getInstance().witty_sdk.get_interests_list(get_interests_content_callback);
            pb.setVisibility(View.VISIBLE);
        }

    }


    class InterestsRVAdapter extends RecyclerView.Adapter<InterestsRVAdapter.ViewHolder> {

        InterestsRVAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = ((Activity)activityContext).getLayoutInflater().inflate(R.layout.card_interest_item, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            final CheckedTextView checkedTextView = holder.checkedTextView;

            final Card card = WittyFeedSDKSingleton.getInstance().interests_block_arr.get(0).getCards().get(holder.getAdapterPosition());
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
                        WittyFeedSDKSingleton.getInstance().witty_sdk.set_interests_list(new WittyFeedSDKMainInterface() {
                            @Override
                            public void onOperationDidFinish() {
                                Log.d(TAG, "onOperationDidFinish: interest  un-selected successfully");
                            }

                            @Override
                            public void onError(Exception e) {
                                checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_20dp);
                                checkedTextView.setChecked(false);
                                card.setBadgeText("");
                                if(e != null){
                                    Log.d(TAG, "onError: error in selecting interest - " + e.getMessage());
                                } else {
                                    Log.d(TAG, "onError: error in selecting interest ");
                                }
                                Toast.makeText(activityContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }, ""+card.getId(), false);
                    } else {
                        checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_20dp);
                        checkedTextView.setChecked(true);
                        card.setBadgeText("selected");
                        WittyFeedSDKSingleton.getInstance().witty_sdk.set_interests_list(new WittyFeedSDKMainInterface() {
                            @Override
                            public void onOperationDidFinish() {
                                Log.d(TAG, "onOperationDidFinish: interest selected successfully");
                            }

                            @Override
                            public void onError(Exception e) {
                                checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_grey_20dp);
                                checkedTextView.setChecked(false);
                                card.setBadgeText("");
                                if(e != null){
                                    Log.d(TAG, "onError: error in selecting interest - " + e.getMessage());
                                } else {
                                    Log.d(TAG, "onError: error in selecting interest ");
                                }
                                Toast.makeText(activityContext, "No Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        }, ""+card.getId(), true);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return WittyFeedSDKSingleton.getInstance().interests_block_arr.get(0).getCards().size();
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

    @Override
    public void onDestroyView() {
        InputMethodManager imm = (InputMethodManager) (activityContext).getSystemService(Context.INPUT_METHOD_SERVICE);
        int config = getResources().getConfiguration().orientation;
        int prev_config  = currentOrientation;
        if(config!=prev_config)
            WittyFeedSDKSingleton.getInstance().hasInterestFragmentOrientationChanged = true;
        super.onDestroyView();
    }


}
