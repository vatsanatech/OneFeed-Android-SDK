package com.sdk.wittyfeed.debug.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.sdk.wittyfeed.debug.R;
import java.util.ArrayList;

/**
 * Created by aishwarydhare on 12/02/18.
 */

public class CategoryFeedRVAdapter extends RecyclerView.Adapter<CategoryFeedRVAdapter.ViewHolder>{

    private ArrayList<View> local_witty_cards = new ArrayList<>();
    private Activity activity;
    private final String TAG = "WF_SDK";


    public CategoryFeedRVAdapter(ArrayList<View> para_witty_cards, Activity para_context){
        local_witty_cards = para_witty_cards;
        this.activity = para_context;
    }


    @Override
    public CategoryFeedRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_witty_normal_card,null,false);
        return new CategoryFeedRVAdapter.ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int itemViewType = holder.getItemViewType();

        switch (itemViewType){
            case 0:
                holder.item_cv.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.progressBar.setVisibility(View.GONE);
                holder.item_cv.setVisibility(View.VISIBLE);

                ViewGroup vp = (ViewGroup) local_witty_cards.get(position).getParent();
                if(vp != null){
                    vp.removeView(local_witty_cards.get(position));
                }
                holder.cardHolder_linearLayout.removeAllViews();
                holder.cardHolder_linearLayout.addView(local_witty_cards.get(position));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return local_witty_cards.size() + 1;
    }


    @Override
    public int getItemViewType(int position) {
        if(position == local_witty_cards.size()){
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.d(TAG, "onViewDetachedFromWindow: ");
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        View item_View;
        LinearLayout cardHolder_linearLayout;
        CardView item_cv;
        ProgressBar progressBar;

        ViewHolder(View itemView) {
            super(itemView);
            this.item_View = itemView;
            cardHolder_linearLayout = itemView.findViewById(R.id.item_ll);
            progressBar = itemView.findViewById(R.id.progressBar);
            item_cv = itemView.findViewById(R.id.item_cv);
        }
    }

}
