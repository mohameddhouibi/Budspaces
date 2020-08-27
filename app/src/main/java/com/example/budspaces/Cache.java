package com.example.budspaces;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class Cache {
    private static Cache instance = null;
    private SharedPreferences preferences;
    private File cacheDirectory;

    public static Cache getInstance(Context context) {
        if (instance == null)
            instance = new Cache(context);
        return instance;
    }

    public static Cache getInstance() {
        return instance;
    }

    private Cache(Context context) {
        preferences = context.getSharedPreferences("com.example.budspaces", Context.MODE_PRIVATE);
        cacheDirectory = context.getCacheDir();
    }

    public void saveSession(String key, String value) {
        //Save it
        preferences.edit().putString(key, value).apply();
    }

    public void saveSession(String key, boolean value) {
        //Save it
        preferences.edit().putBoolean(key, value).apply();
    }

    public void clearSession(String key) {
        //Save it
        preferences.edit().remove(key).apply();
    }

    public String getSession(String key) {
        //Fetch it
        // The second parameter is the default value.
        return preferences.getString(key, "");
    }

    public Boolean getSessionB(String key) {
        //Fetch it
        // The second parameter is the default value.
        return preferences.getBoolean(key, true);
    }

    public File getCacheDirectory() {
        return this.cacheDirectory;
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;

            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
