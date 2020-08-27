package com.example.budspaces.Navigation.Keys;

import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Views.Fragments.Groups.CreateModifEventFragment;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CreateModifEventKey extends BaseKey {
    public abstract InterestType type();
    public abstract String id();
    public abstract String groupeName();

    public static AutoValue_CreateModifEventKey create(InterestType type, String id , String groupeName) {
        return new AutoValue_CreateModifEventKey(type,id,groupeName);
    }

    @Override
    protected BaseFragment createFragment() {
        return new CreateModifEventFragment(type(),id(),groupeName());
    }
}