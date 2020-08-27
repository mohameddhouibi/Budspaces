package com.example.budspaces.Views.ViewHolders;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Models.Notification;
import com.example.budspaces.R;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationVH extends RecyclerView.ViewHolder {
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.date)
    TextView date;

    public NotificationVH(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bind(Notification notification) {
        if (notification.isSeen())
            itemView.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.home_item_background));

        Glide.with(itemView).load(notification.getPicture()).into(picture);

        long createdAt = notification.getDate().getTime();
        long now = new Date().getTime();

        String result = DateUtils.getRelativeTimeSpanString(createdAt, now, 0).toString();
        date.setText(result);

        String fullContent = notification.getGroup() + " " + notification.getAction();

        Spannable spannable = new SpannableString(fullContent);
        Context context = itemView.getContext();

        Typeface myTypeface = Typeface.create(ResourcesCompat.getFont(context, R.font.roboto_bold),
                Typeface.NORMAL);

        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue_text)),
                0, notification.getGroup().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(myTypeface, 0, notification.getGroup().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new RelativeSizeSpan(1.3f), 0, notification.getGroup().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        content.setText(spannable, TextView.BufferType.SPANNABLE);
    }
}
