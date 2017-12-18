package nl.tudelft.cs4160.trustchain_android;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PeerTest extends TestCase {
    String id1;
    String id2;
    int port;

    @Before
    public void setUp() {
        port = 11;
        id1 = "123";
        id2 = "24";
    }

    @Test
    public void testEqual() {
        Peer peer1 = new Peer(null, id1, port);
        Peer peer2 = new Peer(null, id1, port);
        assertTrue(peer1.equals(peer2));
    }

    @Test
    public void testNotEqual() {
        Peer peer1 = new Peer(null, id1, port);
        Peer peer2 = new Peer(null, id2, port);
        assertFalse(peer1.equals(peer2));
    }

    @Test
    public void testToString(){
        Peer peer1 = new Peer(new byte[] {0x00}, id1, port);
        assertEquals("<Peer: [" + peer1.getPublicKey() + ":" + peer1.getPort() + ",PubKey: " + bytesToHex(peer1.getPublicKey()) + "]>"
                ,peer1.toString());
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
