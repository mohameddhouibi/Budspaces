package com.example.budspaces.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.MoreMembersAdapter;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Utils.PaginationScrollListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MoreMembersViewModel extends ViewModel {
    private boolean isLoading = false;
    private boolean isLastPage = true;

    private static final int START_ITEM = 0;
    private static int PAGE_SIZE = 10;
    private int currentStartItem = START_ITEM;

    private MoreMembersAdapter adapter;

    private MutableLiveData<List<Member>> members = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private String id;
    private boolean isGroup;

    public MoreMembersViewModel(String id, boolean isGroup) {
        this.id = id;
        members.setValue(new ArrayList<>());
        this.adapter = new MoreMembersAdapter();
        this.isGroup = isGroup;

        if (isGroup)
            loadGroupNextPage(true);
        else
            loadEventNextPage(true);
    }

    public void doRefresh() {
        isLastPage = true;
        adapter.clear();
        adapter.notifyDataSetChanged();

        if (isGroup)
            loadGroupNextPage(true);
        else
            loadEventNextPage(true);
    }

    public void setRecyclerView(RecyclerView recyclerView, LinearLayoutManager manager) {
        recyclerView.setAdapter(adapter);

        PaginationScrollListener paginationScrollListener = new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentStartItem++;// PAGE_SIZE;

                if (isGroup)
                    loadGroupNextPage(false);
                else
                    loadEventNextPage(false);
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

    private void loadGroupNextPage(boolean isFirstPage) {
        if (isFirstPage)
            currentStartItem = START_ITEM;

        AppRetrofit.getInstance().getService()
            .getGroupMembers(id, currentStartItem, PAGE_SIZE, false)
            .enqueue(new Callback<List<Member>>() {
                @Override
                public void onResponse(@NotNull Call<List<Member>> call, @NotNull Response<List<Member>> response) {
                    loadPage(response, isFirstPage);
                }

                @Override
                public void onFailure(@NotNull Call<List<Member>> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
    }

    private void loadEventNextPage(boolean isFirstPage) {
        if (isFirstPage)
            currentStartItem = START_ITEM;

        AppRetrofit.getInstance().getService()
            .getEventMembers(id, currentStartItem, PAGE_SIZE, false)
            .enqueue(new Callback<List<Member>>() {
                @Override
                public void onResponse(@NotNull Call<List<Member>> call, @NotNull Response<List<Member>> response) {
                    loadPage(response, isFirstPage);
                }

                @Override
                public void onFailure(@NotNull Call<List<Member>> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
    }

    private void loadPage(Response<List<Member>> response, boolean isFirst) {
        List<Member> results = fetchResults(response);

        if (isFirst) {
            isLastPage = false;
        } else {
            adapter.removeLoadingFooter();
            isLoading = false;
        }

        adapter.addAll(results);

        if (results == null || (!isFirst && results.size() < PAGE_SIZE))
            isLastPage = true;
        else
            adapter.addLoadingFooter();
    }

    /**
     * @param response extracts List<{@link Member>} from response
     */
    private List<Member> fetchResults(Response<List<Member>> response) {
        return response.body();
    }
}