package com.onefeedsdk.event;

import com.onefeedsdk.model.FeedModel;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 13-September-2018
 * Time: 17:32
 */
public class Event {

    public static class FeedEvent{

        private final FeedModel feed;
        private final boolean isSuccess;
        private final boolean isLoadMoreFeed;

        public FeedEvent(FeedModel feed, boolean isSuccess, boolean isLoadMoreFeed){
            this.feed = feed;
            this.isSuccess = isSuccess;
            this.isLoadMoreFeed = isLoadMoreFeed;
        }

        public FeedModel getFeed() {
            return feed;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public boolean isLoadMoreFeed() {
            return isLoadMoreFeed;
        }
    }

    public static class SearchFeedEvent{

        private final FeedModel feed;
        private final boolean isSuccess;
        private final boolean isLoadMoreFeed;

        public SearchFeedEvent(FeedModel feed, boolean isSuccess, boolean isLoadMoreFeed){
            this.feed = feed;
            this.isSuccess = isSuccess;
            this.isLoadMoreFeed = isLoadMoreFeed;
        }

        public FeedModel getFeed() {
            return feed;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public boolean isLoadMoreFeed() {
            return isLoadMoreFeed;
        }
    }
}
