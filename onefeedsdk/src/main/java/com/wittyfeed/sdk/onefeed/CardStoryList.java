package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by aishwarydhare on 30/03/18.
 */

class CardStoryList {

    private final RequestManager requestManager;
    private final String default_card_type;
    private Context context;
    private ArrayList<Card> cardArrayList = new ArrayList<>();

    CardStoryList(Context para_context, String default_card_type, ArrayList<Card> para_cards, RequestManager para_requestManager) {
        this.context = para_context;
        this.cardArrayList = para_cards;
        this.requestManager = para_requestManager;
        this.default_card_type = default_card_type;
    }

    View get_constructed_view() {
        LinearLayout root_ll = new LinearLayout(context);
        root_ll.setOrientation(LinearLayout.VERTICAL);
        root_ll.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        WittyFeedSDKCardFactory wittyFeedSDKCardFactory = new WittyFeedSDKCardFactory(context, requestManager);

        for(int i=0; i < cardArrayList.size(); i++){
            Card card = cardArrayList.get(i);

            String card_type = default_card_type;
            if(!card.getCardType().equalsIgnoreCase("")) {
                card_type = card.getCardType();
            }


            root_ll.addView(wittyFeedSDKCardFactory.create_single_card(card, card_type, WittyFeedSDKSingleton.getInstance().MEDIUM_TSR));
        }
        return root_ll;
    }

}
