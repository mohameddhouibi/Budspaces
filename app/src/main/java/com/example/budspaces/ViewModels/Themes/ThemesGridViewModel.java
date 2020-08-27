package com.example.budspaces.ViewModels.Themes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Models.Theme;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.ThemesRequests;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThemesGridViewModel extends ViewModel {
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Theme>> themes = new MutableLiveData<>();

    public MutableLiveData<List<Theme>> getThemes() {
        return themes;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public ThemesGridViewModel() { }

    public void getThemesQuery(int page, int limit) {
        AppRetrofit.getInstance().getService()
        .getThemes(page, limit)
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