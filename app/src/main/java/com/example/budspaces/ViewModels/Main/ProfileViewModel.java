package com.example.budspaces.ViewModels.Main;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.Models.User;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.ProfileRequests;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends ViewModel {
    private MutableLiveData<User> user = new MutableLiveData<>();
    private MutableLiveData<List<SimplifiedGroup>> groups = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoggedOut = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private StorageReference storageRef;

    public ProfileViewModel() {
        user.setValue(null);
        groups.setValue(new ArrayList<>());
        isLoggedOut.setValue(false);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    public LiveData<Boolean> getIsLoggedout() { return isLoggedOut; }
    public LiveData<User> getUser() {
        return user;
    }
    public MutableLiveData<List<SimplifiedGroup>> getGroups() {
        return groups;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadProfile(String userId) {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .getUserProfile(token, userId)
        .enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NotNull Call<User> call, @NotNull Response<User> response) {
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
            public void onFailure(@NotNull Call<User> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void loadHostGroups(String userID) {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .getHostGroups(token, userID)
        .enqueue(new Callback<List<SimplifiedGroup>>() {
            @Override
            public void onResponse(@NotNull Call<List<SimplifiedGroup>> call, @NotNull Response<List<SimplifiedGroup>> response) {
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
                    groups.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<SimplifiedGroup>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void loadMemberGroups(String userID) {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
                .getMemberGroups(token, userID)
                .enqueue(new Callback<List<SimplifiedGroup>>() {
                    @Override
                    public void onResponse(@NotNull Call<List<SimplifiedGroup>> call, @NotNull Response<List<SimplifiedGroup>> response) {
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
                            groups.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<List<SimplifiedGroup>> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    public void uploadPicture(Bitmap bitmap, String id) {
        // Create a reference to 'groups/groupName.jpg'
        StorageReference imageRef = storageRef.child("Users/"+id+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);
        upload(imageRef, uploadTask);
    }

    private void upload(StorageReference imageRef, UploadTask uploadTask) {
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String pictureLink = Objects.requireNonNull(task.getResult()).toString();

                updateProfilePicture(pictureLink);
            } else {
                errorMessage.postValue("could not upload picture");
                // Handle failures
            }
        });
    }

    private void updateProfilePicture(String link) {
        String queryBody = ProfileRequests.updatePicture(link).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .updateProfilePicture(token, body)
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
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void logout() {
        String token = Cache.getInstance().getSession("auth-token");
        unsubscribeToTopics(token);

        AppRetrofit.getInstance().getService()
        .logout(token)
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

                    Cache.getInstance().clearSession("auth-token");
                    Cache.getInstance().clearSession("userId");

                    isLoggedOut.postValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void unsubscribeToTopics(String token) {
        AppRetrofit.getInstance()
        .getService()
        .getTopics(token)
        .enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(@NotNull Call<List<String>> call, @NotNull Response<List<String>> response) {
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
                    for (String topic : response.body()) {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<String>> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}