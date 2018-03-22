package com.sdk.wittyfeed.demo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sdk.wittyfeed.wittynativesdk.WittyFeedSDKNotificationManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aishwarydhare on 19/03/18.
 */

public class HomeFragment extends Fragment {

    private String TAG = "WF_SDK";
    private Context activity_context;
    private String FCM_TOKEN = "";

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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FCM_TOKEN = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.goto_waterfall_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)activity_context).set_vp_pos(1);
            }
        });

        view.findViewById(R.id.goto_endless_feed_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity_context, EndlessFeedActivity.class);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.simulate_notiff_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_demo_fcm(); // For Directly going to the WebPage Story of WittyFeed
            }
        });
    }


    private void send_demo_fcm() {
        WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(activity_context, FCM_TOKEN);
        int preferred_notiff_icon = R.mipmap.ic_launcher;
        Map<String, String> dummy_notiff_data = new HashMap<>();
        try {
            dummy_notiff_data.put("story_id", "60496");
            dummy_notiff_data.put("story_title", "10 Things Every Girl Should Put On Her List");
            dummy_notiff_data.put("cover_image", "https://cdn.wittyfeed.com/41441/ilik0kqmr2hpv1i4l8ya.jpeg?imwidth=960");
            dummy_notiff_data.put("story_url","https://www.wittyfeed.me/story/41441/things-every-girl-should-put-in-her-list?utm_hash=ArD51&nohead=1");

            dummy_notiff_data.put("id", "400");
            dummy_notiff_data.put("body", "Hey, Here's a new amazing story for you!");
            dummy_notiff_data.put("title", "10 Things Every Girl Should Put On Her List");
            dummy_notiff_data.put("notiff_agent", "wittyfeed_sdk");
            dummy_notiff_data.put("app_id" , "108");

            dummy_notiff_data.put("action", "" + "WittyFeedSDKContentViewActivity");

        } catch (Exception e) {
            e.printStackTrace();
        }

        wittyFeedSDKNotificationManager.handleNotification(dummy_notiff_data, preferred_notiff_icon);
    }

}
