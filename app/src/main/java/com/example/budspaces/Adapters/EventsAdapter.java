package com.example.budspaces.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Enums.TitleType;
import com.example.budspaces.Models.Event;
import com.example.budspaces.R;
import com.example.budspaces.Views.ViewHolders.LoadingVH;
import com.example.budspaces.Views.ViewHolders.SearchEventVH;
import com.example.budspaces.Views.ViewHolders.TitleVH;

import java.util.ArrayList;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int EARLY_TXT = 0;
    private final int MONTH_TXT = 1;
    private final int EVENT_LST = 2;
    private final int LOADING = 2000;

    private List<Event> data;

    private boolean retryPageLoad = false;
    private String errorMsg;

    public EventsAdapter() {
        this.data = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case EARLY_TXT:
            case MONTH_TXT:
                View viewGT = inflater.inflate(R.layout.item_text_view_search_title, parent, false);
                viewHolder = new TitleVH(viewGT);
                break;
            case EVENT_LST:
                View viewEI = inflater.inflate(R.layout.item_search_result_event, parent, false);
                viewHolder = new SearchEventVH(viewEI);
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
            case MONTH_TXT:
                TitleVH titleMVH = (TitleVH) holder;
                titleMVH.bind(TitleType.this_month);
                break;
            case EARLY_TXT:
                TitleVH titlePVH = (TitleVH) holder;
                titlePVH.bind(TitleType.earlier);
                break;
            case EVENT_LST:
                SearchEventVH eventVH = (SearchEventVH) holder;
                eventVH.bind(data.get(position));
                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;
                loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (getItem(position).getId()) {
            case "month":
                return MONTH_TXT;
            case "past":
                return EARLY_TXT;
            case "footer":
                return LOADING;
        }

        return EVENT_LST;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /*
        Helpers - Pagination
   _________________________________________________________________________________________________
    */

    public void add(Event r) {
        data.add(r);
        notifyItemInserted(data.size() - 1);
    }

    public void addAll(List<Event> newPosts) {
        if (newPosts == null)
            return;
        for (Event result : newPosts) {
            add(result);
        }
    }

    public void remove(Event r) {
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

    public void addMonthHeader() {
        Event header = new Event();
        header.setId("month");
        add(header);
    }

    public void addPastHeader() {
        Event header = new Event();
        header.setId("past");
        add(header);
    }

    public void addLoadingFooter() {
        Event footer = new Event();
        footer.setId("footer");
        add(footer);
    }

    public void removeLoadingFooter() {

        int position = data.size() - 1;
        Event result = getItem(position);

        if (result != null) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Event getItem(int position) {
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
