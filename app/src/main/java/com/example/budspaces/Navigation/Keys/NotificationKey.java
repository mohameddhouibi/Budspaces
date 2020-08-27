package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.MainAppBar.NotificationsFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class NotificationKey extends BaseKey {
    public static NotificationKey create() {
        return new AutoValue_NotificationKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new NotificationsFragment();
    }
}