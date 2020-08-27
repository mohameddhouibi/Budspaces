package com.example.budspaces.Views.ViewHolders;

import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.R;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

public class LoadingVH extends RecyclerView.ViewHolder {
    public ProgressBar mProgressBar;

    public LoadingVH(View itemView) {
        super(itemView);
        mProgressBar = itemView.findViewById(R.id.loadmore_progress);
    }
}