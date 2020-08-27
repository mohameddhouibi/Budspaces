package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.ListGroupVH;

import java.util.List;

public class HomeGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<SimplifiedGroup> data;

    public HomeGroupsAdapter(List<SimplifiedGroup> groups) {
        this.data = groups;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_circle_image_67dp, parent, false);

        return new ListGroupVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListGroupVH)holder).bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
