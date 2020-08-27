package com.example.budspaces.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult {

    @SerializedName("groups")
    @Expose
    List<Group> groups ;

    @SerializedName("events")
    @Expose
    List<Event> events ;

    public SearchResult() {
    }

    public SearchResult(List<Group> groups, List<Event> events) {
        this.groups = groups;
        this.events = events;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
