package com.example.budspaces.ViewModels.Themes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.Count;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Models.Theme;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.AuthRequests;
import com.example.budspaces.Network.GroupRequests;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyInterestsViewModel extends ViewModel {
    private MutableLiveData<Boolean> isValid = new MutableLiveData<>();
    private MutableLiveData<Integer> themesCount = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public ModifyInterestsViewModel() {
        isValid.setValue(false);
        themesCount.setValue(0);
        getCount();
    }

    public MutableLiveData<Integer> getThemesCount() {
        return themesCount;
    }
    public LiveData<Boolean> getIsValid() {
        return isValid;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void updateInterests(String groupID, List<Theme> themes) {
        String queryBody = GroupRequests.updateGroupInterests(groupID, themes).toString();
        String token = Cache.getInstance().getSession("auth-token");
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
        .updateGroupInterests(token, body)
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
                    isValid.postValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    private void getCount() {
        AppRetrofit.getInstance().getService()
        .getThemesCount()
        .enqueue(new Callback<Count>() {
            @Override
            public void onResponse(@NotNull Call<Count> call, @NotNull Response<Count> response) {
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
                    themesCount.postValue(response.body().getResponse());
                }
            }

            @Override
            public void onFailure(@NotNull Call<Count> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void updateInterests(List<Theme> themes) {
        String token = Cache.getInstance().getSession("auth-token");
        String queryBody = AuthRequests.getCategoriesRequest(themes).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
        .updateInterests(token, body)
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
                if (!response.isSuccessful()) {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        errorMessage.postValue(jObjError.getJSONObject("error").getString("message"));
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    assert response.body() != null;
                    errorMessage.postValue(response.body().getResponse());
                    isValid.setValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }
}