package com.example.budspaces.Navigation.Core;

import androidx.fragment.app.Fragment;

/**
 * Created by Owner on 2017. 06. 29..
 */

public class BaseFragment extends Fragment {
    public final <T extends BaseKey> T getKey() {
        return getArguments() != null ? getArguments().<T>getParcelable("KEY") : null;
    }
}