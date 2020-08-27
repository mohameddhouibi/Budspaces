package com.example.budspaces.Views.ViewHolders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.Navigation.Keys.GroupKey;
import com.example.budspaces.R;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.Objects;

public class ListGroupVH extends RecyclerView.ViewHolder {
    private ImageView picture;

    public ListGroupVH(@NonNull View itemView) {
        super(itemView);
        picture = itemView.findViewById(R.id.picture);
    }

    public void bind(SimplifiedGroup group) {
        Glide.with(itemView).load(group.getPicture()).into(picture);
        itemView.setOnClickListener(v -> {
            Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(GroupKey.create(group.getId()));
        });
    }
}