package com.wittyfeed.sdk.onefeed;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

final class CardPosterRv {

    void initRv(RecyclerView rv, List<Card> cardList, double textSizeRatio){
        PosterRVAdapter posterRVAdapter = new PosterRVAdapter(cardList, textSizeRatio);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rv.getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(posterRVAdapter);
    }

    class PosterRVAdapter extends RecyclerView.Adapter<MainAdapterBaseViewHolder>{

        private final List<Card> cardList;
        private final double textSizeRatio;

        PosterRVAdapter(List<Card> cardList, double textSizeRatio) {
            this.cardList = cardList;
            this.textSizeRatio = textSizeRatio;
        }

        @NonNull
        @Override
        public MainAdapterBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardViewHolderFactory cardViewHolderFactory = new CardViewHolderFactory();
            cardViewHolderFactory.setInflater(LayoutInflater.from(parent.getContext()));
            View mView = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.POSTER_SOLO_NUM);
            return new PosterSoloVH(mView);
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapterBaseViewHolder holder, int position) {
            int width = (int) (Constant.getScreenWidth(holder.root_vg.getContext()) * textSizeRatio);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.root_vg.setLayoutParams(params);

            View temp_v1 = holder.story_title;
            temp_v1.setPadding(0, temp_v1.getPaddingTop(), temp_v1.getPaddingRight(), temp_v1.getPaddingBottom());

            View temp_v2 = holder.publisher_rl;
            temp_v2.setPadding(0, temp_v2.getPaddingTop(), temp_v2.getPaddingRight(), temp_v2.getPaddingBottom());

            CardDataViewHolderBinder cardDataViewHolderBinder = new CardDataViewHolderBinder();
            cardDataViewHolderBinder.bindSingleCardData(holder, Constant.POSTER_SOLO_NUM, cardList.get(position), textSizeRatio);

            holder.sep_v.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }

        private class PosterSoloVH extends MainAdapterBaseViewHolder {
            PosterSoloVH(View itemView) {
                super(itemView);
                root_vg = itemView.findViewById(R.id.root_vg);
                publisher_rl = itemView.findViewById(R.id.publisher_rl);
                sep_v = itemView.findViewById(R.id.sep_v);
                story_title = itemView.findViewById(R.id.title_tv);
                cover_image_iv = itemView.findViewById(R.id.cover_image_iv);
                img_container_vg = itemView.findViewById(R.id.img_container_vg);
                shield_tv = itemView.findViewById(R.id.shield_tv);
                badge_tv = itemView.findViewById(R.id.badge_tv);
                publisher_name_tv = itemView.findViewById(R.id.publisher_name_tv);
                publisher_iv = itemView.findViewById(R.id.publisher_iv);
                publisher_meta_tv = itemView.findViewById(R.id.publisher_meta_tv);
            }
        }
    }

}
