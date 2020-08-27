package com.example.budspaces.Views.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Cache;
import com.example.budspaces.Enums.MessageType;
import com.example.budspaces.Listeners.HidingScrollListener;
import com.example.budspaces.Listeners.OnChatMemberListener;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Models.Message;
import com.example.budspaces.R;
import com.example.budspaces.Utils.CameraUtils;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.Main.MessagesViewModel;
import com.example.budspaces.Views.Dialog.UserChatProfileDialogFragment;
import com.example.budspaces.Views.ViewHolders.Messages.CustomIncomingImageMessageViewHolder;
import com.example.budspaces.Views.ViewHolders.Messages.CustomIncomingTextMessageViewHolder;
import com.example.budspaces.Views.ViewHolders.Messages.CustomOutcomingImageMessageViewHolder;
import com.example.budspaces.Views.ViewHolders.Messages.CustomOutcomingTextMessageViewHolder;
import com.iceteck.silicompressorr.SiliCompressor;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import carbon.widget.ProgressBar;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;

public class MessagesActivity extends AppCompatActivity implements DateFormatter.Formatter
        , MessagesListAdapter.OnLoadMoreListener, MessagesListAdapter.OnMessageLongClickListener<Message> {

    @BindView(R.id.messagesList)
    MessagesList messagesList;
    @BindView(R.id.messageInput)
    EditText messageInput;
    @BindView(R.id.interests)
    TextView interests;
    @BindView(R.id.previous)
    ImageView previous;
    @BindView(R.id.membersNb)
    TextView membersNb;
    @BindView(R.id.members)
    RecyclerView members;
    @BindView(R.id.root)
    ConstraintLayout root;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.settings)
    ImageView announcementBtn;
    @BindView(R.id.announceName)
    TextView announceName;
    @BindView(R.id.announceContent)
    TextView announceContent;
    @BindView(R.id.announceLayout)
    ConstraintLayout announceLayout;

    private MessagesViewModel mViewModel;
    private Unbinder unbinder;
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private static final int TOTAL_MESSAGES_COUNT = 100;
    private final int TAKE_PICTURE = 2000;
    private final int SELECT_PICTURE = 1999;
    private final int ADD_ANNOUNCEMENT = 1993;

    private String senderId;
    private String roomId, roomName, roomPicture;
    private Date lastLoadedDate;

    private OnChatMemberListener memberListener = new OnChatMemberListener() {
        @Override
        public void onUserClick(Member user) {
            showMemberDialog(user);
        }

        @Override
        public void onMemberLoaded(Member member) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        unbinder = ButterKnife.bind(this);
        makeStatusBarWhite(this);

        roomId = getIntent().getStringExtra("roomID");
        roomName = getIntent().getStringExtra("roomName");
        senderId = Cache.getInstance().getSession("userId");

        mViewModel = new ViewModelProvider(this).get(MessagesViewModel.class);
        mViewModel.loadGroup(roomId);
        mViewModel.joinRoom(roomId);
        mViewModel.loadAnnouncement(roomId);

        mViewModel.loadHistory(roomId, true);
        mViewModel.getMessageList().observe(this, messages -> {
            if (messages.size() > 0) {
                messagesAdapter.addToEnd(messages, false);
                messagesList.smoothScrollToPosition(0);
            }
        });

        mViewModel.getProgressBar().observe(this, show -> {
            if (show)
                progressBar.setVisibility(View.VISIBLE);
            else
                progressBar.setVisibility(View.GONE);
        });

        mViewModel.getPicture().observe(this, picture -> {
            if (picture != null && !picture.isEmpty()) {
                Message message = new Message(senderId, picture, MessageType.picture);
                messagesAdapter.addToStart(message, true);
                mViewModel.sendMessage(message.getContent(), MessageType.picture);
            }
        });

        mViewModel.getAnnouncement().observe(this, announcement -> {
            if (announcement != null) {
                announceLayout.setVisibility(View.VISIBLE);
                announceName.setText(announcement.getName());
                announceContent.setText(announcement.getDescription());
            }
        });

        imageLoader = (imageView, url, payload) ->
                Glide.with(MessagesActivity.this).load(url).error(R.drawable.com_facebook_profile_picture_blank_square).into(imageView);
        initAdapter();

        pageTitle.setText(roomName);
        interests.setVisibility(View.VISIBLE);
        previous.setVisibility(View.VISIBLE);

        LinearLayoutManager groupsManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        members.setLayoutManager(groupsManager);
        members.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
            }
        });

        mViewModel.setRecyclerView(members, groupsManager, roomId, memberListener);
        mViewModel.listenToThread();
        mViewModel.listenToAnnouncement();

        mViewModel.getMessage().observe(this, message -> {
            if (message != null)
                messagesAdapter.addToStart(message, true);
        });

        mViewModel.getGroup().observe(this, group -> {
            if (group != null) {
                interests.setText(TextUtils.join(", ", group.getInterests()));
                membersNb.setText(String.format("%d members", group.getMembers().size()));
                roomPicture = group.getPicture();

                if (group.getHost().equals(senderId)) {
                    announcementBtn.setVisibility(View.VISIBLE);
                    announcementBtn.setImageResource(R.drawable.ic_announce_pin);
                }
            }
        });
    }

    private void initAdapter() {
        CustomIncomingTextMessageViewHolder.Payload payload = new CustomIncomingTextMessageViewHolder.Payload();
        payload.avatarClickListener = message -> showMemberDialog((Member) message.getUser());

        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message,
                        payload)
                .setIncomingImageConfig(
                        CustomIncomingImageMessageViewHolder.class,
                        R.layout.item_custom_incoming_image_message,
                        payload)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message)
                .setOutcomingImageConfig(
                        CustomOutcomingImageMessageViewHolder.class,
                        R.layout.item_custom_outcoming_image_message);

        messagesAdapter = new MessagesListAdapter<>(senderId, holdersConfig, imageLoader);
        messagesAdapter.setOnMessageLongClickListener(this);
