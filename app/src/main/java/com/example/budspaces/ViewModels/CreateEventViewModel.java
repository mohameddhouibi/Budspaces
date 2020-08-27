package com.example.budspaces.ViewModels;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.budspaces.Cache;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.ServerResponse;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Network.EventRequests;
import com.example.budspaces.Network.GroupRequests;
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

public class CreateEventViewModel extends ViewModel {
    private MutableLiveData<Boolean> isEventCreated = new MutableLiveData<>();
    private MutableLiveData<Boolean> isPictureUploaded = new MutableLiveData<>();
    private MutableLiveData<String> eventID = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Event> events = new MutableLiveData<>();
    private MutableLiveData<Event> event = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSaved = new MutableLiveData<>();

    private StorageReference storageRef;

    private boolean uploadPic = false;
    private boolean isUpdated = false;

    public CreateEventViewModel() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        isEventCreated.setValue(false);
        isPictureUploaded.setValue(false);
        event.setValue(null);
    }

    public LiveData<Event> getevent() { return event; }
    public MutableLiveData<Boolean> getIsEventCreated() { return isEventCreated; }
    public MutableLiveData<Boolean> getIsPictureUploaded() { return isPictureUploaded; }
    public MutableLiveData<String> getEventID() { return eventID;}
    public MutableLiveData<String> getErrorMessage() {return errorMessage;}
    public MutableLiveData<Event> getEvents() { return events; }


    public void createEvent(String groupId,String name, String description, String address,String startDate,String EndDate, String picture) {
        String queryBody = EventRequests.addEventRequest(groupId,name,description,address,startDate,EndDate,picture).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
                .createEvent(token, body)
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
                            eventID.postValue(response.body().getResponse());
                            isEventCreated.postValue(true);
                        }
                    }
                    @Override
                    public void onFailure(@NotNull Call<ServerResponse> call, @NotNull Throwable t) {
                        t.printStackTrace();
//                        adapter.showRetry(true, fetchErrorMessage(t));
                    }
                });
    }

    public void uploadEvent(Bitmap bitmap,String groupId ,String name, String description, String address,String startDate
            ,String EndDate ,String picture) {
        uploadPic = true;

        StorageReference groupImageRef = storageRef.child("groups/"+name+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = groupImageRef.putBytes(data);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return groupImageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String pictureLink = Objects.requireNonNull(task.getResult()).toString();
                createEvent(groupId,name,description,address,startDate,EndDate,pictureLink);
            } else {
                errorMessage.postValue("could not upload picture");
                // Handle failures
            }
        });

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

    public void uploadPictureEvent(Bitmap bmp, String name, String groupID) {
        // Create a reference to 'groups/groupName.jpg'
        StorageReference groupImageRef = storageRef.child("groups/"+name+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = groupImageRef.putBytes(data);
        upload(groupImageRef, uploadTask, groupID);
    }
    public void uploadPictureEvent(String eventID,Bitmap bitmap, String name, boolean otherChanges) {
        uploadPic = true;
        // Create a reference to 'groups/groupName.jpg'
        StorageReference eventImageRef = storageRef.child("events/"+name+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = eventImageRef.putBytes(data);
        uploadev(eventID,eventImageRef, uploadTask, otherChanges);
    }

    private void uploadev(String eventID,StorageReference imageRef, UploadTask uploadTask, boolean otherChanges) {
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return imageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String pictureLink = Objects.requireNonNull(task.getResult()).toString();

                updateEVPicture(eventID,pictureLink, otherChanges);
            } else {
                errorMessage.postValue("could not upload picture");
                // Handle failures
            }
        });
    }
    private void updateEVPicture(String eventID,String link, boolean otherChanges) {
        String queryBody = GroupRequests.updateGroupPicture(eventID, link).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");
        AppRetrofit.getInstance().getService()
                .updateEvent(token,eventID, body)
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

    private void upload(StorageReference groupImageRef, UploadTask uploadTask, String EventID) {
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return groupImageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String pictureLink = Objects.requireNonNull(task.getResult()).toString();
                updateEventPicture(EventID, pictureLink);
            } else {
                errorMessage.postValue("could not upload picture");
                // Handle failures
            }
        });
    }

    public void updateEventPicture(String EventID, String link) {
        String queryBody = GroupRequests.updateGroupPicture(EventID, link).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);
        String token = Cache.getInstance().getSession("auth-token");

        AppRetrofit.getInstance().getService()
                .updateEvent(token,EventID, body)
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
                    }
                });
    }
    public void saveChanges(String EventID,HashMap<String, String> changes) {
        String token = Cache.getInstance().getSession("auth-token");

        String queryBody = GroupRequests.updateGroupDetails(changes).toString();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), queryBody);

        AppRetrofit.getInstance().getService()
                .updateEvent(token,EventID, body)
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

}