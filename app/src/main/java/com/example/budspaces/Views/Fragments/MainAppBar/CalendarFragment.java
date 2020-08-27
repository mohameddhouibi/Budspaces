package com.example.budspaces.Views.Fragments.MainAppBar;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.GroupEventAdapter;
import com.example.budspaces.Adapters.TimelineAdapter;
import com.example.budspaces.Adapters.WeekCalendarAdapter;
import com.example.budspaces.Listeners.OnDateSetListener;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.EventListKey;
import com.example.budspaces.Navigation.Keys.FullCalendarKey;
import com.example.budspaces.R;
import com.example.budspaces.Samples.GroupsFixtures;
import com.example.budspaces.Samples.SearchSamples;
import com.example.budspaces.ViewModels.Appbar.CalendarViewModel;
import com.zhuinden.simplestack.navigator.Navigator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CalendarFragment extends BaseFragment {
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.gridView)
    RecyclerView gridView;
    @BindView(R.id.events)
    RecyclerView events;
    @BindView(R.id.timeline)
    RecyclerView timeline;

    private CalendarViewModel mViewModel;
    private Unbinder unbinder;

    private OnDateSetListener onDateSetListener = new OnDateSetListener() {
        @Override
        public void setDate(Date date) { }

        @Override
        public void setTime(String time) {
            mViewModel.loadDateTimline(time);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        DateTime startDate = new DateTime().withDayOfWeek(DateTimeConstants.SUNDAY);
        if (startDate.isAfterNow())
            startDate = startDate.minusDays(7);

        mViewModel.getEventsCountByDay(startDate.toString("yyyy-MM-dd"), 7);

        pageTitle.setText(getText(R.string.calendar));
        date.setText(DateFormat.format("MMMM, yyyy", new Date()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 7);
        gridView.setLayoutManager(gridLayoutManager);
        WeekCalendarAdapter adapter = new WeekCalendarAdapter(requireContext(), startDate, onDateSetListener);
        gridView.setAdapter(adapter);

        mViewModel.getEventsCount().observe(getViewLifecycleOwner(), eventCount -> {
            if (eventCount.size() > 0)
                adapter.setEventNb(eventCount);
        });

        initEvents();
        initTimeline();

        mViewModel.getEvents().observe(getViewLifecycleOwner(), eventList -> {
            if (eventList != null) {
                events.setAdapter(new GroupEventAdapter(eventList));
                timeline.setAdapter(new TimelineAdapter(eventList));
            }
        });
    }

    private void initEvents() {
        LinearLayoutManager groupsManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        events.setLayoutManager(groupsManager);
        events.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
            }
        });
    }

    private void initTimeline() {
        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        timeline.setLayoutManager(wallManager);
        timeline.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.calendar, R.id.moreEvents})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.calendar:
                Navigator.getBackstack(requireContext()).goTo(FullCalendarKey.create());
                break;
            case R.id.moreEvents:
                Navigator.getBackstack(requireContext()).goTo(EventListKey.create());
                break;
        }
    }
}