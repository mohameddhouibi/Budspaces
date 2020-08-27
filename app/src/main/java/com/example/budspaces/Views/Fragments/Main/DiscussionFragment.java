package com.example.budspaces.Views.Fragments.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.R;
import com.example.budspaces.Utils.SimpleDividerItemDecoration;
import com.example.budspaces.ViewModels.Main.DiscussionViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DiscussionFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.swipeToRefresh)
    SwipeRefreshLayout swipeToRefresh;

    private DiscussionViewModel mViewModel;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discussion_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(DiscussionViewModel.class);
        pageTitle.setText(getText(R.string.discussions));

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeToRefresh.setOnRefreshListener(this::pullrefreshFeed);

        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(wallManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(requireContext(), R.drawable.divider_settings, getResources().getDimension(R.dimen.activity_vertical_margin)));
        mViewModel.setRecyclerView(recyclerView, wallManager);
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