package com.sdk.wittyfeed.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKFeedSupportFragment;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKOneFeedInterface;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKSingleton;

/**
 * Created by aishwarydhare on 19/03/18.
 */

public class FeedFragment extends Fragment {

    private Context activity_context;
    private String TAG = "WF_SDK";
    private WittyFeedSDKFeedSupportFragment wittyFeedSDKFeedSupportFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity_context = context;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //
        // Initializing OneFeed Fragment. Note- Make sure you have initialized the SDK in previous steps. It has two steps -
        //    1-  Create an interface instanceof WittyFeedSDKOneFeedInterface
        //           (i)- put in your code that you will be using to revert back from your app
        //                Example:  finish the activity if onefeed is opened in a seperate activity,
        //                          or impletement it as in example if onefeed is in a view_pager fragment
        //    2-  Pass the interface to get feed method
        //
        WittyFeedSDKOneFeedInterface oneFeedInterface = new WittyFeedSDKOneFeedInterface() {
            @Override
            public void goBackToHostApp() {
                //
                // will be called when user performs 'backToHostApp' action from OneFeed
                //
                ((MainActivity)activity_context).set_vp_pos(0);
            }
        };

        //
        // NOTE- To match your App's UI, please use the method `set_onefeed_fragment_background_color`, by default its white - #ffffff
        //
        WittyFeedSDKSingleton.getInstance().witty_sdk.set_onefeed_fragment_background_color("#ffffff");

        //
        // Initializing OneFeed Fragment. Note- Make sure you have initialized the SDK in previous steps
        // NOTE- use method `get_support_feed_fragment()` in order to get support.fragment from support library
        //
        wittyFeedSDKFeedSupportFragment = WittyFeedSDKSingleton.getInstance().witty_sdk.get_support_feed_fragment(oneFeedInterface);

        //
        // Using our OneFeed Fragment
        //
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(view.findViewById(R.
                id.fragmentHolder_fl).getId(), wittyFeedSDKFeedSupportFragment, "One_Feed").commit();

        view.findViewById(R.id.goBack_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)activity_context).set_vp_pos(0);
            }
        });
    }


    public boolean performOnBack(){
        // ============
        // === NOTE ===
        // ============
        //
        // The OneFeed SDK Section utilises overridden functionality of `onBackPressed` button,
        // Hence to handle it well with the host activity.
        //
        // Please use the method WittyFeedSDKFeedSupportFragment.is_doing_onefeed_back()
        // as we have done in this sample app
        //
        return wittyFeedSDKFeedSupportFragment.is_doing_onefeed_back();
    }

}
