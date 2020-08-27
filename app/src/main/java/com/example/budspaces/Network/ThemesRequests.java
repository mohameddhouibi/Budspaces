package com.example.budspaces.Network;

import com.example.budspaces.Models.Setting;

import org.json.JSONException;
import org.json.JSONObject;

public class ThemesRequests {

    public static JSONObject saveSettings(Setting setting) {
        JSONObject root = new JSONObject();

        try {
            root.put("notification", setting.getNotification());
            root.put("eventModified", setting.getEventModified());
            root.put("message", setting.getMessage());
            root.put("eventReminder", setting.getEventReminder());
            root.put("groupNotifications", setting.getGroupNotifications());

            root.put("showGroups", setting.getShowGroups());
            root.put("showInterests", setting.getShowInterests());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }
}
