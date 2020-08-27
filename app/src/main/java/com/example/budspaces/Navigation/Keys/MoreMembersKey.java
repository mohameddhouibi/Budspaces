package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.MoreMembersFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class MoreMembersKey extends BaseKey {
    public abstract String title();
    public abstract String id();
    public abstract Boolean isGroup();

    public static MoreMembersKey create(String title, String id, boolean isGroup) {
        return new AutoValue_MoreMembersKey(title, id, isGroup);
    }

    @Override
    protected BaseFragment createFragment() {
        return new MoreMembersFragment(title(), id(), isGroup());
    }
}