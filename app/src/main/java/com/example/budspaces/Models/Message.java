package com.example.budspaces.Models;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.budspaces.Enums.MessageType;
import com.example.budspaces.Listeners.OnChatMemberListener;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

public class Message implements IMessage, MessageContentType.Image {
    @SerializedName("type")
    @Expose
    private MessageType type;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("timestamp")
    @Expose
    private Date timestamp;

    private Member member;

    public Message(String uid, String content, MessageType type) {
        this.timestamp = new Date();
        this.userId = uid;
        this.content = content.trim();
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Nullable
    @Override
    public String getImageUrl() {
        if (type.equals(MessageType.picture))
            return content;
        return null;
    }

    @Override
    public String getText() {
        return content;
    }

    @Override
    public IUser getUser() {
        if (member == null) {
            member = Member.getMember(userId, new OnChatMemberListener() {
                @Override
                public void onUserClick(Member user) { }

                @Override
                public void onMemberLoaded(Member member) {
                    Message.this.member = member;
                }
            });
        }
        return member;
    }

    @Override
    public Date getCreatedAt() {
        return timestamp;
    }
}