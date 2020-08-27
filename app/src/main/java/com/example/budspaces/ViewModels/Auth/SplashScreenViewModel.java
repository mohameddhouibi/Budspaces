package com.example.budspaces.ViewModels.Auth;

import android.util.Log;

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

public class SplashScreenViewModel extends ViewModel {
    private MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private boolean verified;

    public SplashScreenViewModel() {
        isLoggedIn.setValue(false);
        verified = false;
    }

    public LiveData<Boolean> getIsLoggedIn() { return isLoggedIn; }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public boolean isVerified() { return verified; }

    public void checkIfLoggedIn() {
        String token = Cache.getInstance().getSession("auth-token");

        if (token.isEmpty()) {
            isLoggedIn.postValue(false);
            return;
        }

        AppRetrofit.getInstance().getService()
                .isLoggedIn(token)
                .enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<ServerResponse> call, @NotNull Response<ServerResponse> response) {
                        if (!response.isSuccessful()) {
                            try {
                                assert response.errorBody() != null;
                                String result = response.errorBody().string();
                                errorMessage.postValue(result);

//                                JSONObject jObjError = new JSONObject(response.errorBody().string());
//                                errorMessage.postValue(jObjError.getJSONObject("error").getString("message"));
                            } catch (IOException e){// | JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            assert response.body() != null;
                            errorMessage.postValue(response.body().getResponse());
                            String id = response.body().getResponse();
                            Cache.getInstance().saveSession("userId", id);

                            verified = Boolean.parseBoolean(response.headers().get("verified"));
                            isLoggedIn.postValue(true);
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
