package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Enums.PostType;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.AnnouncementVH;
import com.example.budspaces.Views.ViewHolders.GroupVH;
import com.example.budspaces.Views.ViewHolders.LoadingVH;
import com.example.budspaces.Views.ViewHolders.WallEventVH;
import com.example.budspaces.Views.ViewHolders.NewPersonVH;

import java.util.ArrayList;
import java.util.List;

public class HomeWallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int PERSON = 2915;
    private final int ANNOUNCEMENT = 2910;
    private final int EVENT = 2905;
    private final int GROUP_LIST = 2895;
    private final int LOADING = 2000;

    private List<HomeModel> data;
    private boolean isHeaderVisisble = true;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private String errorMsg;

    private GroupVH groupsHolder;

    public HomeWallAdapter() {
        data = new ArrayList<>();
        addHeader();
    }

    public HomeWallAdapter(boolean isHeaderVisisble) {
        this.isHeaderVisisble = isHeaderVisisble;
        data = new ArrayList<>();
        addHeader();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case GROUP_LIST:
                View viewGL = inflater.inflate(R.layout.recycler_view_layout, parent, false);
                viewHolder = new GroupVH(viewGL);
                break;
            case ANNOUNCEMENT:
                View viewAnnounecement = inflater.inflate(R.layout.item_home_announcement, parent, false);
                viewHolder = new AnnouncementVH(viewAnnounecement);
                break;
            case EVENT:
                View viewEvent = inflater.inflate(R.layout.item_home_event, parent, false);
                viewHolder = new WallEventVH(viewEvent);
                break;
            case PERSON:
                View viewPerson = inflater.inflate(R.layout.item_home_new_person, parent, false);
                viewHolder = new NewPersonVH(viewPerson);
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
            case GROUP_LIST:
                groupsHolder = (GroupVH)holder;
                break;
            case ANNOUNCEMENT:
                ((AnnouncementVH)holder).bind(getItem(position), isHeaderVisisble);
                break;
            case EVENT:
                ((WallEventVH)holder).bind(getItem(position), isHeaderVisisble);
                break;
            case PERSON:
                ((NewPersonVH)holder).bind(getItem(position), isHeaderVisisble);
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (getItem(position).getType()) {
            case header:
                return GROUP_LIST;
            case announcement:
                return ANNOUNCEMENT;
            case event:
                return EVENT;
            case person:
                return PERSON;
            case footer: default:
                return LOADING;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void bindGroups(List<SimplifiedGroup> groups) {
        if (groupsHolder != null) {
            groupsHolder.bind(groups);
        }
    }

    public void add(HomeModel r) {
        data.add(r);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(List<HomeModel> newPosts) {
        if (newPosts == null)
            return;

        for (HomeModel result : newPosts) {
            add(result);
        }
    }

    public void remove(HomeModel r) {
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
        addHeader();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    private void addHeader() {
        HomeModel header = new HomeModel();
        header.setType(PostType.header);
        add(header);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        HomeModel footer = new HomeModel();
        footer.setType(PostType.footer);
        add(footer);
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = data.size() - 1;
        HomeModel result = getItem(position);

        if (result != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public HomeModel getItem(int position) {
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