package com.example.budspaces.Views.ViewHolders;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Navigation.Keys.EventKey;
import com.example.budspaces.Navigation.Keys.GroupKey;
import com.example.budspaces.R;
import com.example.budspaces.Views.Activities.MessagesActivity;
import com.zhuinden.simplestack.navigator.Navigator;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class WallEventVH extends RecyclerView.ViewHolder {
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.groupName)
    TextView groupName;
    @BindView(R.id.eventName)
    TextView eventName;
    @BindView(R.id.eventTime)
    TextView eventTime;
    @BindView(R.id.eventDesc)
    TextView eventDesc;
    @BindView(R.id.header)
    View header;

    private HomeModel event;

    public WallEventVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(HomeModel event, boolean isHeaderVisible) {
        this.event = event;
        Glide.with(itemView).load(event.getGroupPicture()).into(picture);
        groupName.setText(event.getGroupName());
        eventName.setText(event.getName());
        eventDesc.setText(event.getDescription());
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("dd MMMM yyyy • hh.mm a");
        eventTime.setText(event.getStartDate().toString(timeFormatter));

        if (!isHeaderVisible) {
            header.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.picture, R.id.groupName, R.id.action_chat, R.id.icon, R.id.eventName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.picture:
            case R.id.groupName:
                Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(GroupKey.create(event.getGroupID()));
                break;
            case R.id.action_chat:
                Intent intent = new Intent(itemView.getContext(), MessagesActivity.class);
                intent.putExtra("roomID", event.getGroupID());
                intent.putExtra("roomName", event.getGroupName());

                itemView.getContext().startActivity(intent);
                break;
            case R.id.icon:
            case R.id.eventName:
                Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(EventKey.create(event.getObjID()));
                break;
        }
    }
}
