package com.example.budspaces.Views.ViewHolders.Messages;

import android.graphics.Typeface;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.example.budspaces.Models.Message;
import com.example.budspaces.R;
import com.stfalcon.chatkit.messages.MessageHolders;

public class CustomOutcomingTextMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private boolean isShown = false;

    public CustomOutcomingTextMessageViewHolder(View itemView, Object payload) {
        super(itemView, payload);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        time.setTextColor(itemView.getContext().getResources().getColor(R.color.blue_text));
        Typeface myTypeface = Typeface.create(ResourcesCompat.getFont(itemView.getContext(), R.font.arial_regular),
                Typeface.NORMAL);
        time.setTypeface(myTypeface);

        text.setOnLongClickListener((v) -> {
            showHideState();
            return false;
        });
    }

    public void showHideState() {
        isShown = !isShown;

        if (isShown) {
            time.setVisibility(View.VISIBLE);
        } else {
            time.setVisibility(View.INVISIBLE);
        }
    }
}
