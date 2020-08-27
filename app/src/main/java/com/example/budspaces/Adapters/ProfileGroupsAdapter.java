package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.Navigation.Keys.CreateModifGroupKey;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.ListGroupVH;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.List;
import java.util.Objects;

public class ProfileGroupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int GROUP_LIST = 2895;
    private final int GROUP_ADD = 2899;

    private List<SimplifiedGroup> data;
    private boolean showAdd;

    public ProfileGroupsAdapter(List<SimplifiedGroup> data, boolean showAdd) {
        this.data = data;
        this.showAdd = showAdd;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case GROUP_ADD:
                View viewAGVH = inflater.inflate(R.layout.item_add_new_group, parent, false);
                viewHolder = new AddGroupVH(viewAGVH);
                break;
            case GROUP_LIST:
                View viewGVH = inflater.inflate(R.layout.item_circle_image_67dp, parent, false);
                viewHolder = new ListGroupVH(viewGVH);
                break;
        }

        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int count = showAdd? 1 : 0;
        if (getItemViewType(position) == GROUP_LIST)
            ((ListGroupVH)holder).bind(data.get(position - count));
        else
            ((AddGroupVH)holder).bind();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showAdd) {
            return GROUP_ADD;
        }
        return GROUP_LIST;
    }

    @Override
    public int getItemCount() {
        int count = data.size();
        if (showAdd)
            count++;

        return count;
    }

    public static class AddGroupVH extends RecyclerView.ViewHolder {

        public AddGroupVH(@NonNull View itemView) {
            super(itemView);
        }

        public void bind() {
            itemView.setOnClickListener(v -> {
                Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(CreateModifGroupKey.create(InterestType.group_new,""));
            });
        }
    }
}