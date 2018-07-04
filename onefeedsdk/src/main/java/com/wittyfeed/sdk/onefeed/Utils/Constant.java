package com.wittyfeed.sdk.onefeed.Utils;

/**
 *
 * Stores the Constants which are used across the app
 *
 */

public final class Constant {

    public static final String ONE_FEED_VERSION = "2.1.2";

    public static String APP_ID = "";

    public static final String ANALYTICS_TRACKING_ID = "UA-40875502-17";

    public static final String DEVICE_TYPE = "android";

    public static final String CHROME_PACKAGE_NAME = "com.android.chrome";

    public static final String  POSTER_SOLO = "poster_solo",
                                POSTER_RV = "poster_rv",
                                VIDEO_SOLO = "video_solo",
                                VIDEO_RV = "video_rv",
                                STORY_LIST = "story_list",
                                COLLECTION_1_4 = "collection_1_4";

    public static final int PROGRESS_BAR = -1,
                            POSTER_SOLO_NUM = 1,
                            POSTER_RV_NUM = 2,
                            VIDEO_SOLO_NUM = 3,
                            VIDEO_RV_NUM = 4,
                            STORY_LIST_NUM = 5,
                            COLLECTION_1_4_NUM = 6,
                            VIDEO_SMALL_SOLO_NUM = 7,
                            STORY_LIST_ITEM_NUM = 8,
                            COLLECTION_ITEM_NUM = 9;

    public static final double  TextSizeRatioLarge = 1.0,
                                TextSizeRatioMedium = 0.7,
                                TextSizeRatioSmall = 0.6;

    public static final int STORY_LIST_ROOT_LL_ID = 0x3af04;
    public static int loaderThresholdInt = 85;
    public static boolean hasChromeCustomTabLoaded = false;

    /**
     * In shared preference we store the class name of the activity which app owner desires to open when
     * the user exits from the OneFeed notification activity, its key is "homeScreenIntentClassName"
     */
    public static final String SAVED_HOME_SCREEN_CLASS = "homeScreenIntentClassName";

    public static final String SAVED_ONE_FEED_VERSION = "oneFeedVersion";

    /**
     * In shared preference we store the last fcm token which was sent successfully to the OneFeed Server,
     * its key is "wf_saved_fcm_token"
     */
    public static final String SAVED_FCM_TOKEN = "wf_saved_fcm_token";

    public static final String AnalyticsURL = "https://reqy4b30ec.execute-api.us-east-1.amazonaws.com/apptrack";

}
