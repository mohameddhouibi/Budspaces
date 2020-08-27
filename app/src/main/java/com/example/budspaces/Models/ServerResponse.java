package com.example.budspaces.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerResponse {
    @SerializedName("response")
    @Expose
    private String response;

    public String getResponse() { return response; }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "response='" + response + '\'' +
                '}';
    }
}
