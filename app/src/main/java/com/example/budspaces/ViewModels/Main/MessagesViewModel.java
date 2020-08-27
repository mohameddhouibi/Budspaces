package com.example.budspaces.ViewModels.Main;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.MessagesMembersAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Enums.MessageType;
import com.example.budspaces.Enums.PostType;
import com.example.budspaces.Listeners.OnChatMemberListener;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.HomeModel;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.Message;
import com.example.budspaces.Models.User;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.Utils.PaginationScrollListener;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagesViewModel extends ViewModel {
    private MutableLiveData<Message> message = new MutableLiveData<>();
    private MutableLiveData<String> picture = new MutableLiveData<>();
    private MutableLiveData<HomeModel> announcement = new MutableLiveData<>();
    private MutableLiveData<List<Message>> messageList = new MutableLiveData<>();
    private MutableLiveData<Group> group = new MutableLiveData<>();
    private MutableLiveData<Boolean> progressBar = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    private Socket socket;
    private String userId;
    private String token;

    private boolean isLoading = false;
    private boolean isLastPage = true;

    private StorageReference storageRef;

    private static final int START_ITEM = 0;
    private static int PAGE_SIZE = 10;
    private int currentStartItem = START_ITEM;
    private int u_currentStartItem = START_ITEM;

    private MessagesMembersAdapter adapter;

    public MessagesViewModel() {
        this.token = Cache.getInstance().getSession("auth-token");
        this.userId = Cache.getInstance().getSession("userId");

        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create a storage reference from our app
        storageRef = storage.getReference();

        progressBar.setValue(false);
        message.setValue(null);
        group.setValue(null);
        announcement.setValue(null);
        messageList.setValue(new ArrayList<>());
        picture.setValue(null);

        try {
            socket = IO.socket("http://51.38.232.57:8570");
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public LiveData<HomeModel> getAnnouncement() { return announcement; }

    public LiveData<Boolean> getProgressBar() { return progressBar; }

    public LiveData<Message> getMessage() {
        return message;
    }

    public LiveData<List<Message>> getMessageList() {
        return messageList;
    }

    public LiveData<Group> getGroup() { return group; }

    public LiveData<String> getPicture() { return picture; }

    public void joinRoom(String roomID) {
        socket.emit("join", userId, roomID);
    }

    public void disconnect() {
        socket.disconnect();
    }

    public void sendMessage(String message, MessageType type) {
        socket.emit("messagedetection", message, type);
    }

    public void sendAnnouncement(String title, String content) {
        socket.emit("sendAnnouncement", title, content);
    }

    public void listenToAnnouncement() {
        socket.on("announcement", args -> {
            JSONObject data = (JSONObject) args[0];

            try {
                //extract data from fired event
                String title = data.getString("title");
                String content = data.getString("content");

                HomeModel model = new HomeModel();
                model.setType(PostType.announcement);
                model.setName(title);
                model.setDescription(content);

                announcement.postValue(model);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void listenToThread() {
        socket.on("message", args -> {
            JSONObject data = (JSONObject) args[0];

            try {
                //extract data from fired event
                String userId = data.getString("userId");
                String content = data.getString("content");
                MessageType type = data.getString("type").equals(MessageType.text.name()) ? MessageType.text : MessageType.picture;

                Message message = new Message(userId, content, type);
                this.message.postValue(message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadHistory(String roomID, boolean isFirst) {
        if (isFirst)
            currentStartItem = START_ITEM;

        AppRetrofit.getInstance().getService()
            .getChatMessages(token, roomID, currentStartItem++, PAGE_SIZE)
            .enqueue(new Callback<List<Message>>() {
                @Override
                public void onResponse(@NotNull Call<List<Message>> call, @NotNull Response<List<Message>> response) {
                    messageList.postValue(fetchResults(response));
                }

                @Override
                public void onFailure(@NotNull Call<List<Message>> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
    }

    public void loadGroup(String roomID) {
        AppRetrofit.getInstance().getService()
            .getGroup(token, roomID)
            .enqueue(new Callback<Group>() {
                @Override
                public void onResponse(@NotNull Call<Group> call, @NotNull Response<Group> response) {
                    group.postValue(response.body());
                }

                @Override
                public void onFailure(@NotNull Call<Group> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
    }

    public void setRecyclerView(RecyclerView recyclerView, LinearLayoutManager manager, String roomId, OnChatMemberListener listener) {
        this.adapter = new MessagesMembersAdapter(listener);
        recyclerView.setAdapter(adapter);
        loadNextPage(true, roomId);

        PaginationScrollListener paginationScrollListener = new PaginationScrollListener(manager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                u_currentStartItem++;// PAGE_SIZE;

                loadNextPage(false, roomId);
            }

            @Override
            public int getTotalPageCount() {
                return PAGE_SIZE;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        };

        recyclerView.addOnScrollListener(paginationScrollListener);
    }

    public void loadNextPage(boolean isFirstPage, String roomId) {
        if (isFirstPage)
            u_currentStartItem = START_ITEM;

        AppRetrofit.getInstance().getService()
            .getGroupMembers(roomId, u_currentStartItem, PAGE_SIZE, false)
            .enqueue(new Callback<List<Member>>() {
                @Override
                public void onResponse(@NotNull Call<List<Member>> call, @NotNull Response<List<Member>> response) {
                    loadPage(response, isFirstPage);
                }

                @Override
                public void onFailure(@NotNull Call<List<Member>> call, @NotNull Throwable t) {
                    t.printStackTrace();
                }
            });
    }

    private void loadPage(Response<List<Member>> response, boolean isFirst) {
        List<Member> results = response.body();

        if (isFirst) {
            isLastPage = false;
        } else {
            adapter.removeLoadingFooter();
            isLoading = false;
        }

        adapter.addAll(results);

        if (!isFirst && results.size() < PAGE_SIZE)
            isLastPage = true;
        else
            adapter.addLoadingFooter();
    }

    /**
     * @param response extracts List<{@link Message>} from response
     */
    private List<Message> fetchResults(Response<List<Message>> response) {
        return response.body();
    }

    public void uploadPicture(Bitmap bitmap, String groupId) {
        String timestamp = String.valueOf(new Date().getTime());
        // Create a reference to 'groups/groupName.jpg'
        StorageReference imageRef = storageRef.child("Messages/"+groupId+"/"+timestamp+".jpg");
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
            progressBar.setValue(false);

            if (task.isSuccessful()) {
                String pictureLink = Objects.requireNonNull(task.getResult()).toString();

                picture.postValue(pictureLink);
            } else {
                errorMessage.postValue("could not upload picture");
                // Handle failures
            }
        });
    }

    public void loadAnnouncement(String roomId) {
        AppRetrofit.getInstance().getService()
        .getAnnouncement(token, roomId)
        .enqueue(new Callback<HomeModel>() {
            @Override
            public void onResponse(@NotNull Call<HomeModel> call, @NotNull Response<HomeModel> response) {
                if (response.isSuccessful()) {
                    announcement.postValue(response.body());
                }
            }

            @Override
            public void onFailure(@NotNull Call<HomeModel> call, @NotNull Throwable t) {
                t.printStackTrace();
            }
        });
    }
}