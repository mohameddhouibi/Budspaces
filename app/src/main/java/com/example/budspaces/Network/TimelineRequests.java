package com.example.budspaces.Network;

import org.json.JSONException;
import org.json.JSONObject;

public class TimelineRequests {

    public static JSONObject setAnnouncement(String title, String content, String groupId, String groupName, String groupPicture) {
        JSONObject root = new JSONObject();

        try {
            root.put("title", title);
            root.put("content", content);
            root.put("groupID", groupId);
            root.put("groupName", groupName);
            root.put("groupPicture", groupPicture);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }
}
