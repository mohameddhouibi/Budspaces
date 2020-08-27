package com.example.budspaces.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Setting {
    @SerializedName("notification")
    @Expose
    private Boolean notification;
    @SerializedName("eventModified")
    @Expose
    private Boolean eventModified;
    @SerializedName("message")
    @Expose
    private Boolean message;
    @SerializedName("eventReminder")
    @Expose
    private Boolean eventReminder;
    @SerializedName("groupNotifications")
    @Expose
    private Boolean groupNotifications;
    @SerializedName("showGroups")
    @Expose
    private Boolean showGroups;
    @SerializedName("showInterests")
    @Expose
    private Boolean showInterests;

    public Boolean getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public Boolean getEventModified() {
        return eventModified;
    }

    public void setEventModified(Boolean eventModified) {
        this.eventModified = eventModified;
    }

    public Boolean getMessage() {
        return message;
    }

    public void setMessage(Boolean message) {
        this.message = message;
    }

    public Boolean getEventReminder() {
        return eventReminder;
    }

    public void setEventReminder(Boolean eventReminder) {
        this.eventReminder = eventReminder;
    }

    public Boolean getGroupNotifications() {
        return groupNotifications;
    }

    public void setGroupNotifications(Boolean groupNotifications) {
        this.groupNotifications = groupNotifications;
    }

    public Boolean getShowGroups() {
        return showGroups;
    }

    public void setShowGroups(Boolean showGroups) {
        this.showGroups = showGroups;
    }

    public Boolean getShowInterests() {
        return showInterests;
    }

    public void setShowInterests(Boolean showInterests) {
        this.showInterests = showInterests;
    }
}
