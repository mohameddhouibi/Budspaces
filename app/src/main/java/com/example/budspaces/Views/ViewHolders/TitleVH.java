package com.example.budspaces.Views.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.budspaces.Enums.TitleType;
import com.example.budspaces.R;

public class TitleVH extends RecyclerView.ViewHolder {
    private TextView title;

    public TitleVH(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
    }

    public void bind(TitleType type) {
        switch (type) {
            case events:
                title.setText(itemView.getContext().getString(R.string.events));
                break;
            case groups:
                title.setText(itemView.getContext().getString(R.string.groups));
                break;
            case earlier:
                title.setText(itemView.getContext().getString(R.string.earlier));
                break;
            case this_month:
                title.setText(itemView.getContext().getString(R.string.this_month));
                break;
        }
    }
}