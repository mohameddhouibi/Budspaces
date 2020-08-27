package com.example.budspaces.ViewModels.Main;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.ProfileRequests;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyProfileViewModel extends ViewModel {
    private MutableLiveData<Boolean> isSaved = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private StorageReference storageRef;
    private boolean uploadPic = false;
    private boolean isUpdated = false;
    private String token;

    public ModifyProfileViewModel() {
        token = Cache.getInstance().getSession("auth-token");
        isSaved.setValue(false);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();
    }

    public LiveData<Boolean> getIsSaved() {
        return isSaved;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void saveChanges(HashMap<String, String> changes) {
        String queryBody = ProfileRequests.updateProfile(changes).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
        .updateProfile(token, body)
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

                    if (uploadPic) {
                        if (isUpdated)
                            isSaved.setValue(true);
                        else
                            isUpdated = true;
                    } else {
                        isSaved.setValue(true);
                        isUpdated = true;
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void uploadPicture(Bitmap bitmap, String id, boolean otherChanges) {
        uploadPic = true;
        // Create a reference to 'groups/groupName.jpg'
        StorageReference imageRef = storageRef.child("Users/"+id+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        upload(imageRef, uploadTask, otherChanges);
    }

    private void upload(StorageReference imageRef, UploadTask uploadTask, boolean otherChanges) {
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String pictureLink = Objects.requireNonNull(task.getResult()).toString();

                updateProfilePicture(pictureLink, otherChanges);
            } else {
                errorMessage.postValue("could not upload picture");
                // Handle failures
            }
        });
    }

    private void updateProfilePicture(String link, boolean otherChanges) {
        String queryBody = ProfileRequests.updatePicture(link).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

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

                    if (isUpdated)
                        isSaved.setValue(true);
                    else {
                        if (otherChanges)
                            isUpdated = true;
                        else
                            isSaved.setValue(true);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void updatePassword(String oldPass, String newPass) {
        AppRetrofit.getInstance().getService()
        .updateProfilePassword(token, oldPass, newPass)
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
            }
        });
    }
}