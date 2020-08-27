package com.example.budspaces.ViewModels.Main;


import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Adapters.HomeWallAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.SearchResult;
import com.example.budspaces.Models.SearchSettings;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Models.Theme;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.GroupRequests;
import com.example.budspaces.Network.SearchRequests;
import com.example.budspaces.Network.ThemesRequests;
import com.google.firebase.storage.StorageReference;

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

public class DiscoverViewModel extends ViewModel {

    private MutableLiveData<SearchResult> searchResult = new MutableLiveData<>();
    private MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private MutableLiveData<List<Group>> groups = new MutableLiveData<>();
    private MutableLiveData<List<Theme>> themes = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<SearchSettings> settings = new MutableLiveData<>();


    public DiscoverViewModel() {
        groups.setValue(new ArrayList<>());
        events.setValue(new ArrayList<>());
        settings.setValue(null);
        searchResult.setValue(null);
    }

    public MutableLiveData<SearchResult> getSearchResult() {return searchResult;}
    public LiveData<List<Event>> getEvents() {return events;}
    public LiveData<List<Group>> getGroups() {return groups;}
    public LiveData<String> getErrorMessage() {return errorMessage;}
    public LiveData<SearchSettings> getSettings() {return settings;}
    public MutableLiveData<List<Theme>> getThemes() {return themes;}

    public void AddSearchFilter(SearchSettings searchsettings) {
        String queryBody = SearchRequests.AddFilterRequest(searchsettings).toString();
        Log.e("test",queryBody);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        Log.e("test",body.toString());

        String token = Cache.getInstance().getSession("auth-token");
        AppRetrofit.getInstance().getService()
                .AddSearchFilter(token, body)
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
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    public void GetSearchFilter() {

        String token = Cache.getInstance().getSession("auth-token");
        AppRetrofit.getInstance().getService()
                .GetSearchFilter(token)
                .enqueue(new Callback<SearchSettings>() {
                    @Override
                    public void onResponse(@NotNull Call<SearchSettings> call, @NotNull Response<SearchSettings> response) {
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
                            settings.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<SearchSettings> call, @NotNull Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    public void SearchThroughSettings(SearchSettings searchsettings, String name, Integer Limit) {
        String queryBody = SearchRequests.searchRequest(searchsettings,name,Limit).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
                .Search(token, body)
                .enqueue(new Callback<SearchResult>() {
                    @Override
                    public void onResponse(@NotNull Call<SearchResult> call, @NotNull Response<SearchResult> response) {
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
                            searchResult.postValue(response.body());
                            events.postValue(response.body().getEvents());
                            Log.e("ev","here");
                            groups.postValue(response.body().getGroups());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<SearchResult> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    public void getThemesQuery() {
        AppRetrofit.getInstance().getService()
                .getThemes(1, 9)
                .enqueue(new Callback<List<Theme>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<Theme>> call, @NotNull Response<List<Theme>> response) {
                        if (!response.isSuccessful()) {
                            try {
                                assert response.errorBody() != null;
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                errorMessage.postValue(jObjError.getJSONObject("error").getString("message"));
                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            themes.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<Theme>> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }
}