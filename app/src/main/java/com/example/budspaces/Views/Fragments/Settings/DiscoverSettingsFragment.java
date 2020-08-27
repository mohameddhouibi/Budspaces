package com.example.budspaces.Views.Fragments.Settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.CategoryChooserAdapter;
import com.example.budspaces.Adapters.InterestsFilterAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Listeners.OnInterestCheckedListener;
import com.example.budspaces.Models.SearchSettings;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.DiscoverKey;
import com.example.budspaces.R;
import com.example.budspaces.ViewModels.Main.DiscoverViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import carbon.widget.Button;

public class DiscoverSettingsFragment extends BaseFragment {

    @BindView(R.id.maxDistance)
    TextView maxDistance;

    @BindView(R.id.distance)
    RangeSeekBar distance;

    @BindView(R.id.interestTxt)
    TextView interestTxt;

    @BindView(R.id.page_title)
    TextView pageTitle;

    @BindView(R.id.interestLayout)
    ConstraintLayout interestLayout;

    @BindView(R.id.previous)
    ImageView previous;

    @BindView(R.id.location)
    TextInputLayout location;

    @BindView(R.id.autoComplete)
    AutoCompleteTextView autoComplete;

    @BindView(R.id.validate)
    Button validate;

    @BindView(R.id.groupCheckbox)
    CheckBox groupCheckbox;

    @BindView(R.id.eventCheckbox)
    CheckBox eventCheckbox;


    private List<String> themeList = new ArrayList<>();
    private List<String> themeSelected = new ArrayList<>();
    private Unbinder unbinder;
    private PopupWindow popupWindow;
    private PlacesClient placesClient;
    private final String TAG = "SettingsSearchFrag";
    private LatLng addressCoordinates ;
    private Boolean ContainGroup=false;
    private Boolean Containevent=false;
    private DiscoverViewModel mViewModel;
    private SearchSettings searchSettings;

    public DiscoverSettingsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,@Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        placesClient = Places.createClient(requireContext());
        Objects.requireNonNull(location.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                autoCompleteRequest(s.toString());
            }
        });
        setPopUpWindow();
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DiscoverViewModel.class);
        mViewModel.GetSearchFilter();
        mViewModel.getThemesQuery();
        mViewModel.getSettings().observe(getViewLifecycleOwner(), settings -> {
            if (settings != null) {
               FillSettings(settings);
            }
        });
        mViewModel.getThemes().observe(getViewLifecycleOwner(), themes -> {
            if (themes.size() > 0) {
                for (int i=0;i<themes.size();i++)
                {

                    themeList.add(themes.get(i).getName());
                }

            }
        });
        pageTitle.setText(getString(R.string.search_filter));
        maxDistance.setText("0");
        distance.setIndicatorTextDecimalFormat("0");
        distance.setOnRangeChangedListener(new OnRangeChangedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {

                maxDistance.setText( String. valueOf(Math.round(leftValue)) );
            }
            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });
    }
    private void setPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_menu_discover, null);
        previous.setVisibility(View.VISIBLE);
        RecyclerView themes = view.findViewById(R.id.themes);
        popupWindow = new PopupWindow(view, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        themes.setLayoutManager(gridLayoutManager);
        themes.setAdapter(new InterestsFilterAdapter(themeList, checkedListener));
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

    private void FillSettings(SearchSettings searchSettings) {
        this.searchSettings = searchSettings;
        groupCheckbox.setChecked(searchSettings.getContainGroup());
        eventCheckbox.setChecked(searchSettings.getContainEvent());
        Objects.requireNonNull(interestTxt).setText(null);
        Objects.requireNonNull(location.getEditText()).setText(searchSettings.getAddress());
        Objects.requireNonNull( maxDistance).setText(searchSettings.getDistance().toString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.interestLayout, R.id.previous,R.id.validate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.interestLayout:
                popupWindow.showAsDropDown(interestLayout, 0, 45); //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)
                break;
            case R.id.previous:
                Navigator.getBackstack(requireContext()).goBack();
                break;
            case R.id.validate:
                ContainGroup = groupCheckbox.isChecked();
                Containevent = eventCheckbox.isChecked();
                String AdresseUser = Objects.requireNonNull(location.getEditText()).getText().toString();
                Cache.getInstance().saveSession("location", AdresseUser);


                String Interst = interestTxt.getText().toString();
                String regex = ",";
                List<String>Intersts = Arrays.asList(Interst.split(regex));
                int distance =Integer.parseInt(maxDistance.getText().toString());
                SearchSettings searchSettings = new SearchSettings(ContainGroup,Containevent,Intersts,distance,AdresseUser);
                saveData(searchSettings);
                break;

        }
    }
    private void saveData(SearchSettings searchSettings) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("com.example.budspaces", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(searchSettings);
        editor.putString("searchSettings", json);
        editor.apply();
        Log.e("zz","done");
    }

    OnInterestCheckedListener checkedListener = new OnInterestCheckedListener() {
        @Override
        public void setTheme(String theme) {
            themeSelected.add(theme);
            interestTxt.setText(TextUtils.join(",",themeSelected));
        }

        @Override
        public void delTheme(String theme) {
            themeSelected.remove(theme);
            interestTxt.setText(TextUtils.join(",",themeSelected));
        }
    };


}