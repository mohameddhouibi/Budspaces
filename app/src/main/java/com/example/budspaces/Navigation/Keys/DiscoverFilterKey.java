package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Settings.DiscoverSettingsFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DiscoverFilterKey extends BaseKey {
    public static DiscoverFilterKey create() {
        return new AutoValue_DiscoverFilterKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new DiscoverSettingsFragment();
    }
}