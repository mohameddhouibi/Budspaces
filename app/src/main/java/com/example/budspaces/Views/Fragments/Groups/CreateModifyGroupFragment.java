package com.example.budspaces.Views.Fragments.Groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.budspaces.Cache;
import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.User;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.ModifyInterestsKey;
import com.example.budspaces.R;
import com.example.budspaces.Utils.CameraUtils;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.CreateGroupViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputLayout;
import com.iceteck.silicompressorr.SiliCompressor;
import com.zhuinden.simplestack.navigator.Navigator;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import carbon.widget.Button;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.budspaces.Utils.LoginChecker.validateName;

public class CreateModifyGroupFragment extends BaseFragment {
    private final int SELECT_PICTURE = 1999;
    private final int TAKE_PICTURE = 2000;
    private String groupID;

    @BindView(R.id.name)
    TextInputLayout name;
    @BindView(R.id.location)
    TextInputLayout locationLayout;
    @BindView(R.id.description)
    TextInputLayout description;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.camera)
    ImageView camera;
    @BindView(R.id.validate)
    Button validate;
    @BindView(R.id.cancel_action)
    Button cancel;
    @BindView(R.id.groupprogress)
    ProgressBar groupprogress;

    private PopupWindow cameraPopupWindow;
    private CreateGroupViewModel mViewModel;
    private Group group;

    private Unbinder unbinder;
    private InterestType type;
    private final String TAG = "CreateModifyGroupFrag";
    private User user;
    private PlacesClient placesClient;
    private String userId = Cache.getInstance().getSession("userId");
    private HashMap<String, String> changes = new HashMap<>();

    private boolean isImageSelected = false;
    private String photoPath;

    @BindView(R.id.autoComplete)
    AutoCompleteTextView autoComplete;


    public CreateModifyGroupFragment() {
    }

    public CreateModifyGroupFragment(InterestType type, String id) {
        this.type = type;
        this.groupID = id ;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_group_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Create a new Places client instance
        placesClient = Places.createClient(requireContext());
        Objects.requireNonNull(locationLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                autoCompleteRequest(s.toString());
            }
        });
        setCameraPopUpWindow();
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CreateGroupViewModel.class);
        if (type.equals(InterestType.group_new))
            pageTitle.setText(getText(R.string.new_group_1));
        else {
            pageTitle.setText(getText(R.string.modif_group_1));
            mViewModel.loadGroup(groupID);

            Toast.makeText(requireContext(), groupID, Toast.LENGTH_SHORT).show();
            mViewModel.getGroup().observe(getViewLifecycleOwner(), group -> {
                if (group != null) {
                    fillGroup(group);
                    //mViewModel.loadMembers(groupID, 7);
                }
            });

            mViewModel.getIsSaved().observe(getViewLifecycleOwner(), isSaved -> {
                if (isSaved) {
                    Navigator.getBackstack(requireContext()).goTo(ModifyInterestsKey.create(type, groupID, group.getInterests()));
                }
            });
        }
        mViewModel.getIsGroupCreated().observe(getViewLifecycleOwner(), isValid -> {
            if (isValid) {
                String _name = Objects.requireNonNull(name.getEditText()).getText().toString();
                try {
                    Bitmap bitmap = SiliCompressor.with(requireContext()).getCompressBitmap(photoPath);
                    mViewModel.uploadPicture(bitmap, _name, mViewModel.getGroupID().getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mViewModel.getIsPictureUploaded().observe(getViewLifecycleOwner(), isValid -> {
            if (isValid)
                Navigator.getBackstack(requireContext()).goTo(ModifyInterestsKey.create(type, mViewModel.getGroupID().getValue(), new ArrayList<>()));
        });
        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
        });

        Objects.requireNonNull(name.getEditText()).setOnFocusChangeListener((View view, boolean hasFocus) -> {
            if (!hasFocus) {
                if (name.getEditText().getText().length() >= 3)
                    name.setEndIconTintList(getResources().getColorStateList(R.color.mainBlue));
                else
                    name.setEndIconTintList(getResources().getColorStateList(R.color.mainRed));
            }
        });
    }
    @OnClick({R.id.cancel_action, R.id.validate, R.id.picture, R.id.camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_action:
                Navigator.getBackstack(requireContext()).goBack();
                break;
            case R.id.validate:
                name.setErrorEnabled(true);
                locationLayout.setErrorEnabled(true);
                description.setErrorEnabled(true);
                if (validateName(getContext(), name) && validateName(getContext(), locationLayout) && validateName(getContext(), description)) {
                    String _name = Objects.requireNonNull(name.getEditText()).getText().toString();
                    String _description = Objects.requireNonNull(description.getEditText()).getText().toString();
                    String _address = Objects.requireNonNull(locationLayout.getEditText()).getText().toString();
                    validate.setVisibility(view.INVISIBLE);
                    cancel.setVisibility(view.INVISIBLE);
                    groupprogress.setVisibility(view.VISIBLE);

                    if (type.equals(InterestType.group_new))
                    {
                        mViewModel.createGroup(_name, _description, _address);
                    }
                    else {
                        checkChanges();
                        if (photoPath != null && !photoPath.isEmpty())
                            savePicture(photoPath, changes.size() > 0);

                        if (changes.size() > 0)
                            mViewModel.saveChanges(changes);
                    }

                }
                break;
            case R.id.picture:
            case R.id.camera:
                cameraPopupWindow.showAsDropDown(camera, -225, 10); //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)
                break;

        }
    }

    private void fillGroup(Group group) {
        this.group = group;
        Glide.with(this).load(group.getPicture()).dontAnimate().into(picture);
        Objects.requireNonNull(name.getEditText()).setText(group.getName());
        Objects.requireNonNull(locationLayout.getEditText()).setText(group.getAddress());
        Objects.requireNonNull(description.getEditText()).setText(group.getDescription());
        name.setEndIconTintList(getResources().getColorStateList(R.color.mainBlue));
    }

    private void checkChanges() {
        String id = groupID ;
        String _name = Objects.requireNonNull(name.getEditText()).getText().toString();
        String _address = Objects.requireNonNull(locationLayout.getEditText()).getText().toString();
        String _description = Objects.requireNonNull(description.getEditText()).getText().toString();
        changes.put("_id", id);

        if (!_address.equals(group.getAddress()))
            changes.put("address", _address);

        if (!_name.equals(group.getName()))
            changes.put("name", _name);

        if (!_description.equals(group.getDescription()))
            changes.put("description", _description);

    }
    private void savePicture(String photoPath, boolean otherChanges) {
        try {
            Bitmap bitmap = SiliCompressor.with(requireContext()).getCompressBitmap(photoPath);
            mViewModel.uploadPictureGr(groupID,bitmap, photoPath, otherChanges);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setCameraPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                isImageSelected = true;
                photoPath = Utils.getFilePath(requireActivity(), data.getData());
            } else if (requestCode == TAKE_PICTURE) {
                try {
                    Bitmap bitmap = CameraUtils.checkBitmapRotation(getContext());

                    if (bitmap != null) {
                        Glide.with(this).load(bitmap).into(picture);
                        isImageSelected = true;
                        photoPath = CameraUtils.getmCurrentPhotoPath();
                    } else {
                        Log.e(TAG, "null bitmap");
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

    private void autoCompleteRequest(String query) {
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.CITIES)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            String[] addresses = new String[response.getAutocompletePredictions().size()];
            int i = 0;
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                addresses[i++] = prediction.getFullText(null).toString();
                Log.i(TAG, prediction.getPlaceId());
                Log.i(TAG, prediction.getPrimaryText(null).toString());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, addresses);
            autoComplete.setAdapter(adapter);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }
}