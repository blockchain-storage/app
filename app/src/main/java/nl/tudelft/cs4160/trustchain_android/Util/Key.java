package nl.tudelft.cs4160.trustchain_android.Util;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import org.libsodium.jni.Sodium;
import org.libsodium.jni.encoders.Raw;
import org.libsodium.jni.keys.KeyPair;
import org.libsodium.jni.keys.PrivateKey;
import org.libsodium.jni.keys.PublicKey;

/**
 * Manages key operations.
 */
public class Key {
    private final static String TAG = "KEY";

    public final static String DEFAULT_PUB_KEY_FILE = "pub.key";
    public final static String DEFAULT_PRIV_KEY_FILE = "priv.key";


    public static KeyPair ensureKeysExist(Context context) {
        KeyPair keyPair = loadKeys(context);

        if (keyPair == null) {
            return createAndSaveKeys(context);
        }
        return keyPair;
    }

    public static KeyPair createAndSaveKeys(Context context) {
        KeyPair kp = Key.createNewKeyPair();
        saveKeyPair(context, kp);
        return kp;
    }

    public static void saveKeyPair(Context context, KeyPair kp) {
        Key.saveKey(context, Key.DEFAULT_PUB_KEY_FILE, kp.getPublicKey().toBytes());
        Key.saveKey(context, Key.DEFAULT_PRIV_KEY_FILE, kp.getPublicKey().toBytes());
    }

    /**
     * Creates a new curve25519 KeyPair.
     *
     * @return KeyPair.
     */
    public static KeyPair createNewKeyPair() {
        byte[] vk = new byte[Sodium.crypto_box_secretkeybytes()];
        Raw encoder = new Raw();
        return new KeyPair(encoder.encode(vk), encoder);
    }


    /**
     * Sign a message using the given private key.
     *
     * @param privateKey The private key
     * @param data       The message
     * @return The signature
     */
    public static byte[] sign(PrivateKey privateKey, byte[] data) {
        byte[] out = new byte[Sodium.crypto_sign_ed25519_bytes()];
        Sodium.crypto_sign_detached(out, null, data, data.length, privateKey.toBytes());
        return out;
    }

    /**
     * Verify a signature
     *
     * @param publicKey The public key of the signer.
     * @param msg       The message that was signed.
     * @param rawSig    The signature.
     * @return True if this a correct signature, false if not.
     */
    public static boolean verify(PublicKey publicKey, byte[] msg, byte[] rawSig) {
        int result = Sodium.crypto_sign_ed25519_verify_detached(rawSig, msg, msg.length, publicKey.toBytes());
        return result == 0;
    }

    /**
     * Load a public key from the given file.
     *
     * @param context The context (needed so we can read the file)
     * @param file    The file to read.
     * @return The public key.
     */
    public static PublicKey loadPublicKey(Context context, String file) {
        String key = Util.readFile(context, file);
        if (key == null) {
            return null;
        }
        Log.i(TAG, "PUBLIC FROM FILE: " + key);
        return loadPublicKey(key);
    }


    /**
     * Load a raw base64 encoded key.
     *
     * @param key The base64 encoded key.
     * @return Public key
     */
    public static PublicKey loadPublicKey(String key) {
        byte[] rawKey = Base64.decode(key, Base64.DEFAULT);
        PublicKey pubKeySpec1 = getPublicKeyFromBytes(rawKey);
        if (pubKeySpec1 != null) return pubKeySpec1;
        return null;
    }

    @Nullable
    public static PublicKey getPublicKeyFromBytes(byte[] rawKey) {
        return new PublicKey(rawKey);
    }

    /**
     * Load a private key from the given file
     *
     * @param context The context (needed to read the file)
     * @param file    The file
     * @return The private key
     */
    public static PrivateKey loadPrivateKey(Context context, String file) {
        String key = Util.readFile(context, file);
        if (key == null) {
            return null;
        }
        Log.i(TAG, "PRIVATE FROM FILE: " + key);
        return loadPrivateKey(key);
    }

    /**
     * Load a private key from a base64 encoded string
     *
     * @param key The base64 encoded key
     * @return The private key
     */
    public static PrivateKey loadPrivateKey(String key) {
        byte[] rawKey = Base64.decode(key, Base64.DEFAULT);
        return getPrivateKeyFromBytes(rawKey);
    }

    @Nullable
    public static PrivateKey getPrivateKeyFromBytes(byte[] rawKey) {
        return new PrivateKey(rawKey);
    }

    /**
     * Load public and private keys from the standard files.
     *
     * @param context The context (needed to read the files)
     * @return A KeyPair with the private and public key.
     */
    public static KeyPair loadKeys(Context context) {
        PrivateKey privateKey = Key.loadPrivateKey(context, Key.DEFAULT_PRIV_KEY_FILE);
        Raw encoder = new Raw();
        return new KeyPair(encoder.encode(privateKey.toBytes()), encoder);
    }

    /**
     * Write a key to storage
     *
     * @param context Context (needed to write to the file)
     * @param file    The file to write to
     * @param key     The key to be written
     * @return True if successful, false if not
     */
    public static boolean saveKey(Context context, String file, byte[] key) {
        return Util.writeToFile(context, file, Base64.encodeToString(key, Base64.DEFAULT));
    }


}
