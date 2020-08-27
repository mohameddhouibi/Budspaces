package com.example.budspaces.Adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.User;
import com.example.budspaces.Navigation.Keys.MoreMembersKey;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.Messages.ChatListMembersVH;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.List;
import java.util.Objects;

public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int INVISIBLE_ITEM = 0;
    private final int VISIBLE_ITEM = 1;

    private List<Member> data;
    private int max_items;
    private int memberCount;
    private String groupName;
    private String groupId;

    public GroupMembersAdapter(List<Member> data, int memberCount, int max_items, String groupName, String id) {
        this.data = data;
        this.max_items = max_items;
        this.memberCount = memberCount;
        this.groupName = groupName;
        this.groupId = id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case INVISIBLE_ITEM:
                View viewINV = inflater.inflate(R.layout.item_show_more_members, parent, false);
                viewHolder = new ShowMoreVH(viewINV);
                break;
            case VISIBLE_ITEM:
                View viewV = inflater.inflate(R.layout.item_circle_image_38dp, parent, false);
                viewHolder = new ChatListMembersVH(viewV);
                break;
        }

        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case INVISIBLE_ITEM:
                ((ShowMoreVH) holder).bind(Math.abs(memberCount - max_items), groupName, groupId);
                break;
            case VISIBLE_ITEM:
                ((ChatListMembersVH) holder).bind(data.get(position), null);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= max_items)
            return INVISIBLE_ITEM;
        else
            return VISIBLE_ITEM;
    }

    @Override
    public int getItemCount() {
        Log.e("ahmed", "memberCount: " + memberCount);
        Log.e("ahmed", "size: " + data.size());
        int min = Math.min(memberCount, max_items + 1);
        Log.e("ahmed", "itemCount: " + min);
        return min;
    }

    public void add(Member member) {
        for (Member m : data) {
            if (m.getId().equals(member.getId())) {
                return;
            }
        }

        data.add(member);
        memberCount++;
        notifyItemInserted(data.size() - 1);
        notifyDataSetChanged();
    }

    public void remove(Member member) {
        Member tmp = null;

        for (Member m : data) {
            if (m.getId().equals(member.getId())) {
                tmp = m;
                break;
            }
        }

        if (tmp != null) {
            int position = data.indexOf(tmp);
            if (position > -1) {
                memberCount--;
                data.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        }
    }

    public static class ShowMoreVH extends RecyclerView.ViewHolder {
        private TextView remainingMembers;

        public ShowMoreVH(@NonNull View itemView) {
            super(itemView);
            remainingMembers = itemView.findViewById(R.id.remainingMembers);
        }

        @SuppressLint("SetTextI18n")
        public void bind(int remaining, String groupName, String id) {
            remainingMembers.setText("+"+remaining);
            itemView.setOnClickListener(v -> {
                Navigator.getBackstack(Objects.requireNonNull(itemView.getContext()))
                        .goTo(MoreMembersKey.create(groupName, id, true));
            });
        }
    }
}