package com.wittyfeed.sdk.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import com.wittyfeed.sdk.onefeed.OFInterface;
import com.wittyfeed.sdk.onefeed.OneFeedMain;

public class NonRepeatingDemoActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_standard_wf);

        final LinearLayout item_ll = findViewById(R.id.item_ll);

        OFInterface ofInterface = new OFInterface() {


            @Override
            public void OnSuccess(View view) {
                item_ll.addView(view);
            }

            @Override
            public void onError(Exception e) {
                // unexpected happens here
                Log.i("TAG", "onError: ");
            }
        };

        // Setting interface particular to this card here with setWittyFeedSDKCardFetcherInterface method of WittyFeedSDKCardFetcher,
        // which takes one parameter which is object of WittyFeedSDKCardFetcherInterface
        OneFeedMain.getInstance().ofCardFetcher.setOfInterface(ofInterface);

        // Third and Last Step is this
        OneFeedMain.getInstance().ofCardFetcher.fetch_non_repeating_card(2, 1, true,null, false);

    }
}
