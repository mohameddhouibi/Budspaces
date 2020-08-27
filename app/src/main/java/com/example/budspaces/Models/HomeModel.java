package com.example.budspaces.Models;

import com.example.budspaces.Enums.PostType;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class HomeModel {
    @SerializedName("type")
    @Expose
    private PostType type;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("groupPicture")
    @Expose
    private String groupPicture;
    @SerializedName("groupName")
    @Expose
    private String groupName;
    @SerializedName("groupID")
    @Expose
    private String groupID;
    @SerializedName("objID")
    @Expose
    private String objID;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("endDate")
    @Expose
    private String endDate;

    public PostType getType() {
        return type;
    }

    public void setType(PostType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupPicture() {
        return groupPicture;
    }

    public void setGroupPicture(String groupPicture) {
        this.groupPicture = groupPicture;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getObjID() {
        return objID;
    }

    public void setObjID(String objID) {
        this.objID = objID;
    }

    public DateTime getStartDate() {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return format.parseDateTime(startDate).withZone(DateTimeZone.forID("Europe/Paris"));
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return format.parseDateTime(endDate).withZone(DateTimeZone.forID("Europe/Paris"));
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

}
