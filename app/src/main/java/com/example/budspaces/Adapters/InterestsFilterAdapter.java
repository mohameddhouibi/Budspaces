package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Listeners.OnInterestCheckedListener;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.InterestSearchVH;

import java.util.List;

public class InterestsFilterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> data;
    private OnInterestCheckedListener listener;

    public InterestsFilterAdapter(List<String> data, OnInterestCheckedListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_search_theme, parent, false);

        return new InterestSearchVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((InterestSearchVH) holder).bind(data.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}