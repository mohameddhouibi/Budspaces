package com.example.budspaces.Models;

import java.util.Date;

public class Notification {
    private Date date;
    private String group;
    private String action;
    private String picture;
    private boolean seen;

    public Notification(Date date, String group, String picture, String action, boolean seen) {
        this.date = date;
        this.group = group;
        this.picture = picture;
        this.action = action;
        this.seen = seen;
    }

    public Date getDate() { return date; }
    public String getGroup() { return group; }
    public String getPicture() { return picture; }
    public String getAction() { return action; }
    public boolean isSeen() { return seen; }

    public void setDate(Date date) { this.date = date; }
    public void setGroup(String group) { this.group = group; }
    public void setPicture(String picture) { this.picture = picture; }
    public void setAction(String action) { this.action = action; }
    public void setSeen(boolean seen) { this.seen = seen; }
}
