package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Models.Event;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.EventVH;
import com.example.budspaces.Views.ViewHolders.TimelineVH;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Event> events;
    private EventVH eventsHolder;
    private List<Event> data;
    private String errorMsg;

    public TimelineAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_timeline, parent, false);
        return new TimelineVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TimelineVH)holder).bind(events.get(position));
    }
    @Override
    public int getItemCount() {
        return events.size();
    }

}
