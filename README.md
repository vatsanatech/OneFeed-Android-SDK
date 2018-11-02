# Vatsana Technologies Pvt. Ltd. OneFeed SDK (WittyfeedAndroidApi)

> # Note
> WittyFeed SDK API is now `OneFeed Android SDK`,
> New v2.3.8 made live on 01-Nov-2018

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](#)
[![Source](https://img.shields.io/badge/Source-JitPack-brightgreen.svg)](https://jitpack.io/private#vatsanatech/OneFeed-Android-SDK/2.3.8)
[![License](https://img.shields.io/badge/LICENSE-WittyFeed%20SDK%20License-blue.svg)](https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE)

## Table Of Contents
1. [Getting Started](#1-getting-started)
2. [License](#2-license)

## Basic concepts
OneFeed brings you new revolutionary way to monetize your App Business. OneFeed provides engaging content from top publishers in your app, and through the [OneFeed Dashboard](http://www.onefeed.ai/) you can track your earning with the content consumption.

[OneFeed is World's Top Paying Network](http://www.onefeed.ai/)

OneFeed SDK has its core competency at its personalised feed recommendation algorithms and lightweight architecture.

### Features

* OneFeed ready-to-deploy feed layout
* Notification service

Browse through the example app in this repository to see how the OneFeed SDK can be implemented in different types of apps.

## 1. Getting Started

### 1.1 Minimum requirements

* Android version 4.1  (```android:minSdkVersion="16"```)

### 1.2. Incorporating the SDK

1. [Integrate OneFeed with JitPack](https://jitpack.io/private#vatsanatech/OneFeed-Android-SDK/2.3.8)

2. SignUp at [OneFeed](http://www.onefeed.ai/) and create a new application to integrate with

3. Gather the API_KEY and APP_ID from the OneFeed Dashboard

4. Import OneFeed SDK in your project

* add JitPack repo in project level build.gradle
```gradle
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
* add OneFeed-Android-SDK:2.3.8 in your app level build.gradle
```gradle
    dependencies {
	        implementation 'com.github.vatsanatech:OneFeed-Android-SDK:2.3.8'
	}
```

> ## Notice
> We encourage developers to always check for latest SDK version and refer to its updated documentation to use it.

### 1.3 Initializing the SDK
   
* Add this code in your String.xml    
```xml
    <string name="onefeed_app_id">YOUR APP ID</string>
    <string name="onefeed_api_key">YOUR API KEY</string>
    
    <!--Optional if you are using Repeating Card-->
    <string name="onefeed_card_id">YOUR CARD ID</string>
```
   * Add this code in your Manifests.xml

```xml
        <meta-data android:name="com.onefeed.sdk.ApiKey"
            android:value="@string/onefeed_api_key"/>

        <meta-data android:name="com.onefeed.sdk.AppId"
            android:value="@string/onefeed_app_id"/>

        <!--Optional if you are using Repeating Card-->
        <meta-data android:name="com.onefeed.sdk.CardId"
            android:value="@string/onefeed_card_id"/>
```
* Add this code in your Application class
```java

    public class Root extends Application {
    
        @Override
        public void onCreate() {
            super.onCreate();
            //Initialize OneFeedSdk
            OneFeedSdk.getInstance().init(getApplicationContext());
            
            //Optional Initialize Repeating card if you are using
            OneFeedSdk.getInstance().initNativeCard();
        }
    }
    
```

* Add this code in your MainActivity class if you are using FCM
```java
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get FCM token
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
```

* Add this code in your MainActivity class if you are using OneSignal
```java

      @Override
         protected void onCreate(@Nullable Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             
         OneSignal.getTags(new OneSignal.GetTagsHandler() {
            @Override
            public void tagsAvailable(JSONObject tags) {
                if (tags != null) {
                try {
                    String tag = tags.getString("onefeed");
                    if (!tag.equalsIgnoreCase("Your App ID" + "_" + OneFeedSdk.VERSION)) {
                    OneSignal.sendTag("onefeed", "Your App ID" + "_" + OneFeedSdk.VERSION);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }else{
                OneSignal.sendTag("onefeed", "Your App ID" + "_" + OneFeedSdk.VERSION);
                }
            }
         });
        
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                Log.e("debug", "User:" + userId);
                
                //set User id
                OneFeedSdk.getInstance().setToken(userId);
            }
        });
    }

```

### 1.4 For OneFeed ready-to-deploy feed layout

```java
        
    Fragment oneFeedFragment = new OneFeedFragment();
    
    //Replaced your id name to "XXXXX" 
    getSupportFragmentManager().beginTransaction().replace(R.id."XXXXX", oneFeedFragment, "OneFeed").commit();
```

### 1.5. Fetch a Modular Native Card

Step 1:
In dashboard(https://onefeed.ai), Go to your added app section and Make a card according to your requirements and note the card id.

Step 2:  Set this tag in your View (Tag is mandatory):

```xml
    <ImageView
        android:id="@+id/image_story"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        android:tag="native_card_image"/>
  
    <TextView
        android:id="@+id/view_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="15dp"
        android:tag="native_card_title"
        android:layout_gravity="bottom"
        android:textSize="18sp"
        android:textColor="@color/white"/>
```

Step 3: Add this code in your Activity/Fragment class (First time initialize your card):

```java

OneFeedSdk.getInstance().initNativeCard(YOUR CARD_ID, new CallBackListener() {
            @Override
            public void success() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       //Add your adapter here
                    }
                });
            }

            @Override
            public void error() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       //Add your adapter here
                    }
                });
            }
        });
    }
```

Step 4: Add this code in your adapter class:

```java
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        
        //Repeating card view
        if(holder instanceof YourViewHolder){
    
            YourViewHolder holder1 = (YourViewHolder) holder;
            
          String categoryType = OneFeedNativeCard.showCard(RepeatingCardActivity.this, YOUR CARD_ID, holder1.linearLayout,
        (Add reference Which type of layout manager you are using) OneFeedSdk.V_List, true);
                
        }else{
            //Handle by user
        }
    }
