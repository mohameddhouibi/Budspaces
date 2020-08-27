package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Main.HomeFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class HomeKey extends BaseKey {
    public static HomeKey create() {
        return new AutoValue_HomeKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new HomeFragment();
    }
}