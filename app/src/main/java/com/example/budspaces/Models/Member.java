package com.example.budspaces.Models;

import android.util.Log;

import com.example.budspaces.Listeners.OnChatMemberListener;
import com.example.budspaces.Network.AppRetrofit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IUser;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Member implements IUser {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("picture")
    @Expose
    private String avatar;

    private static List<String> isSearching = new ArrayList<>();
    private static Map<String, List<OnChatMemberListener>> chatListeners = new HashMap<>();
    private static Map<String, Member> memberList = new HashMap<>();

    public static Member getMember(String uid, OnChatMemberListener listener) {
        if (memberList.containsKey(uid)) {
            listener.onMemberLoaded(memberList.get(uid));
            return memberList.get(uid);
        }

        if (!isSearching.contains(uid)) {
            chatListeners.put(uid, new ArrayList<>());

            isSearching.add(uid);

            AppRetrofit.getInstance().getService()
            .getChatUser(uid)
            .enqueue(new Callback<Member>() {
                @Override
                public void onResponse(@NotNull Call<Member> call, @NotNull Response<Member> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        Member member = response.body();
                        member.name = member.name.split(" ")[0];

                        memberList.put(member.getId(), member);
                        isSearching.remove(uid);

                        for (OnChatMemberListener lst : Objects.requireNonNull(chatListeners.get(uid)))
                            lst.onMemberLoaded(member);

                        chatListeners.remove(uid);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Member> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        Objects.requireNonNull(chatListeners.get(uid)).add(listener);
        return new Member(uid);
    }

    public Member(String uid) {
        this.id = uid;
        this.avatar = "unknown";
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name.split(" ")[0];
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}