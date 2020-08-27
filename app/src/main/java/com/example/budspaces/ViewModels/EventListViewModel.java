package com.example.budspaces.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.EventsAdapter;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Utils.PaginationScrollListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventListViewModel extends ViewModel {
    private boolean isLoading = false;
    private boolean isLastPage = true;

    private static final int START_ITEM = 0;
    private static int PAGE_SIZE = 10;
    private int currentStartItem = START_ITEM;

    private EventsAdapter adapter;
    private EventsAdapter nameAdapter;

    private MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private String groupId;
    private boolean actualMonth = true;

    public EventListViewModel(String groupId) {
        this.groupId = groupId;
        this.events.setValue(new ArrayList<>());
        this.adapter = new EventsAdapter();
        this.nameAdapter = new EventsAdapter();
        loadActualMonthEvents(true);
    }

    public void setRecyclerView(RecyclerView recyclerView, LinearLayoutManager manager) {
        recyclerView.setAdapter(adapter);

        PaginationScrollListener paginationScrollListener = new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentStartItem++;// PAGE_SIZE;

                if (actualMonth)
                    loadActualMonthEvents(false);
                else
                    loadPastEvents(currentStartItem == START_ITEM);
            }

            @Override
            public int getTotalPageCount() {
                return PAGE_SIZE;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        };

        recyclerView.addOnScrollListener(paginationScrollListener);
    }

    private void loadActualMonthEvents(boolean isFirstPage) {
        if (isFirstPage)
            currentStartItem = START_ITEM;

        AppRetrofit.getInstance().getService()
        .getGroupEventsByMonth(groupId, currentStartItem, PAGE_SIZE)
        .enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NotNull Call<List<Event>> call, @NotNull Response<List<Event>> response) {
                loadPage(response, isFirstPage);
            }

            @Override
            public void onFailure(@NotNull Call<List<Event>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadPastEvents(boolean isFirstPage) {
        if (isFirstPage)
            currentStartItem = START_ITEM;

        AppRetrofit.getInstance().getService()
        .getGroupPastEvents(groupId, currentStartItem, PAGE_SIZE)
        .enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NotNull Call<List<Event>> call, @NotNull Response<List<Event>> response) {
                loadPage(response, isFirstPage);
            }

            @Override
            public void onFailure(@NotNull Call<List<Event>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadPage(Response<List<Event>> response, boolean isFirst) {
        List<Event> results = fetchResults(response);

        if (isFirst) {
            if (results != null && results.size() > 0) {
                if (adapter.getItemCount() == 0)
                    adapter.addMonthHeader();
                else
                    adapter.addPastHeader();
            }
            isLastPage = false;
        } else {
            adapter.removeLoadingFooter();
            isLoading = false;
        }

        adapter.addAll(results);

        assert results != null;
        if (results.size() < PAGE_SIZE) {
            if (!actualMonth)
                isLastPage = true;
            else {
                actualMonth = false;
                currentStartItem = -1;
            }
        } else
            adapter.addLoadingFooter();
    }

    private void searchEventByName(String name) {
        nameAdapter.clear();

        AppRetrofit.getInstance().getService()
        .getGroupEventsByName(groupId, START_ITEM, PAGE_SIZE, name)
        .enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NotNull Call<List<Event>> call, @NotNull Response<List<Event>> response) {
                List<Event> results = fetchResults(response);
                nameAdapter.addAll(results);
            }

            @Override
            public void onFailure(@NotNull Call<List<Event>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void setAdapter(RecyclerView recyclerView, String name) {
        if (name == null || name.isEmpty())
            recyclerView.setAdapter(adapter);
        else {
            recyclerView.setAdapter(nameAdapter);
            searchEventByName(name);
        }
    }

    /**
     * @param response extracts List<{@link Event>} from response
     */
    private List<Event> fetchResults(Response<List<Event>> response) {
        return response.body();
    }
}