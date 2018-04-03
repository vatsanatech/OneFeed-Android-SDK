package com.wittyfeed.sdk.onefeed;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

/**
 * Created by aishwarydhare on 22/10/17.
 */


class WittyFeedSDKCardFactory {

    private static final String TAG = "WF_SDK";
    private Context context;
    private RequestManager requestManager;
    private WittyFeedSDKOneFeedBuilder wittyFeedSDKOneFeedBuilder;

    WittyFeedSDKCardFactory(final Context para_context, RequestManager para_requestManager){
        this.context = para_context;
        this.requestManager = para_requestManager;
        wittyFeedSDKOneFeedBuilder = new WittyFeedSDKOneFeedBuilder(context, 2);
    }


    View create_single_card(Card card, String card_type, double text_size_ratio){
        View view = null;
        switch (card_type){
            case "poster_solo":
                view = create_poster_solo_card(card, text_size_ratio);
                break;

            case "video_solo":
                view = create_video_solo_card(card, text_size_ratio);
                break;

            case "video_small_solo":
                view = create_small_video_solo_card(card, text_size_ratio);
                break;

            case "story_list_item":
                view = create_story_list_item_card(card, text_size_ratio);
                break;

            case "collection_item":
                view = create_collection_item_card(card, text_size_ratio);
                break;

            default:
                break;
        }
        return view;
    }


    View create_cards_rv(ArrayList<Card> cards, String card_type) {
        View view = null;
        switch (card_type){
            case "poster_rv":
                view = create_poster_rv(cards);
                break;

            case "video_rv":
                view = create_video_rv(cards);
                break;

            case "story_list":
                view = create_story_list(cards);
                break;

            case "collection_1_4":
                view = create_collection_1_4(cards);
                break;

            default:
                break;
        }

        return view;
    }


