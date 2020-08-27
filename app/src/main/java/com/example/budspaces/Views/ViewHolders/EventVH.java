package com.example.budspaces.Views.ViewHolders;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.HomeGroupsAdapter;
import com.example.budspaces.Adapters.TimelineAdapter;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.SimplifiedGroup;
import com.example.budspaces.R;

import java.util.List;

public class EventVH extends RecyclerView.ViewHolder {
    private RecyclerView recyclerView;
    private TextView title;
    private Context context;

    public EventVH(@NonNull View itemView) {
        super(itemView);
        recyclerView = itemView.findViewById(R.id.recyclerView);
        title = itemView.findViewById(R.id.title);
        context = itemView.getContext();

        LinearLayoutManager groupsManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(groupsManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = context.getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.top = context.getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
                outRect.bottom = context.getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
            }
        });
        recyclerView.setBackgroundColor(context.getResources().getColor(R.color.home_new_background));
    }

    public void bind(List<Event> events) {
        title.setVisibility(View.VISIBLE);
        itemView.findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
        recyclerView.setAdapter(new TimelineAdapter(events));
    }
}

