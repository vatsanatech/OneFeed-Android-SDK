package com.sdk.wittyfeed.debug.tindercard;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;
import com.sdk.wittyfeed.debug.R;

/**
 * Created by aishwarydhare on 28/10/17.
 */

public class TinderCardActivity extends AppCompatActivity {
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private static String APP_ID = "61";
    private static String API_KEY = "f168529b71aedcbf628fae0bcedb84d4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tinder_card);

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        int bottomMargin = Utils.dpToPx(160);
        Point windowSize = Utils.getDisplaySize(getWindowManager());
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setIsUndoEnabled(true)
                .setHeightSwipeDistFactor(10)
                .setWidthSwipeDistFactor(5)
                .setSwipeDecor(new SwipeDecor()
                        .setViewWidth(windowSize.x)
                        .setViewHeight(windowSize.y - bottomMargin)
                        .setViewGravity(Gravity.TOP)
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

        int i = 0;
        for (Profile profile : Utils.loadProfiles(this.getApplicationContext())) {

            RelativeLayout.LayoutParams layoutParams ;
            layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout relativeLayout = new RelativeLayout(this);
            relativeLayout.setLayoutParams(layoutParams);

            WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card((ViewGroup) relativeLayout, 1.0f);
            mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView, relativeLayout, i));
            i++;
        }


        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });

        findViewById(R.id.undoBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.undoLastSwipe();
            }
        });
    }

}
