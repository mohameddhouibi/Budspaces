package com.example.budspaces.Network;

import com.example.budspaces.Models.SearchSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchRequests {

    public static JSONObject searchRequest(SearchSettings searchSettings,String name,Integer Limit) {
        JSONObject root = new JSONObject();
        try {
            root.put("address", searchSettings.getAddress());
            root.put("distance", searchSettings.getDistance());
            root.put("containGroup", searchSettings.getContainGroup());
            root.put("containEvent", searchSettings.getContainEvent());
            JSONArray interests = new JSONArray();
            for (String interest : searchSettings.getInterests())
                interests.put(interest);
            root.put("interests", interests);
            root.put("name", name);
            root.put("limit", Limit);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }



    public static JSONObject AddFilterRequest(SearchSettings searchSettings) {
        JSONObject root = new JSONObject();
        try {
            root.put("address", searchSettings.getAddress());
            root.put("distance", searchSettings.getDistance());
            root.put("containGroup", searchSettings.getContainGroup());
            root.put("containEvent", searchSettings.getContainEvent());
            JSONArray interests = new JSONArray();
            for (String interest : searchSettings.getInterests())
                interests.put(interest);
            root.put("interests", interests);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
}
