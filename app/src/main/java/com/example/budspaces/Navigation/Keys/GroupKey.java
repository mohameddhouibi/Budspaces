package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Groups.GroupFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GroupKey extends BaseKey {
    public abstract String id();

    public static GroupKey create(String id) {
        return new AutoValue_GroupKey(id);
    }

    @Override
    protected BaseFragment createFragment() {
        return new GroupFragment(id());
    }
}