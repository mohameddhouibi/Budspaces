package com.example.budspaces.Views.ViewHolders;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Navigation.Keys.GroupKey;
import com.example.budspaces.R;
import com.example.budspaces.Views.Activities.MessagesActivity;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class AnnouncementVH extends RecyclerView.ViewHolder {
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.groupName)
    TextView groupName;
    @BindView(R.id.announceName)
    TextView announceName;
    @BindView(R.id.announceContent)
    TextView announceContent;
    @BindView(R.id.header)
    View header;

    private HomeModel announcement;

    public AnnouncementVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(HomeModel announcement, boolean isHeaderVisible) {
        this.announcement = announcement;

        Glide.with(itemView).load(announcement.getGroupPicture()).into(picture);
        groupName.setText(announcement.getGroupName());
        announceName.setText(announcement.getName());
        announceContent.setText(announcement.getDescription());

        if (!isHeaderVisible) {
            header.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.picture, R.id.groupName, R.id.action_chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.picture:
            case R.id.groupName:
                Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(GroupKey.create(announcement.getGroupID()));
                break;
            case R.id.action_chat:
                Intent intent = new Intent(itemView.getContext(), MessagesActivity.class);
                intent.putExtra("roomID", announcement.getGroupID());
                intent.putExtra("roomName", announcement.getGroupName());

                itemView.getContext().startActivity(intent);
                break;
        }
    }
}
