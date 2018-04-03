package com.wittyfeed.sdk.onefeed;

import android.app.Activity;
import android.content.Context;
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

class CardPosterRV {

    private final RequestManager requestManager;
    private final String default_card_type;
    private Context context;
    private ArrayList<Card> cardArrayList = new ArrayList<>();

    CardPosterRV(Context para_context, String default_card_type, ArrayList<Card> para_cards, RequestManager para_requestManager) {
        this.context = para_context;
        this.cardArrayList = para_cards;
        this.requestManager = para_requestManager;
        this.default_card_type = default_card_type;
    }

    View get_constructed_view() {
        View root_view = ((Activity) this.context).getLayoutInflater().inflate(R.layout.block_generic_for_rv, null, false);
        RecyclerView recyclerView = root_view.findViewById(R.id.poster_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new PosterRVAdapter());
        return root_view;
    }

    class PosterRVAdapter extends RecyclerView.Adapter<PosterRVAdapter.ViewHolder>{

        private WittyFeedSDKCardFactory wittyFeedSDKCardFactory;

        PosterRVAdapter() {
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
            default_params.width = (int) (WittyFeedSDKSingleton.getInstance().screenWidth * WittyFeedSDKSingleton.getInstance().MEDIUM_TSR);
            holder.container_view_rl.setLayoutParams(default_params);

            String card_type = default_card_type;
            if(!cardArrayList.get(holder.getAdapterPosition()).getCardType().equalsIgnoreCase("")){
                card_type = cardArrayList.get(holder.getAdapterPosition()).getCardType();
            }
            View child_view = wittyFeedSDKCardFactory.create_single_card(cardArrayList.get(holder.getAdapterPosition()), card_type, WittyFeedSDKSingleton.getInstance().MEDIUM_TSR);

            View temp_v1 = child_view.findViewById(R.id.title_tv);
            temp_v1.setPadding(0, temp_v1.getPaddingTop(), temp_v1.getPaddingRight(), temp_v1.getPaddingBottom());

            View temp_v2 = child_view.findViewById(R.id.publisher_rl);
            temp_v2.setPadding(0, temp_v2.getPaddingTop(), temp_v2.getPaddingRight(), temp_v2.getPaddingBottom());

            holder.container_view_rl.addView(child_view);
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
