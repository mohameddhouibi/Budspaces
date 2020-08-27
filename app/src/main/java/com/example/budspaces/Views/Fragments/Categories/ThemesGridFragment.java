package com.example.budspaces.Views.Fragments.Categories;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.CategoryChooserAdapter;
import com.example.budspaces.Adapters.GridPagerAdapter;
import com.example.budspaces.Listeners.OnCategorySelectedListener;
import com.example.budspaces.R;
import com.example.budspaces.Utils.SpacesItemDecoration;
import com.example.budspaces.ViewModels.Themes.CategoryChooserViewModel;
import com.example.budspaces.ViewModels.Themes.ThemesGridViewModel;

import java.util.ArrayList;
import java.util.List;

public class ThemesGridFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryChooserAdapter adapter;

    private int page;
    private OnCategorySelectedListener listener;

    public ThemesGridFragment() {
        // Required empty public constructor
    }

    public ThemesGridFragment(int page, OnCategorySelectedListener listener) {
        this.page = page;
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler_view_layout, container, false);

        ThemesGridViewModel themesGridViewModel = new ViewModelProvider(this).get(ThemesGridViewModel.class);

        themesGridViewModel.getThemesQuery(page, GridPagerAdapter.ITEM_PER_PAGE);
        themesGridViewModel.getThemes().observe(getViewLifecycleOwner(), themes -> {
            if (themes.size() > 0) {
                adapter = new CategoryChooserAdapter(themes, listener);
                recyclerView.setAdapter(adapter);
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        int spacingInPixelsH = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        int spacingInPixelsV = getResources().getDimensionPixelSize(R.dimen.round_radius);

        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixelsH, spacingInPixelsV));
        return view;
    }
}