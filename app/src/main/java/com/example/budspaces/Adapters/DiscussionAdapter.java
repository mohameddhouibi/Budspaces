package com.example.budspaces.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Enums.PostType;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Models.Room;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.CategoryVH;
import com.example.budspaces.Views.ViewHolders.DiscussionVH;
import com.example.budspaces.Views.ViewHolders.LoadingVH;

import java.util.ArrayList;
import java.util.List;

public class DiscussionAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int CHAT = 1993;
    private final int LOADING = 2000;

    private List<Room> data;
    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private String errorMsg;

    public DiscussionAdapter() {
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case CHAT:
                View view = inflater.inflate(R.layout.item_discussion_list, parent, false);
                viewHolder = new DiscussionVH(view);
                break;
            case LOADING:
                View viewLoading = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(viewLoading);
                break;
        }

        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case CHAT:
                DiscussionVH discussionVH = (DiscussionVH) holder;
                discussionVH.bind(data.get(position));
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getId().equals("footer")) {
            return LOADING;
        }
        return CHAT;
    }

    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(Room r) {
        data.add(r);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(List<Room> newPosts) {
        for (Room result : newPosts) {
            add(result);
        }
    }

    public void remove(Room r) {
        int position = data.indexOf(r);
        if (position > -1) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        Room footer = new Room();
        footer.setId("footer");
        add(footer);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = data.size() - 1;
        Room result = getItem(position);

        if (result != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Room getItem(int position) {
        return data.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(data.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }
}