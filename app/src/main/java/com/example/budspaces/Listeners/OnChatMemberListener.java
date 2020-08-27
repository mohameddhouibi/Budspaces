package com.example.budspaces.Listeners;

import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.User;

public interface OnChatMemberListener {
    void onUserClick(Member member);
    void onMemberLoaded(Member member);
}
