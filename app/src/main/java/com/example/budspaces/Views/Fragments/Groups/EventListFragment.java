package com.example.budspaces.Views.Fragments.Groups;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Factory.EventListFactory;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.R;
import com.example.budspaces.ViewModels.CalendarEventListViewModel;
import com.example.budspaces.ViewModels.EventListViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventListFragment extends BaseFragment {
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.events)
    RecyclerView events;
    @BindView(R.id.SearchTxt)
    EditText SearchTxt;

    private EventListViewModel eventListViewModel;
    private CalendarEventListViewModel calendarEventListViewModel;

    private Unbinder unbinder;
    private String groupId;
    private boolean inCalendar = false;

    public EventListFragment() {
        inCalendar = true;
        Log.e("ahmed", "calendar");
    }

    public EventListFragment(String id) {
        this.groupId = id;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (inCalendar)
            calendarEventListViewModel = new ViewModelProvider(this).get(CalendarEventListViewModel.class);
        else
            eventListViewModel = new ViewModelProvider(this, new EventListFactory(groupId)).get(EventListViewModel.class);

        pageTitle.setText(getText(R.string.events));

        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        events.setLayoutManager(wallManager);
        events.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            }
        });

        if (inCalendar)
            calendarEventListViewModel.setRecyclerView(events, wallManager);
        else
            eventListViewModel.setRecyclerView(events, wallManager);

        SearchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (inCalendar)
                    calendarEventListViewModel.setAdapter(events, s.toString());
                else
                    eventListViewModel.setAdapter(events, s.toString());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}