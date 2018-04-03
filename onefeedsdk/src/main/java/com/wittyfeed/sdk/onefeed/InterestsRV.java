package com.wittyfeed.sdk.onefeed;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * Created by anujagarwal on 02/04/18.
 */


class InterestsRV {

    private Context context;
    private ArrayList<String> category_list = new ArrayList<>();

    InterestsRV(Context para_context, ArrayList<String> category_list) {
        this.context = para_context;
        this.category_list = category_list;
    }

    View get_constructed_view() {
        View root_view = ((Activity) this.context).getLayoutInflater().inflate(R.layout.block_generic_for_rv, null, false);
        RecyclerView recyclerView = root_view.findViewById(R.id.poster_rv);
        recyclerView.addItemDecoration(new DividerItemDecoration(context,LinearLayoutManager.HORIZONTAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new InterestsRVAdapter());
        return root_view;
    }

    class InterestsRVAdapter extends RecyclerView.Adapter<InterestsRVAdapter.ViewHolder>{

        InterestsRVAdapter() {
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = ((Activity)context).getLayoutInflater().inflate(R.layout.item_interest, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final CheckedTextView checkedTextView = holder.checkedTextView;
            checkedTextView.setText(category_list.get(position));

            checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkedTextView.isChecked()) {
                        checkedTextView.setCheckMarkDrawable(0);
                        checkedTextView.setChecked(false);
                    } else {
                        checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_circle_green_20dp);
                        checkedTextView.setChecked(true);
                    }
                }
            });

            checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    this.notify();
                }
            });

        }

        @Override
        public int getItemCount() {
            return category_list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            RelativeLayout container_view_rl;
            CheckedTextView checkedTextView;
            ViewHolder(View itemView) {
                super(itemView);
                container_view_rl = itemView.findViewById(R.id.container_view_rl);
                checkedTextView = container_view_rl.findViewById(R.id.checked_tv);
            }
        }
    }
}
