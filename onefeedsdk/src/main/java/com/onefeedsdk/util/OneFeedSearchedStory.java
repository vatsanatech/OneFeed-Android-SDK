package com.onefeedsdk.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.app.RuntimeStore;
import com.onefeedsdk.job.GetRepeatingCardJob;
import com.onefeedsdk.job.PostUserTrackingJob;
import com.onefeedsdk.listener.CallBackListener;
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.model.RepeatingCardModel;
import com.onefeedsdk.ui.NotificationOpenActivity;


/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 25-September-2018
 * Time: 18:26
 */
public class OneFeedSearchedStory {

    private static int SHOW_CARD_ID = -1;
    private static int OFFSET_CARD = 1;
    private static boolean HIT_API = true;

    public static synchronized String showSearchCard(final Context context, final View view){

        try {
            FeedModel feed = (FeedModel) RuntimeStore.getInstance().getValueFor(Constant.SEARCH_CARD);
            if(feed != null) {
                SHOW_CARD_ID++;
                if (SHOW_CARD_ID > feed.getFeedData().getBlocks().get(0).getCardList().size() - 1) {
                    SHOW_CARD_ID = 0;
                }
                final FeedModel.Card card = feed.getFeedData().getBlocks().get(0).getCardList().get(SHOW_CARD_ID);

                final int[] toolbarColor = {0};
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, NotificationOpenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        intent.putExtra(Constant.SEARCH_CARD_VIEWED, true);
                        intent.putExtra(Constant.COLOR, toolbarColor[0]);
                        intent.putExtra(Constant.TITLE, card.getStoryTitle());
                        intent.putExtra(Constant.URL, card.getStoryUrl());
                        intent.putExtra(Constant.ID, card.getStoryId());
                        context.startActivity(intent);

                       // Util.showCustomTabBrowserByCard(context, toolbarColor[0], card.getStoryTitle(), card.getStoryUrl(), card.getStoryId());
                    }
                });

                TextView titleView = view.getRootView().findViewWithTag("native_card_title");
                titleView.setText(card.getStoryTitle());

                final ImageView imageView = view.getRootView().findViewWithTag("native_card_image");
                imageView.setImageBitmap(null);
                String url = card.getCoverImage();
                Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                imageView.setImageBitmap(resource);
                                Palette.from(resource)
                                        .generate(new Palette.PaletteAsyncListener() {
                                            @Override
                                            public void onGenerated(@NonNull Palette palette) {
                                                Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                                if (textSwatch == null) {
                                                    return;
                                                }
                                                toolbarColor[0] = textSwatch.getRgb();
                                                imageView.setBackgroundColor(textSwatch.getRgb());
                                            }
                                        });
                            }
                        });

                //Tracking OneFeed Card View
                OneFeedSdk.getInstance().getJobManager().addJobInBackground(
                        new PostUserTrackingJob(Constant.CARD_VIEWED, Constant.CARD_VIEWED_BY_SEARCH, card.getStoryId()));

                return card.getSheildText();
            }else{
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
