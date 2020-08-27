package com.example.budspaces.Views.ViewHolders;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.R;
import com.google.android.material.chip.Chip;

public class ChipsVH extends RecyclerView.ViewHolder {
    private Chip chip;

    public ChipsVH(@NonNull View itemView) {
        super(itemView);
        chip = itemView.findViewById(R.id.chip);
    }

    public void bind(String str) {
        chip.setText(str);
    }
}
