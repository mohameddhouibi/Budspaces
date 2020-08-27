package com.example.budspaces.ViewModels.Main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.HomeWallAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.TimelineRequests;
import com.example.budspaces.Utils.PaginationScrollListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";

    private boolean isLoading = false;
    private boolean isLastPage = true;

    private static final int START_ITEM = 0;
    private static int PAGE_SIZE = 10;
    private int currentStartItem = START_ITEM;

    private HomeWallAdapter adapter;
    private String token;
    private MutableLiveData<List<SimplifiedGroup>> groups = new MutableLiveData<>();
    private MutableLiveData<Boolean> isEmpty = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public HomeViewModel() {
        this.token = Cache.getInstance().getSession("auth-token");
        this.adapter = new HomeWallAdapter();
        this.isEmpty.setValue(false);
        loadGroup();
        loadNextPage(true);
        groups.setValue(new ArrayList<>());
    }

    public void doRefresh() {
        isLastPage = true;
        adapter.clear();
        adapter.notifyDataSetChanged();

        loadGroup();
        loadNextPage(true);
    }

    public void setRecyclerView(RecyclerView homeList, LinearLayoutManager manager) {
        homeList.setAdapter(adapter);

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

        homeList.addOnScrollListener(paginationScrollListener);
    }

    private void loadNextPage(boolean isFirstPage) {

        if (isFirstPage)
            currentStartItem = START_ITEM;
        AppRetrofit.getInstance().getService()
            .getHomeTimeline(token, currentStartItem, PAGE_SIZE)
            .enqueue(new Callback<List<HomeModel>>() {
                @Override
                public void onResponse(@NotNull Call<List<HomeModel>> call, @NotNull Response<List<HomeModel>> response) {
                    loadPage(response, isFirstPage);
                }

                @Override
                public void onFailure(@NotNull Call<List<HomeModel>> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
    }

    private void loadPage(Response<List<HomeModel>> response, boolean isFirst) {
        List<HomeModel> results = fetchResults(response);

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
     * @param response extracts List<{@link HomeModel>} from response
     */
    private List<HomeModel> fetchResults(Response<List<HomeModel>> response) {
        return response.body();
    }

    public LiveData<List<SimplifiedGroup>> getGroups() {
        return groups;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> IsEmpty() { return isEmpty; }

    public void loadGroup() {
        AppRetrofit.getInstance().getService()
        .getMemberGroups(token)
        .enqueue(new Callback<List<SimplifiedGroup>>() {
            @Override
            public void onResponse(@NotNull Call<List<SimplifiedGroup>> call, @NotNull Response<List<SimplifiedGroup>> response) {
                if (!response.isSuccessful()) {
                    try {
                        assert response.errorBody() != null;
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        errorMessage.postValue(jObjError.getJSONObject("error").getString("message"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    assert response.body() != null;
                    adapter.bindGroups(response.body());

                    if (response.body().size() > 0)
                        isEmpty.postValue(false);
                    else
                        isEmpty.postValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<SimplifiedGroup>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}