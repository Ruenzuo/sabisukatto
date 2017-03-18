package com.ruenzuo.sabisukatto.media;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruenzuo.sabisukatto.R;

public class MediaFragment extends Fragment {

    public MediaFragment() {
        // Required empty public constructor
    }

    public static MediaFragment newInstance() {
        MediaFragment fragment = new MediaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media, container, false);
    }
}
