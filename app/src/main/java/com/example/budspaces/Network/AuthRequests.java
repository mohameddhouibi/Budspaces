package com.example.budspaces.Network;

import android.util.Pair;

import com.example.budspaces.Models.Theme;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AuthRequests {
    public static JSONObject getRegisterRequest(String name, String birthdate, String mail, String country, String password) {
        JSONObject root = new JSONObject();

        try {
            JSONObject auth = new JSONObject();
            JSONObject info = new JSONObject();

            auth.put("email", mail);
            auth.put("password", password);

            info.put("name", name);
            info.put("gender", "male");
            info.put("country", country);
            info.put("birthdate", birthdate);

            root.put("auth", auth);
            root.put("info", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    public static JSONObject getRegisterRequest(String name, String birthdate, String country) {
        JSONObject root = new JSONObject();

        try {
            root.put("name", name);
            root.put("gender", "male");
            root.put("country", country);
            root.put("birthdate", birthdate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    public static JSONObject getLoginRequest(String mail, String password) {
        JSONObject root = new JSONObject();

        try {
            root.put("email", mail);
            root.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    public static JSONObject getCategoriesRequest(List<Theme> themes) {
        JSONObject root = new JSONObject();

        try {
            JSONArray interests = new JSONArray();
            for (Theme theme : themes)
                interests.put(theme.getName());

            root.put("interests", interests);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }
}
