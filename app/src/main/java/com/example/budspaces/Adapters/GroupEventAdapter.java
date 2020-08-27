package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.User;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.DiscussionVH;
import com.example.budspaces.Views.ViewHolders.GroupEventVH;

import java.util.List;

public class GroupEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Event> data;

    public GroupEventAdapter(List<Event> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_event_layout, parent, false);

        return new GroupEventVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((GroupEventVH) holder).bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}