package com.example.budspaces.Views.Fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.budspaces.Factory.GroupFactory;
import com.example.budspaces.Factory.MembersFactory;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.R;
import com.example.budspaces.ViewModels.GroupViewModel;
import com.example.budspaces.ViewModels.MoreMembersViewModel;
import com.google.android.material.appbar.AppBarLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MoreMembersFragment extends BaseFragment {

    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.members)
    RecyclerView members;
    @BindView(R.id.swipeToRefresh)
    SwipeRefreshLayout swipeToRefresh;

    private Unbinder unbinder;
    private MoreMembersViewModel mViewModel;

    private String title;
    private String id;
    private boolean isGroup;

    public MoreMembersFragment() {
        // Required empty public constructor
    }

    public MoreMembersFragment(String title, String id, boolean isGroup) {
        this.title = title;
        this.id = id;
        this.isGroup = isGroup;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.more_members_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new MembersFactory(id, isGroup)).get(MoreMembersViewModel.class);

        pageTitle.setText(title);

        LinearLayoutManager groupsManager = new LinearLayoutManager(getContext());
        members.setLayoutManager(groupsManager);
        members.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
            }
        });

        mViewModel.setRecyclerView(members, groupsManager);
//        members.setAdapter(new MembersAdapter(MessagesFixtures.getUsers()));

        /*
         * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
         * performs a swipe-to-refresh gesture.
         */
        swipeToRefresh.setOnRefreshListener(this::pullrefreshFeed);
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