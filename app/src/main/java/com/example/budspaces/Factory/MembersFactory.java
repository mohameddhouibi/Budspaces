package com.example.budspaces.Factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.budspaces.ViewModels.MoreMembersViewModel;

public class MembersFactory implements ViewModelProvider.Factory {
    private String id;
    private boolean isGroup;

    public MembersFactory(String id, boolean isGroup) {
        this.id = id;
        this.isGroup = isGroup;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MoreMembersViewModel(id, isGroup);
    }
}
