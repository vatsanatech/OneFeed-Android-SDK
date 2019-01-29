package com.onefeed.sdk.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.listener.AddResponseListener;
import com.onefeedsdk.notification.NotificationHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 26-September-2018
 * Time: 14:34
 */
public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        // Get token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Token", "getInstanceId failed", task.getException());
                            return;
                        }
                        try {
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            // Log and toast
                            String msg = getString(R.string.msg_token_fmt, token);
                            Log.d("Token", msg);

                            //set Token
                            OneFeedSdk.getInstance().setToken(token);
                        } catch (NullPointerException e) {
                            Log.e("Exception", e.getMessage());
                        }
                    }
                });

        OneFeedSdk.getInstance().setUserInterests("Life Style", "Like",
                "Test_1234567890", new AddResponseListener() {
            @Override
            public void success() {
                Log.e("UserInterests", "success");
            }

            @Override
            public void error() {

                Log.e("UserInterests", "error");
            }
        });

        topicSubscription();
    }

    //FCM Topic Subscription and Unsubscription
    private void topicSubscription() {

        //Topic Subscribe
        FirebaseMessaging.getInstance().subscribeToTopic(OneFeedSdk.getInstance().getSubscribeTopic());
        if (TextUtils.isEmpty(OneFeedSdk.getInstance().getOldTopicSubscribe())) {
            OneFeedSdk.getInstance().setTopicSubscription();
        } else if (!OneFeedSdk.getInstance().getSubscribeTopic().equalsIgnoreCase(OneFeedSdk.getInstance().getOldTopicSubscribe())) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(OneFeedSdk.getInstance().getOldTopicSubscribe());
            OneFeedSdk.getInstance().setTopicSubscription();
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_feed:
                startActivity(new Intent(this, FeedActivity.class));
                break;
            case R.id.btn_native_card:
                startActivity(new Intent(this, RepeatingCardActivity.class));
                break;
            case R.id.btn_native_card_2:
                startActivity(new Intent(this, RepeatingCard2Activity.class));
                break;
            case R.id.button:
                Map<String, String> demoNotification = new HashMap<>();
                demoNotification.put("story_id", "60496");
                // demoNotification.put("noid", "10001");
                demoNotification.put("story_title", "20 DC Characters Who Could Easily Defeat Thanos");
                demoNotification.put("cover_image", "https://dwybwpx76p5im.cloudfront.net/71629/20-DC-Characters-Who-Could-Defeat-Thanos-41.jpeg");
                demoNotification.put("story_url", "https://www.geeksmate.io/dc-characters-defeat-thanos-seconds-71629?utm_source=SDK&utm_medium=noti&utm_campaign=38237-campaign&utm_hash=108_71629&noid=5385987&sdk_aid=108");

                demoNotification.put("id", "400");
                demoNotification.put("body", "Hey, Here's a new amazing story for you!");
                demoNotification.put("title", "20 DC Characters Who Could Easily Defeat Thanos");
                demoNotification.put("notiff_agent", "wittyfeed_sdk");
                demoNotification.put("app_id", "108");

                demoNotification.put("action", "" + "WittyFeedSDKContentViewActivity");

                NotificationHelper.sendNotification(getApplicationContext(), FeedActivity.class, demoNotification, android.R.drawable.ic_menu_share);
        }
    }
}