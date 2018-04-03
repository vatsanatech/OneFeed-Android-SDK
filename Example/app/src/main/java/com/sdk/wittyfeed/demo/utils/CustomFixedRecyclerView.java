package com.sdk.wittyfeed.demo.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by aishwarydhare on 13/02/18.
 */

public class CustomFixedRecyclerView extends RecyclerView {

    private static final String TAG = "WF_SDK";

    //
    // This custom recycler view fixes an issue that has been identified with google itself
    // learn more at the link: https://issuetracker.google.com/issues/38375597
    //


    public CustomFixedRecyclerView(Context context) {
        super(context);
    }

    public CustomFixedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFixedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDetachedFromWindow() {
        //
        // here's the fix
        //
        try {
            super.onDetachedFromWindow();
        } catch (Exception e) {
           // Log.d(TAG, "onDetachedFromWindow: ", e);
        }
    }
}
