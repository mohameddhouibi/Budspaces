package com.example.budspaces.ViewModels.Appbar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Adapters.HomeWallAdapter;
import com.example.budspaces.Adapters.TimelineAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.GroupRequests;

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

public class CalendarViewModel extends ViewModel {
    private static final String TAG = "CalendarViewModel";

    private String token;
    private MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<List<Integer>> eventsCount = new MutableLiveData<>();

    public MutableLiveData<List<Event>> getEvents() {return events;}
    public MutableLiveData<String> getErrorMessage() {return errorMessage;}
    public MutableLiveData<List<Integer>> getEventsCount() {return eventsCount;}

    public CalendarViewModel() {
        this.token = Cache.getInstance().getSession("auth-token");
        events.setValue(new ArrayList<>());
        eventsCount.setValue(new ArrayList<>());
    }

    public void loadDateTimline(String date) {
        AppRetrofit.getInstance().getService()
        .getSelectedDayTimeline(token,date)
        .enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(@NotNull Call<List<Event>> call, @NotNull Response<List<Event>> response) {
                events.postValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<List<Event>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void getEventsCountByDay(String firstDay, int daysNb) {
        AppRetrofit.getInstance().getService()
        .getEventsCountByDay(token, firstDay, daysNb)
        .enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(@NotNull Call<List<Integer>> call, @NotNull Response<List<Integer>> response) {
                eventsCount.postValue(response.body());
            }

            @Override
            public void onFailure(@NotNull Call<List<Integer>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}