package com.wittyfeed.sdk.onefeed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by aishwarydhare on 30/03/18.
 */

class CardVideoRV {

    private final RequestManager requestManager;
    private final String default_card_type;
    private Context context;
    private ArrayList<Card> cardArrayList = new ArrayList<>();

    CardVideoRV(Context para_context, String default_card_type, ArrayList<Card> para_cards, RequestManager para_requestManager) {
        this.context = para_context;
        this.cardArrayList = para_cards;
        this.requestManager = para_requestManager;
        this.default_card_type = default_card_type;
    }

    View get_constructed_video_rv() {
        View root_view = ((Activity) this.context).getLayoutInflater().inflate(R.layout.block_generic_for_rv, null, false);
        root_view.setBackgroundColor(Color.parseColor("#e5ebef"));
        RecyclerView recyclerView = root_view.findViewById(R.id.poster_rv);
        recyclerView.setPadding(0,(int) (80*WittyFeedSDKSingleton.getInstance().SMALL_TSR),
                0, (int) (80*WittyFeedSDKSingleton.getInstance().SMALL_TSR));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new VideoRVAdapter());
        return root_view;
    }

    class VideoRVAdapter extends RecyclerView.Adapter<VideoRVAdapter.ViewHolder>{

        private WittyFeedSDKCardFactory wittyFeedSDKCardFactory;

        VideoRVAdapter() {
            this.wittyFeedSDKCardFactory = new WittyFeedSDKCardFactory(context, requestManager);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = ((Activity)context).getLayoutInflater().inflate(R.layout.item_generic_rv, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if(holder.container_view_rl != null) holder.container_view_rl.removeAllViews();

            RecyclerView.LayoutParams default_params = null;

            default_params = (RecyclerView.LayoutParams) holder.container_view_rl.getLayoutParams();
            default_params.width = (int) (WittyFeedSDKSingleton.getInstance().screenWidth * 0.6);
            holder.container_view_rl.setLayoutParams(default_params);

            String card_type = default_card_type;
            if(!cardArrayList.get(holder.getAdapterPosition()).getCardType().equalsIgnoreCase("")){
                card_type = cardArrayList.get(holder.getAdapterPosition()).getCardType();
            }
            holder.container_view_rl.addView(wittyFeedSDKCardFactory.create_single_card(cardArrayList.get(holder.getAdapterPosition()), card_type, WittyFeedSDKSingleton.getInstance().SMALL_TSR));
        }

        @Override
        public int getItemCount() {
            return cardArrayList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout container_view_rl;
            ViewHolder(View itemView) {
                super(itemView);
                container_view_rl = itemView.findViewById(R.id.container_view_rl);
            }
        }
    }
}
