package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.ChipsVH;
import com.example.budspaces.Views.ViewHolders.InterestSearchVH;

import java.util.List;

public class ProfileChipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> data;

    public ProfileChipsAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_profile_chips, parent, false);

        return new ChipsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ChipsVH)holder).bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}