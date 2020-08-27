package com.example.budspaces.Network;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileRequests {
    public static JSONObject updatePicture(String link) {
        JSONObject root = new JSONObject();
        try {
            root.put("picture", link);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static JSONObject updateProfile(HashMap<String, String> changes) {
        JSONObject root = new JSONObject();

        try {
            for (Map.Entry<String, String> change : changes.entrySet()) {
                root.put(change.getKey(), change.getValue());
            }

//            root.put("birthdate", birthdate);
//            root.put("country", country);
//            root.put("city", city);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }
}