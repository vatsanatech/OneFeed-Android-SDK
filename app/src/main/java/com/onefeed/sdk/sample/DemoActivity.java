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
                        }catch (NullPointerException e){
                            Log.e("Exception", e.getMessage());
                        }
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
        } else if (OneFeedSdk.getInstance().getSubscribeTopic() != OneFeedSdk.getInstance().getOldTopicSubscribe()) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(OneFeedSdk.getInstance().getOldTopicSubscribe());
            OneFeedSdk.getInstance().setTopicSubscription();
        }
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_feed:
                startActivity(new Intent(this, FeedActivity.class));
                break;
            case R.id.btn_native_card:
                startActivity(new Intent(this, RepeatingCardActivity.class));
                break;
            case R.id.button:
                Map<String, String> demoNotification = new HashMap<>();
                demoNotification.put("story_id", "60496");
                demoNotification.put("noid", "10001");
                demoNotification.put("story_title", "10 Things Every Girl Should Put On Her List");
                demoNotification.put("cover_image", "https://cdn.wittyfeed.com/41441/ilik0kqmr2hpv1i4l8ya.jpeg?imwidth=960");
                demoNotification.put("story_url","https://www.wittyfeed.me/story/41441/things-every-girl-should-put-in-her-list?utm_hash=ArD51&nohead=1");

                demoNotification.put("id", "400");
                demoNotification.put("body", "Hey, Here's a new amazing story for you!");
                demoNotification.put("title", "10 Things Every Girl Should Put On Her List");
                demoNotification.put("notiff_agent", "wittyfeed_sdk");
                demoNotification.put("app_id" , "108");

                demoNotification.put("action", "" + "WittyFeedSDKContentViewActivity");

                NotificationHelper.sendNotification(getApplicationContext(), FeedActivity.class, demoNotification);

        }
    }
}
