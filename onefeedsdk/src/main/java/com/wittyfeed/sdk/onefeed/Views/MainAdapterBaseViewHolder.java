package com.wittyfeed.sdk.onefeed.Views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


/**
 <p><span style="font-size: 13pt;"><strong>Main Base ViewHolder for all the recyclerview adapters</strong></span></p>
 <p>Keep a hold of all the common variables and objects</p>
 */

public class MainAdapterBaseViewHolder extends RecyclerView.ViewHolder{

    public MainAdapterBaseViewHolder(View itemView) {
        super(itemView);
    }

    public ViewGroup root_vg;
    public ViewGroup publisher_rl;
    public ViewGroup content_container_vg;
    public ViewGroup img_container_vg;

    public TextView story_title;
    public ImageView cover_image_iv;
    public View sep_v;

    public TextView shield_tv;
    public TextView publisher_name_tv;
    public ImageView publisher_iv;
    public TextView publisher_meta_tv;
    public TextView badge_tv;

    public ProgressBar pb;
    public RecyclerView rv;

    public ViewGroup card_1_v, card_2_v, card_3_v, card_4_v, card_5_v;

}
