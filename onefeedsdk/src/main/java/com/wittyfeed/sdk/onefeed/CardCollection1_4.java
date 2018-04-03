package com.wittyfeed.sdk.onefeed;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by aishwarydhare on 31/03/18.
 */

class CardCollection1_4 {

    private final RequestManager requestManager;
    private final String default_card_type;
    private Context context;
    private ArrayList<Card> cardArrayList = new ArrayList<>();

    CardCollection1_4(Context para_context, String default_card_type, ArrayList<Card> para_cards, RequestManager para_requestManager) {
        this.context = para_context;
        this.cardArrayList = para_cards;
        this.requestManager = para_requestManager;
        this.default_card_type = default_card_type;
    }

    View get_constructed_view() {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        ViewGroup root_view = (ViewGroup) layoutInflater.inflate(R.layout.block_collection_1_4, null, false);

        WittyFeedSDKCardFactory wittyFeedSDKCardFactory = new WittyFeedSDKCardFactory(context, requestManager);

        for(int i=0; i < cardArrayList.size(); i++){
            Card card = cardArrayList.get(i);

            String card_type = default_card_type;
            if(!card.getCardType().equalsIgnoreCase("")) {
                card_type = card.getCardType();
            }

            RelativeLayout rl;
            switch (i+1){
                case 1:
                    rl = root_view.findViewById(R.id.card_1_v);
                    rl.addView(wittyFeedSDKCardFactory.create_single_card(card, card_type, WittyFeedSDKSingleton.getInstance().MEDIUM_TSR));
                    rl.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    rl = root_view.findViewById(R.id.card_2_v);
                    rl.addView(wittyFeedSDKCardFactory.create_single_card(card, card_type, WittyFeedSDKSingleton.getInstance().MEDIUM_TSR));
                    rl.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    rl = root_view.findViewById(R.id.card_3_v);
                    rl.addView(wittyFeedSDKCardFactory.create_single_card(card, card_type, WittyFeedSDKSingleton.getInstance().MEDIUM_TSR));
                    rl.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    rl = root_view.findViewById(R.id.card_4_v);
                    rl.addView(wittyFeedSDKCardFactory.create_single_card(card, card_type, WittyFeedSDKSingleton.getInstance().MEDIUM_TSR));
                    rl.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    rl = root_view.findViewById(R.id.card_5_v);
                    rl.addView(wittyFeedSDKCardFactory.create_single_card(card, card_type, WittyFeedSDKSingleton.getInstance().MEDIUM_TSR));
                    rl.setVisibility(View.VISIBLE);
                    break;

            }
        }
        return root_view;
    }

}
