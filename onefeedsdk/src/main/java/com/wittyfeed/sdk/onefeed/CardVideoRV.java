package com.wittyfeed.sdk.onefeed;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

final class CardVideoRv {

    void initRv(RecyclerView rv, List<Card> cardList, double textSizeRatio){
        VideoRVAdapter videoRVAdapter = new VideoRVAdapter(cardList, textSizeRatio);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rv.getContext()){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(videoRVAdapter);
        rv.setBackgroundColor(Color.parseColor("#e5ebef"));
        rv.setPadding(
                0,
                (int)(80*textSizeRatio),
                0,
                (int)(80*textSizeRatio)
        );
    }

    class VideoRVAdapter extends RecyclerView.Adapter<MainAdapterBaseViewHolder> {

        private final List<Card> cardList;
        private final double textSizeRatio;

        VideoRVAdapter(List<Card> cardList, double textSizeRatio) {
            this.cardList = cardList;
            this.textSizeRatio = textSizeRatio;
        }

        @NonNull
        @Override
        public MainAdapterBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CardViewHolderFactory cardViewHolderFactory = new CardViewHolderFactory();
            cardViewHolderFactory.setInflater(LayoutInflater.from(parent.getContext()));
            View mView = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.VIDEO_SMALL_SOLO_NUM);
            return new VideoSmallSoloVH(mView);
        }

        @Override
        public void onBindViewHolder(@NonNull MainAdapterBaseViewHolder holder, int position) {
            int width = (int) (Constant.getScreenWidth(holder.root_vg.getContext()) * textSizeRatio);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            holder.root_vg.setLayoutParams(params);

            CardDataViewHolderBinder cardDataViewHolderBinder = new CardDataViewHolderBinder();
            cardDataViewHolderBinder.bindSingleCardData(holder, Constant.VIDEO_SMALL_SOLO_NUM, cardList.get(position), textSizeRatio);

            holder.sep_v.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }

        private class VideoSmallSoloVH extends MainAdapterBaseViewHolder {
            VideoSmallSoloVH(View itemView) {
                super(itemView);
                root_vg = itemView.findViewById(R.id.root_vg);
                sep_v = itemView.findViewById(R.id.sep_v);
                story_title = itemView.findViewById(R.id.title_tv);
                cover_image_iv = itemView.findViewById(R.id.cover_image_iv);
                img_container_vg = itemView.findViewById(R.id.img_container_vg);
                badge_tv = itemView.findViewById(R.id.badge_tv);
            }
        }
    }
}
