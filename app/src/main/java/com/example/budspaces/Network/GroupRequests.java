package com.example.budspaces.Network;

import android.util.Pair;

import com.example.budspaces.Models.Theme;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupRequests {
    public static JSONObject addGroupRequest(String name, String description, String address) {
        JSONObject root = new JSONObject();
        try {
            root.put("name", name);
            root.put("description", description);
            root.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static JSONObject updateGroupPicture(String groupID, String link) {
        JSONObject root = new JSONObject();
        try {
            root.put("_id", groupID);
            root.put("picture", link);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static JSONObject updateGroupInterests(String groupID, List<Theme> themes) {
        JSONObject root = new JSONObject();

        try {
            JSONArray interests = new JSONArray();
            for (Theme theme : themes)
            interests.put(theme.getName());
            root.put("_id", groupID);
            root.put("interests", interests);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }

    public static JSONObject getGroupFrontMembersReq(int limit) {
        JSONObject root = new JSONObject();
        try {
            root.put("limit", limit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
    public static JSONObject updateGroupDetails(HashMap<String, String> changes) {
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
