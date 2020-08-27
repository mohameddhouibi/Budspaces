package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Models.User;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Settings.ModifyProfileFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class ModifyProfileKey extends BaseKey {
    public abstract User user();

    public static ModifyProfileKey create(User user) {
        return new AutoValue_ModifyProfileKey(user);
    }

    @Override
    protected BaseFragment createFragment() {
        return new ModifyProfileFragment(user());
    }
}