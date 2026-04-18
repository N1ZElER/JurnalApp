package com.example.jurnals.data.remote.accesToken;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
    }

    public String getToken() {
        return prefs.getString("token", null);
    }

    public boolean isAuthorized() {
        return getToken() != null;
    }

    public void clearSession() {
        prefs.edit()
                .remove("token")
                .remove("tokenExpiry")
                .apply();
    }
}