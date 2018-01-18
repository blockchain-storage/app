package nl.tudelft.cs4160.trustchain_android.qr;

import android.util.Base64;

import com.google.protobuf.ByteString;

import org.json.JSONException;
import org.json.JSONObject;
import org.libsodium.jni.Sodium;

import java.util.ArrayList;
import java.util.Arrays;

import nl.tudelft.cs4160.trustchain_android.Util.DualKey;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;

/**
 * Created by rs on 1/8/18.
 */

public class BootstrapBlock {

    public byte[] pk;
    public byte[] seed;
    public ArrayList<MessageProto.TrustChainBlock> blocks = new ArrayList<>();

    public int total_up;
    public int total_down;
    /*



    {"private_key": base64_key,
     "transactions": [
        {"public_key": origin_public_base64_key ,
         "previous_hash": ???? Not sure what this should be probabily genisis hash
         "tx": {"down": 40000, "total_down": 40000, "up": 100000, "total_up": 100000},
         "signature": base64 signature
         "link_public_key": base64 key ( currently public key of the top level private key
         "link_sequence_number": 0}]}
     */
    public BootstrapBlock(JSONObject obj) throws Exception {


        byte[] decoded = Base64.decode( obj.getString("private_key"), Base64.DEFAULT);
        int pkLength = Sodium.crypto_box_secretkeybytes();
        int seedLength = Sodium.crypto_box_seedbytes();

        int expectedLength = pkLength + seedLength;
        if (decoded.length != expectedLength) {

        }

        pk = Arrays.copyOfRange(decoded, 0, pkLength); // first group is pk
        seed = Arrays.copyOfRange(decoded, pkLength, pkLength + seedLength); // second group is seed

        JSONObject firstBlock = obj.getJSONArray("transactions").getJSONObject(0);

        JSONObject tx = firstBlock.getJSONObject("tx");
        total_up = tx.getInt("total_up");
        total_down = tx.getInt("total_down");

        MessageProto.TrustChainBlock block0 = MessageProto.TrustChainBlock.newBuilder()
                .setPublicKey(fromJsonBase64(firstBlock,"public_key"))
                .setPreviousHash(fromJsonBase64(firstBlock,"previous_hash"))
                .setTransaction( ByteString.copyFromUtf8( firstBlock.getJSONObject("tx").toString() ) )
                .setSignature(fromJsonBase64(firstBlock,"signature"))
                .setLinkPublicKey(fromJsonBase64(firstBlock,"link_public_key"))
                .setLinkSequenceNumber(firstBlock.getInt("link_sequence_number"))
                .build();

        blocks.add(block0);

    }

    private static ByteString fromJsonBase64(JSONObject obj , String key) throws JSONException{
        return ByteString.copyFrom(Base64.decode( obj.getString(key), Base64.DEFAULT));
    }

    public DualKey getDualKey() {
        return new DualKey(pk, seed);
    }
}

