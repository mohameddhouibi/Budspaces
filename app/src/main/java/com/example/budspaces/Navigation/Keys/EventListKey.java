package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Groups.EventListFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class EventListKey extends BaseKey {
    public abstract String id();

    public static EventListKey create(String id) {
        return new AutoValue_EventListKey(id);
    }

    public static EventListKey create() {
        return new AutoValue_EventListKey("");
    }

    @Override
    protected BaseFragment createFragment() {
        if (id() == null || id().isEmpty())
            return new EventListFragment();

        return new EventListFragment(id());
    }
}