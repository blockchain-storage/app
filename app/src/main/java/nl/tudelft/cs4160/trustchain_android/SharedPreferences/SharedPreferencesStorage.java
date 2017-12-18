package nl.tudelft.cs4160.trustchain_android.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by timbu on 18/12/2017.
 */

public final class SharedPreferencesStorage {
    public static final String PREFS_NAME = "MyPrefsFile";

    public static String readSharedPreferences(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        if (settings.contains(key)) {
            String object = settings.getString(key, null);
            return object;
        } else {
            return null;
        }
    }

    public static int readSharedPreferencesInt(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        if (settings.contains(key)) {
            int object = settings.getInt(key, -1);
            return object;
        } else {
            return -1;
        }
    }

    public static void writeSharedPreferences(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void writeSharedPreferences(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
    }

}
