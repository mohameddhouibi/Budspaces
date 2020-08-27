package com.example.budspaces.ViewModels.Auth;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecoverViewModel extends ViewModel {
    private MutableLiveData<Boolean> isMailSent = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RecoverViewModel() {
        isMailSent.setValue(false);
    }

    public LiveData<Boolean> getIsMailSent() { return isMailSent; }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void recoverPassword(String mail) {
        AppRetrofit.getInstance()
        .getService()
        .recoverPassword(mail)
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
}