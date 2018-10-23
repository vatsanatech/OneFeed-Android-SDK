package com.onefeedsdk.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.onefeedsdk.R;
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.util.Util;

import java.util.List;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 13-September-2018
 * Time: 19:59
 */
public class PosterFeedAdapter extends RecyclerView.Adapter<PosterFeedAdapter.ViewHolder> {

    private Context context;
    private List<FeedModel.Card> cardList;

    public void setCardList(List<FeedModel.Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.onefeed_adapter_poster_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        //Manage space from right and left
        if(position == 0){
            holder.space.setVisibility(View.VISIBLE);
        }else{
            holder.space.setVisibility(View.GONE);
        }
        if(position == cardList.size() - 1){
            holder.space2.setVisibility(View.VISIBLE);
        }else{
            holder.space2.setVisibility(View.GONE);
        }


        final FeedModel.Card card = cardList.get(position);
        holder.titleView.setText(card.getStoryTitle());
        holder.categoryView.setText(card.getSheildText());
        holder.publisherView.setText(card.getPublisherName());
        String s = card.getfName() + " | " + card.getDoa();
        holder.writerView.setText(s);

        final int[] toolbarColor = {Color.DKGRAY};

        //Load Image
        Glide.with(context).asBitmap().listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                Palette.from(resource)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                if(textSwatch == null){
                                    return;
                                }

                                holder.coverImage.setBackgroundColor(textSwatch.getRgb());
                            }
                        });

                return false;
            }
        }).load(card.getCoverImage()).into(holder.coverImage);

        //Publisher
        Glide.with(context).asBitmap().listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                Palette.from(resource)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                toolbarColor[0] =  Color.DKGRAY;
                                if(textSwatch != null){
                                    toolbarColor[0] = textSwatch.getRgb();
                                }

                                Util.changedBackgroundColorToRes(holder.categoryView ,  toolbarColor[0]);
                            }
                        });

                return false;
            }
        }).load(card.getPublisherIconUrl())
                .into(holder.publisherImage);

        holder.posterRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.preventMultipleClick()) {
                    Util.showCustomTabBrowser(context, toolbarColor[0], card.getStoryTitle(), card.getStoryUrl(), card.getStoryId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardList == null ? 0 : cardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private RelativeLayout posterRowLayout;
        private TextView titleView;
        private TextView categoryView;
        private TextView publisherView;
        private TextView writerView;
        private ImageView coverImage;
        private ImageView publisherImage;
        private Space space;
        private Space space2;

        public ViewHolder(View itemView) {
            super(itemView);
            posterRowLayout = itemView.findViewById(R.id.layout_poster_row);
            titleView = itemView.findViewById(R.id.view_title);
            categoryView = itemView.findViewById(R.id.view_category);
            publisherView = itemView.findViewById(R.id.view_publisher_name);
            writerView = itemView.findViewById(R.id.view_writer);
            publisherImage = itemView.findViewById(R.id.image_publisher);
            coverImage = itemView.findViewById(R.id.image_poster);
            space = itemView.findViewById(R.id.space);
            space2 = itemView.findViewById(R.id.space2);
        }
    }
}
