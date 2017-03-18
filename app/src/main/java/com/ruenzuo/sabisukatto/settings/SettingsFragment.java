package com.ruenzuo.sabisukatto.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruenzuo.sabisukatto.general.DividerItemDecoration;
import com.ruenzuo.sabisukatto.R;
import com.ruenzuo.sabisukatto.general.ItemClickSupport;
import com.ruenzuo.sabisukatto.general.VerticalSpaceItemDecoration;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements UnauthorizeDialogFragment.OnFragmentInteractionListener {

    private RecyclerView recyclerView;
    private SettingsAdapter adapter;
    private List<Setting> dataSet;
    private TwitterAuthClient client = new TwitterAuthClient();

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDataSet();
    }

    private void setupDataSet() {
        dataSet = getSettings();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setupRecyclerView(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        client.onActivityResult(requestCode, resultCode, data);
    }

    private void setupRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SettingsAdapter(getContext(), dataSet);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));

        VerticalSpaceItemDecoration verticalSpaceItemDecoration = new VerticalSpaceItemDecoration(90, new ArrayList<Integer>());
        recyclerView.addItemDecoration(verticalSpaceItemDecoration);

        ItemClickSupport support = ItemClickSupport.addTo(recyclerView);
        support.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View view) {
                switch (position) {
                    case 0:
                        checkTwitterAuthorisation();
                        return;
                    default:
                }
            }
        });
    }

    private void checkTwitterAuthorisation() {
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            UnauthorizeDialogFragment fragment = new UnauthorizeDialogFragment();
            fragment.show(getChildFragmentManager(), UnauthorizeDialogFragment.class.getName());
        } else {
            client.authorize(getActivity(), new Callback<TwitterSession>() {
                @Override
                public void success(Result<TwitterSession> result) {
                    updateSettingsList();
                }

                @Override
                public void failure(TwitterException exception) {
                    //TODO: implement
                }
            });
        }
    }

    private void updateSettingsList() {
        adapter.setDataSet(getSettings());
        adapter.notifyDataSetChanged();
    }

    private List<Setting> getSettings() {
        ArrayList<Setting> settings = new ArrayList<>();
        Setting twitterSetting = new Setting(getContext().getString(R.string.twitter_account), null);
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if (session != null) {
            twitterSetting.setValue(session.getUserName());
        }
        settings.add(twitterSetting);
        return settings;
    }

    @Override
    public void onFragmentInteraction() {
        Twitter.getSessionManager().clearActiveSession();
        updateSettingsList();
    }
}
