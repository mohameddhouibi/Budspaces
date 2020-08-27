package com.example.budspaces.Factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.budspaces.ViewModels.GroupViewModel;

public class GroupFactory implements ViewModelProvider.Factory {
    private String groupID;

    public GroupFactory(String groupID) {
        this.groupID = groupID;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GroupViewModel(groupID);
    }
}