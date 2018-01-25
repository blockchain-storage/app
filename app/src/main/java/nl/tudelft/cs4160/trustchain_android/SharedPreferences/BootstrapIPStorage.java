package nl.tudelft.cs4160.trustchain_android.SharedPreferences;

import android.content.Context;

/**
 * Created by Boning on 12/19/2017.
 */

public class BootstrapIPStorage {

    static String bootstrapIPStorage = "bootstrapIPStorage";

    public static void setIP(Context context, String ip) {
        if(ip == null) {
            return;
        }
        SharedPreferencesStorage.writeSharedPreferences(context, bootstrapIPStorage, ip);
    }

    public static String getIP(Context context) {
        return SharedPreferencesStorage.readSharedPreferences(context, bootstrapIPStorage);
    }
}
