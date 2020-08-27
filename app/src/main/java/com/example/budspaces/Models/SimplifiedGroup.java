package com.example.budspaces.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SimplifiedGroup {
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("picture")
    @Expose
    private String picture;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}