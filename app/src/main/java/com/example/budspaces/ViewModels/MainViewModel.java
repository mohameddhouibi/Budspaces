package com.example.budspaces.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isMailSent = new MutableLiveData<>();
    private MutableLiveData<Boolean> isVerified = new MutableLiveData<>();

    private String token;

    public MainViewModel() {
        this.token = Cache.getInstance().getSession("auth-token");
        this.isMailSent.setValue(false);
        this.isVerified.setValue(false);
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> getIsMailSent() { return isMailSent; }
    public LiveData<Boolean> getIsVerified() { return isVerified; }

    public void verifyAccount() {
        AppRetrofit.getInstance()
        .getService()
        .verifyAccount(token)
        .enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
                if (!response.isSuccessful()) {
                    assert response.errorBody() != null;
                    errorMessage.postValue(response.errorBody().toString());
                } else {
                    isMailSent.postValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void checkCode(String code) {
        AppRetrofit.getInstance()
        .getService()
        .checkCode(token, code)
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
                    isVerified.postValue(true);
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