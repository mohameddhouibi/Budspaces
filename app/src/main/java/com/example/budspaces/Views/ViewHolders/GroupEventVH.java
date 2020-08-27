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

import java.util.Objects;

public class GroupEventVH extends RecyclerView.ViewHolder {
    private ImageView picture;
    private TextView name;
    private TextView date;

    public GroupEventVH(@NonNull View itemView) {
        super(itemView);
        picture = itemView.findViewById(R.id.picture);
        name = itemView.findViewById(R.id.name);
        date = itemView.findViewById(R.id.date);
    }

    public void bind(Event event) {
        Glide.with(itemView).load(event.getPicture()).into(picture);
        name.setText(event.getName());
        date.setText(android.text.format.DateFormat.format("dd MMMM", event.getStartDate()));

        itemView.setOnClickListener(v -> {
            Navigator.getBackstack(Objects.requireNonNull(itemView.getContext())).goTo(EventKey.create(event.getId()));
        });
    }
}
