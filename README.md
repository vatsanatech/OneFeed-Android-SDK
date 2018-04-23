# Vatsana Technologies Pvt. Ltd. OneFeed SDK

> # Note
> New v1.1.0 made live on 23 April' 2018

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](#)
[![JitPack](https://img.shields.io/badge/JitPack-1.1.0-brightgreen.svg)](https://jitpack.io/private#vatsanatech/OneFeed-Android-SDK/1.1.0)
[![License](https://img.shields.io/badge/LICENSE-WittyFeed%20SDK%20License-blue.svg)](https://github.com/vatsanatech/OneFeed_android_dev/blob/master/LICENSE)

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

1. [Integrate OneFeed with JitPack](https://jitpack.io/private#vatsanatech/OneFeed-Android-SDK/1.1.0)

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
	        compile 'compile 'com.github.vatsanatech:OneFeed_android_dev:1.1.0'
	}
```

4. Add the following library dependency to your project
  
  ```gradle
    compile 'com.android.support:appcompat-v7:27.1.1'
    compile 'com.android.support:support-v4:27.1.1'
    compile 'com.android.support:design:27.1.1'
    compile 'com.android.support:recyclerview-v7:27.1.1'
    
    compile 'com.github.bumptech.glide:glide:4.7.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.7.1'
    compile 'com.google.code.gson:gson:2.8.2'
    compile 'com.android.volley:volley:1.0.0'
    compile 'com.android.support:customtabs:27.1.1'
 ```

> ## Notice
> We encourage developers to always check for latest SDK version and refer to its updated documentation to use it.

### 1.3. Initializing the SDK

```java
    /*
     * OPTIONAL to provide basic mUserMeta.
     * By providing mUserMeta your app can receive targeted content which has an higher CPM then regular content.
     */
    HashMap<String, String> user_meta = new HashMap<>();

    /*
     * Send Gender of User:- "M" for Male, "F" for Female, "O" for Other, "N" for None
     */
    mUserMeta.put("client_gender", "M");

    /*
     * User Interests.
     * String with a max_length = 100
     */
    user_meta.put("client_interests", "love, funny, sad, politics, food, technology, DIY, friendship, hollywood, bollywood, NSFW"); // string max_length = 100

    /*
     * -- below line and appending custom 'mUserMeta' is OPTIONAL --
     */
    ApiClient.getInstance().appendCustomUserMetaToUserMeta(mUserMeta);

    /*
     * setting callback here
     * Use this interface callback to do operations when SDK finished loading
     */
    OneFeedMain.getInstance().setOneFeedDidInitialisedCallback(new OneFeedMain.OnInitialized() {
        @Override
        public void onSuccess() {
            Log.d("Main App", "witty sdk did load successfully");
            progressBar.setVisibility(View.GONE);
            btns_ll.setVisibility(View.VISIBLE);
        }

        @Override
        public void onError() {
            Toast.makeText(activity, "OneFeed data couldn't be loaded", Toast.LENGTH_SHORT).show();
            Log.e("mAPP", "onError: OneFeed data couldn't be loaded");
        }
    });
    
    /*
     * below code is ***required*** for Initializing OneFeed-Android-SDK
     */
    OneFeedMain.getInstance().init(getApplicationContext(), APP_ID, API_KEY, FCM_TOKEN);
```

### 1.4. For OneFeed ready-to-deploy feed layout

```java
    /*
     * initializing OneFeed Support Fragment. Note- Make sure you have initialized the SDK in previous steps
     */
    Fragment fragment = OneFeedMain.getInstance().getOneFeedFragment();

    /*
     * using the OneFeed Fragment
     */
    getSupportFragmentManager().executePendingTransactions();
    if(getSupportFragmentManager().findFragmentByTag("mOneFeedFragment") == null){
        getSupportFragmentManager()
                .beginTransaction()
                .add(
                        R.id.onefeed_fl,
                        fragment,
                        "mOneFeedFragment"
                )
                .commit();
    }
```

### 1.5. Handle back-button of onefeed for custom callback

```java
    /*
     * pass the object of 
     
     SDKBackPressInterface to fragment so that when 
     * user taps on back button of onefeed, perform_back() function of interface will call
     */
    OneFeedMain.getInstance().oneFeedBuilder.setOnBackClickInterface(new OneFeedBuilder.OnBackClickInterface() {
        @Override
        public void onBackClick() {
            finish();
        }
    });
```

### 1.6. For Notifications Service of OneFeed-Android-SDK

In your class which extends FirebaseInstanceIDService, update with the code below
```java
    @Override
    public void onTokenRefresh() {
        /*
         * Get updated InstanceID token
         */
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        
        /*
         * Mandatory for Using Notification Service by OneFeed*
         * To notify OneFeed Server about your updated fcm_token
         */
        OneFeedMain.getInstance().init(getApplicationContext(), APP_ID, API_KEY, refreshedToken);
        OneFeedMain.getInstance().getFcmTokenManager().refreshToken(refreshedToken);
    }
```


In your class which extends FirebaseMessagingService, update with the code below
```java
    public void onMessageReceived(RemoteMessage remoteMessage) {
      /*
       * If you want to open any of your app's activity on back press from Story Activity (That loads on clicking the notification)
       * Set Intent of the Activity you want to open on Back press from Story opens from Notification
       */
      OFNotificationManager.getInstance().setHomeScreenIntent(new Intent(getApplicationContext(), MainActivity.class));
      
      /*
       * NOTE: optionally you can check that notification has arrived from OneFeed Server by below line -
       *       if(remoteMessage.getData().get("notiff_agent").equals("wittyfeed_sdk")
       */
      int your_preferred_icon_for_notifications = R.mipmap.ic_launcher; // <YOUR_PREFERRED_ICON_FOR_NOTIFICATION> 
      OFNotificationManager.getInstance().handleNotification(
              getApplicationContext(),
              FirebaseInstanceId.getInstance().getToken(),
              remoteMessage.getData(),
              your_preferred_icon_for_notifications
      );
    }
```

> ## Note
> Notification service with OneFeed-Android-SDK is optional to use but is highly recommended. You will get to handle this notifications on Viral9 Dashbaord


## 2. License
This program is licensed under the Vatsana Technologies Pvt. Ltd. SDK License Agreement (the “License Agreement”).  By copying, using or redistributing this program, you agree to the terms of the License Agreement.  The full text of the license agreement can be found at [https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE](https://github.com/vatsanatech/OneFeed-Android-SDK/blob/master/LICENSE).
Copyright 2017 Vatsana Technologies Pvt. Ltd.  All rights reserved.

