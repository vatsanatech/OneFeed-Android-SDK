package com.onefeedsdk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onefeedsdk.R;

/**
 * Created by Yogesh Soni.
 * Company: WittyFeed
 * Date: 01-October-2018
 * Time: 16:33
 */
public class OneFeedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.onefeed_fragment_onefeed, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, new MainFeedFragment())
                .commit();
    }
}
