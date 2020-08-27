package com.example.budspaces.Models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
public class SearchSettings {

    @SerializedName("containGroup")
    @Expose
    private Boolean containGroup;

    @SerializedName("containEvent")
    @Expose
    private Boolean containEvent;


    @SerializedName("interests")
    @Expose
    private List<String> interests = null;

    @SerializedName("distance")
    @Expose
    private Integer distance;

    @SerializedName("address")
    @Expose
    private String address;

    public Boolean getContainGroup() {
        return containGroup;
    }

    public SearchSettings() {
    }

    public SearchSettings(Boolean containGroup, Boolean containEvent, List<String> interests, Integer distance, String address) {
        this.containGroup = containGroup;
        this.containEvent = containEvent;
        this.interests = interests;
        this.distance = distance;
        this.address = address;
    }

    public void setContainGroup(Boolean containGroup) {
        this.containGroup = containGroup;
    }

    public Boolean getContainEvent() {
        return containEvent;
    }

    public void setContainEvent(Boolean containEvent) {
        this.containEvent = containEvent;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
