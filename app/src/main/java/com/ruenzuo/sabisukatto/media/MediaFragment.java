package com.ruenzuo.sabisukatto.media;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruenzuo.sabisukatto.MediaActivity;
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
        View view = inflater.inflate(R.layout.fragment_media, container, false);
        setupFloatingButton(view);
        return view;
    }

    private void setupFloatingButton(View view) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaActivity activity = (MediaActivity) getActivity();
                activity.attemptDownloadGIF();
            }
        });
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (!clipboard.hasPrimaryClip()) {
            fab.hide();
        }
    }
}
