package com.sdk.wittyfeed.debug;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

/**
 * Created by aishwarydhare on 9/11/17.
 */

public class CarouselDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carousel_demo);

        WittyFeedSDKSingleton.getInstance().witty_sdk.get_carousel(this, (ViewGroup) findViewById(R.id.carousel_ll));
    }
}
