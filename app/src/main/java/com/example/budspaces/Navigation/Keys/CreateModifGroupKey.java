package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Groups.CreateModifyGroupFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CreateModifGroupKey extends BaseKey {
    public abstract InterestType type();
    public abstract String id();

    public static AutoValue_CreateModifGroupKey create(InterestType type, String id) {
        return new AutoValue_CreateModifGroupKey(type,id);
    }

    @Override
    protected BaseFragment createFragment() {
        return new CreateModifyGroupFragment(type(),id());
    }
}