//        messagesAdapter.enableSelectionMode(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesAdapter.setDateHeadersFormatter(this);
        messagesList.setAdapter(messagesAdapter);

        //setting up our OnScrollListener
        messagesList.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideViews();
            }
            @Override
            public void onShow() {
                showViews();
            }
        });
    }

    private void hideViews() {
        announceLayout.animate().alpha(0).translationY(-announceLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2));
    }

    private void showViews() {
        announceLayout.animate().alpha(1).translationY(0).setInterpolator(new DecelerateInterpolator(2));
    }

    private void showMemberDialog(Member user) {
        UserChatProfileDialogFragment dialogFragment =
                UserChatProfileDialogFragment.newInstance(8, 3.5f, true, false, user);
        dialogFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onMessageLongClick(Message message) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        mViewModel.disconnect();
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return getString(R.string.today);
        } else if (DateFormatter.isYesterday(date)) {
            return getString(R.string.yesterday);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }

    @OnClick({R.id.attachmentButton, R.id.input, R.id.previous, R.id.settings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.attachmentButton:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    selectImage();
                }

                break;
            case R.id.input:
                if (!messageInput.getText().toString().isEmpty()) {
                    Message message = new Message(senderId, messageInput.getText().toString(), MessageType.text);
                    messagesAdapter.addToStart(message, true);
                    mViewModel.sendMessage(message.getContent(), MessageType.text);
                    messageInput.getText().clear();
                }
                break;
            case R.id.previous:
                onBackPressed();
                break;
            case R.id.settings:
                Intent intent = new Intent(MessagesActivity.this, AddAnnouncementActivity.class);
                intent.putExtra("groupId", roomId);
                intent.putExtra("groupName", roomName);
                intent.putExtra("groupPicture", roomPicture);

                startActivityForResult(intent, ADD_ANNOUNCEMENT);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        mViewModel.loadHistory(roomId, false);
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = CameraUtils.createImageFile(this);
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("dispatchPicture", Objects.requireNonNull(ex.getLocalizedMessage()));
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.budspaces.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                String photoPath = Utils.getFilePath(this, data.getData());

                savePicture(photoPath);
            } else if (requestCode == TAKE_PICTURE) {
                try {
                    Bitmap bitmap = CameraUtils.checkBitmapRotation(this);

                    if (bitmap != null) {
                        String photoPath = CameraUtils.getmCurrentPhotoPath();
                        savePicture(photoPath);
                    } else {
                        Log.e("Profile Fragment", "null bitmap");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == ADD_ANNOUNCEMENT) {
                String title = data.getStringExtra("title");
                String content = data.getStringExtra("content");

                announceLayout.setVisibility(View.VISIBLE);
                announceName.setText(title);
                announceContent.setText(content);

                mViewModel.sendAnnouncement(title, content);
            }
        }
    }

    private void savePicture(String photoPath) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            Bitmap bitmap = SiliCompressor.with(this).getCompressBitmap(photoPath);
            mViewModel.uploadPicture(bitmap, roomId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");

        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                dispatchTakePictureIntent();
            } else if (options[item].equals("Choose from Gallery")) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, SELECT_PICTURE);

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}