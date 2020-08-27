package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Main.ProfileFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ProfileKey extends BaseKey {
    public abstract String id();
    public static ProfileFragment fragment;

    public static ProfileKey create(String id) {
        if (fragment != null && fragment.isSameUser(id))
            fragment.update();

        return new AutoValue_ProfileKey(id);
    }

    @Override
    protected BaseFragment createFragment() {
        fragment = new ProfileFragment(id());
        return fragment;
    }
}