    private View create_poster_solo_card(final Card card, double text_size_ratio){
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.card_poster_solo,null,false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wittyFeedSDKOneFeedBuilder.launch(card.getStoryUrl());
            }
        });

        TextView shield_tv = view.findViewById(R.id.shield_tv);
        if(card.getSheildText().equalsIgnoreCase("")){
            shield_tv.setVisibility(View.GONE);
        } else {
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] { Color.parseColor(card.getSheildBg()), Color.parseColor(card.getSheildBg()) } );
            gd.setCornerRadius((float) (15.0f*text_size_ratio));
            shield_tv.setBackground(gd);
            shield_tv.setText(card.getSheildText());
            shield_tv.setTextSize((float) (10 * text_size_ratio));
        }

        final TextView story_title = view.findViewById(R.id.title_tv);
        story_title.setText(card.getStoryTitle());
        story_title.setTextSize((float)text_size_ratio * 20);

        TextView publisher_name_tv = view.findViewById(R.id.publisher_name_tv);
        publisher_name_tv.setText(card.getPublisherName());
        publisher_name_tv.setTextSize((float) (14*text_size_ratio));

        ImageView publisher_iv = view.findViewById(R.id.publisher_iv);
        ViewGroup.LayoutParams layoutParams = publisher_iv.getLayoutParams();
        layoutParams.height = (int) (layoutParams.height*text_size_ratio);
        layoutParams.width = (int) (layoutParams.width*text_size_ratio);
        publisher_iv.setLayoutParams(layoutParams);
        requestManager
                .load(card.getPublisherIconUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(publisher_iv);

        TextView publisher_meta_tv = view.findViewById(R.id.publisher_meta_tv);
        if(card.getUserFullName().equalsIgnoreCase("") || card.getDoa().equalsIgnoreCase("")){
            publisher_meta_tv.setVisibility(View.INVISIBLE);
        } else {
            publisher_meta_tv.setText("by " + card.getUserFullName() + " on " + card.getDoa());
            publisher_meta_tv.setTextSize((float) (12 * text_size_ratio));
        }

        ViewGroup img_container_vg = view.findViewById(R.id.img_container_vg);
        ViewGroup.LayoutParams layoutParams2 = img_container_vg.getLayoutParams();
        layoutParams2.height = (int) (layoutParams2.height*text_size_ratio);
        img_container_vg.setLayoutParams(layoutParams2);

        ImageView cover_image_iv = view.findViewById(R.id.cover_image_iv);
        String img_cover = card.getCoverImage();

        final String finalImg_cover = img_cover;
        requestManager
                .load(img_cover)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: imgCover:" + finalImg_cover);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(cover_image_iv);

        TextView badge_tv = view.findViewById(R.id.badge_tv);
        if (!card.getBadgeText().equalsIgnoreCase("")) {
            badge_tv.setText(card.getBadgeText());
            badge_tv.setTextSize((float) (10*text_size_ratio));
        }
        badge_tv.setVisibility(View.GONE);

        return view;
    }


    private View create_video_solo_card(final Card card, double text_size_ratio){
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.card_video_solo,null,false);

        TextView shield_tv = view.findViewById(R.id.shield_tv);
        if(card.getSheildText().equalsIgnoreCase("")){
            shield_tv.setVisibility(View.GONE);
        } else {
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] { Color.parseColor(card.getSheildBg()), Color.parseColor(card.getSheildBg()) } );
            gd.setCornerRadius((float) (15.0f*text_size_ratio));
            shield_tv.setBackground(gd);
            shield_tv.setText(card.getSheildText());
            shield_tv.setTextSize((float) (10 * text_size_ratio));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wittyFeedSDKOneFeedBuilder.launch(card.getStoryUrl());
            }
        });

        TextView story_title = view.findViewById(R.id.title_tv);
        story_title.setText(card.getStoryTitle());
        story_title.setTextSize((float)text_size_ratio * 20);

        TextView publisher_name_tv = view.findViewById(R.id.publisher_name_tv);
        publisher_name_tv.setText(card.getPublisherName());
        publisher_name_tv.setTextSize((float) (14*text_size_ratio));

        ImageView publisher_iv = view.findViewById(R.id.publisher_iv);
        ViewGroup.LayoutParams layoutParams = publisher_iv.getLayoutParams();
        layoutParams.height = (int) (layoutParams.height*text_size_ratio);
        layoutParams.width = (int) (layoutParams.width*text_size_ratio);
        publisher_iv.setLayoutParams(layoutParams);
        requestManager
                .load(card.getPublisherIconUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(publisher_iv);

        TextView publisher_meta_tv = view.findViewById(R.id.publisher_meta_tv);
        if(card.getUserFullName().equalsIgnoreCase("") || card.getDoa().equalsIgnoreCase("")){
            publisher_meta_tv.setVisibility(View.INVISIBLE);
        } else {
            publisher_meta_tv.setText("by " + card.getUserFullName() + " on " + card.getDoa());
            publisher_meta_tv.setTextSize((float) (12 * text_size_ratio));
        }

        ViewGroup img_container_vg = view.findViewById(R.id.img_container_vg);
        ViewGroup.LayoutParams layoutParams2 = img_container_vg.getLayoutParams();
        layoutParams2.height = (int) (layoutParams2.height*text_size_ratio);
        img_container_vg.setLayoutParams(layoutParams2);

        ImageView cover_image_iv = view.findViewById(R.id.cover_image_iv);
        String img_cover = card.getCoverImage();

        final String finalImg_cover = img_cover;
        requestManager
                .load(img_cover)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: imgCover:" + finalImg_cover);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(cover_image_iv);

        TextView badge_tv = view.findViewById(R.id.badge_tv);
        if (!card.getBadgeText().equalsIgnoreCase("")) {
            badge_tv.setText(card.getBadgeText());
            badge_tv.setTextSize((float) (10*text_size_ratio));
        }
        badge_tv.setVisibility(View.GONE);

        return view;
    }


    private View create_small_video_solo_card(final Card card, double text_size_ratio) {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.card_small_video_solo,null,false);

        TextView story_title = view.findViewById(R.id.title_tv);
        story_title.setText(card.getStoryTitle());
        story_title.setTextSize((float)text_size_ratio * 30);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wittyFeedSDKOneFeedBuilder.launch(card.getStoryUrl());
            }
        });

        ViewGroup img_container_vg = view.findViewById(R.id.img_container_vg);
        ViewGroup.LayoutParams layoutParams2 = img_container_vg.getLayoutParams();
        layoutParams2.height = (int) (layoutParams2.height*text_size_ratio);
        img_container_vg.setLayoutParams(layoutParams2);

        ImageView cover_image_iv = view.findViewById(R.id.cover_image_iv);
        String img_cover = card.getCoverImage();

        final String finalImg_cover = img_cover;
        requestManager
                .load(img_cover)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: imgCover:" + finalImg_cover);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(cover_image_iv);

        TextView badge_tv = view.findViewById(R.id.badge_tv);
        if (!card.getBadgeText().equalsIgnoreCase("")) {
            badge_tv.setText(card.getBadgeText());
            badge_tv.setTextSize((float) (10*text_size_ratio));
        }
        badge_tv.setVisibility(View.GONE);

        return view;
    }


    private View create_story_list_item_card(final Card card, double text_size_ratio) {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.card_story_list_item,null,false);

        TextView shield_tv = view.findViewById(R.id.shield_tv);
        if(card.getSheildText().equalsIgnoreCase("")){
            shield_tv.setVisibility(View.GONE);
        } else {
            GradientDrawable gd = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] { Color.parseColor(card.getSheildBg()), Color.parseColor(card.getSheildBg()) } );
            gd.setCornerRadius((float) (15.0f*text_size_ratio));
            shield_tv.setBackground(gd);
            shield_tv.setText(card.getSheildText());
            shield_tv.setTextSize((float) (10 * text_size_ratio));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wittyFeedSDKOneFeedBuilder.launch(card.getStoryUrl());
            }
        });

        TextView story_title = view.findViewById(R.id.title_tv);
        story_title.setText(card.getStoryTitle());
        story_title.setTextSize((float)text_size_ratio * 20);

        TextView publisher_name_tv = view.findViewById(R.id.publisher_name_tv);
        publisher_name_tv.setText(card.getPublisherName());
        publisher_name_tv.setTextSize((float) (16 * text_size_ratio));

        ImageView publisher_iv = view.findViewById(R.id.publisher_iv);
        ViewGroup.LayoutParams layoutParams = publisher_iv.getLayoutParams();
        layoutParams.height = (int) (layoutParams.height*text_size_ratio);
        layoutParams.width = (int) (layoutParams.width*text_size_ratio);
        publisher_iv.setLayoutParams(layoutParams);
        requestManager
                .load(card.getPublisherIconUrl())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(publisher_iv);

        TextView publisher_meta_tv = view.findViewById(R.id.publisher_meta_tv);
        if(card.getUserFullName().equalsIgnoreCase("") || card.getDoa().equalsIgnoreCase("")){
            publisher_meta_tv.setVisibility(View.INVISIBLE);
        } else {
            publisher_meta_tv.setText("by " + card.getUserFullName() + " on " + card.getDoa());
            publisher_meta_tv.setTextSize((float) (14 * text_size_ratio));
        }

        ViewGroup content_container_vg = view.findViewById(R.id.content_container_vg);
        ViewGroup.LayoutParams layoutParams3 = content_container_vg.getLayoutParams();
        layoutParams3.height = (int) (layoutParams3.height*text_size_ratio);
        content_container_vg.setLayoutParams(layoutParams3);

        ViewGroup img_container_vg = view.findViewById(R.id.img_container_vg);
        ViewGroup.LayoutParams layoutParams2 = img_container_vg.getLayoutParams();
        layoutParams2.height = (int) (layoutParams2.height*text_size_ratio);
        layoutParams2.width = layoutParams2.height;
        img_container_vg.setLayoutParams(layoutParams2);

        ImageView cover_image_iv = view.findViewById(R.id.cover_image_iv);
        String img_cover = card.getSquareCoverImage();
        if(img_cover.equalsIgnoreCase(""))
            img_cover = card.getCoverImage();
        final String finalImg_cover = img_cover;
        requestManager
                .load(img_cover)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: imgCover:" + finalImg_cover);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(cover_image_iv);

        return view;
    }


    private View create_collection_item_card(final Card card, double text_size_ratio) {
        View view = ((Activity)context).getLayoutInflater().inflate(R.layout.card_collection_item,null,false);

        TextView shield_tv = view.findViewById(R.id.shield_tv);
        if(card.getSheildText().equalsIgnoreCase("")){
            shield_tv.setVisibility(View.INVISIBLE);
        } else {
            shield_tv.setText(card.getSheildText());
            shield_tv.setTextSize((float) text_size_ratio * 10);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wittyFeedSDKOneFeedBuilder.launch(card.getStoryUrl());
            }
        });

        TextView story_title = view.findViewById(R.id.title_tv);
        story_title.setText(card.getStoryTitle());
        story_title.setTextSize((float)text_size_ratio * 20);

        ImageView cover_image_iv = view.findViewById(R.id.cover_image_iv);
        String img_cover = card.getSquareCoverImage();
        if(img_cover.equalsIgnoreCase(""))
            img_cover = card.getCoverImage();
        final String finalImg_cover = img_cover;
        requestManager
                .load(img_cover)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.DATA))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: imgCover:" + finalImg_cover);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(cover_image_iv);

        return view;
    }


    private View create_poster_rv(ArrayList<Card> cardArrayList){
        CardPosterRV cardPosterRV = new CardPosterRV(context, "poster_solo", cardArrayList, requestManager);
        return cardPosterRV.get_constructed_view();
    }


    private View create_video_rv(ArrayList<Card> cardArrayList) {
        CardVideoRV cardPosterRV = new CardVideoRV(context, "video_small_solo", cardArrayList, requestManager);
        return cardPosterRV.get_constructed_video_rv();
    }


    private View create_story_list(ArrayList<Card> cardArrayList) {
        CardStoryList cardStoryList = new CardStoryList(context, "story_list_item", cardArrayList, requestManager);
        return cardStoryList.get_constructed_view();
    }


    private View create_collection_1_4(ArrayList<Card> cardArrayList) {
        CardCollection1_4 cardCollection1_4 = new CardCollection1_4(context, "collection_item", cardArrayList, requestManager);
        return cardCollection1_4.get_constructed_view();
    }


}
