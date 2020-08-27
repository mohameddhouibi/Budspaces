package com.example.budspaces.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.Message;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Models.Setting;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.SearchRequests;
import com.example.budspaces.Network.ThemesRequests;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingViewModel extends ViewModel {
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Setting> settings = new MutableLiveData<>();

    private String token;

    public SettingViewModel() {
        this.token = Cache.getInstance().getSession("auth-token");
        settings.setValue(null);
    }

    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Setting> getSettings() { return settings; }

    public void loadSettings() {
        AppRetrofit.getInstance().getService()
        .getPreferences(token)
        .enqueue(new Callback<Setting>() {
            @Override
            public void onResponse(@NotNull Call<Setting> call, @NotNull Response<Setting> response) {
                if (response.isSuccessful())
                    settings.postValue(response.body());
                else
                    errorMessage.postValue(response.errorBody().toString());
            }

            @Override
            public void onFailure(@NotNull Call<Setting> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void saveSettings(Setting setting) {
        String queryBody = ThemesRequests.saveSettings(setting).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .setPreferences(token, body)
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
                if (!response.isSuccessful()) {
                    assert response.errorBody() != null;
                    errorMessage.postValue(response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}