package nl.tudelft.cs4160.trustchain_android.Util;
/**
 * Copyright 2013 Bruno Oliveira, and individual contributors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.libsodium.jni.NaCl;
import org.libsodium.jni.Sodium;
import org.libsodium.jni.crypto.Point;
import org.libsodium.jni.crypto.Util;
import org.libsodium.jni.encoders.Encoder;
import org.libsodium.jni.keys.PrivateKey;
import org.libsodium.jni.keys.PublicKey;

import static org.libsodium.jni.SodiumConstants.PUBLICKEY_BYTES;
import static org.libsodium.jni.SodiumConstants.SECRETKEY_BYTES;
import static org.libsodium.jni.NaCl.sodium;
import static org.libsodium.jni.crypto.Util.checkLength;
import static org.libsodium.jni.crypto.Util.zeros;

public class KeyPair {

    private byte[] publicKey;
    private byte[] seed;
    private final byte[] secretKey;

    public KeyPair() {
        this.secretKey = Util.zeros(32);
        this.publicKey = Util.zeros(32);
        NaCl.sodium();
        Sodium.crypto_box_curve25519xsalsa20poly1305_keypair(this.publicKey, this.secretKey);
    }

    public KeyPair(byte[] secretKey, byte[] seed) {
        this.secretKey = secretKey;
        checkLength(this.secretKey, SECRETKEY_BYTES);
        this.seed = seed;
        checkLength(this.seed, Sodium.crypto_box_seedbytes());
    }

    public PublicKey getPublicKey() {
        Point point = new Point();
        byte[] key = publicKey != null ? publicKey : point.mult(secretKey).toBytes();
        return new PublicKey(key);
    }

    public PrivateKey getPrivateKey() {
        return new PrivateKey(secretKey);
    }
    public byte[] getSeed() {
        return seed;
    }
}