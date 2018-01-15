package nl.tudelft.cs4160.trustchain_android.qr;

import com.google.protobuf.ByteString;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.libsodium.jni.keys.PrivateKey;
import org.libsodium.jni.keys.PublicKey;

import nl.tudelft.cs4160.trustchain_android.block.TrustChainBlock;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRBlock;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRTransaction;

public class TrustChainBlockFactory {
    public MessageProto.TrustChainBlock createBlock(QRBlock qrBlock, QRTransaction transaction, MessageProto.TrustChainBlock previousBlock, PublicKey linkPublicKey, PrivateKey key) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<QRTransaction> walletAdapter = moshi.adapter(QRTransaction.class);
        walletAdapter.toJson(transaction);

        MessageProto.TrustChainBlock.Builder builder = MessageProto.TrustChainBlock.newBuilder();

        builder.setTransaction(ByteString.copyFromUtf8(walletAdapter.toJson(transaction)))
                .setPublicKey(ByteString.copyFromUtf8(qrBlock.publicKeyBase64))
                .setSequenceNumber(previousBlock.getSequenceNumber() + 1)
                .setPreviousHash(ByteString.copyFrom(TrustChainBlock.hash(previousBlock)))
                .setLinkPublicKey(ByteString.copyFrom(linkPublicKey.toBytes()))
                .setLinkSequenceNumber(qrBlock.sequenceNumber);

        MessageProto.TrustChainBlock block = builder.buildPartial();
        block = TrustChainBlock.sign(block, key);

        return block;
    }
}
