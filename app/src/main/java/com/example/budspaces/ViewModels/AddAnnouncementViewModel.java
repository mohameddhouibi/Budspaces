package com.example.budspaces.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.TimelineRequests;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAnnouncementViewModel extends ViewModel {
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isAdded = new MutableLiveData<>();

    private String token;

    public AddAnnouncementViewModel() {
        this.token = Cache.getInstance().getSession("auth-token");
        isAdded.setValue(false);
    }

    public LiveData<Boolean> getIsAdded() {
        return isAdded;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void addAnnouncement(String title, String content, String groupId, String groupName, String groupPicture) {
        String queryBody = TimelineRequests.setAnnouncement(title, content, groupId, groupName, groupPicture).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
        .addAnnouncement(token, body)
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
                    isAdded.postValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
