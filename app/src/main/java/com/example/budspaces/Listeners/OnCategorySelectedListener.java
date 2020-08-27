package com.example.budspaces.Listeners;

import com.example.budspaces.Models.Theme;

public interface OnCategorySelectedListener {
    boolean selectCat(Theme cat);
    void delCat(Theme cat);
    boolean isSelected(String catName);
}