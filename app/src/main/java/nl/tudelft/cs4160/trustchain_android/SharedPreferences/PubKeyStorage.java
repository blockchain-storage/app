package nl.tudelft.cs4160.trustchain_android.SharedPreferences;

import android.content.Context;

/**
 * Created by timbu on 18/12/2017.
 */

public class PubKeyStorage {

    static String pubKeyStorageKey = "pubKeyStorageKey";

    public static void addKey(Context context, String key, String ip) {
        //ToDo override??
        String keyOld = getKey(context, ip);
        SharedPreferencesStorage.writeSharedPreferences(context, pubKeyStorageKey + ip, key);
    }

    public static String getKey(Context context, String ip) {
        return SharedPreferencesStorage.readSharedPreferences(context, pubKeyStorageKey + ip);
    }
}
