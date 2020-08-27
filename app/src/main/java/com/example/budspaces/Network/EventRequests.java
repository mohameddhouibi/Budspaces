package com.example.budspaces.Network;
import com.example.budspaces.Models.Theme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventRequests {

    public static JSONObject addEventRequest(String groupId,String name, String description, String address,String startDate,String EndDate,String picture
            ) {
        JSONObject root = new JSONObject();
        try {
            root.put("host", groupId);
            root.put("name", name);
            root.put("description", description);
            root.put("address", address);
            root.put("startDate", startDate);
            root.put("endDate", EndDate);
            root.put("picture", picture);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static JSONObject updateEventPicture(String eventID, String link) {
        JSONObject root = new JSONObject();
        try {
            root.put("_id", eventID);
            root.put("picture", link);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static JSONObject updateEventDetails(HashMap<String, String> changes) {
        JSONObject root = new JSONObject();

        try {
            for (Map.Entry<String, String> change : changes.entrySet()) {
                root.put(change.getKey(), change.getValue());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }
}
