# Vatsana Technologies Pvt. Ltd. Android SDK API (WittyfeedAndroidApi)

> # Note
> New v1.4.0 made live on 8 Feb' 2018

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
[ ![Download](https://img.shields.io/badge/Download-1.4.0-blue.svg)](https://drive.google.com/file/d/1byUv3HjaWZLpx1k9IPPK6AMy_fai9mpS/view?usp=sharing)
[![License](https://img.shields.io/badge/LICENSE-WittyFeed%20SDK%20License-blue.svg)](https://github.com/vatsanatech/wittyfeed_android_api/blob/master/LICENSE)

## Table Of Contents
1. [Getting Started](#1-getting-started)
2. [Example Apps](#2-example-app)
3. [License](#3-license)

## Basic concepts
The WittyfeedAndroidApi allows you to get WittyFeed content to display in your app using WittyfeedAndroidSDK. 
For each item WittyfeedAndroidSDK will provide pre-populated views, which you can style to match your app look and feel and place where needed within your app.
The views will automatically handle everything else: click handling, reporting visibility back to WittyFeed's server and more.

Browse through the example app in this repository to see how the WittyfeedAndroidApi can be implemented in different types of apps.

## 1. Getting Started

### 1.1. Minimum requirements

* Android version 4.1  (```android:minSdkVersion="16"```)

### 1.2. Incorporating the SDK

1. [Download the SDK v1.4.0](https://drive.google.com/file/d/1byUv3HjaWZLpx1k9IPPK6AMy_fai9mpS/view?usp=sharing)

2. Import WittyFeedAndroidSDK in your project
* In Android Studio goto File > New > New Module > Import .JAR/.AAR Package
* Browse to the downloaded WittyFeed SDK in form of .AAR package
* Sync Gradle 
* In your app level build.gradle file, add the below line in the bottom of dependencies
```groovy
    implementation project(':wittynativesdk') 
```

3. Add the library dependency to your project
  
  ```groovy
    compile 'com.github.bumptech.glide:glide:4.3.1'
    annotationProcessor 'com.github.bumptech.glide:compiler:4.3.1'
    compile 'com.google.code.gson:gson:2.8.2'
    compile 'com.android.volley:volley:1.0.0'

    compile 'com.android.support:appcompat-v7:27.0.2'
    compile 'com.android.support:support-v4:27.0.2'
    compile 'com.android.support:design:27.0.2'
    compile 'com.android.support:cardview-v7:27.0.2'
    compile 'com.android.support:recyclerview-v7:27.0.2'
 ```

> ## Notice
> We encourage developers to always check for latest SDK version and refer to its updated documentation to use it.


### 1.3. Initializing the SDK

```java

    //
    // OPTIONAL to provide basic user_meta.
    // By providing basic user_meta your app can receive targeted content which has an higher CPM then regular content.
    //
    HashMap<String, String> user_meta = new HashMap<>();

    //
    // WittyFeedSDKGender has following options = MALE, FEMALE, OTHER, NONE
    // SAMPLE CODE BELOW, DO ADD YOUR OWN CATEOGORIES OF INTERESTS
    // OPTIONAL
    //
    user_meta.put("client_gender", WittyFeedSDKGender.MALE);

    //
    // user Interests. String with a max_length = 100
    // SAMPLE CODE BELOW, DO ADD YOUR OWN CATEOGORIES OF INTERESTS
    // OPTIONAL
    //
    user_meta.put("client_interests", "love, funny, sad, politics, food, technology, DIY, friendship, hollywood, bollywood, NSFW"); // string max_length = 100

    //
    // below code is only ***required*** for Initializing Wittyfeed Android SDK API
    // PROVIDING 'user_meta' ARGUMENT IS OPTIONAL
    //
    WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient = new WittyFeedSDKApiClient(activity, APP_ID, API_KEY, FCM_TOKEN  /*, user_meta*/  ););
    WittyFeedSDKSingleton.getInstance().witty_sdk = new WittyFeedSDKMain(activity, WittyFeedSDKSingleton.getInstance().wittyFeedSDKApiClient);

    //
    // use this interface callback to do operations when SDK finished loading
    //
    WittyFeedSDKMainInterface wittyFeedSDKMainInterface = new WittyFeedSDKMainInterface() {
        @Override
        public void onOperationDidFinish() {
            //
            // witty sdk did loaded completely successfully
            //
            Log.d("Main App", "witty sdk did load successfully");
        }	

        @Override
        public void onError(Exception e) {
            // if unexpected error
        }
    };

    //
    // setting callback here
    //
    WittyFeedSDKSingleton.getInstance().witty_sdk.set_operationDidFinish_callback(wittyFeedSDKMainInterface);

    //
    // initializing SDK here (mandatory)
    //
    WittyFeedSDKSingleton.getInstance().witty_sdk.init_wittyfeed_sdk();
    
    //
    // Fetch fresh feeds from our servers with this method call. 
    // It is not mandatory if only notification feature is desired from the SDK
    //
    WittyFeedSDKSingleton.getInstance().witty_sdk.prepare_feed();
```

### 1.4. For Waterfall Feeds Fragment

```java
    //
    // initializing waterfall fragment. Note- Make sure you have initialized the SDK in previous steps
    //
    Fragment fragment = WittyFeedSDKSingleton.getInstance().witty_sdk.get_waterfall_fragment(this);

    //
    // using our WittyFeedSDKWaterfallFragment, replace <ID_OF_YOUR_VIEWGROUP_IN_WHICH_WATERFALL_FEED_FRAGMENT_WILL_BE_PLACED> with your
    // viewgroup's ID (i.e. LinearLayout, RelativeLayout etc)
    //
    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
    fragmentTransaction.add(<ID_OF_YOUR_VIEWGROUP_IN_WHICH_WATERFALL_FEED_FRAGMENT_WILL_BE_PLACED>, fragment, "WittyFeed_SDK_Waterfall").commit();
```

> ## Note
> For fetching all the category names for your account, use the below method which return an string[] array with the names of the category
>WittyFeedSDKSingleton.getInstance().witty_sdk.get_all_categoies_available();


### 1.5. To Fetch a WittyFeed Story Card from (i) Any Category or, (ii) Specific Category

```java
    // Total Steps 3
    // First Step: Create an interface of type WittyFeedSDKCardFetcherInterface in which four methods will be there as demonstrated below
    
    // Second Step: Initialize an object of WittyFeedSDKCardFetcher to fetch cards, NOTE- use same object from WittyFeedSDKSingleton as demonstrated below
        // if you don't want to see any repeated card anywhere in the app. Otherwise you can initialize different object of WittyFeedSDKCardFetcher

    // Third Step: Use fetch_a_card() method of WittyFeedSDKCardFetcher to place a WittyFeed SDK Card in one your ViewGroups (i.e. views, layouts etc)
        // fetch_a_card() has two overload methods,
        // First overloaded fetch_a_card() method fetches a random card from any cateogry and,
        // Second overloaded fetch_a_card() method fetches a card of specific category which will passed as the third argument of String type
            // First argument: is of String TYPE and is used to define your own custom tag that you will later recieve in onCardReceived (its purpose is similar to itemType parameter in OnCreateViewHolder of RecyclerView)
            // Second argument: is FLOAT TYPE for adjusting font_size_ratio of cards which should be between 0.0f to 1.0f (example: if your layout covers full screen then pass 1.0f)
            // Third argument: is of STRING TYPE for the specific category, it may return null if category is sent wrong

    // Other Available Methods by WittyFeedSDKCardFetcher:
        // clearCardFetchedHistory(): Clears history that keep tracks what card have been used and what not,
            // clearing this will fetch again the very first card, that was fetched.

    // First Step is this
    WittyFeedSDKCardFetcherInterface wittyFeedSDKCardFetcherInterface = new WittyFeedSDKCardFetcherInterface() {
        @Override
        public void onWillStartFetchingMoreData() {
            // fetching more data, do necessary UI updates here. onMoreDataFetched will be called when the data will be fetched
            Log.d(TAG, "onWillStartFetchingMoreData: ");
        }


        @Override
        public void onMoreDataFetched() {
            // after fetching more data. onMoreDataFetched will be called when the data will be fetched
            Log.d(TAG, "onMoreDataFetched: ");
        }

        @Override
        public void onCardReceived(String customTag, View cardViewFromWittyFeed) {
            // when a cardView is made, onCardReceived will return WittyFeedCard of type (View)
           	// add generated view 'cardViewFromWittyFeed' as sub-view in your own ViewGroup
        }

        @Override
        public void onError(Exception e) {
            // unexpected happens here
            Log.d(TAG, "onError: "+ e.getMessage(), e);
        }
    };

    // Second Step is this
    // Tip: use the object of WittyFeedSDKCardFetcher from Singleton class if you want to use cards in difference activities
        // and also don't want to repeat cards that have been loaded previously in previous screens
        // otherwise just create an object local to current activity only and use that, see TinderCardActivity for such implementation

    WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher = new WittyFeedSDKCardFetcher(activity, wittyFeedSDKCardFetcherInterface);


    // Third and Last Step is this
    WittyFeedSDKSingleton.getInstance().witty_sdk.wittyFeedSDKCardFetcher.fetch_a_card("content1_rl", 0.5f);
```

### 1.6. For creating WittyFeed cards Carousel

```java
    // below method will directly place a carousel of WittyFeed cards endlessly implemented
    // Note- Make sure you have initialized the SDK in previous steps
    // replace YOUR_VIEWGROUP_WHERE_INSIDE_WHICH_CAROUSEL_WILL_BE_PLACED with your viewgroup (i.e. LinearLayout, RelativeLayout etc)
    WittyFeedSDKSingleton.getInstance().witty_sdk.get_carousel(this, <YOUR_VIEWGROUP_WHERE_INSIDE_WHICH_CAROUSEL_WILL_BE_PLACED>);
```

### 1.7. For Notifications Service of WittyFeedAndroidSDK

In your class which extends FirebaseMessagingService, update with the code below

```java
    // should be initialised at the class level
    WittyFeedSDKNotificationManager wittyFeedSDKNotificationManager;

    public void onMessageReceived(RemoteMessage remoteMessage) {
      // This line is required to be just after the onMessageReceived block starts
      wittyFeedSDKNotificationManager = new WittyFeedSDKNotificationManager(getApplicationContext(), FirebaseInstanceId.getInstance().getToken());

      // this 2 lines below handle the notifications
      int your_preferred_icon_for_notifications =  <YOUR_PREFERRED_ICON_FOR_NOTIFICATION>  //example: R.mipmap.ic_launcher
      wittyFeedSDKNotificationManager.handleNotification(remoteMessage.getData(), your_preferred_icon_for_notifications);
    }
```

> ## Note
> Notification service with WittyFeedNativeAndroidSDK is optional to use but is highly recommended. You will get to handle this notifications on Engage9 Dashbaord


## 2. Example App
This repository includes an example Android app which uses all the features of `WittyFeedNativeAndroidSDK` documented above.


## 3. License
This program is licensed under the Vatsana Technologies Pvt. Ltd. SDK License Agreement (the “License Agreement”).  By copying, using or redistributing this program, you agree to the terms of the License Agreement.  The full text of the license agreement can be found at [https://github.com/vatsanatech/wittyfeed_android_api/blob/master/LICENSE](https://github.com/vatsanatech/wittyfeed_android_api/blob/master/LICENSE).
Copyright 2017 Vatsana Technologies Pvt. Ltd.  All rights reserved.

