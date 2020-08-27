package com.example.budspaces.Views.ViewHolders;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Models.Event;
import com.example.budspaces.Navigation.Keys.EventKey;
import com.example.budspaces.R;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TimelineVH extends RecyclerView.ViewHolder {

    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.eventName)
    TextView eventName;
    @BindView(R.id.groupName)
    TextView groupName;
    @BindView(R.id.period)
    TextView period;

    public TimelineVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Event event) {
        eventName.setText(event.getName());
        groupName.setText(event.getgroupName());
        //Log.e("ee",event.getGroupeName());
        String startTime = (String) DateFormat.format("hh:mm", event.getStartDate());
        String endTime = (String) DateFormat.format("hh:mm", event.getEndDate());
        time.setText(startTime);
        period.setText(startTime + " - " + endTime);

        itemView.setOnClickListener(v -> {
            Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(EventKey.create(event.getId()));
        });
    }
}