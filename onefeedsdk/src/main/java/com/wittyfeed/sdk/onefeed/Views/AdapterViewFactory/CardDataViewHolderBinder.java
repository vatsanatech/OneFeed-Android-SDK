package com.wittyfeed.sdk.onefeed.Views.AdapterViewFactory;

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
import com.wittyfeed.sdk.onefeed.Utils.Constant;
import com.wittyfeed.sdk.onefeed.Models.Card;
import com.wittyfeed.sdk.onefeed.OFAnalytics;
import com.wittyfeed.sdk.onefeed.Utils.OFGlide;
import com.wittyfeed.sdk.onefeed.Utils.OFLogger;
import com.wittyfeed.sdk.onefeed.OneFeedMain;
import com.wittyfeed.sdk.onefeed.Views.MainAdapterBaseViewHolder;

import java.util.List;

/**
 <p><span style="font-size: 13pt;"><strong>Binds data to respective ViewHolder as received</strong></span></p>
 <p>It has following responsibilities -</p>
 <ol>
 <li>receive viewHolder object of MainAdapterBaseViewHolder</li>
 <li>receive itemView type</li>
 <li>receive card or card_array Data-Model bind view or views with</li>
 <li>receive required textSizeRatio which will be used to resize the card</li>
 <li>according the this ratio. Ratio lies between 0 to 1</li>
 <li>read more in Constant</li>
 <li>bind data to respective ViewHolder using received arguments</li>
 </ol>
 <p>It has following methods -</p>
 <ol>
 <li>bindSingleCardData: for itemView type with single card<br />such as: PosterSolo, VideoSolo, StoryListItem, CollectionItem<br />receive all required arguments and then start binding data<br />as per itemView type received</li>
 <li>bindMultiCardsData: for itemView type with multiple cards<br />such as: PosterRV, VideoRV, StoryList, Collection1_4<br />receive all required arguments and then use respective view binder and builder classes<br />i.e.: CardPosterRV, CardVideoRV, CardStoryList, CardCollection1_4List<br />use methods below for binding card data and modify according to textSizeRatio
 <ol style="list-style-type: lower-roman;">
 <li>setStoryTitle</li>
 <li>setStoryCoverImage</li>
 <li>setPublisherImage</li>
 <li>setBadgeTv</li>
 <li>setShieldTv</li>
 <li>setPublisherNameTv</li>
 <li>setPublisherMetaTv</li>
 <li>setMarginAndPadding</li>
 </ol>
 </li>
 <li>set cardOnClickListener to open content or story of the respective card</li>
 </ol>
 */

public final class CardDataViewHolderBinder {

    public synchronized void bindSingleCardData(final MainAdapterBaseViewHolder baseViewHolder, final int itemViewType, final Card card, double textSizeRatio) {
       try {
           switch (itemViewType) {
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
                   if (itemViewType != Constant.COLLECTION_ITEM_NUM) {
                       OFAnalytics.getInstance().sendAnalytics(
                               OFAnalytics.AnalyticsType.Story,
                               ""
                                       + card.getId()
                                       + ":"
                                       + "onefeed"
                       );
                   }
                   OneFeedMain.getInstance().getContentViewMaker(baseViewHolder.root_vg.getContext()).launch(baseViewHolder.root_vg.getContext(), card.getStoryUrl());
               }
           });
       }catch (Exception ignore){

       }
    }

    public synchronized void bindMultiCardsData(MainAdapterBaseViewHolder baseViewHolder, int itemViewType, List<Card> cardList, double textSizeRatio) {
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

    private void setStoryTitle(String arg, TextView tv, double textSizeRatio) throws Exception{
        tv.setText(arg);
        tv.setTextSize((float)textSizeRatio * 20);
    }

    private void setStoryCoverImage(String urlArg, ImageView iv, View imgContainerView, double textSizeRatio) throws Exception{

        ViewGroup.LayoutParams layoutParams = imgContainerView.getLayoutParams();
        layoutParams.height = (int) (layoutParams.height*textSizeRatio);
        imgContainerView.setLayoutParams(layoutParams);

        String imgCoverUrl = urlArg;
        final String finalImg_cover = imgCoverUrl;

        OFGlide.with(iv.getContext()).clear(iv);

        OFGlide.with(iv.getContext())
                .load(imgCoverUrl)
                .thumbnail(0.1f)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        OFLogger.log(OFLogger.DEBUG, OFLogger.OnLoadFailedImgCover + finalImg_cover);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(iv);
    }

    private void setPublisherImage(final String urlArg, ImageView iv, double textSizeRatio) throws Exception{
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
                        OFLogger.log(OFLogger.DEBUG, OFLogger.OnLoadFailedImgCover + urlArg);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(iv);
    }

    private void setBadgeTv(String arg, TextView tv, double textSizeRatio) throws Exception{
        if (!arg.isEmpty()) {
            tv.setText(arg);
            tv.setTextSize((float) (10*textSizeRatio));
        }
        tv.setVisibility(View.GONE);
    }

    private void setShieldTv(String arg, String bgColorString, TextView tv, double textSizeRatio) throws Exception{
        if(arg.isEmpty()){
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

    private void setPublisherNameTv(String arg, TextView tv, double textSizeRatio) throws Exception{
        tv.setText(arg);
        tv.setTextSize((float) (14*textSizeRatio));
    }

    private void setPublisherMetaTv(String authorName, String doa, TextView tv, double textSizeRatio) throws Exception{
        tv.setVisibility(View.INVISIBLE);
        if(!authorName.isEmpty() || !doa.isEmpty()){
            tv.setVisibility(View.VISIBLE);
        }
        tv.setText(authorName + " | " + doa);
        tv.setTextSize((float) (12 * textSizeRatio));
    }
}
