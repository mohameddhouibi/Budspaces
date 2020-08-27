package com.example.budspaces.Network;

import org.json.JSONException;
import org.json.JSONObject;

public class CalendarTimelineRequests {

    public static JSONObject getDayTimelineQuery(String date) {
        JSONObject root = new JSONObject();

        try {
            root.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }
}
