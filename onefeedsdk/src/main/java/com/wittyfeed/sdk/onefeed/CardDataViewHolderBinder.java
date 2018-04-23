package com.wittyfeed.sdk.onefeed;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

/**
 *
 * Binds data to respective ViewHolder as received
 *
 * it has following responsibilities -
 *      1) receive viewHolder object of MainAdapterBaseViewHolder
 *      2) receive itemView type
 *      3) receive card or card_array Data-Model bind view or views with
 *      4) receive required textSizeRatio which will be used to resize the card
 *          according the this ratio. Ratio lies between 0 to 1
 *          read more in Constant
 *      5) bind data to respective ViewHolder using received arguments
 *
 * it has following methods -
 *      1) bindSingleCardData: for itemView type with single card
 *              such as: PosterSolo, VideoSolo, StoryListItem, CollectionItem
 *          receive all required arguments and then start binding data
 *          as per itemView type received
 *      2) bindMultiCardsData: for itemView type with multiple cards
 *              such as: PosterRV, VideoRV, StoryList, Collection1_4
 *          receive all required arguments and then use respective view binder and builder classes
 *              i.e.: CardPosterRV, CardVideoRV, CardStoryList, CardCollection1_4List
 *      3) use methods below for binding card data and modify according to textSizeRatio
 *              (i) setStoryTitle
 *             (ii) setStoryCoverImage
 *            (iii) setPublisherImage
 *             (iv) setBadgeTv
 *              (v) setShieldTv
 *             (vi) setPublisherNameTv
 *            (vii) setPublisherMetaTv
 *           (viii) setMarginAndPadding
 *      4) set cardOnClickListener to open content or story of the respective card
 *
 */

final class CardDataViewHolderBinder {

