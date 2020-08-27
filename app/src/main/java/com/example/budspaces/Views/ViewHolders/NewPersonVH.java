package com.example.budspaces.Views.ViewHolders;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Navigation.Keys.GroupKey;
import com.example.budspaces.Navigation.Keys.ProfileKey;
import com.example.budspaces.R;
import com.example.budspaces.Views.Activities.MessagesActivity;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewPersonVH extends RecyclerView.ViewHolder {
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.groupName)
    TextView groupName;
    @BindView(R.id.personName)
    TextView personName;
    @BindView(R.id.header)
    View header;

    private HomeModel model;

    public NewPersonVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(HomeModel model, boolean isHeaderVisible) {
        this.model = model;

        Glide.with(itemView).load(model.getGroupPicture()).into(picture);
        groupName.setText(model.getGroupName());
        personName.setText(model.getName());

        if (!isHeaderVisible) {
            header.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.picture, R.id.groupName, R.id.action_chat, R.id.icon, R.id.personName})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.picture:
            case R.id.groupName:
                Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(GroupKey.create(model.getGroupID()));
                break;
            case R.id.action_chat:
                Intent intent = new Intent(itemView.getContext(), MessagesActivity.class);
                intent.putExtra("roomID", model.getGroupID());
                intent.putExtra("roomName", model.getGroupName());

                itemView.getContext().startActivity(intent);
                break;
            case R.id.icon:
            case R.id.personName:
                Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(ProfileKey.create(model.getObjID()));
                break;
        }
    }
}