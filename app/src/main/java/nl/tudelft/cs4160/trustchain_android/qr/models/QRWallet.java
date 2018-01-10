package nl.tudelft.cs4160.trustchain_android.qr.models;

import com.squareup.moshi.Json;

import java.util.List;

public class QRWallet {
    @Json(name = "private_key")
    public String privateKeyBase64;

    @Json(name = "transactions")
    public List<QRBlock> blocks;
}