    synchronized void bindSingleCardData(final MainAdapterBaseViewHolder baseViewHolder, final int itemViewType, final Card card, double textSizeRatio) {
        switch (itemViewType){
            case Constant.POSTER_SOLO_NUM:
            case Constant.VIDEO_SOLO_NUM:
                setStoryTitle(card.getStoryTitle(), baseViewHolder.story_title, textSizeRatio);
                setStoryCoverImage(card.getCoverImage(), baseViewHolder.cover_image_iv, baseViewHolder.img_container_vg, textSizeRatio);
                setShieldTv(card.getSheildText(), card.getSheildBg(), baseViewHolder.shield_tv, textSizeRatio);
                setBadgeTv(card.getBadgeText(), baseViewHolder.badge_tv, textSizeRatio);
                setPublisherImage(card.getPublisherIconUrl(), baseViewHolder.publisher_iv, textSizeRatio);
                setPublisherNameTv(card.getPublisherName(), baseViewHolder.publisher_name_tv, textSizeRatio);
                setPublisherMetaTv(card.getUserFullName(), card.getDoa(), baseViewHolder.publisher_meta_tv, textSizeRatio);

                break;
            case Constant.VIDEO_SMALL_SOLO_NUM:
                setStoryTitle(card.getStoryTitle(), baseViewHolder.story_title, textSizeRatio);
                setStoryCoverImage(card.getCoverImage(), baseViewHolder.cover_image_iv, baseViewHolder.img_container_vg, textSizeRatio);
                setBadgeTv(card.getBadgeText(), baseViewHolder.badge_tv, textSizeRatio);

                break;
            case Constant.STORY_LIST_ITEM_NUM:
                setStoryTitle(card.getStoryTitle(), baseViewHolder.story_title, textSizeRatio);
                setStoryCoverImage(card.getCoverImage(), baseViewHolder.cover_image_iv, baseViewHolder.img_container_vg, textSizeRatio);
                setShieldTv(card.getSheildText(), card.getSheildBg(), baseViewHolder.shield_tv, textSizeRatio);
                setPublisherImage(card.getPublisherIconUrl(), baseViewHolder.publisher_iv, textSizeRatio);
                setPublisherNameTv(card.getPublisherName(), baseViewHolder.publisher_name_tv, textSizeRatio);
                setPublisherMetaTv(card.getUserFullName(), card.getDoa(), baseViewHolder.publisher_meta_tv, textSizeRatio);

                break;
            case Constant.COLLECTION_ITEM_NUM:
                setStoryTitle(card.getStoryTitle(), baseViewHolder.story_title, textSizeRatio);
                setStoryCoverImage(card.getCoverImage(), baseViewHolder.cover_image_iv, baseViewHolder.img_container_vg, textSizeRatio);
                setShieldTv(card.getSheildText(), card.getSheildBg(), baseViewHolder.shield_tv, textSizeRatio);

                break;
            default:

                break;
        }
        baseViewHolder.root_vg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( itemViewType != Constant.COLLECTION_ITEM_NUM ) {
                    OFAnalytics.getInstance().sendAnalytics(
                            OFAnalytics.AnalyticsCat.WF_Story,
                            ""
                                    + card.getStoryTitle()
                                    + " : "
                                    + card.getId()
                    );
                }

                OneFeedMain.getInstance().getContentViewMaker(baseViewHolder.root_vg.getContext()).launch(baseViewHolder.root_vg.getContext(), card.getStoryUrl());
            }
        });
    }

    synchronized void bindMultiCardsData(MainAdapterBaseViewHolder baseViewHolder, int itemViewType, List<Card> cardList, double textSizeRatio) {
        switch (itemViewType){
            case Constant.POSTER_RV_NUM:
                CardPosterRv cardPosterRv = new CardPosterRv();
                cardPosterRv.initRv(baseViewHolder.rv, cardList, textSizeRatio);

                break;
            case Constant.VIDEO_RV_NUM:
                CardVideoRv cardVideoRv = new CardVideoRv();
                cardVideoRv.initRv(baseViewHolder.rv, cardList, textSizeRatio);

                break;
            case Constant.STORY_LIST_NUM:
                CardStoryList cardStoryList = new CardStoryList();
                cardStoryList.init(baseViewHolder.root_vg, cardList, textSizeRatio);

                break;
            case Constant.COLLECTION_1_4_NUM:
                CardCollection1_4List cardCollection1_4List = new CardCollection1_4List();
                cardCollection1_4List.init(baseViewHolder, cardList, textSizeRatio);

                break;
            default:
                break;
        }
    }

    private void setStoryTitle(String arg, TextView tv, double textSizeRatio){
        tv.setText(arg);
        tv.setTextSize((float)textSizeRatio * 20);
    }

    private void setStoryCoverImage(String urlArg, ImageView iv, View imgContainerView, double textSizeRatio){

        ViewGroup.LayoutParams layoutParams = imgContainerView.getLayoutParams();
        layoutParams.height = (int) (layoutParams.height*textSizeRatio);
        imgContainerView.setLayoutParams(layoutParams);

        String imgCoverUrl = urlArg;
        final String finalImg_cover = imgCoverUrl;

        OFGlide.with(iv.getContext()).clear(iv);

        OFGlide.with(iv.getContext())
                .load(imgCoverUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        OFLogger.log(OFLogger.DEBUG, "onLoadFailed: imgCover:" + finalImg_cover);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(iv);
    }

    private void setPublisherImage(final String urlArg, ImageView iv, double textSizeRatio){
        ViewGroup.LayoutParams layoutParams = iv.getLayoutParams();
        layoutParams.height = (int) (layoutParams.height*textSizeRatio);
        layoutParams.width = (int) (layoutParams.width*textSizeRatio);
        iv.setLayoutParams(layoutParams);

        OFGlide.with(iv.getContext()).clear(iv);

        OFGlide.with(iv.getContext())
                .load(urlArg)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))

                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        OFLogger.log(OFLogger.DEBUG, "onLoadFailed: imgCover:" + urlArg);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(iv);
    }

    private void setBadgeTv(String arg, TextView tv, double textSizeRatio){
        if (!arg.equalsIgnoreCase("")) {
            tv.setText(arg);
            tv.setTextSize((float) (10*textSizeRatio));
        }
        tv.setVisibility(View.GONE);
    }

    private void setShieldTv(String arg, String bgColorString, TextView tv, double textSizeRatio){
        if(arg.equalsIgnoreCase("")){
            tv.setVisibility(View.GONE);
        } else {
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] { Color.parseColor(bgColorString), Color.parseColor(bgColorString) } );
            gd.setCornerRadius((float) (15.0f*textSizeRatio));
            tv.setBackground(gd);
            tv.setText(arg);
            tv.setTextSize((float) (10 * textSizeRatio));
        }
    }

    private void setPublisherNameTv(String arg, TextView tv, double textSizeRatio){
        tv.setText(arg);
        tv.setTextSize((float) (14*textSizeRatio));
    }

    private void setPublisherMetaTv(String authorName, String doa, TextView tv, double textSizeRatio){
        if(authorName.equalsIgnoreCase("") || doa.equalsIgnoreCase("")){
            tv.setVisibility(View.INVISIBLE);
        } else {
            tv.setText("by " + authorName + " | " + doa);
            tv.setTextSize((float) (12 * textSizeRatio));
        }
    }

    private void setMarginAndPadding(ViewGroup root_vg) {
        root_vg.setPadding(0,0,0,0);
    }
}
