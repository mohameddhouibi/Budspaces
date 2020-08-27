package com.example.budspaces.Views.Fragments.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.bumptech.glide.Glide;
import com.example.budspaces.Adapters.PopupAdapter;
import com.example.budspaces.Adapters.ProfileChipsAdapter;
import com.example.budspaces.Adapters.ProfileGroupsAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Enums.PopupActions;
import com.example.budspaces.Listeners.OnPopupActionSelectedListener;
import com.example.budspaces.Models.User;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.ModifyInterestsKey;
import com.example.budspaces.Navigation.Keys.ModifyProfileKey;
import com.example.budspaces.R;
import com.example.budspaces.Utils.CameraUtils;
import com.example.budspaces.Utils.SimpleDividerItemDecoration;
import com.example.budspaces.Utils.Tuple3;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.Main.ProfileViewModel;
import com.example.budspaces.Views.Activities.MainActivity;
import com.example.budspaces.Views.Activities.SettingsActivity;
import com.example.budspaces.Views.Activities.SignInActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.iceteck.silicompressorr.SiliCompressor;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends BaseFragment implements View.OnClickListener {
    private final int SELECT_PICTURE = 1999;
    private final int TAKE_PICTURE = 2000;

    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.groups)
    RecyclerView groups;
    @BindView(R.id.fullName)
    TextView fullName;
    @BindView(R.id.genre)
    TextView genre;
    @BindView(R.id.birthdate)
    TextView birthdate;
    @BindView(R.id.country)
    TextView country;
    @BindView(R.id.motionLayout)
    MotionLayout motionLayout;
    @BindView(R.id.chips)
    RecyclerView chips;
    @BindView(R.id.groupsTxt)
    TextView groupsTxt;
    @BindView(R.id.interestsTxt)
    TextView interestsTxt;

    private ImageView settings;
    private TextView mail;
    private ImageView interestsBtn;
    private ProfileViewModel mViewModel;
    private Unbinder unbinder;

    private boolean start = true;
    private boolean mainUser = true;
    private boolean verified = true;

    private User user;
    private PopupWindow cameraPopupWindow, popupWindow;
    private List<Tuple3<String, Drawable, PopupActions>> tupleList = new ArrayList<>();
    private int[] buttonPos = new int[2];
    private String userID = null;
    private Backstack backstack;

    private OnPopupActionSelectedListener actionSelectedListener = new OnPopupActionSelectedListener() {
        @Override
        public void actionSelected(PopupActions action) {
            popupWindow.dismiss();
            switch (action) {
                case modify:
                    backstack.goTo(ModifyProfileKey.create(user));
                    break;
                case settings:
                    startActivity(new Intent(getContext(), SettingsActivity.class));
                    break;
                case conditions:
                    break;
                case logout:
                    mViewModel.logout();
                    GoogleSignIn.getClient(requireActivity(), Utils.getGso()).signOut();
                    break;
            }
        }
    };

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(String userID) {
        this.userID = userID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view;
        backstack = Navigator.getBackstack(requireContext());
        if (userID == null || userID.isEmpty()) {
            view = inflater.inflate(R.layout.user_profile_fragment, container, false);
        } else {
            view = inflater.inflate(R.layout.profile_fragment, container, false);
            mainUser = false;
        }

        return view;
    }

    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        if (userID == null || userID.isEmpty()) {
            settings = view.findViewById(R.id.settings);
            mail = view.findViewById(R.id.mail);
            interestsBtn = view.findViewById(R.id.interestsBtn);
        }

        setCameraPopUpWindow();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        verified = ((MainActivity)requireActivity()).isVerified();

        if (mainUser) {
            this.userID = Cache.getInstance().getSession("userId");

            settings.setOnClickListener(this);
            interestsBtn.setOnClickListener(this);

            settings.getLocationOnScreen(buttonPos);
            initTuples();
            setPopUpWindow();
        }

        mViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                initUser();
            }
        });

        mViewModel.getGroups().observe(getViewLifecycleOwner(), groupList -> {
            if (groupList != null && groupList.size() > 0) {
                if (verified)
                    groups.setAdapter(new ProfileGroupsAdapter(groupList, mainUser));
                else {
                    groups.setAdapter(new ProfileGroupsAdapter(groupList, false));
                }
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
        });

        mViewModel.getIsLoggedout().observe(getViewLifecycleOwner(), logout -> {
            if (logout) {
                Cache.deleteCache(requireContext());
                startActivity(new Intent(getContext(), SignInActivity.class));
                requireActivity().finish();
            }
        });

        initGroups();
        initChips();
    }

    public void update() {
        Log.e("ahmed", "update: "+userID);

        if (mViewModel != null && userID != null && !userID.isEmpty()) {
            mViewModel.loadProfile(userID);
            if (!verified || !mainUser) {
                mViewModel.loadMemberGroups(userID);
            } else {
                mViewModel.loadHostGroups(userID);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    public boolean isSameUser(String uid) {
        return ((uid.isEmpty() && mainUser) || uid.equals(userID));
    }

    private void initTuples() {
        tupleList.add(new Tuple3<>(requireContext().getString(R.string.modify_account), requireContext().getResources().getDrawable(R.drawable.ic_settings), PopupActions.modify));
        tupleList.add(new Tuple3<>(requireContext().getString(R.string.setting), requireContext().getResources().getDrawable(R.drawable.ic_wrench), PopupActions.settings));
        tupleList.add(new Tuple3<>(requireContext().getString(R.string.terms_conditions), requireContext().getResources().getDrawable(R.drawable.ic_conditions), PopupActions.conditions));
        tupleList.add(new Tuple3<>(requireContext().getString(R.string.logout), requireContext().getResources().getDrawable(R.drawable.ic_logout), PopupActions.logout));
    }

    private void initUser() {
        Glide.with(this).load(user.getPicture()).error(R.drawable.com_facebook_profile_picture_blank_square).into(picture);
        name.setText(user.getName().split(" ")[0]);

        if (user.getCity() != null && !user.getCity().isEmpty())
            location.setText(user.getCity());
        else
            location.setText(user.getCountry());

        fullName.setText(user.getName());
        genre.setText(user.getGender());
        birthdate.setText(user.getBirthdate());

        if (mainUser)
            mail.setText(user.getEmail());

        country.setText(user.getCountry());
        chips.setAdapter(new ProfileChipsAdapter(user.getInterests()));

        if (!mainUser) {
            if (!user.getShowGroups()) {
                groupsTxt.setVisibility(View.GONE);
                groups.setVisibility(View.GONE);
            }

            if (!user.getShowInterests()) {
                interestsTxt.setVisibility(View.GONE);
                chips.setVisibility(View.GONE);
            }
        }
    }

    private void initGroups() {
        LinearLayoutManager groupsManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        groups.setLayoutManager(groupsManager);
        groups.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
            }
        });

        groups.setAdapter(new ProfileGroupsAdapter(new ArrayList<>(), mainUser));
    }

    private void initChips() {
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(requireContext())
                .setChildGravity(Gravity.TOP)

                .setScrollingEnabled(true)

                .setGravityResolver(position -> Gravity.CENTER)

                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_SPACE)

                .build();
        chips.setLayoutManager(chipsLayoutManager);
        chips.setNestedScrollingEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings:
                int pos = (int) Utils.convertDpToPixel(160.0f, requireContext()) - buttonPos[1];
                popupWindow.showAsDropDown(settings, pos * -1, 0); //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)
                break;
            case R.id.interestsBtn:
                backstack.goTo(ModifyInterestsKey.create(InterestType.profile, user.getId(), user.getInterests()));
                break;
        }
    }

    @OnClick({R.id.expandedArrow, R.id.camera, R.id.picture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.expandedArrow:
                if (start)
                    motionLayout.transitionToEnd();
                else
                    motionLayout.transitionToStart();
                start = !start;
                break;
            case R.id.picture:
            case R.id.camera:
                cameraPopupWindow.showAsDropDown(picture, -150, 10); //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)
                break;
        }
    }

    private void setPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_menu, null);

        RecyclerView menuItems = view.findViewById(R.id.menu);
        popupWindow = new PopupWindow(view, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);

        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        menuItems.setItemAnimator(new DefaultItemAnimator());
        menuItems.setLayoutManager(wallManager);
        menuItems.addItemDecoration(new SimpleDividerItemDecoration(requireContext(), R.drawable.popup_devider, getResources().getDimension(R.dimen.activity_vertical_margin)));
        menuItems.setAdapter(new PopupAdapter(tupleList, actionSelectedListener));
    }

    private void setCameraPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_menu_camera, null);

        TextView camera = view.findViewById(R.id.action_camera);
        TextView gallery = view.findViewById(R.id.action_gallery);

        gallery.setOnClickListener(v -> {
            cameraPopupWindow.dismiss();
            startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), SELECT_PICTURE);
        });

        camera.setOnClickListener(v -> {
            cameraPopupWindow.dismiss();
            dispatchTakePictureIntent();
        });

        cameraPopupWindow = new PopupWindow(view, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Glide.with(this).load(data.getData()).into(picture);
                String photoPath = Utils.getFilePath(requireActivity(), data.getData());

                savePicture(photoPath);
            } else if (requestCode == TAKE_PICTURE) {
                try {
                    Bitmap bitmap = CameraUtils.checkBitmapRotation(getContext());

                    if (bitmap != null) {
                        Glide.with(this).load(bitmap).into(picture);
                        String photoPath = CameraUtils.getmCurrentPhotoPath();
                        savePicture(photoPath);
                    } else {
                        Log.e("Profile Fragment", "null bitmap");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = CameraUtils.createImageFile(requireContext());
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("dispatchPicture", Objects.requireNonNull(ex.getLocalizedMessage()));
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.example.budspaces.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        }
    }

    private void savePicture(String photoPath) {
        try {
            Bitmap bitmap = SiliCompressor.with(requireContext()).getCompressBitmap(photoPath);
            mViewModel.uploadPicture(bitmap, user.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}