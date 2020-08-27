package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Listeners.OnCategorySelectedListener;
import com.example.budspaces.Models.Theme;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.CategoryVH;

import java.util.List;

public class CategoryChooserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Theme> themes;
    private OnCategorySelectedListener listener;
    public CategoryChooserAdapter(List<Theme> themes, OnCategorySelectedListener listener) {
        this.themes = themes;
        this.listener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_category, parent, false);
        return new CategoryVH(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CategoryVH) holder).bind(themes.get(position), listener);
    }
    @Override
    public int getItemCount() {
        return themes.size();
    }
}
