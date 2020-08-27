package com.example.budspaces.Samples;

import android.content.Context;

import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Group;
import com.example.budspaces.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public final class GroupsFixtures extends FixturesData {
    private GroupsFixtures() {
        throw new AssertionError();
    }
    private static int millis = 1000;
    private static List<Event> events = new ArrayList<>();

    private static ArrayList<Integer> eventNb = new ArrayList<Integer>() {
        {
            add(0);
            add(3);
            add(1);
            add(0);
            add(2);
            add(0);
            add(5);
        }
    };

    private static ArrayList<String> interestsSampleList = new ArrayList<String>() {
        {
            add("Ski");
            add("Snowboard");
            add("Aventure");
            add("Hiver");
            add("Neige");
        }
    };

    public static ArrayList<Integer> getEventNb() {
        return eventNb;
    }

    public static Group getGroup(Context context) {
        Group group = new Group();
        group.setName("Le ski est notre vie");
        group.setInterests(interestsSampleList);
        group.setAddress("Mürren, Suisse");
        group.setDescription(context.getString(R.string.post_tmp));
        group.setPicture("https://media.lesechos.com/api/v1/images/view/5e4fb8353e4546766f258c04/1280x720/b49e0658da6147fc4f3e9d1a575bc946e9b54dbd.jpg");

        return group;
    }

    public static Event getEvent() {
        Event event = new Event();


        return event;
    }

    public static List<Event> getEvents() {
        if (events.size() <= 0) {
            Event a = new Event();

        }

        return events;
    }
}
