package com.example.budspaces.Views.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Navigation.Keys.ProfileKey;
import com.example.budspaces.R;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.Objects;

public class ListMembersVH extends RecyclerView.ViewHolder {
    private ImageView picture;
    private TextView name;

    public ListMembersVH(@NonNull View itemView) {
        super(itemView);
        picture = itemView.findViewById(R.id.picture);
        name = itemView.findViewById(R.id.name);
    }

    public void bind(Member user) {
        Glide.with(itemView).load(user.getAvatar())
                .error(R.drawable.com_facebook_profile_picture_blank_square).into(picture);
        name.setText(user.getName());

        itemView.setOnClickListener(v -> {
            Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(ProfileKey.create(user.getId()));
        });
    }
}