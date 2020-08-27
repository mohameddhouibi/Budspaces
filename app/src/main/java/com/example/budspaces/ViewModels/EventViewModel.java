package com.example.budspaces.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventViewModel extends ViewModel {
    private MutableLiveData<Boolean> isJoined = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLeft = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDeleted = new MutableLiveData<>();
    private MutableLiveData<Event> event = new MutableLiveData<>();
    private MutableLiveData<List<Member>> members = new MutableLiveData<>();
    private MutableLiveData<Member> user = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Event> events = new MutableLiveData<>();

    public EventViewModel() {
        isJoined.setValue(false);
        isLeft.setValue(false);
        isDeleted.setValue(false);
        event.setValue(null);
        members.setValue(new ArrayList<>());
    }

    public LiveData<Member> getUser() { return user; }

    public MutableLiveData<Event> getEvents() {
        return events;
    }

    public MutableLiveData<Boolean> getIsJoined() {
        return isJoined;
    }

    public MutableLiveData<Boolean> getIsLeft() {
        return isLeft;
    }

    public MutableLiveData<Boolean> getIsDeleted() {
        return isDeleted;
    }

    public LiveData<Event> getevent() {
        return event;
    }
    public MutableLiveData<List<Member>> getMembers() {
        return members;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadEvent(String eventID) {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
            .getEvent(token, eventID)
            .enqueue(new Callback<Event>() {
                @Override
                public void onResponse(@NotNull Call<Event> call, @NotNull Response<Event> response) {
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
                        event.postValue(response.body());
                    }
                }

                @Override
                public void onFailure(@NotNull Call<Event> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
    }
    public void loadMembers(String eventId, int limit) {
        AppRetrofit.getInstance().getService()
            .getEventMembers(eventId, 0, limit, true)
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
//                        adapter.showRetry(true, fetchErrorMessage(t));
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

    public void joinEvent(String EventID) {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
                .joinEvent(token, EventID)
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
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    public void leaveEvent(String EventID) {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
                .leaveEvent(token, EventID)
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
                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    public void deleteEvent(String EventID) {
        String token = Cache.getInstance().getSession("auth-token");
        AppRetrofit.getInstance().getService()
                .deleteEvent(token, EventID)
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