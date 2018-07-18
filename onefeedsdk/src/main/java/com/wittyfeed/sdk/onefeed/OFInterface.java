package com.wittyfeed.sdk.onefeed;

import android.view.View;

public interface OFInterface {
    //Changed by Yogesh Soni
    void OnSuccess(View view, String categoryName, String storyTitle);
    void onError(Exception e);

}