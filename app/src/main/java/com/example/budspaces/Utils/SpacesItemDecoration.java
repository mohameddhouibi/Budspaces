package com.example.budspaces.Utils;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int spaceH;
    private int spaceV;

    public SpacesItemDecoration(int spaceH, int spaceV) {
        this.spaceH = spaceH;
        this.spaceV = spaceV;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = spaceH;
        outRect.right = spaceH;
        outRect.bottom = spaceV;

        // Add top margin only for the first item to avoid double space between items
//        if (parent.getChildLayoutPosition(view) == 0) {
//            outRect.top = space;
//        } else {
            outRect.top = 10;
//        }
    }
}