package com.example.budspaces.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.budspaces.Listeners.OnCategorySelectedListener;
import com.example.budspaces.Views.Fragments.Categories.ThemesGridFragment;

import java.util.List;

public class GridPagerAdapter extends FragmentStateAdapter {
    private int CARD_ITEM_SIZE;
    public static final int ITEM_PER_PAGE = 9;
    private OnCategorySelectedListener listener;

    public GridPagerAdapter(@NonNull FragmentActivity fragmentActivity, OnCategorySelectedListener listener, int count) {
        super(fragmentActivity);
        this.listener = listener;
        CARD_ITEM_SIZE = (int) Math.ceil(count / (ITEM_PER_PAGE * 1.0f));
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ThemesGridFragment(position, listener);
    }

    @Override public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}
