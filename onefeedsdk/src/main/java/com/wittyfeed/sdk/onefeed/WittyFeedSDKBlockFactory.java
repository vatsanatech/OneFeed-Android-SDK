package com.wittyfeed.sdk.onefeed;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;

/**
 * Created by anujagarwal on 29/03/18.
 */

class WittyFeedSDKBlockFactory {

    private RequestManager requestManager;
    private Context context;
    private WittyFeedSDKCardFactory wittyFeedSDKCardFactory;

    WittyFeedSDKBlockFactory(RequestManager requestManager, Context para_context){
        this.requestManager = requestManager;
        this.context = para_context;
        wittyFeedSDKCardFactory = new WittyFeedSDKCardFactory(context, requestManager);
    }

    View inflate_block(int type){
        View view = new View(context);
        switch (type){

            case 1: //poster_solo
                view = ((Activity)context).getLayoutInflater().inflate(R.layout.block_generic_container, null);
                break;

            case 2: //poster_rv
                view = ((Activity)context).getLayoutInflater().inflate(R.layout.block_generic_container, null);
                break;

            case 3: //video_solo
                view = ((Activity)context).getLayoutInflater().inflate(R.layout.block_generic_container, null);
                break;

            case 4: //video_rv
                view = ((Activity)context).getLayoutInflater().inflate(R.layout.block_generic_container, null);
                break;

            case 5: //story_list
                view = ((Activity)context).getLayoutInflater().inflate(R.layout.block_generic_container, null);
                break;

            case 6: //collection_1_4
                view = ((Activity)context).getLayoutInflater().inflate(R.layout.block_generic_container, null);
                break;

            default:
                break;
        }
        return view;
    }


    View get_constructed_block_view(Block block) {
        View view_to_return = null;

        String card_type = block.getMeta().getType();
        if (!block.getMeta().getType().equalsIgnoreCase("poster_rv")
                && !block.getMeta().getType().equalsIgnoreCase("video_rv")
                && !block.getMeta().getType().equalsIgnoreCase("story_list")) {
            if(!block.getCards().get(0).getCardType().equalsIgnoreCase("")){
                card_type = block.getCards().get(0).getCardType();
            }
        }


        switch (card_type){

            case "poster_solo":
                view_to_return = wittyFeedSDKCardFactory.create_single_card(block.getCards().get(0), card_type, WittyFeedSDKSingleton.getInstance().LARGE_TSR);
                break;

            case "video_solo":
                view_to_return = wittyFeedSDKCardFactory.create_single_card(block.getCards().get(0), card_type, WittyFeedSDKSingleton.getInstance().LARGE_TSR);
                break;

            case "poster_rv":
                view_to_return = wittyFeedSDKCardFactory.create_cards_rv((ArrayList<Card>) block.getCards(), card_type);
                break;

            case "video_rv":
                view_to_return = wittyFeedSDKCardFactory.create_cards_rv((ArrayList<Card>) block.getCards(), card_type);
                break;

            case "story_list":
                view_to_return = wittyFeedSDKCardFactory.create_cards_rv((ArrayList<Card>) block.getCards(), card_type);
                break;

            case "collection_1_4":
                view_to_return = wittyFeedSDKCardFactory.create_cards_rv((ArrayList<Card>) block.getCards(), card_type);
                break;

            default:
                break;
        }

        return view_to_return;
    }
}