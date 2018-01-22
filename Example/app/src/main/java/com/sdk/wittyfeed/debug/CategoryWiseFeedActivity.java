package com.sdk.wittyfeed.debug;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdk.wittyfeed.wittynativesdk.Interfaces.WittyFeedSDKCardFetcherInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKCardFetcher;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

import java.util.ArrayList;

/**
 * Created by aishwarydhare on 15/11/17.
 */

public class CategoryWiseFeedActivity extends AppCompatActivity {

    private static final String TAG = "WF_SDK";
    TabLayout categories_tabLayout;
    ViewPager feed_vp;

    private Activity activity;

    ArrayList<CustomViewPagerDataClass> customViewPagerDataClassArrayList = new ArrayList<>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_wise_feed);

        activity = this;

        categories_tabLayout = findViewById(R.id.categories_tabLayout);
        feed_vp = findViewById(R.id.feed_vp);

        for (int i = 0; i < WittyFeedSDKSingleton.getInstance().categoryData_arr.size(); i++) {
            int cat_pos = i;
            customViewPagerDataClassArrayList.add(new CustomViewPagerDataClass(new ArrayList<View>(), cat_pos, WittyFeedSDKSingleton.getInstance().categoryData_arr.get(i).getCatName(), new WittyFeedSDKCardFetcher(activity) ));
        }

        categories_tabLayout.setupWithViewPager(feed_vp);
        feed_vp.setAdapter(new CategoryPagerAdapter());
        feed_vp.setOffscreenPageLimit(3);
    }


    class CategoryPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return customViewPagerDataClassArrayList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = getLayoutInflater().inflate(R.layout.item_view_pager, null, false);

            customViewPagerDataClassArrayList.get(position).feed_rv = v.findViewById(R.id.feed_rv);

            customViewPagerDataClassArrayList.get(position).linearLayoutManager = new LinearLayoutManager(activity);
            customViewPagerDataClassArrayList.get(position).feed_rv.setLayoutManager(customViewPagerDataClassArrayList.get(position).linearLayoutManager);
            customViewPagerDataClassArrayList.get(position).feed_rv.setAdapter(customViewPagerDataClassArrayList.get(position).categoryAdapter);

            final int final_position = position;
            customViewPagerDataClassArrayList.get(position).wittyFeedSDKCardFetcherInterface = new WittyFeedSDKCardFetcherInterface() {

                @Override
                public void onWillStartFetchingMoreData() {
                    Log.d(TAG, "onWillStartFetchingMoreData: fetching more cards from server");
                    customViewPagerDataClassArrayList.get(final_position).is_fetching_data = true;
                }

                @Override
                public void onMoreDataFetched() {
                    Log.d(TAG, "onMoreDataFetched: more cards fetched");
                    customViewPagerDataClassArrayList.get(final_position).is_fetching_data = false;
                }

                @Override
                public void onCardReceived(String customTag, View cardViewFromWittyFeed) {
                    customViewPagerDataClassArrayList.get(final_position).witty_cards.add(cardViewFromWittyFeed);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            customViewPagerDataClassArrayList.get(final_position).categoryAdapter.notifyDataSetChanged();
                        }
                    }).run();
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "onError: "+ e.getMessage(), e);
                }
            };

            customViewPagerDataClassArrayList.get(position).feed_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int visibleItemCount = customViewPagerDataClassArrayList.get(final_position).linearLayoutManager.getChildCount();
                    int totalItemCount = customViewPagerDataClassArrayList.get(final_position).linearLayoutManager.getItemCount();
                    int firstVisibleItemPosition = customViewPagerDataClassArrayList.get(final_position).linearLayoutManager.findFirstVisibleItemPosition();

                    if(firstVisibleItemPosition+visibleItemCount > totalItemCount- 1  && !customViewPagerDataClassArrayList.get(final_position).is_fetching_data){
                        for (int j = 0; j <= 0; j++) {
                            customViewPagerDataClassArrayList.get(final_position).wittyFeedSDKCardFetcher.fetch_a_card("category_card", 0.8f, customViewPagerDataClassArrayList.get(final_position).cat_name);
                        }
                    }

                }
            });

            customViewPagerDataClassArrayList.get(position).wittyFeedSDKCardFetcher.setWittyFeedSDKCardFetcherInterface(customViewPagerDataClassArrayList.get(position).wittyFeedSDKCardFetcherInterface);

            for (int i = 0; i <= 2; i++) {
                customViewPagerDataClassArrayList.get(position).wittyFeedSDKCardFetcher.fetch_a_card("category_card", 0.8f, customViewPagerDataClassArrayList.get(position).cat_name);
            }

            container.addView(v);
            return v;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return  customViewPagerDataClassArrayList.get(position).cat_name;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

    }


    class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

        int cat_pos;

        CategoryAdapter(int pos){
            this.cat_pos = pos;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.item_witty_normal_card,null,false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            int itemViewType = holder.getItemViewType();

            switch (itemViewType){
                case 0:
                    holder.item_cv.setVisibility(View.GONE);
                    holder.progressBar.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    ArrayList<View> witty_cards = customViewPagerDataClassArrayList.get(cat_pos).witty_cards;
                    holder.progressBar.setVisibility(View.GONE);
                    TextView tv = customViewPagerDataClassArrayList.get(cat_pos).witty_cards.get(position).findViewById(R.id.title_tv);
//                    Log.d(TAG, "onBindViewHolder: " + tv.getText());
                    holder.item_cv.setVisibility(View.VISIBLE);

                    ViewGroup vp = (ViewGroup) customViewPagerDataClassArrayList.get(cat_pos).witty_cards.get(position).getParent();
                    if(vp != null){
                        vp.removeView(customViewPagerDataClassArrayList.get(cat_pos).witty_cards.get(position));
                    }

                    holder.cardHolder_linearLayout.removeAllViews();
                    holder.cardHolder_linearLayout.addView(customViewPagerDataClassArrayList.get(cat_pos).witty_cards.get(position));
//                    Log.d(TAG, "onBindViewHolder: " + tv.getText());
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return customViewPagerDataClassArrayList.get(cat_pos).witty_cards.size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if(position == customViewPagerDataClassArrayList.get(cat_pos).witty_cards.size()){
                return 0;
            } else {
                return 1;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            View item_View;
            LinearLayout cardHolder_linearLayout;
            CardView item_cv;
            ProgressBar progressBar;
            public ViewHolder(View itemView) {
                super(itemView);
                this.item_View = itemView;
                cardHolder_linearLayout = itemView.findViewById(R.id.item_ll);
                progressBar = itemView.findViewById(R.id.progressBar);
                item_cv = itemView.findViewById(R.id.item_cv);
            }
        }
    }


    public class CustomViewPagerDataClass {
        ArrayList<View> witty_cards = new ArrayList<>();
        String cat_name = "";
        WittyFeedSDKCardFetcher wittyFeedSDKCardFetcher;
        boolean is_fetching_data = false;
        CategoryAdapter categoryAdapter;
        int cat_pos;
        RecyclerView feed_rv;
        LinearLayoutManager linearLayoutManager;
        WittyFeedSDKCardFetcherInterface wittyFeedSDKCardFetcherInterface;

        CustomViewPagerDataClass(ArrayList<View> param_witty_cards, int cat_pos, String param_cat_name, WittyFeedSDKCardFetcher param_wittyFeedSDKCardFetcher){
            this.witty_cards = param_witty_cards;
            this.cat_name = param_cat_name;
            this.cat_pos = cat_pos;
            this.wittyFeedSDKCardFetcher = param_wittyFeedSDKCardFetcher;
            this.categoryAdapter = new CategoryAdapter(cat_pos);
        }
    }

}