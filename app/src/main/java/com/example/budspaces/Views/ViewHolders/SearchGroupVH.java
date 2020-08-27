package com.example.budspaces.Views.ViewHolders;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Navigation.Keys.GroupKey;
import com.example.budspaces.R;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchGroupVH extends RecyclerView.ViewHolder {
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.location)
    TextView location;

    public SearchGroupVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Group group) {
        Glide.with(itemView).load(group.getPicture()).into(picture);
        name.setText(group.getName());
        content.setText(TextUtils.join(", ", group.getInterests()));
        location.setText(group.getAddress());

        itemView.setOnClickListener((v) -> {
            Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(GroupKey.create(group.getId()));
        });
    }
}