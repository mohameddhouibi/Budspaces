package com.example.budspaces.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Count {
    @SerializedName("response")
    @Expose
    private Integer response;

    public Integer getResponse() { return response; }

    @Override
    public String toString() {
        return "Count{" +
                "response='" + response + '\'' +
                '}';
    }
}
