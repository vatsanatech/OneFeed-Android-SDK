package com.wittyfeed.sdk.onefeed.Views.AdapterViewFactory;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import com.wittyfeed.sdk.onefeed.R;
import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;

/**
 *
 * Prepares required ViewHolder for usages throughout the SDK
 *
 * it has following responsibilities:
 *      1) receive layout inflater object to inflate layouts from resources
 *      2) receive itemViewType to prepare the ViewHolder corresponding to
 *          read more in Constant
 *
 * it has following methods:
 *      1) getProgressBarCardViewHolder: ViewHolder for progress bar
 *          refer to: R.layout.item_generic_progressbar
 *
 *      2) getPosterSoloCardViewHolder: view holder for itemView type PosterSolo
 *          refer to: R.layout.card_poster_solo
 *
 *      3) getPosterRVCardViewHolder: ViewHolder for itemView type PosterRV
 *          refer to: R.layout.block_generic_for_rv
 *
 *      4) getVideoSoloCardViewHolder: ViewHolder for itemView type VideoSolo
 *          refer to: R.layout.card_video_solo
 *
 *      5) getVideoRVCardViewHolder: ViewHolder for itemView type VideoRV
 *          refer to: R.layout.block_generic_for_rv
 *
 *      6) getStoryListCardViewHolder: ViewHolder for itemView type StoryList
 *
 *      7) getCollection14CardViewHolder: ViewHolder for itemView type Collection_1_4
 *          refer to: R.layout.block_collection_1_4
 *
 *      8) getVideoSmallSoloCard: ViewHolder for itemView type VideoSmallSolo
 *          refer to: R.layout.card_small_video_solo
 *
 *      9) getStoryListItemCard: ViewHolder for itemView type StoryListItem
 *          refer to: R.layout.card_story_list_item
 *
 *     10) getCollectionItemCard: ViewHolder for itemView type CollectionItem
 *          refer to: R.layout.card_collection_item
 *
 */

public class CardViewHolderFactory {

    private LayoutInflater inflater;

    public synchronized View getInflatedBlockViewHolder(int blockTypeNum){

        if(inflater == null){
            OFLogger.log(OFLogger.ERROR, OFLogger.InflaterIsNull);
            return null;
        }

        View inflatedBlockViewGroup = null;

        switch (blockTypeNum) {
            case Constant.PROGRESS_BAR:
                inflatedBlockViewGroup = getProgressBarCardViewHolder();

                break;
            case Constant.POSTER_SOLO_NUM:
                inflatedBlockViewGroup = getPosterSoloCardViewHolder();

                break;
            case Constant.POSTER_RV_NUM:
                inflatedBlockViewGroup = getPosterRVCardViewHolder();

                break;
            case Constant.VIDEO_SOLO_NUM:
                inflatedBlockViewGroup = getVideoSoloCardViewHolder();

                break;
            case Constant.VIDEO_RV_NUM:
                inflatedBlockViewGroup = getVideoRVCardViewHolder();

                break;
            case Constant.STORY_LIST_NUM:
                inflatedBlockViewGroup = getStoryListCardViewHolder();

                break;
            case Constant.COLLECTION_1_4_NUM:
                inflatedBlockViewGroup = getCollection14CardViewHolder();

                break;
            case Constant.VIDEO_SMALL_SOLO_NUM:
                inflatedBlockViewGroup = getVideoSmallSoloCard();

                break;
            case Constant.STORY_LIST_ITEM_NUM:
                inflatedBlockViewGroup = getStoryListItemCard();

                break;
            case Constant.COLLECTION_ITEM_NUM:
                inflatedBlockViewGroup = getCollectionItemCard();

                break;
        }

        return inflatedBlockViewGroup;
    }

    public synchronized void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    private View getProgressBarCardViewHolder(){
        return inflater.inflate(R.layout.item_generic_progressbar,null,false);
    }

    private View getPosterSoloCardViewHolder(){
        return inflater.inflate(R.layout.card_poster_solo,null,false);
    }

    private View getPosterRVCardViewHolder(){
        return inflater.inflate(R.layout.block_generic_for_rv, null, false);
    }

    private View getVideoSoloCardViewHolder(){
        return inflater.inflate(R.layout.card_video_solo,null,false);
    }

    private View getVideoRVCardViewHolder(){
        return inflater.inflate(R.layout.block_generic_for_rv, null, false);
    }

    private View getStoryListCardViewHolder(){
        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setId(Constant.STORY_LIST_ROOT_LL_ID);
        return linearLayout;
    }

    private View getCollection14CardViewHolder(){
        return inflater.inflate(R.layout.block_collection_1_4, null, false);
    }

    private View getVideoSmallSoloCard() {
        return inflater.inflate(R.layout.card_small_video_solo,null,false);
    }

    private View getStoryListItemCard() {
        return inflater.inflate(R.layout.card_story_list_item, null, false);
    }

    private View getCollectionItemCard() {
        return inflater.inflate(R.layout.card_collection_item,null,false);
    }

}
