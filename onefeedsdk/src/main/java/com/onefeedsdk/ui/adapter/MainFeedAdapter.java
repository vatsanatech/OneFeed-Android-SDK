package com.onefeedsdk.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.onefeedsdk.R;
import com.onefeedsdk.app.Constant;
import com.onefeedsdk.app.OneFeedSdk;
import com.onefeedsdk.job.PostUserTrackingJob;
import com.onefeedsdk.model.FeedModel;
import com.onefeedsdk.util.Util;

import java.util.List;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 13-September-2018
 * Time: 19:59
 */
public class MainFeedAdapter extends RecyclerView.Adapter {

    private final int VIEW_FEED = 1;
    private final int VIEW_PROG = 0;

    private Context context;
    private List<FeedModel.Blocks> feedList;
    private AddListener addListener;

    private int lastVisibleItem, totalItemCount;
    private boolean isLoading;

    private int visibleThreshold = 1;
    private int i = 0;
    private int showBlock = 0;

    public MainFeedAdapter(RecyclerView mRecyclerView) {
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (Util.checkNetworkConnection(context)) {
                    if (!isLoading && totalItemCount - 1 <= (lastVisibleItem + visibleThreshold)) {
                        if (addListener != null) {
                            addListener.loadMoreFeed();
                        }
                        isLoading = true;
                    }

                    if (linearLayoutManager.findLastVisibleItemPosition() >= i) {
                        //Tracking
                        sendEvent();
                    }

                } else {
                    isLoading = false;
                }
            }
        });
    }

    public void addListener(AddListener addListener) {
        this.addListener = addListener;
    }

    public void setBlocks(List<FeedModel.Blocks> feedList) {
        this.feedList = feedList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_PROG) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.onefeed_progressbar, parent, false);
            viewHolder = new ProgressHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.onefeed_adapter_main_feed, parent, false);
            viewHolder = new ViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (!isLoading && getItemViewType(position) == VIEW_PROG && position == getItemCount() - 1) {
            if (position != 0) {
//                addListener.loadMoreFeed();
//                isLoading = true;
            }
        }

        if (getItemViewType(position) == VIEW_FEED) {
            try {
                ViewHolder holder = (ViewHolder) viewHolder;

                holder.posterLayout.setVisibility(View.GONE);
                holder.line1.setVisibility(View.GONE);
                holder.posterRecycler.setVisibility(View.GONE);
                holder.videoLayout.setVisibility(View.GONE);
                holder.line2.setVisibility(View.GONE);
                holder.videoRecycler.setVisibility(View.GONE);
                holder.storyRecycler.setVisibility(View.GONE);

                FeedModel.Blocks blocks = feedList.get(position);
                if (blocks.getMeta().getType().equalsIgnoreCase("poster_solo")) {
                    showPosterSoloView(holder, blocks.getCardList().get(0));
                    holder.posterLayout.setVisibility(View.VISIBLE);
                    holder.line1.setVisibility(View.VISIBLE);
                    //Tracking
                    //sendEvent();
                } else if (blocks.getMeta().getType().equalsIgnoreCase("poster_rv")) {
                    showPosterRVView(holder, blocks.getCardList());
                    holder.posterRecycler.setVisibility(View.VISIBLE);
                    //Tracking
                    // sendEvent();
                } else if (blocks.getMeta().getType().equalsIgnoreCase("video_solo")) {
                    holder.videoLayout.setVisibility(View.VISIBLE);
                    holder.line2.setVisibility(View.VISIBLE);
                    showVideoSoloView(holder, blocks.getCardList().get(0));
                    //Tracking
                    //sendEvent();
                } else if (blocks.getMeta().getType().equalsIgnoreCase("video_rv")) {
                    showVideoRVView(holder, blocks.getCardList());
                    holder.videoRecycler.setVisibility(View.VISIBLE);
                    //Tracking
                    //sendEvent();
                } else if (blocks.getMeta().getType().equalsIgnoreCase("story_list")) {
                    showStoryList(holder, blocks.getCardList());
                    holder.storyRecycler.setVisibility(View.VISIBLE);
                    //Tracking
                    //sendEvent();
                }

            } catch (Exception e) {
                Log.e("Exceptions: ", e.getMessage());
            }
        }
    }

    private void sendEvent() {

        OneFeedSdk.getInstance().getJobManager()
                .addJobInBackground(new PostUserTrackingJob(Constant.FEED_VIEWED, String.valueOf(++i)));
    }

    private void showStoryList(final ViewHolder holder, List<FeedModel.Card> cardList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.storyRecycler.setLayoutManager(layoutManager);
        StoryFeedAdapter storyFeedAdapter = new StoryFeedAdapter();
        storyFeedAdapter.setCardList(cardList);
        holder.storyRecycler.setAdapter(storyFeedAdapter);
    }

    private void showPosterRVView(final ViewHolder holder, List<FeedModel.Card> cardList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.posterRecycler.setLayoutManager(layoutManager);
        PosterFeedAdapter posterFeedAdapter = new PosterFeedAdapter();
        posterFeedAdapter.setCardList(cardList);
        holder.posterRecycler.setAdapter(posterFeedAdapter);
    }

    private void showPosterSoloView(final ViewHolder holder, final FeedModel.Card card) {
        holder.titleView.setText(card.getStoryTitle());
        holder.categoryView.setText(card.getSheildText());
        holder.publisherView.setText(card.getPublisherName());
        String s = card.getfName() + " | " + card.getDoa();
        holder.writerView.setText(s);

        final int[] toolbarColor = {Color.DKGRAY};

        //Load Image
        Glide.with(context).asBitmap().listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                Palette.from(resource)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                if (textSwatch == null) {
                                    return;
                                }

                                holder.coverImage.setBackgroundColor(textSwatch.getRgb());
                            }
                        });

                return false;
            }
        }).load(card.getCoverImage()).into(holder.coverImage);

        //Publisher
        Glide.with(context).asBitmap().listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                Palette.from(resource)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch textSwatch = palette.getVibrantSwatch();

                                toolbarColor[0] =  Color.DKGRAY;
                                if (textSwatch != null) {
                                    toolbarColor[0] = textSwatch.getRgb();
                                }
                                Util.changedBackgroundColorToRes(holder.categoryView, toolbarColor[0]);

                            }
                        });

                return false;
            }
        }).load(card.getPublisherIconUrl())
                .into(holder.publisherImage);

        holder.posterLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.preventMultipleClick()) {
                    Util.showCustomTabBrowser(context, toolbarColor[0], card.getStoryTitle(), card.getStoryUrl(), card.getStoryId());
                }
            }
        });

    }

    private void showVideoSoloView(final ViewHolder holder, final FeedModel.Card card) {
        holder.videoTitleView.setText(card.getStoryTitle());
        holder.videoCategoryView.setText(card.getSheildText());
        holder.videoPublisherView.setText(card.getPublisherName());
        String s = card.getfName() + " | " + card.getDoa();
        holder.videoWriterView.setText(s);

        final int[] toolbarColor = {Color.DKGRAY};

        //Load Image
        Glide.with(context).asBitmap().listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                Palette.from(resource)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                if (textSwatch == null) {
                                    return;
                                }
                                holder.videoCoverImage.setBackgroundColor(textSwatch.getRgb());
                            }
                        });

                return false;
            }
        }).load(card.getCoverImage()).into(holder.videoCoverImage);

        //Publisher
        Glide.with(context).asBitmap().listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                Palette.from(resource)
                        .generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(@NonNull Palette palette) {
                                Palette.Swatch textSwatch = palette.getVibrantSwatch();
                                toolbarColor[0] =  Color.DKGRAY;
                                if (textSwatch != null) {
                                    toolbarColor[0] = textSwatch.getRgb();

                                }

                                Util.changedBackgroundColorToRes(holder.videoCategoryView,  toolbarColor[0]);
                            }
                        });

                return false;
            }
        }).load(card.getPublisherIconUrl())
                .into(holder.videoPublisherImage);

        holder.videoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.preventMultipleClick()) {
                    Util.showCustomTabBrowser(context, toolbarColor[0], card.getStoryTitle(), card.getStoryUrl(), card.getStoryId());

                }
            }
        });
    }

    private void showVideoRVView(final ViewHolder holder, List<FeedModel.Card> cardList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.videoRecycler.setLayoutManager(layoutManager);
        VideoFeedAdapter videoFeedAdapter = new VideoFeedAdapter();
        videoFeedAdapter.setCardList(cardList);
        holder.videoRecycler.setAdapter(videoFeedAdapter);
    }

    @Override
    public int getItemCount() {
        return feedList == null ? 0 : feedList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return feedList.size() != position ? VIEW_FEED : VIEW_PROG;
    }

    public static class ProgressHolder extends RecyclerView.ViewHolder {

        public ProgressHolder(View itemView) {
            super(itemView);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout posterLayout;
        private RelativeLayout videoLayout;
        private LinearLayout line1;
        private LinearLayout line2;

        private RecyclerView posterRecycler;
        private RecyclerView videoRecycler;
        private RecyclerView storyRecycler;

        private TextView titleView;
        private TextView categoryView;
        private TextView publisherView;
        private TextView writerView;
        private ImageView coverImage;
        private ImageView publisherImage;

        private TextView videoTitleView;
        private TextView videoCategoryView;
        private TextView videoPublisherView;
        private TextView videoWriterView;
        private ImageView videoCoverImage;
        private ImageView videoPublisherImage;

        public ViewHolder(View itemView) {
            super(itemView);
            posterLayout = itemView.findViewById(R.id.layout_poster);
            videoLayout = itemView.findViewById(R.id.layout_video);

            line1 = itemView.findViewById(R.id.line_1);
            line2 = itemView.findViewById(R.id.line_2);

            posterRecycler = itemView.findViewById(R.id.recycler_poster);
            videoRecycler = itemView.findViewById(R.id.recycler_video);
            storyRecycler = itemView.findViewById(R.id.recycler_story);

            titleView = itemView.findViewById(R.id.view_title);
            categoryView = itemView.findViewById(R.id.view_category);
            publisherView = itemView.findViewById(R.id.view_publisher_name);
            writerView = itemView.findViewById(R.id.view_writer);
            publisherImage = itemView.findViewById(R.id.image_publisher);
            coverImage = itemView.findViewById(R.id.image_poster_solo);

            videoTitleView = itemView.findViewById(R.id.view_video_title);
            videoCategoryView = itemView.findViewById(R.id.view_video_category);
            videoPublisherView = itemView.findViewById(R.id.view_video_publisher_name);
            videoWriterView = itemView.findViewById(R.id.view_video_writer);
            videoPublisherImage = itemView.findViewById(R.id.image_video_publisher);
            videoCoverImage = itemView.findViewById(R.id.image_video_solo);
        }
    }

    public interface AddListener {
        void loadMoreFeed();
    }

    public void setLoaded() {
        isLoading = false;
    }
}
