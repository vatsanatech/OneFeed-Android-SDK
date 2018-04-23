package com.wittyfeed.sdk.onefeed;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

class MainAdapterBaseViewHolder extends RecyclerView.ViewHolder{

    MainAdapterBaseViewHolder(View itemView) {
        super(itemView);
    }

    ViewGroup root_vg;
    ViewGroup publisher_rl;
    ViewGroup content_container_vg;
    ViewGroup img_container_vg;

    TextView story_title;
    ImageView cover_image_iv;
    View sep_v;

    TextView shield_tv;
    TextView publisher_name_tv;
    ImageView publisher_iv;
    TextView publisher_meta_tv;
    TextView badge_tv;

    ProgressBar pb;
    RecyclerView rv;

    ViewGroup card_1_v, card_2_v, card_3_v, card_4_v, card_5_v;

}
