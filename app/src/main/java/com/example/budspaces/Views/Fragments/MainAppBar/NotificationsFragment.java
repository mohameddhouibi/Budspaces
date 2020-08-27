package com.example.budspaces.Views.Fragments.MainAppBar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.NotificationsAdapter;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.R;
import com.example.budspaces.Samples.NotifSamples;
import com.example.budspaces.Utils.SimpleDividerItemDecoration;
import com.example.budspaces.ViewModels.Appbar.NotificationsViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NotificationsFragment extends BaseFragment {
    @BindView(R.id.notifList)
    RecyclerView notifList;
    @BindView(R.id.page_title)
    TextView pageTitle;

    private NotificationsViewModel mViewModel;
    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notifications_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);

        pageTitle.setText(getString(R.string.notifications));

        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        notifList.setItemAnimator(new DefaultItemAnimator());
        notifList.setLayoutManager(wallManager);
        notifList.addItemDecoration(new SimpleDividerItemDecoration(requireContext(), R.drawable.divider_notif, getResources().getDimension(R.dimen.activity_vertical_margin)));
        notifList.setAdapter(new NotificationsAdapter(NotifSamples.getInstance(getContext()).getNotifications()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}