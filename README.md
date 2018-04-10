# Vatsana Technologies Pvt. Ltd. OneFeed SDK (WittyfeedAndroidApi)

> # Note
> WittyFeed SDK API is now `OneFeed Android SDK`,
> New v1.0.3 made live on 6 April' 2018

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](#)
[![Source](https://img.shields.io/badge/Source-JitPack-brightgreen.svg)](https://jitpack.io/private#vatsanatech/OneFeed-Android-SDK/1.0.3)
[![License](https://img.shields.io/badge/LICENSE-WittyFeed%20SDK%20License-blue.svg)](https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE)

## Table Of Contents
1. [Getting Started](#1-getting-started)
2. [License](#2-license)

## Basic concepts
OneFeed brings you new revolutionary way to monetize your App Business. OneFeed provides engaging content from top publishers in your app, and through the [Viral9 Dashboard](https://viral9.com) you can track your earning with the content consumption.

[Viral9 is World's Top Paying Network](https://viral9.com)

OneFeed SDK has its core competency at its personalised feed recommendation algorithms and lightweight architecture.

### Features

* OneFeed ready-to-deploy feed layout
* Notification service

Browse through the example app in this repository to see how the OneFeed SDK can be implemented in different types of apps.

## 1. Getting Started

### 1.1. Minimum requirements

* Android version 4.1  (```android:minSdkVersion="16"```)

### 1.2. Incorporating the SDK

1. [Integrate OneFeed with JitPack](https://jitpack.io/private#vatsanatech/OneFeed-Android-SDK/1.0.2)

2. SignUp at [viral9.com](https://viral9.com) and create a new application to integrate with

3. Gather the API_KEY and APP_ID from the Viral9 Dashboard

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
* add OneFeed-Android-SDK:1.0.2 in your app level build.gradle
```gradle
    dependencies {
	        compile 'com.github.vatsanatech:OneFeed-Android-SDK:1.0.3'
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
    // OPTIONAL to provide basic user_meta.
    // By providing basic user_meta your app can receive targeted content which has an higher CPM then regular content.
    HashMap<String, String> user_meta = new HashMap<>();

    // WittyFeedSDKGender has following options = MALE, FEMALE, OTHER, NONE
    // SAMPLE CODE BELOW, DO ADD YOUR OWN CATEOGORIES OF INTERESTS
    // OPTIONAL
    user_meta.put("client_gender", WittyFeedSDKGender.MALE);

    // user Interests. String with a max_length = 100
    // SAMPLE CODE BELOW, DO ADD YOUR OWN CATEOGORIES OF INTERESTS
    // OPTIONAL
    user_meta.put("client_interests", "love, funny, sad, politics, food, technology, DIY, friendship, hollywood, bollywood, NSFW"); // string max_length = 100

    // below code is only ***required*** for Initializing Wittyfeed Android SDK API
    // PROVIDING 'user_meta' ARGUMENT IS OPTIONAL
    WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient = new WittyFeedSDKApiClient(activity, APP_ID, API_KEY, FCM_TOKEN  /*, user_meta*/  ););

    WittyFeedSDKSingleton.getInstance().witty_sdk = new WittyFeedSDKMain(activity, WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient);

    // use this interface callback to do operations when SDK finished loading
    WittyFeedSDKMainInterface wittyFeedSDKMainInterface = new WittyFeedSDKMainInterface() {
        @Override
        public void onOperationDidFinish() {
            // witty sdk did loaded completely successfully
            Log.d("Main App", "witty sdk did load successfully");
        }	

        @Override
        public void onError(Exception e) {
            // if unexpected error
        }
    };

    // setting callback here
    WittyFeedSDKSingleton.getInstance().witty_sdk.set_operationDidFinish_callback(wittyFeedSDKMainInterface);

    // initializing SDK here (mandatory)
    WittyFeedSDKSingleton.getInstance().witty_sdk.init_wittyfeed_sdk();
```

### 1.4. For OneFeed ready-to-deploy feed layout

```java
    // initializing OneFeed Support Fragment. Note- Make sure you have initialized the SDK in previous steps
    WittyFeedSDKOneFeedFragment wittyFeedSDKOneFeedFragment = new WittyFeedSDKOneFeedFragment();

    // using our WittyFeedSDKWaterfallFragment, replace <ID_OF_YOUR_VIEWGROUP_IN_WHICH_WATERFALL_FEED_FRAGMENT_WILL_BE_PLACED> with your
    // viewgroup's ID (i.e. LinearLayout, RelativeLayout etc)
    getSupportFragmentManager().beginTransaction().add(<ID_OF_YOUR_VIEWGROUP_IN_WHICH_WATERFALL_FEED_FRAGMENT_WILL_BE_PLACED>, fragment, "OneFeed").commit();
```

### 1.5. Handle back-button of onefeed for custom callback

```java
    // create a WittyFeedSDKBackPressInterface object
    WittyFeedSDKBackPressInterface wittyFeedSDKBackPressInterface = new WittyFeedSDKBackPressInterface() {
        @Override
        public void perform_back() {
            finish();
        }
    };

    // pass the object of WittyFeedSDKBackPressInterface to fragment so that when user taps on back button of onefeed, perform_back() function of interface will call
    wittyFeedSDKOneFeedFragment.setWittyFeedSDKBackPressInterface(wittyFeedSDKBackPressInterface);
```

### 1.6. For Notifications Service of WittyFeedAndroidSDK

In your class which extends FirebaseInstanceIDService, update with the code below
```java
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_CUSTOM", "Refreshed token:- " + refreshedToken);

        //
        // * Mandatory for Using Notification Service by OneFeed*
        // To notify WittyFeedSDK about your updated fcm_token
        //
        WittyFeedSDKMain witty_sdk_main = new WittyFeedSDKMain(getApplicationContext(), new WittyFeedSDKApiClient(getApplicationContext(), APP_ID,  API_KEY, refreshedToken));
        witty_sdk_main.update_fcm_token(refreshedToken);
    }
```


In your class which extends FirebaseMessagingService, update with the code below
```java
    //
    // should be initialised at the class level
    //
    WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager;

    public void onMessageReceived(RemoteMessage remoteMessage) {
      //
      // This line is required to be just after the onMessageReceived block starts
      //
      wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(getApplicationContext(), FirebaseInstanceId.getInstance().getToken());
      
      //If you want to open any of your app's activity on back press from Story Activity(That loads on clicking the notification)
      // Set Intent of the activity you want to open as follows:
      
      wittyFeedSDKNotificationManager.setHomeScreenIntent(new Intent(getApplicationContext(), <ACTIVITY_NAME>.class));
      //
      // this 2 lines below handle the notifications
      //
      int your_preferred_icon_for_notifications =  <YOUR_PREFERRED_ICON_FOR_NOTIFICATION>  //example: R.mipmap.ic_launcher
      wittyFeedSDKNotificationManager.handleNotification(remoteMessage.getData(), your_preferred_icon_for_notifications);
    }
```

> ## Note
> Notification service with WittyFeedNativeAndroidSDK is optional to use but is highly recommended. You will get to handle this notifications on Viral9 Dashbaord


## 2. License
This program is licensed under the Vatsana Technologies Pvt. Ltd. SDK License Agreement (the “License Agreement”).  By copying, using or redistributing this program, you agree to the terms of the License Agreement.  The full text of the license agreement can be found at [https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE](https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE).
Copyright 2017 Vatsana Technologies Pvt. Ltd.  All rights reserved.

