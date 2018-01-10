package nl.tudelft.cs4160.trustchain_android.qr.models;

import com.squareup.moshi.Json;

public class QRBlock {
    @Json(name = "public_key")
    public String publicKeyBase64;

    public CoinTransfer transaction; // TODO should probably become transfer

    public String hash;

    @Json(name = "previous_hash")
    public String previousHash;

    @Json(name = "insert_time")
    public Object insertTime;

    public String signature;

    @Json(name = "link_public_key")
    public String linkPublicKeyBase64;

    @Json(name = "link_sequence_number")
    public int linkSequenceNumber;

    @Json(name = "sequence_number")
    public int sequenceNumber;
}
