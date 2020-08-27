package com.example.budspaces.Views.Fragments.MainAppBar;

import android.graphics.Rect;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Adapters.TimelineAdapter;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.R;
import com.example.budspaces.ViewModels.Appbar.CalendarViewModel;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class FullCalendarFragment extends BaseFragment {
    @BindView(R.id.previous)
    ImageView previous;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.compactcalendar_view)
    CompactCalendarView compactcalendarView;
    @BindView(R.id.timeline)
    RecyclerView timeline;

    private Unbinder unbinder;
    private CalendarViewModel mViewModel;
    private List<com.example.budspaces.Models.Event> EVents = null;
    private DateTime actualDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.full_calendar_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        initMonth(new DateTime());

        pageTitle.setText(getText(R.string.calendar));
        previous.setVisibility(View.VISIBLE);
        date.setText(DateFormat.format("MMMM, yyyy", new Date()));
        initCalendar();
        timeline.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);

                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
            }
        });
    }

    private void initMonth(DateTime dateTime) {
        actualDate = dateTime;

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        DateTime firstDayOfMonth = dateTime.dayOfMonth().withMinimumValue();
        String firstDay = new LocalDate(firstDayOfMonth).toString(formatter);
        int daysOfMonth = dateTime.dayOfMonth().getMaximumValue();

        mViewModel.getEventsCountByDay(firstDay, daysOfMonth);
    }

    private void initCalendar() {
        compactcalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactcalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                String ClickedDate = (( DateFormat.format("yyyy", dateClicked)+ "-"
                        + DateFormat.format("MM", dateClicked) + "-"
                        + DateFormat.format("dd", dateClicked)));
                mViewModel.loadDateTimline(ClickedDate);
                mViewModel.getEvents().observe(getViewLifecycleOwner(), events -> {
                    if (events != null) {
                        initTimeline(events);
                 }
                    else {
                    }
                });
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                date.setText(DateFormat.format("MMMM, yyyy", firstDayOfNewMonth));
                initMonth(new DateTime(firstDayOfNewMonth.getTime()));
            }
        });

        mViewModel.getEventsCount().observe(getViewLifecycleOwner(), eventsCount -> {
            compactcalendarView.removeAllEvents();
            if (eventsCount.size() <= 0)
                return;

            DateTime firstDayOfMonth = actualDate.dayOfMonth().withMinimumValue();

            for (int day=0; day<eventsCount.size(); day++) {
                int count = eventsCount.get(day) > 3 ? 3 : eventsCount.get(day);

                while (count-- > 0) {
                    compactcalendarView.addEvent(new Event(getResources().getColor(R.color.mainRed), firstDayOfMonth.getMillis() + day * 86400000L));
                }
            }
        });
    }

    private void initTimeline(List<com.example.budspaces.Models.Event>eventList) {
        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        timeline.setLayoutManager(wallManager);
        timeline.setAdapter(new TimelineAdapter(eventList));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.previous, R.id.prevMonth, R.id.nextMonth})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.previous:
                requireActivity().onBackPressed();
                break;
            case R.id.prevMonth:
                compactcalendarView.scrollLeft();
                break;
            case R.id.nextMonth:
                compactcalendarView.scrollRight();
                break;
        }
    }
}