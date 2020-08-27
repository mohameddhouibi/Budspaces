package com.example.budspaces.Views.Fragments.Main;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.GroupEventAdapter;
import com.example.budspaces.Adapters.SearchAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.SearchSettings;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.DiscoverFilterKey;
import com.example.budspaces.R;
import com.example.budspaces.Samples.SearchSamples;
import com.example.budspaces.ViewModels.Main.DiscoverViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iceteck.silicompressorr.SiliCompressor;
import com.zhuinden.simplestack.navigator.Navigator;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.budspaces.Network.AppRetrofit.TAG;

public class DiscoverFragment extends BaseFragment {
    @BindView(R.id.searchResults)
    RecyclerView searchResults;
    @BindView(R.id.page_title)
    TextView pageTitle;

    @BindView(R.id.settings)
    ImageView settings;

    @BindView(R.id.SearchTxt)
    EditText SearchTxt;

    private Unbinder unbinder;
    private DiscoverViewModel mViewModel;
    private SearchAdapter mAdapter;
    private String locationName;
    private SearchSettings searchSettings = null;
    private SearchSettings searchSetting = null;
    private SearchSettings PrimarySearchSettings = null;

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    PendingIntent pendingIntent;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discover_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DiscoverViewModel.class);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLocationAvailability().addOnSuccessListener(new OnSuccessListener<LocationAvailability>() {
                @Override
                public void onSuccess(LocationAvailability locationAvailability) {
                    Log.d(TAG, "onSuccess: locationAvailability.isLocationAvailable " + locationAvailability.isLocationAvailable());
                    if (locationAvailability.isLocationAvailable()) {
                        if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                            locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    Location location = task.getResult();
                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                        locationName = addresses.get(0).getLocality();
                                        Toast.makeText(requireContext(), "search in your current zone :"+locationName, Toast.LENGTH_SHORT).show();
                                        List<String>Intersts = new ArrayList<>();
                                        PrimarySearchSettings = new SearchSettings(true,true,Intersts,100,locationName);
                                        //mViewModel.AddSearchFilter(PrimarySearchSettings);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } else {
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
                        }

                    } else {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, pendingIntent);
                    }

                }
            });


        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
        }
        loadData();
        if (searchSetting != null) {
            searchSettings = searchSetting ;
            Toast.makeText(requireContext(), "you are now searching using your filter", Toast.LENGTH_SHORT).show();
        }
        else
        {
            searchSettings = PrimarySearchSettings ;
            Toast.makeText(requireContext(), "you are now searching using your current position", Toast.LENGTH_SHORT).show();
        }

        mViewModel.getSearchResult().observe(getViewLifecycleOwner(), searchResult -> {
            if (searchResult != null) {
                searchResults.setAdapter(new SearchAdapter(searchResult.getGroups(), searchResult.getEvents()));
            }
        });
        SearchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchSettings != null)
                    mViewModel.SearchThroughSettings(searchSettings, s.toString(), 6);
            }
        });

        pageTitle.setText(getText(R.string.discover));
        settings.setVisibility(View.VISIBLE);
        LinearLayoutManager searchManager = new LinearLayoutManager(getContext());
        searchResults.setLayoutManager(searchManager);

        searchResults.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            }
        });
    }

    private void loadData() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("com.example.budspaces", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("searchSettings", null);
        Type type = new TypeToken<SearchSettings>() {}.getType();
        searchSetting = gson.fromJson(json, type);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.settings)
    public void onViewClicked() {
        Navigator.getBackstack(requireContext()).goTo(DiscoverFilterKey.create());
    }
}