package com.example.budspaces.Views.Fragments.Groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Listeners.OnDateSetListener;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.R;
import com.example.budspaces.Utils.CameraUtils;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.CreateEventViewModel;
import com.example.budspaces.Views.Fragments.Settings.DatePickerFragment;
import com.example.budspaces.Views.Fragments.Settings.TimePickerFragment;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import carbon.widget.Button;

import static android.app.Activity.RESULT_OK;
import static com.example.budspaces.Utils.LoginChecker.validateName;

public class CreateModifEventFragment extends BaseFragment {
    private final int SELECT_PICTURE = 1999;
    private final int TAKE_PICTURE = 2000;

    @BindView(R.id.name)
    TextInputLayout name;
    @BindView(R.id.location)
    TextInputLayout location;
    @BindView(R.id.description)
    TextInputLayout description;
    @BindView(R.id.eventDate)
    TextView eventDate;
    @BindView(R.id.eventDateF)
    TextView eventDateF;
    @BindView(R.id.date)
    ConstraintLayout date;
    @BindView(R.id.datef)
    ConstraintLayout datef;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.picture)
    ImageView picture;
    @BindView(R.id.launch)
    Button launch;
    @BindView(R.id.cancel_action)
    Button cancelAction;
    @BindView(R.id.validate)
    Button validate;
    @BindView(R.id.previous)
    ImageView previous;
    @BindView(R.id.camera)
    ImageView camera;
    @BindView(R.id.autoComplete)
    AutoCompleteTextView autoComplete;
    @BindView(R.id.event_progress)
    ProgressBar event_progress;

    private PopupWindow cameraPopupWindow;
    private Unbinder unbinder;
    private CreateEventViewModel mViewModel;
    private InterestType type;
    private String id;
    private String groupeName;
    private Event event;
    private HashMap<String, String> changes = new HashMap<>();
    private boolean isImageSelected = false;
    private String photoPath;
    private final String TAG = "CreateModifyEventFrag";
    private PlacesClient placesClient;

    public CreateModifEventFragment() {
    }

    public CreateModifEventFragment(InterestType type, String id, String groupeName) {
        this.type = type;
        this.id = id;
        this.groupeName = groupeName;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Create a new Places client instance
        placesClient = Places.createClient(requireContext());
        Objects.requireNonNull(location.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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
        mViewModel = new ViewModelProvider(this).get(CreateEventViewModel.class);

        previous.setVisibility(View.VISIBLE);

        if (type.equals(InterestType.event_modify)) {
            pageTitle.setText(R.string.modify_event);
            launch.setVisibility(View.GONE);
            cancelAction.setVisibility(View.VISIBLE);
            validate.setVisibility(View.VISIBLE);
            mViewModel.loadEvent(id);
            mViewModel.getevent().observe(getViewLifecycleOwner(), event -> {
                if (event != null) {
                    fillEvent(event);//mViewModel.loadMembers(groupID, 7);
                }

            });

        } else {
            pageTitle.setText(R.string.new_event);
        }
        mViewModel.getIsEventCreated().observe(getViewLifecycleOwner(), isValid -> {
            if (isValid) {
                String _name = Objects.requireNonNull(name.getEditText()).getText().toString();
                try {
                    Bitmap bitmap = SiliCompressor.with(requireContext()).getCompressBitmap(photoPath);
                    mViewModel.uploadPictureEvent(bitmap, _name, mViewModel.getEventID().getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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

    private void fillEvent(Event event) {
        this.event = event;
        Glide.with(this).load(event.getPicture()).dontAnimate().into(picture);
        Objects.requireNonNull(name.getEditText()).setText(event.getName());
        Objects.requireNonNull(location.getEditText()).setText(event.getAddress());
        Objects.requireNonNull(description.getEditText()).setText(event.getDescription());
        String startDay = (String) DateFormat.format("dd", event.getStartDate());
        String endDay = (String) DateFormat.format("dd MMMM ", event.getEndDate());
        eventDate.setText(startDay + " - " + endDay);

    }


    @OnClick({R.id.previous, R.id.date, R.id.datef, R.id.launch, R.id.cancel_action, R.id.validate, R.id.picture, R.id.camera})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_action:
            case R.id.previous:
                requireActivity().onBackPressed();
                break;
            case R.id.date:
                showDatePickerDialog();
                break;
            case R.id.datef:
                showDatePickerDialog_();
                break;
            case R.id.validate:
                name.setErrorEnabled(true);
                location.setErrorEnabled(true);
                description.setErrorEnabled(true);
                if (validateName(getContext(), name) && validateName(getContext(), location) && validateName(getContext(), description)) {
                    checkChanges();
                    if (photoPath != null && !photoPath.isEmpty())
                        savePictureEvent(photoPath, changes.size() > 0);
                    Toast.makeText(requireContext(), "Event Added Successfully ", Toast.LENGTH_SHORT).show();
                    if (changes.size() > 0)
                        mViewModel.saveChanges(id, changes);
                    Toast.makeText(requireContext(), "Event updated Successfully ....", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.launch:

                name.setErrorEnabled(true);
                location.setErrorEnabled(true);
                description.setErrorEnabled(true);
                if (validateName(getContext(), name) && validateName(getContext(), location) && validateName(getContext(), description)) {
                    launch.setVisibility(view.INVISIBLE);
                    event_progress.setVisibility(view.VISIBLE);
                    String _name = Objects.requireNonNull(name.getEditText()).getText().toString();
                    String _description = Objects.requireNonNull(description.getEditText()).getText().toString();
                    String _address = Objects.requireNonNull(location.getEditText()).getText().toString();
                    String _date = Objects.requireNonNull(eventDate.getText()).toString();
                    String __date = Objects.requireNonNull(eventDateF.getText()).toString();
                    savePicture(photoPath, id, _name, _description, _address, _date, __date);
                }
                Navigator.getBackstack(requireContext()).goBack();
                break;
            case R.id.picture:
            case R.id.camera:
                cameraPopupWindow.showAsDropDown(camera, -225, 10); //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)
                break;
        }
    }

    private void savePictureEvent(String photoPath, boolean otherChanges) {
        try {
            Bitmap bitmap = SiliCompressor.with(requireContext()).getCompressBitmap(photoPath);
            mViewModel.uploadPictureEvent(id, bitmap, photoPath, otherChanges);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkChanges() {
        String ids = id;
        String _name = Objects.requireNonNull(name.getEditText()).getText().toString();
        String _address = Objects.requireNonNull(location.getEditText()).getText().toString();
        String _description = Objects.requireNonNull(description.getEditText()).getText().toString();
        changes.put("_id", ids);

        if (!_address.equals(event.getAddress()))
            changes.put("address", _address);

        if (!_name.equals(event.getName()))
            changes.put("name", _name);

        if (!_description.equals(event.getDescription()))
            changes.put("description", _description);

    }

    private void savePicture(String photoPath, String groupId, String name, String description, String address, String startDate
            , String EndDate) {
        try {
            Bitmap bitmap = SiliCompressor.with(requireContext()).getCompressBitmap(photoPath);
            mViewModel.uploadEvent(bitmap, groupId, name, description, address, startDate, EndDate, photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment(new OnDateSetListener() {
            @Override
            public void setDate(Date date) {

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateTxt = simpleDateFormat.format(date);
                eventDate.setText(dateTxt);
                showTimePickerDialog(dateTxt);
            }

            @Override
            public void setTime(String time) {
            }
        });
        newFragment.show(getChildFragmentManager(), "datePicker");
        eventDate.setError(null);
    }

    private void showDatePickerDialog_() {
        DialogFragment newFragment = new DatePickerFragment(new OnDateSetListener() {
            @Override
            public void setDate(Date date) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String dateTxt = simpleDateFormat.format(date);
                eventDateF.setText(dateTxt);
                showTimePickerDialog_(dateTxt);
            }

            @Override
            public void setTime(String time) {
            }
        });
        newFragment.show(getChildFragmentManager(), "datePicker");
        eventDateF.setError(null);
    }

    private void showTimePickerDialog(String dateTxt) {
        DialogFragment newFragment = new TimePickerFragment(new OnDateSetListener() {
            @Override
            public void setDate(Date date) {
            }

            @Override
            public void setTime(String time) {
                eventDate.setText(dateTxt + "T" + time + "Z");
            }
        });
        newFragment.show(getChildFragmentManager(), "datePicker");
    }

    private void showTimePickerDialog_(String dateTxt) {
        DialogFragment newFragment = new TimePickerFragment(new OnDateSetListener() {
            @Override
            public void setDate(Date date) {
            }

            @Override
            public void setTime(String time) {
                eventDateF.setText(dateTxt + "T" + time + "Z");
            }
        });
        newFragment.show(getChildFragmentManager(), "datePicker");
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