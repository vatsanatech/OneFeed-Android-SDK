package com.onefeedsdk.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.onefeedsdk.R;
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.util.Util;

import java.util.List;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 03-October-2018
 * Time: 12:01
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<FeedModel.Card> cardList;
    private Context context;

    public void setCardList(List<FeedModel.Card> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_category_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        //Load Image
        Glide.with(context)
                .load(cardList.get(position).getCoverImage())
                .into(holder.categoryImage);

        holder.categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.preventMultipleClick()) {
                    Util.showCustomTabBrowser(context, Color.DKGRAY, cardList.get(position).getStoryTitle(),
                            cardList.get(position).getStoryUrl(), cardList.get(position).getStoryId());
                }
            }
        });

        holder.categoryName.setText(cardList.get(position).getStoryTitle());
    }

    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView categoryImage;
        private TextView categoryName;

        public ViewHolder(View itemView) {
            super(itemView);

            categoryImage = itemView.findViewById(R.id.image_category);
            categoryName = itemView.findViewById(R.id.view_category_name);
        }
    }
}
