package com.example.budspaces.Views.ViewHolders;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.Room;
import com.example.budspaces.R;
import com.example.budspaces.Views.Activities.MessagesActivity;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class DiscussionVH extends RecyclerView.ViewHolder {

    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.content)
    TextView content;

    public DiscussionVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Room room) {
        if (!room.getSeen())
            itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.home_item_background));

        Glide.with(itemView).load(room.getPicture()).into(picture);
        name.setText(room.getName());
        if (room.getLastMessage() == null)
            content.setText(itemView.getContext().getText(R.string.say_hi));
        else if (room.getLastMessage().isEmpty())
            content.setText(itemView.getContext().getText(R.string.picture_message));
        else
            content.setText(room.getLastMessage());

        time.setText(setTime(room.getTimeStamp()));

        itemView.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), MessagesActivity.class);
            intent.putExtra("roomID", room.getId());
            intent.putExtra("roomName", room.getName());
            itemView.getContext().startActivity(intent);
        });
    }

    private String setTime(Date date) {
        if (DateFormatter.isToday(date)) {
            return DateFormatter.format(date, DateFormatter.Template.TIME);
        } else if (DateFormatter.isYesterday(date)) {
            return itemView.getContext().getString(R.string.yesterday);
        } else if (DateFormatter.isCurrentYear(date)) {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }
}
