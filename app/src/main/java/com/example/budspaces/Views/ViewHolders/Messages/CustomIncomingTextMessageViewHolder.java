package com.example.budspaces.Views.ViewHolders.Messages;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Listeners.OnChatMemberListener;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.Message;
import com.example.budspaces.Models.User;
import com.example.budspaces.R;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomIncomingTextMessageViewHolder
        extends MessageHolders.IncomingTextMessageViewHolder<Message> {

    private TextView messageSender;
    private boolean isShown = false;

    public CustomIncomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
        messageSender = itemView.findViewById(R.id.messageSender);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        messageSender.setText(message.getUser().getName());

        if (message.getUser().getName() == null || message.getUser().getName().isEmpty()) {
            Member.getMember(message.getUserId(), new OnChatMemberListener() {
                @Override
                public void onUserClick(Member user) { }

                @Override
                public void onMemberLoaded(Member member) {
                    messageSender.setText(member.getName());
                    if (member.getAvatar() == null) {
                        Glide.with(itemView).load(R.drawable.com_facebook_profile_picture_blank_square).into(userAvatar);
                    } else
                        Glide.with(itemView).load(member.getAvatar())
                            .error(R.drawable.com_facebook_profile_picture_blank_square).into(userAvatar);
                }
            });
        }

        //We can set click listener on view from payload
        final Payload payload = (Payload) this.payload;
        userAvatar.setOnClickListener((view) -> {
            if (payload != null && payload.avatarClickListener != null) {
                payload.avatarClickListener.onAvatarClick(message);
            }
        });

        text.setOnLongClickListener((v) -> {
            showHideState();
            return false;
        });
    }

    public void showHideState() {
        isShown = !isShown;

        if (isShown) {
            time.setVisibility(View.VISIBLE);
            messageSender.setVisibility(View.VISIBLE);
        } else {
            time.setVisibility(View.INVISIBLE);
            messageSender.setVisibility(View.INVISIBLE);
        }
    }

    public static class Payload {
        public OnAvatarClickListener avatarClickListener;
    }

    public interface OnAvatarClickListener {
        void onAvatarClick(Message message);
    }
}
