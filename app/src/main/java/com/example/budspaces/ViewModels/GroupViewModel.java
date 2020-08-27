package com.example.budspaces.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.HomeWallAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.GroupRequests;
import com.example.budspaces.Network.TimelineRequests;
import com.example.budspaces.Utils.PaginationScrollListener;
import com.google.firebase.messaging.FirebaseMessaging;

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

public class GroupViewModel extends ViewModel {
    private MutableLiveData<Boolean> isJoined = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLeft = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
    private MutableLiveData<Group> group = new MutableLiveData<>();
    private MutableLiveData<Member> user = new MutableLiveData<>();
    private MutableLiveData<List<Member>> members = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Event>> events = new MutableLiveData<>();

    private HomeWallAdapter adapter;
    private String token;
    private String groupID;

    private static final int START_ITEM = 0;
    private static int PAGE_SIZE = 10;
    private int currentStartItem = START_ITEM;

    private boolean isLoading = false;
    private boolean isLastPage = true;

    public GroupViewModel(String groupID) {
        this.groupID = groupID;
        isJoined.setValue(false);
        isLeft.setValue(false);
        isDeleted.setValue(false);
        group.setValue(null);
        members.setValue(new ArrayList<>());
        events.setValue(new ArrayList<>());
        this.token = Cache.getInstance().getSession("auth-token");
        this.adapter = new HomeWallAdapter(false);
        loadNextPage(true);

    }

    public MutableLiveData<List<Event>> getEvents() {
        return events;
    }

    public LiveData<Member> getUser() { return user; }


    public MutableLiveData<Boolean> getIsJoined() {
        return isJoined;
    }

    public MutableLiveData<Boolean> getIsLeft() {
        return isLeft;
    }

    public MutableLiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public LiveData<Group> getGroup() {
        return group;
    }
    public LiveData<List<Member>> getMembers() {
        return members;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadGroup() {
        AppRetrofit.getInstance().getService()
        .getGroup(token, groupID)
        .enqueue(new Callback<Group>() {
            @Override
            public void onResponse(@NotNull Call<Group> call, @NotNull Response<Group> response) {
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
                    group.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<Group> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void loadMembers(int limit) {

        AppRetrofit.getInstance().getService()
        .getGroupMembers(groupID, 0, limit, true)
        .enqueue(new Callback<List<Member>>() {
            @Override
            public void onResponse(@NotNull Call<List<Member>> call, @NotNull Response<List<Member>> response) {
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
                    members.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Member>> call, @NotNull Throwable t) {
                t.printStackTrace();
//                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void loadMyMember() {
        AppRetrofit.getInstance().getService()
                .getChatUser(Cache.getInstance().getSession("userId"))
                .enqueue(new Callback<Member>() {
                    @Override
                    public void onResponse(@NotNull Call<Member> call, @NotNull Response<Member> response) {
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
                            user.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Member> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    public void loadEvents(String groupId) {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .getGroupEvents(token, groupId)
        .enqueue(new Callback<List<Event>>() {

            @Override
            public void onResponse(@NotNull Call<List<Event>> call, @NotNull Response<List<Event>> response) {
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
                    events.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Event>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void joinGroup() {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .joinGroup(token, groupID)
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
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
                    errorMessage.postValue(response.body().getResponse());
                    isJoined.postValue(true);
                    isLeft.postValue(false);
                    FirebaseMessaging.getInstance().subscribeToTopic(groupID);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void leaveGroup() {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .leaveGroup(token, groupID)
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
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
                    errorMessage.postValue(response.body().getResponse());
                    isLeft.postValue(true);
                    isJoined.postValue(false);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(groupID);
                }
            }
            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void deleteGroup() {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .deleteGroup(token, groupID)
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
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
                    errorMessage.postValue(response.body().getResponse());
                    isDeleted.postValue(true);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(groupID);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
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
        //Log.d(TAG, "loadNextPage: " + currentStartItem);

        if (isFirstPage)
            currentStartItem = START_ITEM;

//        String queryBody = TimelineRequests.getHomeQuery(currentStartItem, PAGE_SIZE).toString();
//        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
                .getGroupTimeline(token, groupID, currentStartItem, PAGE_SIZE)
                .enqueue(new Callback<List<HomeModel>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<HomeModel>> call, @NotNull Response<List<HomeModel>> response) {
                        if (response.isSuccessful())
                            loadPage(response, isFirstPage);
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<HomeModel>> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    private void loadPage(Response<List<HomeModel>> response, boolean isFirst) {
    /*Log.i(TAG, "onResponse: " + currentStartItem
            + (response.raw().cacheResponse() != null ? "Cache" : "Network"));*/

        List<HomeModel> results = fetchResults(response);

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
     * @param response extracts List<{@link HomeModel>} from response
     */
    private List<HomeModel> fetchResults(Response<List<HomeModel>> response) {
        return response.body();
    }
}