package com.example.budspaces.Views.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Listeners.OnCategorySelectedListener;
import com.example.budspaces.Models.Theme;
import com.example.budspaces.R;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoryVH extends RecyclerView.ViewHolder {
    private CircleImageView picture;
    private CircleImageView selected;
    private TextView name;

    private boolean isSelected = false;

    public CategoryVH(@NonNull View itemView) {
        super(itemView);
        picture = itemView.findViewById(R.id.picture);
        selected = itemView.findViewById(R.id.selected);
        name = itemView.findViewById(R.id.name);
    }

    public void bind(Theme item, OnCategorySelectedListener listener) {
        Glide.with(itemView).load(item.getPicture()).into(picture);
        name.setText(item.getName());

        itemView.setOnClickListener((View v) -> {
            if (isSelected) {
                if (listener != null)
                    listener.delCat(item);
                selected.setVisibility(View.GONE);
                isSelected = !isSelected;
            } else {
                if (listener == null || listener.selectCat(item)) {
                    selected.setVisibility(View.VISIBLE);
                    isSelected = !isSelected;
                }
            }
        });

        if (listener.isSelected(item.getName()))
            itemView.callOnClick();
    }

    public void randomSelection() {
        isSelected = new Random().nextBoolean();
        if (isSelected)
            selected.setVisibility(View.VISIBLE);
    }
}
