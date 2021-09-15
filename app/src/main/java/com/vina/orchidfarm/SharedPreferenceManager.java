package com.vina.orchidfarm;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    public static final String MonoPref = "my_shared_preferences";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SESSION = "session";
    public static final String SN = "SN";

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public SharedPreferenceManager (Context context) {
        sp = context.getSharedPreferences(MonoPref, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    public void saveString(String keySp, String value) {
        editor.putString(keySp, value);
        editor.apply();
    }

    public void saveInt(String keySp, int value) {
        editor.putInt(keySp, value);
        editor.apply();
    }

    public void saveBoolean(String keySp, Boolean value) {
        editor.putBoolean(keySp, value);
        editor.apply();
    }

    public String getUsername() {return sp.getString(USERNAME, ""); }
    public String getPassword() {return sp.getString(PASSWORD, ""); }
    public Boolean getSession() {return sp.getBoolean(SESSION, false); }
    public String getSn() {return sp.getString(SN, "");}

    public void clearUsername() {
        editor.remove(USERNAME);
        editor.apply();
    }

    public void clearPassword() {
        editor.remove(PASSWORD);
        editor.apply();
    }

    public void clearSession() {
        editor.remove(SESSION);
        editor.apply();
    }

    public void clearSn() {
        editor.remove(SN);
        editor.apply();
    }
}
