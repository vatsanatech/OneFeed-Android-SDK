package com.wittyfeed.sdk.onefeed;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wittyfeed.sdk.onefeed.DataStoreManagement.DataStore;
import com.wittyfeed.sdk.onefeed.DataStoreManagement.DataStoreParser;
import com.wittyfeed.sdk.onefeed.Models.Card;
import com.wittyfeed.sdk.onefeed.Utils.OFGlide;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class OFCardFetcher {

    private DataStore dataStore;
    private static int currentindex = 0;
    public View inflatedView;
    private Context context;
    private boolean is_fetching_data = false;
    private Map<String, Boolean> cards_already_used_map = new HashMap<>();
    private Map<Integer, Integer> cards_mapper = new HashMap<>();
    private static int local_index = 0;
    private static boolean shouldHideCategory = false;


    public void setOfInterface(OFInterface ofInterface) {
        this.ofInterface = ofInterface;
    }

    private OFInterface ofInterface;

    public View makeCardUI(final Card card, float text_size_ratio, @Nullable String color) {
        inflatedView = LayoutInflater.from(context).inflate(R.layout.card_native, null);

        TextView title_tv = inflatedView.findViewById(R.id.title_tv);
        TextView cat_tv = inflatedView.findViewById(R.id.cat_tv);

        if (shouldHideCategory) {
            cat_tv.setVisibility(View.GONE);
            shouldHideCategory = false;
        }

        ImageView cover_iv = inflatedView.findViewById(R.id.cover_iv);

        final ImageView arrow = inflatedView.findViewById(R.id.up);

        title_tv.setText(card.getStoryTitle());

        title_tv.setTextSize(27 * text_size_ratio);

        cat_tv.setText(card.getSheildText());

        cat_tv.setTextSize(20 * text_size_ratio);

        if (color != null) {
            title_tv.setTextColor(Color.parseColor(color));
            cat_tv.setTextColor(Color.parseColor(color));
        }

        OFGlide.with(context)
                .load(card.getCoverImage())
                .into(cover_iv);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) title_tv.getLayoutParams();
        layoutParams.leftMargin = (int) (layoutParams.leftMargin);
        layoutParams.rightMargin = (int) (layoutParams.rightMargin);
        title_tv.setLayoutParams(layoutParams);


        inflatedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OFAnalytics.getInstance().sendAnalytics(
                        OFAnalytics.AnalyticsType.Story,
                        ""
                                + card.getId()
                                + ":"
                                + "onefeed"
                );
                OneFeedMain.getInstance().getContentViewMaker(v.getContext()).launch(v.getContext(), card.getStoryUrl());

            }
        });

        return inflatedView;


    }

    public void setDataStore(DataStore dataStore, Context context) {
        this.dataStore = dataStore;
        this.context = context;
    }


    public void loadInitData(int card_id, final OnInitialized onInitialized) {
        OneFeedMain.getInstance().networkServiceManager.resetRepeatingDataOffset();
        clean_hashmap();
//        if (OneFeedMain.getInstance().dataStore.getAllCardData().size() < 1) {
//
//            if (!is_fetching_data) {
//
//                is_fetching_data = true;

                OneFeedMain.getInstance().networkServiceManager.hitRepeatingDataAPI(
                        OneFeedMain.getInstance().networkServiceManager.getRepeatingDataOffset(),
                        new NetworkServiceManager.OnNetworkServiceDidRespond() {
                            @Override
                            public void onSuccessResponse(String response) {
                                OneFeedMain.getInstance().networkServiceManager.incrementRepeatingDataOffset();
                                OneFeedMain.getInstance().dataStore.setRepeatingBlock(DataStoreParser.parseRepeatingDataString(response));
                                OneFeedMain.getInstance().dataStore.buildAllCardArray();
                                is_fetching_data = false;
                                onInitialized.onSuccess();
                                Log.i("TAG", "SUCCESSFULLY LOADED INITIAL DATA: ");
                            }

                            @Override
                            public void onError() {
                                is_fetching_data = false;
                                onInitialized.onError();
                            }
                        },
                        card_id
                );


//            }
//        }

    }


    public void fetch_non_repeating_card(int card_id, float text_size_ratio, boolean hideCategory, String textColor) {
        View view = null;

        shouldHideCategory = hideCategory;

        Card story = null;

        if (OneFeedMain.getInstance().dataStore.getNonRepeatingDatum() != null) {
            for (int i = 0; i < OneFeedMain.getInstance().dataStore.getNonRepeatingDatum().getBlocks().size(); i++) {
                if (OneFeedMain.getInstance().dataStore.getNonRepeatingDatum().getBlocks().get(i).getMeta().getCardId() == card_id) {
                    story = OneFeedMain.getInstance().dataStore.getNonRepeatingDatum().getBlocks().get(i).getCards().get(0);
                }
            }

            if (story != null) {
                view = makeCardUI(story, text_size_ratio, textColor);
                ofInterface.OnSuccess(view);

            }
        }
    }

    //First time initialize for fetching repeating card
    public void fetch_repeating_card(int card_id, float text_size_ratio, boolean hideCategory, String textColor, OnInitialized onInitialized) {
        Log.i("TAG", "fetch_repeating_card: " + "ALL CARD IS EMPTY, LOADING NEW DATA NOW");
        clean_hashmap();
        load_fresh_data(card_id, onInitialized);
        return;
    }

    public synchronized void fetch_repeating_card(int card_id, float text_size_ratio, boolean hideCategory, String textColor) {

        shouldHideCategory = hideCategory;

        if (OneFeedMain.getInstance().dataStore.getAllCardData().size() > 0) {
            if (OneFeedMain.getInstance().dataStore.getRepeatingDatum().getMeta().getCardId() == card_id) {
                View view = null;
                Card story = null;

                story = get_block_wise_next_available_card();

                if (story == null) {
                    load_more_data_np(card_id, OneFeedMain.getInstance().dataStore.getAllCardData().size(), text_size_ratio, textColor);
                    return;
                }

                view = makeCardUI(story, text_size_ratio, textColor);
                ofInterface.OnSuccess(view);
                return;

            } else {
                OneFeedMain.getInstance().dataStore.getAllCardData().clear();
                OneFeedMain.getInstance().dataStore.getRepeatingDatum().setMeta(null);
                OneFeedMain.getInstance().dataStore.getRepeatingDatum().setCards(null);
                load_fresh_data(card_id);
                return;
            }
        } else {
            Log.i("TAG", "fetch_repeating_card: " + "ALL CARD IS EMPTY, LOADING NEW DATA NOW");
            load_fresh_data(card_id);
            return;
        }
    }


    public void fetch_repeating_card(int card_id, int position, float text_size_ratio, boolean hideCategory, String textColor) {

        shouldHideCategory = hideCategory;

        if (OneFeedMain.getInstance().dataStore.getAllCardData().size() > 0) {
            if (OneFeedMain.getInstance().dataStore.getRepeatingDatum().getMeta().getCardId() == card_id) {
                View view = null;
                Card story = null;

                if (OneFeedMain.getInstance().dataStore.getAllCardData().size() > position) {
                    int temp_pos = get_next_static_card(position);
                    story = OneFeedMain.getInstance().dataStore.getAllCardData().get(temp_pos);
                }

                if (story == null) {
                    load_more_data(card_id, position, textColor);
                    return;
                }

                view = makeCardUI(story, text_size_ratio, textColor);
                ofInterface.OnSuccess(view);
                return;

            } else {
                OneFeedMain.getInstance().dataStore.getAllCardData().clear();
                OneFeedMain.getInstance().dataStore.getRepeatingDatum().setMeta(null);
                OneFeedMain.getInstance().dataStore.getRepeatingDatum().setCards(null);
                load_fresh_data(card_id, position, textColor);
                return;
            }
        } else {
            load_fresh_data(card_id, position, textColor);
            return;
        }

    }


    public int get_next_static_card(int position) {
        int arr_index = 0;

        if (cards_mapper.get(position) != null) {
            arr_index = cards_mapper.get(position);

        } else {
            cards_mapper.put(position, local_index);
            arr_index = local_index;
            local_index++;
        }

        return arr_index;
    }

    public void load_more_data_np(final int card_id, final int all_card_size, final float text_size_ratio, final String textColor) {

        if (!is_fetching_data) {

            is_fetching_data = true;
            OneFeedMain.getInstance().networkServiceManager.hitRepeatingDataAPI(
                    OneFeedMain.getInstance().networkServiceManager.getRepeatingDataOffset(),
                    new NetworkServiceManager.OnNetworkServiceDidRespond() {
                        @Override
                        public void onSuccessResponse(String response) {
                            OneFeedMain.getInstance().networkServiceManager.incrementRepeatingDataOffset();
                            OneFeedMain.getInstance().dataStore.appendInRepeatingDataArray(DataStoreParser.parseRepeatingDataString(response));
                            OneFeedMain.getInstance().dataStore.buildAllCardArray();
                            if (all_card_size == OneFeedMain.getInstance().dataStore.getAllCardData().size()) {
                                OneFeedMain.getInstance().networkServiceManager.resetRepeatingDataOffset();
                            }
                            is_fetching_data = false;
                            OneFeedMain.getInstance().ofCardFetcher.fetch_repeating_card(card_id, text_size_ratio, shouldHideCategory, textColor);

                        }

                        @Override
                        public void onError() {
                            is_fetching_data = false;
                            Log.i(TAG, "onError: Could'nt load More Data");
                            //Handle same card repeating when api is not fetching data
                            if (cards_already_used_map.size() == OneFeedMain.getInstance().dataStore.getAllCardData().size()) {
                                clean_hashmap();
                            }
                        }
                    },
                    card_id
            );

        }

    }

    public void load_more_data(final int card_id, final int position, final String textColor) {

        if (!is_fetching_data) {
            is_fetching_data = true;

            OneFeedMain.getInstance().networkServiceManager.hitRepeatingDataAPI(
                    OneFeedMain.getInstance().networkServiceManager.getRepeatingDataOffset(),
                    new NetworkServiceManager.OnNetworkServiceDidRespond() {
                        @Override
                        public void onSuccessResponse(String response) {
                            OneFeedMain.getInstance().networkServiceManager.incrementRepeatingDataOffset();
                            OneFeedMain.getInstance().dataStore.appendInRepeatingDataArray(DataStoreParser.parseRepeatingDataString(response));
                            OneFeedMain.getInstance().dataStore.buildAllCardArray();
                            OneFeedMain.getInstance().ofCardFetcher.fetch_repeating_card(card_id, position, shouldHideCategory, textColor);
                            is_fetching_data = false;
                        }

                        @Override
                        public void onError() {
                            is_fetching_data = false;
                            Log.i(TAG, "onError: Could'nt load More Data");
                            //Handle same card repeating when api is not fetching data
                            if (cards_already_used_map.size() == OneFeedMain.getInstance().dataStore.getAllCardData().size()) {
                                clean_hashmap();
                            }
                        }
                    },
                    card_id
            );
        }

    }

    public void load_fresh_data(final int card_id, final int position, final String textColor) {

        if (!is_fetching_data) {
            is_fetching_data = true;

            OneFeedMain.getInstance().networkServiceManager.hitRepeatingDataAPI(
                    OneFeedMain.getInstance().networkServiceManager.getRepeatingDataOffset(),
                    new NetworkServiceManager.OnNetworkServiceDidRespond() {
                        @Override
                        public void onSuccessResponse(String response) {
                            OneFeedMain.getInstance().networkServiceManager.incrementRepeatingDataOffset();
                            OneFeedMain.getInstance().dataStore.setRepeatingBlock(DataStoreParser.parseRepeatingDataString(response));
                            OneFeedMain.getInstance().dataStore.buildAllCardArray();
                            OneFeedMain.getInstance().ofCardFetcher.fetch_repeating_card(card_id, position, shouldHideCategory, textColor);
                            is_fetching_data = false;
                        }

                        @Override
                        public void onError() {
                            is_fetching_data = false;
                            Log.i("ALLCARDSTITLE", "onError: Could'nt load Fresh Data");

                        }
                    },
                    card_id
            );
        }

    }

    public void load_fresh_data(final int card_id) {

        if (!is_fetching_data) {
            is_fetching_data = true;

            OneFeedMain.getInstance().networkServiceManager.hitRepeatingDataAPI(
                    OneFeedMain.getInstance().networkServiceManager.getRepeatingDataOffset(),
                    new NetworkServiceManager.OnNetworkServiceDidRespond() {
                        @Override
                        public void onSuccessResponse(String response) {
                            OneFeedMain.getInstance().networkServiceManager.incrementRepeatingDataOffset();
                            OneFeedMain.getInstance().dataStore.setRepeatingBlock(DataStoreParser.parseRepeatingDataString(response));
                            OneFeedMain.getInstance().dataStore.buildAllCardArray();
                            is_fetching_data = false;
                        }

                        @Override
                        public void onError() {
                            is_fetching_data = false;
                            Log.i("ALLCARDSTITLE", "onError: Could'nt load Fresh Data");

                        }
                    },
                    card_id
            );
        }

    }

    public void load_fresh_data(final int card_id, final OnInitialized onInitialized) {


        OneFeedMain.getInstance().networkServiceManager.resetRepeatingDataOffset();

        OneFeedMain.getInstance().networkServiceManager.hitRepeatingDataAPI(
                OneFeedMain.getInstance().networkServiceManager.getRepeatingDataOffset(),
                new NetworkServiceManager.OnNetworkServiceDidRespond() {
                    @Override
                    public void onSuccessResponse(String response) {
                        OneFeedMain.getInstance().networkServiceManager.incrementRepeatingDataOffset();
                        OneFeedMain.getInstance().dataStore.setRepeatingBlock(DataStoreParser.parseRepeatingDataString(response));
                        OneFeedMain.getInstance().dataStore.buildAllCardArray();
                        onInitialized.onSuccess();
                        is_fetching_data = false;
                    }

                    @Override
                    public void onError() {
                        Log.i("ALLCARDSTITLE", "onError: Could'nt load Fresh Data");
                        onInitialized.onError();
                        is_fetching_data = false;
                    }
                },
                card_id
        );
    }


    private Card getNextCard() {
        Card card = null;
        if (OneFeedMain.getInstance().dataStore.getAllCardData().size() - 1 >= currentindex + 1) {
            card = OneFeedMain.getInstance().dataStore.getAllCardData().get(currentindex);
            currentindex++;
            return card;
        } else
            return null;
    }

    public void clean_hashmap() {
        if (cards_already_used_map != null)
            cards_already_used_map.clear();
    }

    private Card get_block_wise_next_available_card() {
        int i = 0;
        Card story_to_return = null;
        if (cards_already_used_map == null)
            cards_already_used_map = new HashMap<>();
        for (; i < OneFeedMain.getInstance().dataStore.getAllCardData().size(); i++) {
            Card temp_story = OneFeedMain.getInstance().dataStore.getAllCardData().get(i);
            if (!cards_already_used_map.containsKey(temp_story.getId() + "")) {
                story_to_return = temp_story;
                cards_already_used_map.put(story_to_return.getId() + "", true);
                break;
            }
        }
        return story_to_return;
    }


    public interface OnInitialized {
        void onSuccess();
        void onError();
    }

}


