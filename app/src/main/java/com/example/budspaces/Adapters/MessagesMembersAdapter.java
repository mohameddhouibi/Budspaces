package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Enums.PostType;
import com.example.budspaces.Listeners.OnChatMemberListener;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.User;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.LoadingVH;
import com.example.budspaces.Views.ViewHolders.Messages.ChatListMembersVH;
import com.example.budspaces.Views.ViewHolders.NewPersonVH;

import java.util.ArrayList;
import java.util.List;

public class MessagesMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int PERSON = 2915;
    private final int LOADING = 2000;

    private List<Member> data;
    private OnChatMemberListener listener;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private String errorMsg;

    public MessagesMembersAdapter(OnChatMemberListener listener) {
        this.data = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case PERSON:
                View view = inflater.inflate(R.layout.item_circle_image_40dp, parent, false);
                viewHolder = new ChatListMembersVH(view);
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
            case PERSON:
                ((ChatListMembersVH)holder).bind(data.get(position), listener);
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
        return PERSON;
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
        Member footer = new Member("footer");
        add(footer);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

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