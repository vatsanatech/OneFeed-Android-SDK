package com.wittyfeed.sdk.onefeed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * The generic Adapter for the OneFeed used in both MainFeed and SearchFeed
 *  Read about: Holder Fragment, MainFeedFragment, SearchFeedFragment
 *
 * It executes the following way -
 *      1) receives data in constructor as array of Block
 *          Read about: Block, DataStore,
 *      2) receives FragmentType and identifies if fetchingMore progressBar is required
 *      3) Corresponding to blockArray-type getItemViewType sets the itemViewType
 *          read more in: Constant
 *      4) in onCreateViewHolder: corresponding to the itemViewType, viewHolder is prepared by object
 *          of CardViewHolderFactory
 *          Read about: CardViewHolderFactory
 *      5) in onBindViewHolder: corresponding to the itemViewType and position of viewHolder, data
 *          is bind to the viewHolder by object of the class CardDataViewHolderBinder
 *          Read about: CardDataViewHolderBinder
 *      6) ViewHolder class initialize the respective layout View & ViewGroup variables
 *          as required by their respective ViewHolder
 *          Read about: MainAdapterBaseViewHolder
 *
 */

class MainAdapter extends RecyclerView.Adapter<MainAdapterBaseViewHolder>{

    private ArrayList<Block> block_arr;
    private int fragment_type;
    private CardViewHolderFactory cardViewHolderFactory;
    private CardDataViewHolderBinder cardDataViewHolderBinder;


    MainAdapter(ArrayList<Block> block_arr, int fragment_type) {
        this.block_arr = block_arr;
        this.fragment_type = fragment_type;
        cardViewHolderFactory = new CardViewHolderFactory();
        cardDataViewHolderBinder = new CardDataViewHolderBinder();
    }

    @NonNull
    @Override
    public MainAdapterBaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        OFLogger.log(OFLogger.DEBUG, OFLogger.InOnCreate);

        cardViewHolderFactory.setInflater(LayoutInflater.from(viewGroup.getContext()));

        View view = null;

        switch (viewType){
            case Constant.POSTER_SOLO_NUM:
                view = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.POSTER_SOLO_NUM);
                return new PosterSoloVH(view);

