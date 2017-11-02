# Vatsana Technologies Pvt. Ltd. Android SDK API (WittyfeedAndroidApi)

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
[ ![Download](https://img.shields.io/badge/Download-1.1.0-blue.svg) ](https://drive.google.com/file/d/0BzL7HCh86uWWVUw0N2NGbl9YcWNuRFJHR0pVcklIQ05YUnJF/view?usp=sharing)
[![License](https://img.shields.io/badge/LICENSE-WittyFeed%20SDK%20License-blue.svg)](https://github.com/vatsanatech/wittyfeed_android_api/blob/master/LICENSE)

## Table Of Contents
1. [Getting Started](#1-getting-started)
2. [Example Apps](#2-example-app)
3. [License](#3-license)

## Basic concepts
The WittyfeedAndroidApi allows you to get WittyFeed content to display in your app using WittyfeedAndroidSDK. 
For each item WittyfeedAndroidSDK will provide pre-populated views, which you can style to match your app look and feel and place where needed within your app.
The views will automatically handle everything else: click handling, reporting visibility back to Taboola's server and more.

Browse through the example app in this repository to see how the WittyfeedAndroidApi can be implemented in different types of apps.

## 1. Getting Started

### 1.1. Minimum requirements

* Android version 4.1  (```android:minSdkVersion="16"```)

### 1.2. Incorporating the SDK

1. [Download the SDK](https://drive.google.com/file/d/0BzL7HCh86uWWVUw0N2NGbl9YcWNuRFJHR0pVcklIQ05YUnJF/view?usp=sharing)

2. Import WittyFeedAndroidSDK in your project
* In Android Studio goto File > New > New Module > Import .JAR/.AAR Package
* Browse to the downloaded WittyFeed SDK in form of .AAR package
* Sync Gradle 
* In your app level build.gradle file, add the below line in the bottom of dependencies
  ```groovy
    compile project(':wf-sdk-release') 
  ```

3. Add the library dependency to your project
  
  ```groovy
   compile 'com.android.volley:volley:1.0.0'
   compile 'com.android.support:percent:26.+'
    compile 'com.koushikdutta.ion:ion:2.+' 
   
    compile 'com.android.support:support-v4:26.+' 
   compile 'com.android.support:design:26.+' 
   compile 'com.android.support:appcompat-v7:26.+' 
   compile ‘com.android.support:recyclerview-v7:26+'
 ```

> ## Notice
> We encourage developers to always check for latest SDK version and and use it.


### 1.3. Opening Waterfall Feeds Layout of SDK

In your `Application` class, use these lines of code to open waterfall layout anywhere in the app

```java
   Intent go_to_wf_screen = new Intent(activity, WittyFeedOldSDKWaterfallActivity.class);
   go_to_wf_screen.putExtra("FCM_TOKEN", "<FIREBASE_TOKEN_OF_THE_USER>");
   go_to_wf_screen.putExtra("APP_ID", "<YOUR_APP_ID_AS_PROVIDED_FROM_ENGAGE9_DASHBOARD>");
   go_to_wf_screen.putExtra("API_KEY", "<YOUR_API_KEY_AS_PROVIDED_FROM_ENGAGE9_DASHBOARD>");
   go_to_wf_screen.putExtra("ACTION_BAR_BG_COLOR", "<BG_COLOR_YOU_PREFER>");
   go_to_wf_screen.putExtra("ACTION_BAR_TEXT_COLOR", "<TEXT_COLOR_YOU_PREFER>");
   startActivity(go_to_wf_screen);
```

### 1.4. For Notifications Service of WittyFeedAndroidSDK

In your class which extends FirebaseMessagingService, update with the code below

```java
   // should be initialised at the class level
   WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager;

   public void onMessageReceived(RemoteMessage remoteMessage) {
      // This line is required to be just after the onMessageReceived block starts
      wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(getApplicationContext());

      // this 2 lines below handle the notifications
      int your_preferred_icon_for_notifications =  <YOUR_PREFERRED_ICON_FOR_NOTIFICATION>  //example: R.mipmap.ic_launcher
      wittyFeedSDKNotificationManager.handleNotification(remoteMessage.getData(), your_preferred_icon_for_notifications);
   }
```

> ## Note
> Notification service with WittyfeedAndroidSDK is optional to use but is highly recommended. You will get to handle this notifications on Engage9 Dashbaord

### 1.5. For WittyFeedAndroidSDK Native Cards

1. Initialise the SDK first 

```java
   // below code is only required for Native SDK CARDS support

   WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient = new WittyFeedSDKApiClient(activity, YOUR_APP_ID, YOUR_API_KEY, YOUR_USERS_FIREBASE_TOKEN);

   WittyFeedSDKSingleton.getInstance().witty_sdk = new
   WittyFeedSDKMain(activity,

   WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient);

   // use this interface callback to do operations when SDK finished loading
   WittyFeedSDKSingleton.getInstance().wittyFeedSDKMainInterface = new WittyFeedSDKMainInterface() {
      @Override
      public void onOperationDidFinish() {
         // witty sdk did loaded completely successfully
         Log.d("Main App", "witty sdk did load successfully");
      }
   };

   WittyFeedSDKSingleton.getInstance().witty_sdk.set_operationDidFinish_callback( WittyFeedSDKSingleton.getInstance().wittyFeedSDKMainInterface );

   WittyFeedSDKSingleton.getInstance().witty_sdk.init_wittyfeed_sdk();
```

2. Setting Native Card on custom empty ViewGroup (i.e. RelativeLayout, LinearLayout, FrameLayout etc)

```java
   // use get_a_new_card() method to place a WittyFeed SDK Card in one your ViewGroups (i.e. views, layouts etc)
   // get_a_new_card() accepts two arguments
   // first argument is of ViewGroup TYPE and is used the layout inside which you wish to place your card,
   // and the second argument is FLOAT TYPE for adjusting font_size_ratio of cards which should be between 0 to 1
   WittyFeedSDKSingleton.getInstance().witty_sdk.get_a_new_card(<your_custom_ViewGroup>, <your_float_type_font_size_ratio_for_card>);
```

## 2. Example App
This repository includes an example Android app which uses the `WittyfeedAndroidSDK`.


## 3. License
This program is licensed under the Vatsana Technologies Pvt. Ltd. SDK License Agreement (the “License Agreement”).  By copying, using or redistributing this program, you agree to the terms of the License Agreement.  The full text of the license agreement can be found at [https://github.com/vatsanatech/wittyfeed_android_api/blob/master/LICENSE](https://github.com/vatsanatech/wittyfeed_android_api/blob/master/LICENSE).
Copyright 2017 Vatsana Technologies Pvt. Ltd.  All rights reserved.

