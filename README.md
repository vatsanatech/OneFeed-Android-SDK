# Vatsana Technologies Pvt. Ltd. OneFeed SDK (WittyfeedAndroidApi)

> # Note
> WittyFeed SDK API is now `OneFeed Android SDK`,
> New v2.1.13 made live on 07 Aug' 2018

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](#)
[![Source](https://img.shields.io/badge/Source-JitPack-brightgreen.svg)](https://jitpack.io/private#vatsanatech/OneFeed-Android-SDK/2.1.13)
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

### 1.1. Minimum requirements

* Android version 4.1  (```android:minSdkVersion="16"```)

### 1.2. Incorporating the SDK

1. [Integrate OneFeed with JitPack](https://jitpack.io/private#vatsanatech/OneFeed-Android-SDK/2.1.13)

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
* add OneFeed-Android-SDK:2.1.13 in your app level build.gradle
```gradle
    dependencies {
	        compile 'com.github.vatsanatech:OneFeed-Android-SDK:2.1.13'
	}
```

4. Add the following library dependency to your project
  
  ```gradle
    compile 'com.android.support:appcompat-v7:27.1.0'
    compile 'com.android.support:support-v4:27.1.0'
    compile 'com.android.support:design:27.1.0'
    compile 'com.android.support:cardview-v7:27.1.0'
    compile 'com.android.support:recyclerview-v7:27.1.0'
    
    compile 'com.github.bumptech.glide:glide:4.3.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.3.1'
    compile 'com.google.code.gson:gson:2.8.2'
    compile 'com.android.volley:volley:1.0.0'
    compile 'com.android.support:customtabs:27.1.0'
 ```

> ## Notice
> We encourage developers to always check for latest SDK version and refer to its updated documentation to use it.

### 1.3. Initializing the SDK

```java

    //For Notifications

     String topicName = "OneFeed_" + APP_ID + "_" + Constant.ONE_FEED_VERSION;
     FirebaseMessaging.getInstance().subscribeToTopic(topicName);

    // OPTIONAL to provide basic user_meta.
    // By providing basic user_meta your app can receive targeted content which has an higher CPM then regular content.
    HashMap<String, String> mUserMeta = new HashMap<>();

    // Send Gender of User:- "M" for Male, "F" for Female, "O" for Other, "N" for None
       
    mUserMeta.put("client_gender", "M");

    // user Interests. String with a max_length = 100
    // SAMPLE CODE BELOW, DO ADD YOUR OWN CATEOGORIES OF INTERESTS
    // OPTIONAL
    mUserMeta.put("client_interests", "love, funny, sad, politics, food, technology, DIY, friendship, hollywood, bollywood, NSFW"); // string max_length = 100

    // Passing 'mUserMeta'  IS OPTIONAL
    ApiClient.getInstance().appendCustomUserMetaToUserMeta(mUserMeta);

    // Create and set this interface callback to do operations when SDK finished loading
    OneFeedMain.getInstance().setOneFeedDidInitialisedCallback(new OneFeedMain.OnInitialized() {
            @Override
            public void onSuccess() {
                // OneFeed sdk did loaded completely successfully
                Log.d("Main App", "witty sdk did load successfully");
            }

            @Override
            public void onError() {
                // if unexpected error
            }
        });

    //For Hiding back button from MainFeed Fragment
    //Pass parameter true for hiding back button and false for showing back button

    OneFeedMain.setHideBackButtonFromMainFeed(true);

    // initializing SDK here (mandatory)
    OneFeedMain.getInstance().init(getBaseContext(), APP_ID, API_KEY, FCM_TOKEN);
    
    // Below code is required for consistent unsubscription from FCM topics and token update on onefeed sdk version change
    
    if(!(OneFeedMain.getInstance().ofSharedPreference.getSDKVersion().equals(""))){
            if(!OneFeedMain.getInstance().ofSharedPreference.getSDKVersion().equals(Constant.ONE_FEED_VERSION)){
                OneFeedMain.getInstance().fcmTokenManager.updateTokenForVersionChange();
                String oldTopic = "OneFeed_" + APP_ID + "_" + OneFeedMain.getInstance().ofSharedPreference.getSDKVersion();
                FirebaseMessaging.getInstance().unsubscribeFromTopic(oldTopic);
            }
        }
    OneFeedMain.getInstance().ofSharedPreference.setSDKVersion(Constant.ONE_FEED_VERSION);
    
    // OPTIONAL: Set Intent of the Activity you want to open on Back press from Story that opens from Notification
    OFNotificationManager.getInstance().setHomeScreenIntent(this, new Intent(this.getApplicationContext(),MainActivity.class));
    
    
```

### 1.4. For OneFeed ready-to-deploy feed layout

```java
    // initializing OneFeed Support Fragment. Note- Make sure you have initialized the SDK in previous steps
    Fragment oneFeedFragment = OneFeedMain.getInstance().getOneFeedFragment()

    // using our oneFeedFragment, replace <ID_OF_YOUR_VIEWGROUP_IN_WHICH_OneFeed_FRAGMENT_WILL_BE_PLACED> with your
    // viewgroup's ID (i.e. LinearLayout, RelativeLayout etc)
    
    getSupportFragmentManager().beginTransaction().add(<ID_OF_YOUR_VIEWGROUP_IN_WHICH_OneFeed_FRAGMENT_WILL_BE_PLACED>, fragment, "OneFeed").commit();
```

### 1.5. Handle back-button of onefeed for custom callback

```java
    // create and set the function like below, so that when user taps on back button of onefeed, onBackClick() function of interface will call
    OneFeedMain.getInstance().oneFeedBuilder.setOnBackClickInterface(new OneFeedBuilder.OnBackClickInterface() {
                    @Override
                    public void onBackClick() {
                        finish();
                    }
                })
```

### 1.6. Fetch a Modular Native Card

Step 1:
In dashboard(https://onefeed.ai), Go to your added app section and Make a card according to your requirements and note the card id.

Step 2:
In activity/Fragment you intend to place native cards, update this line of code in OnCreate/OnCreateView

```java
        OneFeedMain.getInstance().ofCardFetcher.loadInitData(CARD_ID, new OFCardFetcher.OnInitialized() {
            @Override
            public void onSuccess() {
                // Initialize your view when repeating card load successfully
            }

            @Override
            public void onError() {
		// if unexpected error
            }
        });
```

Step 3:
```java
    // Create a OfInterface to receive the card in view form in callback method OnSuccess.
    // Also set the interface using setOfInterface().
    
    OFInterface ofInterface = new OFInterface() {

                        @Override
                        public void OnSuccess(View view, String categoryName, String storyTitle) {
                            //Use this veiw to drop in a holder layout

                        }

                        @Override
                        public void onError(Exception e) {
                            // handle unexpected happens here
                        }
                    };
		   
		   
    OneFeedMain.getInstance().ofCardFetcher.setOfInterface(ofInterface);
                    
```

Step 4:
```java
    // Finally call the below method to fetch a card (If you want same set of cards on every app launch)
    // Give text size ratio between 0.1f to 1f.
    // Hide Category title
    // Pass parameter true for vertical image and false for horizontal image
    
    OneFeedMain.getInstance().ofCardFetcher.fetch_repeating_card(CARD_ID, TEXT_SIZE_RATIO, HIDE_CATEGORY, TEXT_COLOR, VERTICAL_IMAGE);
		                     
```

### 1.7. For Notifications Service of OneFeed

In your class which extends FirebaseInstanceIDService, update with the code below
```java
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_CUSTOM", "Refreshed token:- " + refreshedToken);

        //
        // * Mandatory for Using Notification Service by OneFeed*
        // To notify OneFeed SDK about your updated fcm_token
        //
        if(OneFeedMain.getInstance().getFcmTokenManager()!=null)
            OneFeedMain.getInstance().getFcmTokenManager().refreshToken(refreshedToken);
    }
```


In your class which extends FirebaseMessagingService, update with the code below
```java

    public void onMessageReceived(RemoteMessage remoteMessage) {
      //
      // Check if message contains a data payload.
      //
      if (remoteMessage.getData() != null) {
            if (remoteMessage.getData().size() > 0) {
                Log.d(FCM_TAG, "Message data payload: " + remoteMessage.getData());
            }
        }

      //
      // this 2 lines below handle the notifications
      // NOTE: optionally you can check that notification has arrived from WittyFeed by below line -
      // if(remoteMessage.getData().get("notiff_agent").equals("wittyfeed_sdk")
      
      int your_preferred_icon_for_notifications = R.mipmap.ic_launcher; // <YOUR_PREFERRED_ICON_FOR_NOTIFICATION>
        OFNotificationManager.getInstance().handleNotification(
                getApplicationContext(),
                FirebaseInstanceId.getInstance().getToken(),
                remoteMessage.getData(),
                your_preferred_icon_for_notifications,
                YOUR_APP_ID
        );
    }
```

### 1.8. OneFeed is built for Portrait
OneFeed works best in the world's default mode i.e Potrait, So don't forget to add the below line in Manifest.

```java
    android:screenOrientation="portrait"
```

> ## Note
> Notification service with WittyFeedNativeAndroidSDK is optional to use but is highly recommended. You will get to handle this notifications on OneFeed Dashbaord


## 2. License
This program is licensed under the Vatsana Technologies Pvt. Ltd. SDK License Agreement (the “License Agreement”).  By copying, using or redistributing this program, you agree to the terms of the License Agreement.  The full text of the license agreement can be found at [https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE](https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE).
Copyright 2017 Vatsana Technologies Pvt. Ltd.  All rights reserved.



