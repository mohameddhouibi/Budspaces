package com.example.budspaces.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Listeners.OnDateSetListener;
import com.example.budspaces.R;
import com.example.budspaces.Samples.GroupsFixtures;
import com.example.budspaces.Views.ViewHolders.SearchEventVH;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.ArrayList;
import java.util.List;

public class WeekCalendarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int DAY_NAME = 1;
    private final int DAY_DATE = 2;
    private final int DAY_EVENT = 3;

    private String[] day_names;
    private List<String> day_dates = new ArrayList<>();
    private List<Integer> eventNb = new ArrayList<>();
    private int todayPosition = -1;
    private DateTime startDate;

    private OnDateSetListener onDateSetListener;

    public WeekCalendarAdapter(Context context, DateTime startDate, OnDateSetListener onDateSetListener) {
        this.startDate = startDate;
        this.onDateSetListener = onDateSetListener;

        day_names = context.getResources().getStringArray(R.array.week_days);
        initDays();
    }

    public void setEventNb(List<Integer> eventNb) {
        this.eventNb = eventNb;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case DAY_NAME:
                View viewGT = inflater.inflate(R.layout.item_calendar_day_name, parent, false);
                viewHolder = new DayTextVH(viewGT);
                break;
            case DAY_DATE:
                View viewGI = inflater.inflate(R.layout.item_calendar_day_date, parent, false);
                viewHolder = new DayTextVH(viewGI);
                break;
            case DAY_EVENT:
                View viewEI = inflater.inflate(R.layout.item_calendar_events, parent, false);
                viewHolder = new DayEventVH(viewEI);
                break;
        }

        assert viewHolder != null;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case DAY_NAME:
                ((DayTextVH)holder).bind(day_names[position], position);
                if (position == todayPosition)
                    ((DayTextVH)holder).showBackground(true, 0);
                else
                    ((DayTextVH)holder).showBackground(false, 0);
                break;
            case DAY_DATE:
                ((DayTextVH)holder).bind(day_dates.get(position - 7), position - 7);
                if ((position - 7) == todayPosition)
                    ((DayTextVH)holder).showBackground(true, 1);
                else
                    ((DayTextVH)holder).showBackground(false, 1);
                break;
            case DAY_EVENT:
                ((DayEventVH)holder).bind(eventNb.get(position - 14));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 14+ eventNb.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 7)
            return DAY_NAME;
        else if (position < 14)
            return DAY_DATE;

        return DAY_EVENT;
    }

    private void initDays() {
        DateTime today = new DateTime();

        //Getting all seven days
        for (int i = 0; i <= 6; i++) {
            DateTime day = startDate.plusDays(i);
            day_dates.add(String.valueOf(day.getDayOfMonth()));

            if (day.getDayOfMonth() == today.getDayOfMonth()) {
                todayPosition = i;

                onDateSetListener.setTime(startDate.plusDays(todayPosition).toString("yyyy-MM-dd"));
            }
        }
    }

    public class DayTextVH extends RecyclerView.ViewHolder {
        private TextView textView;

        public DayTextVH(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item);
        }

        public void bind(String day, int position) {
            textView.setText(day);

            itemView.setOnClickListener(v -> {
                onDateSetListener.setTime(startDate.plusDays(position).toString("yyyy-MM-dd"));

                int tmp = todayPosition;
                todayPosition = position;

                notifyItemChanged(todayPosition);
                notifyItemChanged(todayPosition+7);
                notifyItemChanged(tmp);
                notifyItemChanged(tmp+7);
            });
        }

        public void showBackground(boolean show, int direction) {
            if (!show) {
                itemView.setBackgroundColor(Color.WHITE);
            } else {
                if (direction == 1) {
                    itemView.setBackgroundResource(R.drawable.semi_circle_down);
                } else
                    itemView.setBackgroundResource(R.drawable.semi_circle_up);
            }
        }
    }

    public static class DayEventVH extends RecyclerView.ViewHolder {
        private ImageView first, second, third;

        public DayEventVH(@NonNull View itemView) {
            super(itemView);
            first = itemView.findViewById(R.id.first);
            second = itemView.findViewById(R.id.second);
            third = itemView.findViewById(R.id.third);
        }

        public void bind(int eventNb) {
            switch (eventNb) {
                case 1:
                    first.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    second.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    third.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
