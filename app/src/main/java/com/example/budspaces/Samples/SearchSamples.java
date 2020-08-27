package com.example.budspaces.Samples;

import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchSamples {
    private static SearchSamples instance = null;

    private List<Group> groups = new ArrayList<>();
    private List<Event> events = new ArrayList<>();

    public static SearchSamples getInstance() {
        if (instance == null)
            instance = new SearchSamples();
        return instance;
    }

    private SearchSamples() { }

    public List<Group> getGroups() {
        return groups;
    }

    public List<Event> getEvents() {
        return events;
    }
}
