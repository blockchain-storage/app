package nl.tudelft.cs4160.trustchain_android.qr;

import android.util.Base64;

import com.google.protobuf.ByteString;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.libsodium.jni.Sodium;

import java.util.Arrays;

import nl.tudelft.cs4160.trustchain_android.Util.KeyPair;
import nl.tudelft.cs4160.trustchain_android.block.TrustChainBlock;
import nl.tudelft.cs4160.trustchain_android.database.TrustChainDBHelper;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;
import nl.tudelft.cs4160.trustchain_android.qr.exception.InvalidDualKeyException;
import nl.tudelft.cs4160.trustchain_android.qr.exception.QRWalletImportException;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRTransaction;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRWallet;

public class TrustChainBlockFactory {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<QRTransaction> walletAdapter = moshi.adapter(QRTransaction.class);

    public MessageProto.TrustChainBlock createBlock(QRWallet wallet, TrustChainDBHelper helper, KeyPair ownKeyPair) throws QRWalletImportException {
        String transactionString = walletAdapter.toJson(wallet.transaction);
        KeyPair walletKeyPair = getKeyPairFromWallet(wallet);

        MessageProto.TrustChainBlock identityHalfBlock = reconstructTemporaryIdentityHalfBlock(wallet);

        MessageProto.TrustChainBlock block = TrustChainBlock.createBlock(transactionString.getBytes(), helper, ownKeyPair.getPublicKey().toBytes(), identityHalfBlock, walletKeyPair.getPublicKey().toBytes());
        block = TrustChainBlock.sign(block, ownKeyPair.getPrivateKey());

        return block;
    }

    private MessageProto.TrustChainBlock reconstructTemporaryIdentityHalfBlock(QRWallet wallet) throws InvalidDualKeyException {
        String transactionString = walletAdapter.toJson(wallet.transaction);

        KeyPair walletKeyPair = getKeyPairFromWallet(wallet);

        MessageProto.TrustChainBlock block = MessageProto.TrustChainBlock.newBuilder().
                setTransaction(ByteString.copyFromUtf8(transactionString))
                .setPublicKey(ByteString.copyFrom(walletKeyPair.getPublicKey().toBytes()))
                .setSequenceNumber(wallet.block.sequenceNumber)
                .setPreviousHash(ByteString.copyFrom(Base64.decode(wallet.block.blockHashBase64, Base64.DEFAULT)))
                .setLinkPublicKey(ByteString.copyFrom(walletKeyPair.getPublicKey().toBytes()))
                .build();
        MessageProto.TrustChainBlock signedBlock = TrustChainBlock.sign(block, walletKeyPair.getPrivateKey());
        return signedBlock;
    }


    private KeyPair getKeyPairFromWallet(QRWallet wallet) throws InvalidDualKeyException {
        byte[] keyBytes = Base64.decode(wallet.privateKeyBase64, Base64.DEFAULT);
        return readKeyPair(keyBytes);
    }

    private KeyPair readKeyPair(byte[] message) throws InvalidDualKeyException {
        String check = "LibNaCLSK:";
        byte[] expectedCheckByteArray = check.getBytes();
        byte[] checkByteArray = Arrays.copyOfRange(message, 0, expectedCheckByteArray.length);

        if (!(Arrays.equals(expectedCheckByteArray, checkByteArray))) {
            throw new InvalidDualKeyException("Private key does not match expected format");
        }

        int pkLength = Sodium.crypto_box_secretkeybytes();
        int seedLength = Sodium.crypto_box_seedbytes();

        int expectedLength = expectedCheckByteArray.length + pkLength + seedLength;
        if (message.length != expectedLength) {
            throw new InvalidDualKeyException("Expected key length " + expectedLength + " but got " + message.length);
        }

        byte[] pk = Arrays.copyOfRange(message, expectedCheckByteArray.length, expectedCheckByteArray.length + pkLength); // first group is pk
        byte[] seed = Arrays.copyOfRange(message, expectedCheckByteArray.length + pkLength, expectedCheckByteArray.length + pkLength + seedLength); // second group is seed
        return new KeyPair(pk, seed);
    }
}
