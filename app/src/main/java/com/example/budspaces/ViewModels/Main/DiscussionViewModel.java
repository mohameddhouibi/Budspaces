package com.example.budspaces.ViewModels.Main;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.DiscussionAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Models.Room;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Utils.PaginationScrollListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussionViewModel extends ViewModel {
    private static final String TAG = "DiscussionViewModel";

    private boolean isLoading = false;
    private boolean isLastPage = true;

    private static final int START_ITEM = 0;
    private static int PAGE_SIZE = 10;
    private int currentStartItem = START_ITEM;

    private DiscussionAdapter adapter;
    private String token;

    public DiscussionViewModel() {
        this.token = Cache.getInstance().getSession("auth-token");
        this.adapter = new DiscussionAdapter();
        loadNextPage(true);
    }

    public void doRefresh() {
        isLastPage = true;
        adapter.clear();
        adapter.notifyDataSetChanged();
        loadNextPage(true);
    }

    public void setRecyclerView(RecyclerView chatList, LinearLayoutManager manager) {
        chatList.setAdapter(adapter);

        PaginationScrollListener paginationScrollListener = new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentStartItem++;// PAGE_SIZE;

                loadNextPage(false);
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

        chatList.addOnScrollListener(paginationScrollListener);
    }

    private void loadNextPage(boolean isFirstPage) {
        if (isFirstPage)
            currentStartItem = START_ITEM;
        AppRetrofit.getInstance().getService()
        .getChatRooms(token, currentStartItem, PAGE_SIZE)
        .enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(@NotNull Call<List<Room>> call, @NotNull Response<List<Room>> response) {
                loadPage(response, isFirstPage);
            }

            @Override
            public void onFailure(@NotNull Call<List<Room>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void loadPage(Response<List<Room>> response, boolean isFirst) {
        List<Room> results = fetchResults(response);

        if (isFirst) {
            isLastPage = false;
        } else {
            adapter.removeLoadingFooter();
            isLoading = false;
        }

        adapter.addAll(results);

        if (!isFirst && results.size() < PAGE_SIZE)
            isLastPage = true;
        else
            adapter.addLoadingFooter();
    }

    /**
     * @param response extracts List<{@link Room>} from response
     */
    private List<Room> fetchResults(Response<List<Room>> response) {
        return response.body();
    }
}