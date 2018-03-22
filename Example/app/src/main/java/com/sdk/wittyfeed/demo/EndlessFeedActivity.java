package com.sdk.wittyfeed.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKCardFetcher;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKCardFetcherInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyGlide;

/**
 * Created by aishwarydhare on 11/11/17.
 */

public class EndlessFeedActivity extends AppCompatActivity {

    private static final String TAG = "WF_SDK";
    Activity activity;
    RecyclerView endless_feed_rv;
    EndlessFeedAdapter endlessFeedAdapter;
    LinearLayoutManager linearLayoutManager;
    WittyFeedSDKCardFetcher wittyFeedSDKCardFetcher;
    String dummyString;
    boolean is_fetching_data = false;
    RequestManager requestManager;

    int total_sample_feeds_count = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endless_feed);

        activity = this;
        this.requestManager = WittyGlide.with(activity);

        dummyString = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

        endless_feed_rv = findViewById(R.id.endless_feed_rv);

        linearLayoutManager = new LinearLayoutManager(activity);
        endless_feed_rv.setLayoutManager(linearLayoutManager);

        endlessFeedAdapter = new EndlessFeedAdapter(this);
        endless_feed_rv.setAdapter(endlessFeedAdapter);

        // Second Step is this
        // Tip: use the object of WittyFeedSDKCardFetcher from Singleton class if you want to use cards in difference activities
        // and also don't want to repeat cards that have been loaded previously in previous screens
        // otherwise just create an object local to current activity only and use that, see TinderCardActivity for such implementation
        wittyFeedSDKCardFetcher = new WittyFeedSDKCardFetcher(activity, requestManager);


        endless_feed_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = linearLayoutManager.getChildCount();
                int totalItemCount = linearLayoutManager.getItemCount();
                int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

                if(firstVisibleItemPosition+visibleItemCount > totalItemCount- 1  && !is_fetching_data){
                    total_sample_feeds_count += 10;
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            endlessFeedAdapter.notifyDataSetChanged();
                        }
                    }).run();
                }
            }
        });

    }


    class EndlessFeedAdapter extends RecyclerView.Adapter<EndlessFeedAdapter.ViewHolder>{

        Activity activity;

        EndlessFeedAdapter(Activity activity){
            this.activity = activity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = null;
            switch (viewType){
                case 1:
                    View itemView = getLayoutInflater().inflate(R.layout.item_endless_feed,null,false);
                    viewHolder = new ViewHolder(itemView);
                    break;
                case 2:
                    ViewGroup itemView_with_wittyfeed_card = (ViewGroup) getLayoutInflater().inflate(R.layout.item_witty_normal_card,null,false);
                    viewHolder = new ViewHolder(itemView_with_wittyfeed_card);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int itemType = getItemViewType(position);
            switch (itemType){
                case 1:
                    holder.feed_tv = holder.itemView.findViewById(R.id.feed_tv);
                    holder.feed_tv.setText(dummyString);
                    break;

                case 2:
                    holder.item_ll = holder.itemView.findViewById(R.id.item_ll);
                    final ViewHolder finalHolder = holder;

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

                    // First Step is this
                    WittyFeedSDKCardFetcherInterface wittyFeedSDKCardFetcherInterface = new WittyFeedSDKCardFetcherInterface() {
                        @Override
                        public void onWillStartFetchingMoreData() {
                            // fetching more data, do necessary UI updates here. onMoreDataFetched will be called when the data will be fetched
                            Log.d(TAG, "onWillStartFetchingMoreData: ");
                        }

                        @Override
                        public void onMoreDataFetched() {
                            // after fetching more data. onMoreDataFetched will be called when the data will be fetched
                            Log.d(TAG, "onMoreDataFetched: ");
                        }

                        @Override
                        public void onCardReceived(String customTag, View cardViewFromWittyFeed) {
                            //
                            switch (customTag){
                                case "witty_card":
                                    finalHolder.item_ll.removeAllViews();
                                    finalHolder.item_ll.addView(cardViewFromWittyFeed);
                                    break;
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            // unexpected happens here
                            Log.d(TAG, "onError: "+ e.getMessage(), e);
                        }
                    };

                    // Setting interface particular to this card here with setWittyFeedSDKCardFetcherInterface method of WittyFeedSDKCardFetcher,
                        // which takes one parameter which is object of WittyFeedSDKCardFetcherInterface
                    wittyFeedSDKCardFetcher.setWittyFeedSDKCardFetcherInterface(wittyFeedSDKCardFetcherInterface);

                    // Third and Last Step is this
                    wittyFeedSDKCardFetcher.fetch_a_card("witty_card", 0.8f);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return total_sample_feeds_count + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position%4 == 0 && position != 0){
                return 2;
            }
            return 1;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            View itemView;
            TextView feed_tv;
            LinearLayout item_ll;
            public ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }
    }


}