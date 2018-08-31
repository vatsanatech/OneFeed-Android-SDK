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

public final class CardCollection1_4List {

    public void init(MainAdapterBaseViewHolder baseViewHolder, List<Card> cardList, double textSizeRatio){
        baseViewHolder.root_vg.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Card card;
        for(int i=0; i < cardList.size(); i++){
            card = cardList.get(i);

            CardViewHolderFactory cardViewHolderFactory = new CardViewHolderFactory();
            cardViewHolderFactory.setInflater(LayoutInflater.from(baseViewHolder.root_vg.getContext()));
            View mView = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.COLLECTION_ITEM_NUM);

            CollectionItem collectionItem = new CollectionItem(mView);

            CardDataViewHolderBinder cardDataViewHolderBinder = new CardDataViewHolderBinder();
            cardDataViewHolderBinder.bindSingleCardData(baseViewHolder.root_vg.getContext(), collectionItem, Constant.COLLECTION_ITEM_NUM, card, textSizeRatio);

            ViewGroup.LayoutParams layoutParams = collectionItem.img_container_vg.getLayoutParams();
            layoutParams.height = layoutParams.width;
            collectionItem.img_container_vg.setLayoutParams(layoutParams);

            ViewGroup.LayoutParams layoutParams2 = collectionItem.content_container_vg.getLayoutParams();
            layoutParams2.height = layoutParams2.width;
            collectionItem.content_container_vg.setLayoutParams(layoutParams2);

            switch (i+1){
                case 1:
                    baseViewHolder.card_1_v.addView(mView);
                    baseViewHolder.card_1_v.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    baseViewHolder.card_2_v.addView(mView);
                    baseViewHolder.card_2_v.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    baseViewHolder.card_3_v.addView(mView);
                    baseViewHolder.card_3_v.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    baseViewHolder.card_4_v.addView(mView);
                    baseViewHolder.card_4_v.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    baseViewHolder.card_5_v.addView(mView);
                    baseViewHolder.card_5_v.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private class CollectionItem extends MainAdapterBaseViewHolder{

        CollectionItem(View itemView) {
            super(itemView);
            root_vg = itemView.findViewById(R.id.root_vg);
            story_title = itemView.findViewById(R.id.title_tv);
            cover_image_iv = itemView.findViewById(R.id.cover_image_iv);
            img_container_vg = itemView.findViewById(R.id.img_container_vg);
            shield_tv = itemView.findViewById(R.id.shield_tv);
            content_container_vg = itemView.findViewById(R.id.content_container_vg);
        }
    }
}