```
	                    
### 1.6.1 For FCM Notifications Service of OneFeed

* Add this code in your class which extends FirebaseMessagingService:
```java
    public class YourFirebaseMessagingService extends FirebaseMessagingService {
    
             @Override
             public void onMessageReceived(RemoteMessage remoteMessage) {
             super.onMessageReceived(remoteMessage);
            
            //OneFeed notification
             if(remoteMessage.getData().get("notiff_agent").equalsIgnoreCase("wittyfeed_sdk")) {
                 NotificationHelper.sendNotification(getApplicationContext(), FeedActivity.class, remoteMessage.getData(), android.R.drawable.ic_menu_share);
             } else {
                 //Handle by user
                 }
             }
    
             @Override
             public void onNewToken(String s) {
             super.onNewToken(s);
    
             //Update Token
             OneFeedSdk.getInstance().setToken(s);
             Log.e("Token", s);
             }
        }
```
### 1.6.2 For OneSignal Notifications Service of OneFeed

In your class which extends Application, update with the code below
```java
    @Override
    public void onCreate() {
	super.onCreate();	
	OneSignal.startInit(this)
		    .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
		    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
		    .unsubscribeWhenNotificationsAreDisabled(true)
		    .init();
     }

     private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {
        @Override
        public void notificationReceived(OSNotification notification) {
            String notificationID = notification.payload.notificationID;

            String agent = notification.payload.additionalData.optString("notiff_agent", null);

     	   // NOTE: optionally you can check that notification has arrived from WittyFeed by below line -
            if(!TextUtils.isEmpty(agent) && agent.equalsIgnoreCase("wittyfeed_sdk")) {
                OFNotificationManager
                        .getInstance()
                        .handleOneSignalNotification(Root.this, "",
                                notification.payload.additionalData, R.mipmap.ic_launcher, YOUR_APP_ID);
            }else {
                // Handle by user
            }
        }
    }

```

### 1.7 OneFeed is built for Portrait
OneFeed works best in the world's default mode i.e Potrait, So don't forget to add the below line in Manifest.

```java
    android:screenOrientation="portrait"
```

> ## Note
> Notification service with WittyFeedNativeAndroidSDK is optional to use but is highly recommended. You will get to handle this notifications on OneFeed Dashbaord


## 2. License
This program is licensed under the Vatsana Technologies Pvt. Ltd. SDK License Agreement (the “License Agreement”).  By copying, using or redistributing this program, you agree to the terms of the License Agreement.  The full text of the license agreement can be found at [https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE](https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE).
Copyright 2017 Vatsana Technologies Pvt. Ltd.  All rights reserved.



