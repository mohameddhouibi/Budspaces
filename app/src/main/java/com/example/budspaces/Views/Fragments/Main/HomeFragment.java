package com.example.budspaces.Views.Fragments.Main;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.budspaces.Enums.NavBarType;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.CalendarKey;
import com.example.budspaces.Navigation.Keys.DiscoverKey;
import com.example.budspaces.Navigation.Keys.NotificationKey;
import com.example.budspaces.R;
import com.example.budspaces.ViewModels.Main.HomeViewModel;
import com.example.budspaces.Views.Activities.MainActivity;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipeToRefresh)
    SwipeRefreshLayout swipeToRefresh;
    @BindView(R.id.discoverLayout)
    ConstraintLayout discoverLayout;

    private HomeViewModel mViewModel;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeToRefresh.setOnRefreshListener(this::pullrefreshFeed);

        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(wallManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.round_radius);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
            }
        });

        mViewModel.setRecyclerView(recyclerView, wallManager);

        mViewModel.IsEmpty().observe(getViewLifecycleOwner(), isEmpty -> {
            if (isEmpty) {
                discoverLayout.setVisibility(View.VISIBLE);
            } else {
                discoverLayout.setVisibility(View.GONE);
            }
        });
    }

    @OnClick({R.id.calendar, R.id.notifications, R.id.action})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.calendar:
                Navigator.getBackstack(requireContext()).goTo(CalendarKey.create());
                break;
            case R.id.notifications:
                Navigator.getBackstack(requireContext()).goTo(NotificationKey.create());
                break;
            case R.id.action:
                ((MainActivity) requireActivity()).goTo(NavBarType.discover);
//                Navigator.getBackstack(requireContext()).setHistory(History.of(DiscoverKey.create()), StateChange.REPLACE);
                break;
        }
    }

    private void pullrefreshFeed() {
        swipeToRefresh.setRefreshing(false);
        mViewModel.doRefresh();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}