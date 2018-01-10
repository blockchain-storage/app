package nl.tudelft.cs4160.trustchain_android.qr;

import com.google.protobuf.ByteString;

import nl.tudelft.cs4160.trustchain_android.message.MessageProto;
import nl.tudelft.cs4160.trustchain_android.qr.models.QRBlock;

public class TrustChainBlockFactory {
    public MessageProto.TrustChainBlock createBlock(QRBlock qrBlock) {
        MessageProto.TrustChainBlock.Builder builder = MessageProto.TrustChainBlock.newBuilder();

        builder.setTransaction(ByteString.copyFromUtf8(qrBlock.transaction.toString()))
                .setPublicKey(ByteString.copyFromUtf8(qrBlock.publicKeyBase64))
                .setSequenceNumber(qrBlock.sequenceNumber)
                .setLinkPublicKey(ByteString.copyFromUtf8(qrBlock.linkPublicKeyBase64))
                .setLinkSequenceNumber(qrBlock.linkSequenceNumber)
                .setPreviousHash(ByteString.copyFromUtf8(qrBlock.previousHash))
                .setSignature(ByteString.copyFromUtf8(qrBlock.signature));

        return builder.build();
    }
}
