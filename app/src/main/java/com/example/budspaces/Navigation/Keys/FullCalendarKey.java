package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.MainAppBar.FullCalendarFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class FullCalendarKey extends BaseKey{
    public static FullCalendarKey create() {
        return new AutoValue_FullCalendarKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new FullCalendarFragment();
    }
}