package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Main.DiscussionFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class DiscussionKey extends BaseKey  {
    public static DiscussionKey create() {
        return new AutoValue_DiscussionKey();
    }

    @Override
    protected BaseFragment createFragment() {
        return new DiscussionFragment();
    }
}
