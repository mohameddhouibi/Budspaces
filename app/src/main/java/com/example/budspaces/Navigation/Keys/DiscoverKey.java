package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Main.DiscoverFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DiscoverKey extends BaseKey {
    public static DiscoverKey create() {
        return new AutoValue_DiscoverKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new DiscoverFragment();
    }
}