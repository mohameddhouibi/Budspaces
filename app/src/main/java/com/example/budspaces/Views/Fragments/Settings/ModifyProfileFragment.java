package com.example.budspaces.Views.Fragments.Settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.budspaces.Cache;
import com.example.budspaces.Enums.ProviderType;
import com.example.budspaces.Listeners.OnDateSetListener;
import com.example.budspaces.Models.User;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.R;
import com.example.budspaces.Utils.CameraUtils;
import com.example.budspaces.Utils.LoginChecker;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.Main.ModifyProfileViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.TextInputLayout;
import com.iceteck.silicompressorr.SiliCompressor;
import com.zhuinden.simplestack.navigator.Navigator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.example.budspaces.Utils.LoginChecker.isEmpty;
import static com.example.budspaces.Utils.LoginChecker.validateBirthdate;
import static com.example.budspaces.Utils.LoginChecker.validateCountry;
import static com.example.budspaces.Utils.LoginChecker.validateName;
import static com.example.budspaces.Utils.LoginChecker.validatePassword;
import static com.example.budspaces.Utils.LoginChecker.validatePasswordConfirmation;

public class ModifyProfileFragment extends BaseFragment implements TextWatcher {
    private final int SELECT_PICTURE = 1999;
    private final int TAKE_PICTURE = 2000;

    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.name)
    TextInputLayout name;
    @BindView(R.id.birthdate)
    TextView birthdate;
    //    @BindView(R.id.mail_layout)
