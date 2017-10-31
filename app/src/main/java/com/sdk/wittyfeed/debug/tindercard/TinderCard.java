package com.sdk.wittyfeed.debug.tindercard;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.sdk.wittyfeed.debug.R;


import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by janisharali on 19/08/16.
 */
@Layout(R.layout.tinder_card_view)
public class TinderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    @View(R.id.cardV_fl)
    FrameLayout cardV_fl;

    @View(R.id.wholecard)
    CardView wholecard;
    @View(R.id.SDK_card)
    CardView SDK_card;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;
    android.view.View cardV;
    int i;

    public TinderCard(Context context, Profile profile, SwipePlaceHolderView swipeView, android.view.View cardV, int j) {

        mContext = context;
        mProfile = profile;
        mSwipeView = swipeView;
        this.cardV = cardV;
        i = j;
    }


    @Resolve
    private void onResolved() {

        cardV_fl.setVisibility(android.view.View.GONE);
        SDK_card.setVisibility(android.view.View.GONE);
        wholecard.setVisibility(android.view.View.VISIBLE);
        Glide.with(mContext).load(mProfile.getImageUrl())
                .bitmapTransform(new RoundedCornersTransformation(mContext, Utils.dpToPx(7), 0,
                        RoundedCornersTransformation.CornerType.TOP))
                .into(profileImageView);
        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        locationNameTxt.setText(mProfile.getLocation());



    if (i%3 == 0) {
            cardV_fl.removeView(cardV);
            SDK_card.removeView(cardV_fl);

            wholecard.setVisibility(android.view.View.GONE);
            this.cardV_fl.addView(cardV);
            this.SDK_card.addView(cardV_fl);
            cardV_fl.setVisibility(android.view.View.VISIBLE);
            SDK_card.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Click(R.id.profileImageView)
    private void onClick() {
        Log.d("EVENT", "profileImageView click");
//        mSwipeView.addView(this);
    }

    @SwipeOut
    private void onSwipedOut() {
        Log.d("EVENT", "onSwipedOut");
//        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn() {
        Log.d("EVENT", "onSwipedIn");
    }

    @SwipeInState
    private void onSwipeInState() {
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState");
    }
}
