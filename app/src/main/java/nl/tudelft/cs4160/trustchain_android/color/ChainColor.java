package nl.tudelft.cs4160.trustchain_android.color;

import android.content.Context;

import nl.tudelft.cs4160.trustchain_android.R;

/**
 * Created by timbu on 18/12/2017.
 */

public class ChainColor {
    public static int getColor(Context context, String hash) {
        int[] colors = context.getResources().getIntArray(R.array.colorsChain);
        int number = Math.abs(hash.hashCode() % colors.length);
        return colors[number];
    }

    public static int getMyColor(Context context){
        return context.getResources().getColor(R.color.colorPrimary);
    }
}
