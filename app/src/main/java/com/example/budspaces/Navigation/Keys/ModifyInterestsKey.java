package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Models.User;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Categories.ModifyInterestsFragment;
import com.google.auto.value.AutoValue;

import java.util.List;

@AutoValue
public abstract class ModifyInterestsKey extends BaseKey {
    public abstract InterestType type();
    public abstract String id();
    public abstract List<String> previousInterests();
    public static ModifyInterestsKey create(InterestType type, String id, List<String> previousInterests) {
        return new AutoValue_ModifyInterestsKey(type, id, previousInterests);
    }
    @Override
    protected BaseFragment createFragment() {
        return new ModifyInterestsFragment(type(), id(), previousInterests());
    }
}