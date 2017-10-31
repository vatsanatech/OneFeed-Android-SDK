package com.sdk.wittyfeed.debug;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

public class FourCardActivity extends AppCompatActivity {

    Activity activity;

    RelativeLayout content1_rl, content2_rl, content3_rl, content4_rl;
    private String ACTION_BAR_BG_COLOR = "";
    private String ACTION_BAR_TEXT_COLOR = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_card);

        activity = this;

        content1_rl = (RelativeLayout) findViewById(R.id.content1_rl);
        content2_rl = (RelativeLayout) findViewById(R.id.content2_rl);
        content3_rl = (RelativeLayout) findViewById(R.id.content3_rl);
        content4_rl = (RelativeLayout) findViewById(R.id.content4_rl);

        // use get_a_new_card() method to place a WittyFeed SDK Card in one your ViewGroups (i.e. views, layouts etc)
        // get_a_new_card() accepts two arguments
        // first argument is of ViewGroup TYPE and is used the layout inside which you wish to place your card,
        // and the second argument is FLOAT TYPE for adjusting font_size_ratio of cards which should be between 0 to 1
        WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(content1_rl, 0.5f);
        WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(content2_rl, 0.5f);
        WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(content3_rl, 0.5f);
        WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(content4_rl, 0.5f);


        findViewById(R.id.change1_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(content1_rl, 0.5f);
            }
        });

        findViewById(R.id.change2_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(content2_rl, 0.5f);
            }
        });

        findViewById(R.id.change3_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(content3_rl, 0.5f);
            }
        });

        findViewById(R.id.change4_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(content4_rl, 0.5f);
            }
        });

    }

}
