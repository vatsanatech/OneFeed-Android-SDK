package com.onefeed.sdk.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.util.OneFeedNativeCard;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 25-September-2018
 * Time: 17:34
 */
public class RepeatingCardActivity extends AppCompatActivity{

    private RecyclerView recyclerCard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_card);

        recyclerCard = findViewById(R.id.recycler_card);
        recyclerCard.setLayoutManager(new LinearLayoutManager(this));
        AdapterDemoCars adapter = new AdapterDemoCars();
        recyclerCard.setAdapter(adapter);
    }

    private class AdapterDemoCars extends RecyclerView.Adapter{

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType == 1) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_onefeed_card_row, parent, false);
                return new CardViewHolder(view);
            }else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.adapter_demo_row, parent, false);
                return new ViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {

            if(holder instanceof CardViewHolder){

                CardViewHolder holder1 = (CardViewHolder) holder;
                OneFeedNativeCard.showCard(RepeatingCardActivity.this, holder1.linearLayout, OneFeedSdk.V_List);
            }else{
                ViewHolder holder1 = (ViewHolder) holder;
                holder1.titleView.setText(R.string.string_dummy);
                holder1.imageView.setImageBitmap(null);
            }
        }

        @Override
        public int getItemCount() {
            return 30;
        }

        @Override
        public int getItemViewType(int position) {
            return position%3 == 0 ? 1 : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView titleView;
            LinearLayout linearLayout;
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);
                linearLayout = itemView.findViewById(R.id.layout);
                titleView = itemView.findViewById(R.id.view_text);
                imageView = itemView.findViewById(R.id.image_story);
            }
        }

        public class CardViewHolder extends RecyclerView.ViewHolder{

            TextView titleView;
            LinearLayout linearLayout;
            ImageView imageView;

            public CardViewHolder(View itemView) {
                super(itemView);
                linearLayout = itemView.findViewById(R.id.layout);
                titleView = itemView.findViewById(R.id.view_text);
                imageView = itemView.findViewById(R.id.image_story);
            }
        }
    }
}
