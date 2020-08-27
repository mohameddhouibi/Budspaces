package com.example.budspaces.ViewModels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.GroupRequests;
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

public class CreateGroupViewModel extends ViewModel {
    private MutableLiveData<Boolean> isGroupCreated = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPictureUploaded = new MutableLiveData<>();
    private MutableLiveData<String> groupID = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Group> group = new MutableLiveData<>();
    private StorageReference storageRef;
    private MutableLiveData<Boolean> isSaved = new MutableLiveData<>();
    private boolean uploadPic = false;
    private boolean isUpdated = false;

    public CreateGroupViewModel() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        isGroupCreated.setValue(false);
        isPictureUploaded.setValue(false);
    }

    public LiveData<Boolean> getIsGroupCreated() {
        return isGroupCreated;
    }
    public LiveData<Boolean> getIsPictureUploaded() {
        return isPictureUploaded;
    }
    public LiveData<String> getGroupID() {
        return groupID;
    }
    public LiveData<Group> getGroup() {
        return group;
    }
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    public LiveData<Boolean> getIsSaved() { return isSaved; }

    public void uploadPicture(Bitmap bmp, String name, String groupID) {
        // Create a reference to 'groups/groupName.jpg'
        StorageReference groupImageRef = storageRef.child("groups/"+name+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = groupImageRef.putBytes(data);
        upload(groupImageRef, uploadTask, groupID);
    }


    private void upload(StorageReference groupImageRef, UploadTask uploadTask, String groupID) {
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return groupImageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String pictureLink = Objects.requireNonNull(task.getResult()).toString();
                updateGroupPicture(groupID, pictureLink);
            } else {
                errorMessage.postValue("could not upload picture");
                // Handle failures
            }
        });
    }

    public void createGroup(String name, String description, String address) {
        String queryBody = GroupRequests.addGroupRequest(name, description, address).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .createGroup(token, body)
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
                    groupID.postValue(response.body().getResponse());
                    isGroupCreated.postValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void updateGroupPicture(String groupID, String link) {
        String queryBody = GroupRequests.updateGroupPicture(groupID, link).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
        .updateGroupPicture(token, body)
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
                    isPictureUploaded.postValue(true);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    public void loadGroup(String groupId) {
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
                .getGroup(token, groupId)
                .enqueue(new Callback<Group>() {
                    @Override
                    public void onResponse(@NotNull Call<Group> call, @NotNull Response<Group> response) {
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
                            group.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<Group> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    public void saveChanges(HashMap<String, String> changes) {
        String token = Cache.getInstance().getSession("auth-token");

        String queryBody = GroupRequests.updateGroupDetails(changes).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
                .updateGroupDetails(token, body)
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
                                    isSaved.postValue(true);
                                else
                                    isUpdated = true;
                            } else {
                                isSaved.postValue(true);
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
    public void uploadPictureGr(String groupId,Bitmap bitmap, String name, boolean otherChanges) {
        uploadPic = true;
        // Create a reference to 'groups/groupName.jpg'
        StorageReference groupImageRef = storageRef.child("groups/"+name+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = groupImageRef.putBytes(data);
        uploadGr(groupId,groupImageRef, uploadTask, otherChanges);
    }
    private void uploadGr(String groupId,StorageReference imageRef, UploadTask uploadTask, boolean otherChanges) {
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String pictureLink = Objects.requireNonNull(task.getResult()).toString();

                updateGrPicture(groupId,pictureLink, otherChanges);
            } else {
                errorMessage.postValue("could not upload picture");
                // Handle failures
            }
        });
    }
    private void updateGrPicture(String groupID,String link, boolean otherChanges) {
        String queryBody = GroupRequests.updateGroupPicture(groupID, link).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
                .updateGroupPicture(token, body)
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
}