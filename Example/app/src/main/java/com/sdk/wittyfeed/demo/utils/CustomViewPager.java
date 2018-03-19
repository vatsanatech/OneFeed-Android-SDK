package com.sdk.wittyfeed.demo.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by aishwarydhare on 19/03/18.
 */

public class CustomViewPager extends ViewPager {

    private boolean is_swipeable = true;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (is_swipeable){
            return super.onInterceptTouchEvent(ev);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (is_swipeable) {
            return super.onTouchEvent(ev);
        } else {
            return false;
        }
    }


    public void setIs_swipeable(boolean is_swipeable) {
        this.is_swipeable = is_swipeable;
    }
}
