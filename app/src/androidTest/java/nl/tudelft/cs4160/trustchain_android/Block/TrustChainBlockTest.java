package nl.tudelft.cs4160.trustchain_android.Block;


import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityUnitTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.libsodium.jni.NaCl;

import nl.tudelft.cs4160.trustchain_android.Util.Key;
import nl.tudelft.cs4160.trustchain_android.Util.KeyPair;
import nl.tudelft.cs4160.trustchain_android.block.TrustChainBlock;
import nl.tudelft.cs4160.trustchain_android.database.TrustChainDBHelper;
import nl.tudelft.cs4160.trustchain_android.main.OverviewConnectionsActivity;
import nl.tudelft.cs4160.trustchain_android.message.MessageProto;

import static nl.tudelft.cs4160.trustchain_android.Peer.bytesToHex;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

/**
 * Created by Boning on 12/17/2017.
 */
@RunWith(AndroidJUnit4.class)
public class TrustChainBlockTest extends ActivityUnitTestCase<OverviewConnectionsActivity> {
    public TrustChainBlockTest() {
        super(OverviewConnectionsActivity.class);
    }

    private KeyPair keyPair;
    private KeyPair keyPair2;
    private byte[] transaction = new byte[2];
    private byte[] pubKey = new byte[2];
    private byte[] linkKey = new byte[2];
    private MessageProto.TrustChainBlock genesisBlock;
    private TrustChainDBHelper dbHelper;

    @Before
    public void initialization() {
        NaCl.sodium();
        keyPair = Key.createNewKeyPair();
        keyPair2 = Key.createNewKeyPair();
        dbHelper = mock(TrustChainDBHelper.class);
        when(dbHelper.getMaxSeqNum(keyPair.getPublicKey().toBytes())).thenReturn(0);
        transaction[0] = 12;
        transaction[1] = 42;
        pubKey[0] = 2;
        pubKey[1] = 4;
        linkKey[0] = 14;
        linkKey[1] = 72;
        genesisBlock = TrustChainBlock.createGenesisBlock(keyPair);
    }

    @Test
    public void testPublicKeyGenesisBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createGenesisBlock(keyPair);
        assertEquals(bytesToHex(keyPair.getPublicKey().toBytes()), bytesToHex(block.getPublicKey().toByteArray()));
    }

    @Test
    public void testGetSequenceNumberGenesisBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createBlock(transaction, dbHelper, pubKey, genesisBlock, linkKey);
        assertEquals(0, block.getSequenceNumber());
    }

    @Test
    public void testPublicKeyBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createBlock(transaction, dbHelper, pubKey, genesisBlock, linkKey);
        assertEquals(bytesToHex(pubKey), bytesToHex(block.getPublicKey().toByteArray()));
    }

    @Test
    public void testLinkPublicKeyBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createBlock(transaction, dbHelper, pubKey, genesisBlock, linkKey);
        assertEquals(bytesToHex(keyPair.getPublicKey().toBytes()), bytesToHex(block.getLinkPublicKey().toByteArray()));
    }

    @Test
    public void testGetSequenceNumberBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createBlock(transaction, dbHelper, pubKey, genesisBlock, linkKey);
        assertEquals(0, block.getSequenceNumber());
    }

    @Test
    public void testIsInitializedGenesisBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createGenesisBlock(keyPair);
        assertTrue(block.isInitialized());
    }

    @Test
    public void testGetSameSerializedSizeBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createGenesisBlock(keyPair);
        assertEquals(block.getSerializedSize(), block.getSerializedSize());
    }

    @Test
    public void testGetDiffSerializedSizeBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createGenesisBlock(keyPair);
        assertEquals(block.getSerializedSize(), block.getSerializedSize());
    }

    @Test
    public void testGetDiffHashBlockTest() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createGenesisBlock(keyPair);
        MessageProto.TrustChainBlock block2 = TrustChainBlock.createGenesisBlock(keyPair2);
        assertNotEquals(block.hashCode(), block2.hashCode());
    }

    @Test
    public void testEqualBlocks() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createGenesisBlock(keyPair);
        assertTrue(block.equals(block));
    }

    @Test
    public void testNotEqualBlocks() {
        MessageProto.TrustChainBlock block = TrustChainBlock.createGenesisBlock(keyPair);
        MessageProto.TrustChainBlock block2 = TrustChainBlock.createGenesisBlock(keyPair2);
        assertFalse(block.equals(block2));
    }

    @Test
    public void testVerify() {
        KeyPair pair = Key.createNewKeyPair();
        byte[] message = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        byte[] signature = Key.sign(pair.getPrivateKey(), message);
        assertTrue(Key.verify(pair.getPublicKey(), message, signature));
    }

    @After
    public void resetMocks() {
        validateMockitoUsage();
    }

}