//    TextInputLayout mailLayout;
    @BindView(R.id.country_layout)
    TextInputLayout countryLayout;
    @BindView(R.id.birthday_layout)
    ConstraintLayout birthdayLayout;
    @BindView(R.id.autoComplete)
    AutoCompleteTextView autoComplete;
    @BindView(R.id.autoCompleteLoc)
    AutoCompleteTextView autoCompleteLoc;
    @BindView(R.id.male)
    MaterialRadioButton male;
    @BindView(R.id.location)
    TextInputLayout location;
    @BindView(R.id.oldPassword)
    TextInputLayout oldPassword;
    @BindView(R.id.newPassword)
    TextInputLayout newPassword;
    @BindView(R.id.confirmPassword)
    TextInputLayout confirmPassword;

    private User user;
    private Unbinder unbinder;
    private PlacesClient placesClient;
    private String photoPath = null;
    private PopupWindow cameraPopupWindow;

    private final String TAG = "ModifyProfileFrag";
    private ModifyProfileViewModel mViewModel;
    private HashMap<String, String> changes = new HashMap<>();

    public ModifyProfileFragment() {
        // Required empty public constructor
    }

    public ModifyProfileFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modify_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        mViewModel = new ViewModelProvider(this).get(ModifyProfileViewModel.class);

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
        });

        mViewModel.getIsSaved().observe(getViewLifecycleOwner(), isSaved -> {
            if (isSaved)
                Navigator.getBackstack(requireContext()).goBack();
        });

        if (Cache.getInstance().getSession("provider").equals(ProviderType.mail.name())) {
            oldPassword.setVisibility(View.VISIBLE);
            newPassword.setVisibility(View.VISIBLE);
            confirmPassword.setVisibility(View.VISIBLE);
        }

        Objects.requireNonNull(name.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(countryLayout.getEditText()).addTextChangedListener(this);
//        Objects.requireNonNull(mailLayout.getEditText()).addTextChangedListener(this);
        Objects.requireNonNull(location.getEditText()).addTextChangedListener(this);

        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // Create a new Places client instance
        placesClient = Places.createClient(requireContext());

        bindUser();
        countryPicker();
        setCameraPopUpWindow();

        return view;
    }

    @OnClick(R.id.birthday_layout)
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment(new OnDateSetListener() {
            @Override
            public void setDate(Date date) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                birthdate.setText(simpleDateFormat.format(date));
            }

            @Override
            public void setTime(String time) {

            }
        });
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
        birthdate.setError(null);
    }

    private void bindUser() {
        Glide.with(requireContext()).load(user.getPicture()).into(picture);

        Objects.requireNonNull(name.getEditText()).setText(user.getName());
//        birthdate.setText(new SimpleDateFormat("dd/MM/yyyy").format(user.getBirthday()));
        birthdate.setText(user.getBirthdate());
        Objects.requireNonNull(countryLayout.getEditText()).setText(user.getCountry());
        Objects.requireNonNull(location.getEditText()).setText(user.getCity());

        if (user.getGender().equals("male"))
            male.setChecked(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.cancel_action, R.id.validate, R.id.camera, R.id.picture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel_action:
                Navigator.getBackstack(requireContext()).goBack();
                break;
            case R.id.validate:
                name.setErrorEnabled(true);
                countryLayout.setErrorEnabled(true);
                checkPasswordChanged();

                if (validateName(getContext(), name) && validateBirthdate(getContext(), birthdate, birthdayLayout)
                        && validateCountry(getContext(), countryLayout)) {

                    checkChanges();
                    if (photoPath != null && !photoPath.isEmpty())
                        savePicture(photoPath, changes.size() > 0);

                    if (changes.size() > 0)
                        mViewModel.saveChanges(changes);
                }
                break;
            case R.id.camera:
            case R.id.picture:
                cameraPopupWindow.showAsDropDown(picture, -150, 10); //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)
                break;
        }
    }

    private void checkChanges() {
        String _name = Objects.requireNonNull(name.getEditText()).getText().toString();
        String _birthdate = birthdate.getText().toString();
        String _country = Objects.requireNonNull(countryLayout.getEditText()).getText().toString();
        String _city = Objects.requireNonNull(location.getEditText()).getText().toString();

        if (!_name.equals(user.getName()))
            changes.put("name", _name);

        if (!_birthdate.equals(user.getBirthdate()))
            changes.put("birthdate", _birthdate);

        if (!_country.equals(user.getCountry()))
            changes.put("country", _country);

        if (!_city.equals(user.getCity()))
            changes.put("city", _city);
    }

    private void checkPasswordChanged() {
        if (!isEmpty(oldPassword) || !isEmpty(newPassword) || !isEmpty(confirmPassword)) {
            if (validatePassword(requireContext(), oldPassword)
                    && validatePasswordConfirmation(requireContext(), newPassword, confirmPassword)) {

                mViewModel.updatePassword(Objects.requireNonNull(oldPassword.getEditText()).getText().toString(),
                        Objects.requireNonNull(newPassword.getEditText()).getText().toString());
            }
        }
    }

    private void countryPicker() {
        String[] items = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, items);
        autoComplete.setAdapter(adapter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (Objects.requireNonNull(name.getEditText()).getText().hashCode() == s.hashCode()) {
            name.setErrorEnabled(false);
//        } else if (Objects.requireNonNull(mailLayout.getEditText()).getText().hashCode() == s.hashCode()) {
//            mailLayout.setErrorEnabled(false);
        } else if (Objects.requireNonNull(countryLayout.getEditText()).getText().hashCode() == s.hashCode()) {
            countryLayout.setErrorEnabled(false);
        } else if (Objects.requireNonNull(location.getEditText()).getText().hashCode() == s.hashCode()) {
//            location.setErrorEnabled(false);
            autoCompleteRequest(s.toString());
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
                .setCountry("De")
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
            autoCompleteLoc.setAdapter(adapter);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
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
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, TAKE_PICTURE);
            }
        });

        cameraPopupWindow = new PopupWindow(view, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Glide.with(this).load(data.getData()).into(picture);
                photoPath = Utils.getFilePath(requireActivity(), data.getData());
            } else if (requestCode == TAKE_PICTURE) {
                if (data != null && data.getExtras() != null) {
                    Glide.with(this).load((Bitmap) data.getExtras().get("data")).into(picture);
                    photoPath = CameraUtils.getmCurrentPhotoPath();
                }
            }
        }
    }

    private void savePicture(String photoPath, boolean otherChanges) {
        try {
            Bitmap bitmap = SiliCompressor.with(requireContext()).getCompressBitmap(photoPath);
            mViewModel.uploadPicture(bitmap, user.getId(), otherChanges);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}