            case Constant.POSTER_RV_NUM:
                view = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.POSTER_RV_NUM);
                return new PosterRVVH(view);

            case Constant.VIDEO_SOLO_NUM:
                view = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.VIDEO_SOLO_NUM);
                return new VideoSoloVH(view);

            case Constant.VIDEO_RV_NUM:
                view = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.VIDEO_RV_NUM);
                return new VideoRvVH(view);

            case Constant.STORY_LIST_NUM:
                view = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.STORY_LIST_NUM);
                return new StoryListVH(view);

            case Constant.COLLECTION_1_4_NUM:
                view = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.COLLECTION_1_4_NUM);
                return new Collection1_4VH(view);

            case Constant.PROGRESS_BAR:
                view = cardViewHolderFactory.getInflatedBlockViewHolder(Constant.PROGRESS_BAR);
                return new ProgressBarVH(view);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapterBaseViewHolder holder, int position) {
        int mPosition = holder.getAdapterPosition();
        int itemViewType = holder.getItemViewType();
        OFLogger.log(OFLogger.DEBUG, OFLogger.InOnBind);

        if(itemViewType == Constant.PROGRESS_BAR){
            return;
        }

        List<Card> cardList = null;
        if (fragment_type == 1) {
            cardList = OneFeedMain.getInstance().dataStore.getMainFeedDataBlockArr().get(mPosition).getCards();
        } else if(fragment_type == 2) {
            cardList = OneFeedMain.getInstance().dataStore.getSearchFeedDataBlockArr().get(mPosition).getCards();
        }

        switch (itemViewType){
            case Constant.POSTER_SOLO_NUM:
                cardDataViewHolderBinder.bindSingleCardData(holder, itemViewType, cardList.get(0), Constant.TextSizeRatioLarge);
                holder.root_vg.setPadding(0,0,0,0);

                break;
            case Constant.POSTER_RV_NUM:
                cardDataViewHolderBinder.bindMultiCardsData(holder, itemViewType, cardList, Constant.TextSizeRatioMedium);

                break;
            case Constant.VIDEO_SOLO_NUM:
                cardDataViewHolderBinder.bindSingleCardData(holder, itemViewType, cardList.get(0), Constant.TextSizeRatioLarge);
                holder.root_vg.setPadding(0,0,0,0);
                holder.sep_v.setVisibility(View.GONE);

                break;
            case Constant.VIDEO_RV_NUM:
                cardDataViewHolderBinder.bindMultiCardsData(holder, itemViewType, cardList, Constant.TextSizeRatioSmall);
                holder.root_vg.setPadding(0,0,0,0);

                break;
            case Constant.STORY_LIST_NUM:
                cardDataViewHolderBinder.bindMultiCardsData(holder, itemViewType, cardList, Constant.TextSizeRatioMedium);
                holder.root_vg.setPadding(0,0,0,0);

                break;
            case Constant.COLLECTION_1_4_NUM:
                cardDataViewHolderBinder.bindMultiCardsData(holder, itemViewType, cardList, Constant.TextSizeRatioMedium);
                holder.root_vg.setPadding(0,0,0,0);

                break;
        }
    }

    @Override
    public int getItemCount() {
        if(fragment_type == 1)
            return block_arr.size() + 1;
        return block_arr.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(fragment_type == 1 && position == block_arr.size()) {
            return Constant.PROGRESS_BAR;
        }
        switch (block_arr.get(position).getMeta().getType()){
            case Constant.POSTER_SOLO:      return Constant.POSTER_SOLO_NUM;
            case Constant.POSTER_RV:        return Constant.POSTER_RV_NUM;
            case Constant.VIDEO_SOLO:       return Constant.VIDEO_SOLO_NUM;
            case Constant.VIDEO_RV:         return Constant.VIDEO_RV_NUM;
            case Constant.STORY_LIST:       return Constant.STORY_LIST_NUM;
            case Constant.COLLECTION_1_4:   return Constant.COLLECTION_1_4_NUM;
            default:                        return 0;
        }
    }

    public void setBlock_arr(ArrayList<Block> block_arr) {
        this.block_arr = block_arr;
    }

    private class ProgressBarVH extends MainAdapterBaseViewHolder {
        private ProgressBarVH(View itemView){
            super(itemView);
            pb = itemView.findViewById(R.id.pb);
        }
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

    private class PosterRVVH extends MainAdapterBaseViewHolder {
        PosterRVVH(View itemView) {
            super(itemView);
            root_vg = itemView.findViewById(R.id.root_vg);
            rv = itemView.findViewById(R.id.rv);
        }
    }

    private class VideoSoloVH extends MainAdapterBaseViewHolder {
        private VideoSoloVH(View itemView) {
            super(itemView);
            root_vg = itemView.findViewById(R.id.root_vg);
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

    private class VideoRvVH extends MainAdapterBaseViewHolder {
        VideoRvVH(View itemView) {
            super(itemView);
            root_vg = itemView.findViewById(R.id.root_vg);
            rv = itemView.findViewById(R.id.rv);
        }
    }

    private class StoryListVH extends MainAdapterBaseViewHolder {
        StoryListVH(View itemView) {
            super(itemView);
            root_vg = itemView.findViewById(Constant.STORY_LIST_ROOT_LL_ID);
        }
    }

    private class Collection1_4VH extends MainAdapterBaseViewHolder {
        Collection1_4VH(View itemView) {
            super(itemView);
            root_vg = itemView.findViewById(R.id.root_vg);
            card_1_v = itemView.findViewById(R.id.card_1_v);
            card_2_v = itemView.findViewById(R.id.card_2_v);
            card_3_v = itemView.findViewById(R.id.card_3_v);
            card_4_v = itemView.findViewById(R.id.card_4_v);
            card_5_v = itemView.findViewById(R.id.card_5_v);
        }
    }
}


