package com.wittyfeed.sdk.onefeed.Views.AdapterViewFactory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Models.Card;
import com.wittyfeed.sdk.onefeed.R;
import com.wittyfeed.sdk.onefeed.Views.MainAdapterBaseViewHolder;

import java.util.List;

public final class CardStoryList {

    public void init(ViewGroup vg, List<Card> cardList, double textSizeRatio){
        vg.removeAllViews();
        vg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Card card;
        for(int i=0; i < cardList.size(); i++){
            card = cardList.get(i);

            CardViewHolderFactory cardViewHolderFactory = new CardViewHolderFactory();
            cardViewHolderFactory.setInflater(LayoutInflater.from(vg.getContext()));
            View mView = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.STORY_LIST_ITEM_NUM);

            StoryListItemVH storyListItemVH = new StoryListItemVH(mView);

            CardDataViewHolderBinder cardDataViewHolderBinder = new CardDataViewHolderBinder();
            cardDataViewHolderBinder.bindSingleCardData(storyListItemVH, Constant.STORY_LIST_ITEM_NUM, card, textSizeRatio);

            ViewGroup.LayoutParams layoutParams = storyListItemVH.img_container_vg.getLayoutParams();
            layoutParams.width = layoutParams.height;
            storyListItemVH.img_container_vg.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams layoutParams2 = storyListItemVH.content_container_vg.getLayoutParams();
            layoutParams2.height = (int) (layoutParams2.height*textSizeRatio);
            storyListItemVH.content_container_vg.setLayoutParams(layoutParams2);

            storyListItemVH.sep_v.setVisibility(View.GONE);
            if(i == cardList.size()-1){
                storyListItemVH.sep_v.setVisibility(View.VISIBLE);
            }

            vg.addView(mView);
        }
    }

    private class StoryListItemVH extends MainAdapterBaseViewHolder {

        StoryListItemVH(View itemView) {
            super(itemView);
            root_vg = itemView.findViewById(R.id.root_vg);
            publisher_rl = itemView.findViewById(R.id.publisher_rl);
            sep_v = itemView.findViewById(R.id.sep_v);
            story_title = itemView.findViewById(R.id.title_tv);
            cover_image_iv = itemView.findViewById(R.id.cover_image_iv);
            img_container_vg = itemView.findViewById(R.id.img_container_vg);
            shield_tv = itemView.findViewById(R.id.shield_tv);
            publisher_name_tv = itemView.findViewById(R.id.publisher_name_tv);
            publisher_iv = itemView.findViewById(R.id.publisher_iv);
            publisher_meta_tv = itemView.findViewById(R.id.publisher_meta_tv);
            content_container_vg = itemView.findViewById(R.id.content_container_vg);
        }
    }
}
