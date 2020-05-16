package io.github.vhow.finder.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    private static final String TAG = "Settings";
    private static final Settings INSTANCE = new Settings();

    private Settings() {
        //no instance
    }

    public static Settings getInstance() {
        return INSTANCE;
    }

    private void commitBoolean(String key, boolean value, Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private boolean getBoolean(String key, boolean defaultValue, Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(key, defaultValue);
    }

    public boolean showHidden(Context context) {
        return getBoolean(KEY.SHOW_HIDDEN_FILES, false, context);
    }

    void setShowHidden(boolean show, Context context) {
        commitBoolean(KEY.SHOW_HIDDEN_FILES, show, context);
    }

    private static class KEY {
        static final String SHOW_HIDDEN_FILES = "show_hidden_files";
    }

}
