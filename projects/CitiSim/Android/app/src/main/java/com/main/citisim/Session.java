package com.main.citisim;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Session {
    private SharedPreferences prefs;
    final private String AUTH_TOKEN_KEY = "authToken";


    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setAuthToken(String token) {
        prefs.edit().putString(AUTH_TOKEN_KEY, token).commit();
    }

    public String getAuthToken() {
        String usename = prefs.getString(AUTH_TOKEN_KEY,"");
        return usename;
    }
}
