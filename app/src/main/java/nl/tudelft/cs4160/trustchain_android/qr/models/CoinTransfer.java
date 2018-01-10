package nl.tudelft.cs4160.trustchain_android.qr.models;

import com.squareup.moshi.Json;

public class CoinTransfer {
    public int down;
    public int up;

    @Json(name = "total_up")
    public int totalUp;

    @Json(name = "total_down")
    public int totalDown;
}
