package com.ruenzuo.sabisukatto.settings;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruenzuo.sabisukatto.R;

import java.util.List;

/**
 * Created by ruenzuo on 18/03/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    private List<Setting> dataSet;
    private Context context;

    public SettingsAdapter(Context context, List<Setting> dataSet) {
        this.context = context;
        this.dataSet = dataSet;
    }

    public void setDataSet(List<Setting> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public SettingsAdapter.SettingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_setting, parent, false);
        SettingViewHolder viewHolder = new SettingViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SettingsAdapter.SettingViewHolder viewHolder, int position) {
        Setting setting = dataSet.get(position);
        viewHolder.textViewTitle.setText(setting.getTitle());
        if (setting.getValue() != null) {
            viewHolder.textViewValue.setText(setting.getValue());
        } else {
            viewHolder.textViewValue.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public static class SettingViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTitle;
        public TextView textViewValue;

        public SettingViewHolder(View root) {
            super(root);
            this.textViewTitle = (TextView) root.findViewById(R.id.text_view_title);
            this.textViewValue = (TextView) root.findViewById(R.id.text_view_value);
        }
    }

}
