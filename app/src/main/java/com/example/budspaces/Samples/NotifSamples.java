package com.example.budspaces.Samples;

import android.content.Context;

import com.example.budspaces.Models.Notification;
import com.example.budspaces.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class NotifSamples {
    private List<Notification> notifications = new ArrayList<>();
    private static NotifSamples instance = null;
    private final int MAX_NOTIF = 15;

    public static NotifSamples getInstance(Context context) {
        if (instance == null)
            instance = new NotifSamples(context);
        return instance;
    }

    public NotifSamples(Context context) {
        Random random = new Random();
        for (int i = 0; i < MAX_NOTIF; i++) {
            boolean seen = random.nextBoolean();
            notifications.add(new Notification(new Date(), "Nom du groupe", HomeSamples.getInstance().getGroups().get(random.nextInt(7)), context.getString(R.string.post_tmp), seen));
        }
    }

    public List<Notification> getNotifications() {
        return notifications;
    }
}