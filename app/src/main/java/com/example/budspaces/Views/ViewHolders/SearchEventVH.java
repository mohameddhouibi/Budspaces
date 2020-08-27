package com.example.budspaces.Views.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Navigation.Keys.EventKey;
import com.example.budspaces.Navigation.Keys.GroupKey;
import com.example.budspaces.R;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchEventVH extends RecyclerView.ViewHolder {
    @BindView(R.id.picture)
    ImageView picture;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.location)
    TextView location;

    public SearchEventVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Event event) {
        Glide.with(itemView).load(event.getPicture()).into(picture);
        name.setText(event.getName());

        Calendar today = Calendar.getInstance();
        Calendar eventDate = Calendar.getInstance();
        eventDate.setTime(event.getStartDate());

        if (today.get(Calendar.YEAR) == eventDate.get(Calendar.YEAR))
            date.setText(android.text.format.DateFormat.format("E dd MMMM - hh:mm", event.getStartDate()));
        else
            date.setText(android.text.format.DateFormat.format("E dd MMMM yyyy - hh:mm", event.getStartDate()));
        location.setText(event.getAddress());
        itemView.setOnClickListener((v) -> {
            Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(EventKey.create(event.getId()));
        });
    }
}