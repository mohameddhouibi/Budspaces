package com.example.budspaces.Views.ViewHolders;

import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Listeners.OnInterestCheckedListener;
import com.example.budspaces.R;

public class InterestSearchVH extends RecyclerView.ViewHolder {
    private AppCompatCheckBox item;

    public InterestSearchVH(@NonNull View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.item);
    }

    public void bind(String theme, OnInterestCheckedListener listener) {
        item.setText(theme);
        item.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            if (isChecked)
                listener.setTheme(theme);
            else
                listener.delTheme(theme);
        });
    }
}