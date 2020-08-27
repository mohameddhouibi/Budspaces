package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Listeners.OnSettingChecked;
import com.example.budspaces.R;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Map<String, Boolean> data;
    private List<String> keys;
    private OnSettingChecked listener;

    public SettingsAdapter(OnSettingChecked listener) {
        this.data = new HashMap<>();
        this.keys = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_settings, parent, false);

        return new SettingsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SettingsVH)holder).bind(position, keys.get(position), data.get(keys.get(position)), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void add(String key, boolean value) {
        keys.add(key);
        data.put(key, value);
        notifyItemChanged(keys.size() - 1);
    }

    public static class SettingsVH extends RecyclerView.ViewHolder {
        private TextView textView;
        private SwitchButton switchButton;

        public SettingsVH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textWidget);
            switchButton = itemView.findViewById(R.id.switchWidget);
        }

        public void bind(int pos, String str, Boolean check, OnSettingChecked listener) {
            textView.setText(str);
            switchButton.setChecked(check);

            switchButton.setOnCheckedChangeListener((view, isChecked) -> listener.isChecked(pos, isChecked));

            itemView.setOnClickListener(v -> {
                switchButton.setChecked(!switchButton.isChecked());
                listener.isChecked(pos, switchButton.isChecked());
            });
        }
    }
}