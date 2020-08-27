package com.example.budspaces.Views.ViewHolders.Messages;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Listeners.OnChatMemberListener;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.User;
import com.example.budspaces.R;

public class ChatListMembersVH extends RecyclerView.ViewHolder {
    private ImageView picture;

    public ChatListMembersVH(@NonNull View itemView) {
        super(itemView);
        picture = itemView.findViewById(R.id.picture);
    }

    public void bind(Member member, OnChatMemberListener listener) {
        Glide.with(itemView).load(member.getAvatar()).error(R.drawable.com_facebook_profile_picture_blank_square).into(picture);
//        Glide.with(itemView).load(member.getAvatar()).error(R.drawable.background_splash).into(picture);

        if (listener != null) {
            itemView.setOnClickListener((v) -> {
                listener.onUserClick(member);
            });
        }
    }
}