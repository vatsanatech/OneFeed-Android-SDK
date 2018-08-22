package com.wittyfeed.sdk.demo;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wittyfeed.sdk.onefeed.OFCardFetcher;
import com.wittyfeed.sdk.onefeed.OFInterface;
import com.wittyfeed.sdk.onefeed.OneFeedMain;

import org.json.JSONObject;

/**
 * Created by aishwarydhare on 11/11/17.
 */

public class NativeCardsActivity extends AppCompatActivity {

    private static final String TAG = "WF_SDK";
    Activity activity;
    RecyclerView endless_feed_rv;
    EndlessFeedAdapter endlessFeedAdapter;
    LinearLayoutManager linearLayoutManager;
    OFCardFetcher ofCardFetcher;
    String dummyString;
    boolean is_fetching_data = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_cards);

        activity = this;

        dummyString = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

        endless_feed_rv = findViewById(R.id.endless_feed_rv);

        linearLayoutManager = new LinearLayoutManager(activity);
        endless_feed_rv.setLayoutManager(linearLayoutManager);

        OneFeedMain.getInstance().ofCardFetcher.loadInitData(103, new OFCardFetcher.OnInitialized() {
            @Override
            public void onSuccess() {
                endlessFeedAdapter = new EndlessFeedAdapter(NativeCardsActivity.this);
                endlessFeedAdapter.setNotifyListener(new NotifyListener() {
                    @Override
                    public void loadMore() {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                endlessFeedAdapter.setSize(10);
                                endlessFeedAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                endless_feed_rv.setAdapter(endlessFeedAdapter);
                Toast.makeText(NativeCardsActivity.this, "onSuccess", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError() {
                endlessFeedAdapter = new EndlessFeedAdapter(NativeCardsActivity.this);
                endless_feed_rv.setAdapter(endlessFeedAdapter);
                Toast.makeText(NativeCardsActivity.this, "onError", Toast.LENGTH_LONG).show();
            }
        });




        // Second Step is this
        // Tip: use the object of OfCardFetcher from Singleton class if you want to use cards in difference activities
        // and also don't want to repeat cards that have been loaded previously in previous screens
        // otherwise just create an object local to current activity only and use that, see TinderCardActivity for such implementation
        ofCardFetcher = OneFeedMain.getInstance().ofCardFetcher;

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
        int size = 10;

        NotifyListener notifyListener;

        public void setSize(int size) {
            this.size += size;
        }

        public void setNotifyListener(NotifyListener notifyListener) {
            this.notifyListener = notifyListener;
        }

        EndlessFeedAdapter(Activity activity){
            this.activity = activity;
            setHasStableIds(true);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder viewHolder = null;
            switch (viewType){
                case 1:
                    View itemView = getLayoutInflater().inflate(R.layout.item_native_cards_activity,null,false);
                    viewHolder = new ViewHolder(itemView);
                    break;
                case 2:
                    ViewGroup itemView_with_wittyfeed_card = (ViewGroup) getLayoutInflater().inflate(R.layout.item_standard_wf,null,false);
                    viewHolder = new ViewHolder(itemView_with_wittyfeed_card);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            int itemType = getItemViewType(position);
            switch (itemType){
                case 1:
                    holder.feed_tv = holder.itemView.findViewById(R.id.feed_tv);
                    holder.feed_tv.setText(dummyString);
                    break;

                case 2:
                    holder.item_ll = holder.itemView.findViewById(R.id.item_ll);
                    holder.root_rl = holder.itemView.findViewById(R.id.root_rl);
                    holder.progressBar = holder.itemView.findViewById(R.id.progressBar);
                    final ViewHolder finalHolder = holder;


                    // First Step is this
                    OFInterface ofInterface = new OFInterface() {

                        @Override
                        public void OnSuccess(View view, String categoryName, String storyTitle) {
                            finalHolder.item_ll.removeAllViewsInLayout();
                            finalHolder.item_ll.addView(view);
                        }

                        @Override
                        public void onError(Exception e) {
                            // unexpected happens here
                            Log.d(TAG, "onError: "+ e.getMessage(), e);
                        }
                    };

                    // Setting interface particular to this card here with setOfInterface method of WittyFeedSDKCardFetcher,
                    // which takes one parameter which is object of WittyFeedSDKCardFetcherInterface
                    OneFeedMain.getInstance().ofCardFetcher.setOfInterface(ofInterface);

                    // Third and Last Step is this
                    OneFeedMain.getInstance().ofCardFetcher.fetch_repeating_card(103, 0.7f, false, "#4286f4", true);
                    break;

            }

            if(size-1 == position && notifyListener != null){
                size+=10;
                notifyListener.loadMore();
            }
        }

        @Override
        public int getItemCount() {
            return size;
        }

        @Override
        public int getItemViewType(int position) {
            if(position%7 == 0 && position != 0){
                return 2;
            }
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            View itemView;
            TextView feed_tv;
            LinearLayout item_ll;
            RelativeLayout root_rl;
            LinearLayout carousel_ll;
            ProgressBar progressBar;
            public ViewHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public interface NotifyListener{
        void loadMore();
    }
}