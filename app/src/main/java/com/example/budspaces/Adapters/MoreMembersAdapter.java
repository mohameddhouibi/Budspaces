package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Models.Member;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.ListMembersVH;
import com.example.budspaces.Views.ViewHolders.LoadingVH;

import java.util.ArrayList;
import java.util.List;

public class MoreMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int MEMBERS_LIST = 2895;
    private final int LOADING = 2000;

    private List<Member> data;

    private boolean retryPageLoad = false;

    private String errorMsg;

    public MoreMembersAdapter() {
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case MEMBERS_LIST:
                View view = inflater.inflate(R.layout.item_member, parent, false);
                viewHolder = new ListMembersVH(view);
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
            case MEMBERS_LIST:
                ListMembersVH membersVH = (ListMembersVH) holder;
                membersVH.bind(getItem(position));
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getId().equals("footer")) {
            return LOADING;
        }
        return MEMBERS_LIST;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(Member r) {
        data.add(r);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(List<Member> newPosts) {
        if (newPosts == null)
            return;
        for (Member result : newPosts) {
            add(result);
        }
    }

    public void remove(Member r) {
        int position = data.indexOf(r);
        if (position > -1) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        Member footer = new Member("footer");
        add(footer);
    }

    public void removeLoadingFooter() {

        int position = data.size() - 1;
        Member result = getItem(position);

        if (result != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Member getItem(int position) {
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
