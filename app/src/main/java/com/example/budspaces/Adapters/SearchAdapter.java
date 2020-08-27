package com.example.budspaces.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Enums.TitleType;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Group;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.SearchEventVH;
import com.example.budspaces.Views.ViewHolders.SearchGroupVH;
import com.example.budspaces.Views.ViewHolders.TitleVH;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Group> groups;
    private List<Event> events;

    private final int GROUP_TXT = 0;
    private final int GROUP_ITM = 1;
    private final int EVENT_TXT = 2;
    private final int EVENT_ITM = 3;

    public SearchAdapter(List<Group> groups, List<Event> events) {
        this.groups = groups;
        this.events = events;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case GROUP_TXT:
            case EVENT_TXT:
                View viewGT = inflater.inflate(R.layout.item_text_view_search_title, parent, false);
                viewHolder = new TitleVH(viewGT);
                break;
            case GROUP_ITM:
                View viewGI = inflater.inflate(R.layout.item_search_result_group, parent, false);
                viewHolder = new SearchGroupVH(viewGI);
                break;
            case EVENT_ITM:
                View viewEI = inflater.inflate(R.layout.item_search_result_event, parent, false);
                viewHolder = new SearchEventVH(viewEI);
                break;
        }

        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case GROUP_TXT:
                ((TitleVH) holder).bind(TitleType.groups);
                break;
            case EVENT_TXT:
                ((TitleVH) holder).bind(TitleType.events);
                break;
            case GROUP_ITM:
                ((SearchGroupVH) holder).bind(groups.get(position - 1));
                break;
            case EVENT_ITM:
                int gSize = groups.size() > 0 ? groups.size() + 2 : 1;
                ((SearchEventVH) holder).bind(events.get(position - gSize));
                break;
        }
    }

    @Override
    public int getItemCount() {
        int gSize = groups.size() > 0 ? groups.size() + 1 : 0;
        int eSize = events.size() > 0 ? events.size() + 1 : 0;
        return gSize + eSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (groups.size() > 0) {
            if (position == 0)
                return GROUP_TXT;
            else if (position <= groups.size())
                return GROUP_ITM;
        }

        if (events.size() > 0) {
            if (position == 0 || position == groups.size() + 1 && groups.size() > 0)
                return EVENT_TXT;
        }
        return EVENT_ITM;
    }
}
