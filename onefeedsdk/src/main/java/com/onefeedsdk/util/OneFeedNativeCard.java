package com.onefeedsdk.util;

import android.content.Context;
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
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.model.RepeatingCardModel;


/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 25-September-2018
 * Time: 18:26
 */
public class OneFeedNativeCard {

    private static int SHOW_CARD_ID = -1;
    private static int OFFSET_CARD = 1;
    private static boolean HIT_API = true;

    public static synchronized void showCard(final Context context, final View view, String reference, boolean isVerticalImage){

        try {
            RepeatingCardModel feed = (RepeatingCardModel) RuntimeStore.getInstance().getValueFor(Constant.NATIVE_CARD);
            if(feed != null) {
                SHOW_CARD_ID++;
                fetchNewCard(feed);
                if (SHOW_CARD_ID > feed.getRepeatingCard().getCardList().size() - 1) {
                    SHOW_CARD_ID = 0;
                }
                final FeedModel.Card card = feed.getRepeatingCard().getCardList().get(SHOW_CARD_ID);

                final int[] toolbarColor = {0};
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showCustomTabBrowserByCard(context, toolbarColor[0], card.getStoryTitle(), card.getStoryUrl(), card.getStoryId());
                    }
                });

                TextView titleView = view.getRootView().findViewWithTag("native_card_title");
                titleView.setText(card.getStoryTitle());

                final ImageView imageView = view.getRootView().findViewWithTag("native_card_image");
                imageView.setImageBitmap(null);
                String url = card.getCoverImage();
                if(isVerticalImage && !TextUtils.isEmpty(card.getSquareImage())){
                    url = card.getSquareImage();
                }

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
                        new PostUserTrackingJob(Constant.CARD_VIEWED, reference, card.getStoryId()));
            }else{
                fetchNewCard(feed);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void fetchNewCard( RepeatingCardModel feed) {
        try {
            if(feed == null){
                OneFeedSdk.getInstance().getJobManager().addJobInBackground(new GetRepeatingCardJob(0));
            } else
            if (SHOW_CARD_ID > feed.getRepeatingCard().getCardList().size() - 3 && HIT_API) {
                HIT_API = false;
                GetRepeatingCardJob job = new GetRepeatingCardJob(OFFSET_CARD);
                job.setListener(new GetRepeatingCardJob.CallBackListener() {
                    @Override
                    public void success() {
                        HIT_API = true;
                    }

                    @Override
                    public void error() {
                        HIT_API = true;
                    }
                });
                OneFeedSdk.getInstance().getJobManager().addJobInBackground(job);
                OFFSET_CARD++;
            }
        }catch (Exception e){

        }
    }
}
