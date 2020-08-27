package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.MainAppBar.CalendarFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CalendarKey extends BaseKey {
    public static CalendarKey create() {
        return new AutoValue_CalendarKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new CalendarFragment();
    }
}