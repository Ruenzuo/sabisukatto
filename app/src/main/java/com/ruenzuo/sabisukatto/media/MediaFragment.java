package com.ruenzuo.sabisukatto.media;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruenzuo.sabisukatto.R;
import com.ruenzuo.sabisukatto.sabisukattokit.MediaDownloader;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class MediaFragment extends Fragment {

    private final CompositeDisposable disposables = new CompositeDisposable();

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    private void setupFloatingButton(View view) {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDownloadGIF();
            }
        });
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        if (!clipboard.hasPrimaryClip()) {
            fab.hide();
        }
    }

    private void attemptDownloadGIF() {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
        if (item.getText() == null) {
            //TODO: Implement
            return;
        }
        MediaDownloader mediaDownloader = new MediaDownloader(getContext());
        Uri clipboardUri = Uri.parse(item.getText().toString());
        disposables.add(mediaDownloader.downloadMedia(clipboardUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        return;
                    }

                    @Override
                    public void onError(Throwable e) {
                        return;
                    }
                }));
    }
}
