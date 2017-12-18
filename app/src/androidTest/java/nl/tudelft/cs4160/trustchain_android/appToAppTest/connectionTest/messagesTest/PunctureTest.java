package nl.tudelft.cs4160.trustchain_android.appToAppTest.connectionTest.messagesTest;

import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import nl.tudelft.cs4160.trustchain_android.appToApp.connection.messages.Message;
import nl.tudelft.cs4160.trustchain_android.appToApp.connection.messages.MessageException;
import nl.tudelft.cs4160.trustchain_android.appToApp.connection.messages.Puncture;

import static org.junit.Assert.assertEquals;

/**
 * Created by Boning on 12/18/2017.
 */

public class PunctureTest {

    Puncture p;
    InetSocketAddress dest;
    InetSocketAddress source;
    Map<String, Object> m;
    Map<String, Object> sourceAddressMap = new HashMap<>();
    Map<String, Object> destAddressMap = new HashMap<>();

    @Before
    public void initialization(){
        source = new InetSocketAddress("111.111.11.11", 11);
        dest = new InetSocketAddress("222.222.22.22", 22);
        m = new HashMap<>();
        m.put("peer_id", "34");

        sourceAddressMap.put("address", "111.111.11.11");
        long port = 11;
        sourceAddressMap.put("port", port);
        m.put("source", sourceAddressMap);

        destAddressMap.put("address", "222.222.22.22");
        long port2 = 22;
        destAddressMap.put("port", port2);
        m.put("destination", destAddressMap);
    }

    @Test
    public void testToString(){
        p = new Puncture("123", dest, source);
        assertEquals(p.toString(), "Puncture{{peer_id=123, type=4, destination={address=222.222.22.22, port=22}, source={address=111.111.11.11, port=11}}}");
    }

    @Test
    public void testFromMap() throws MessageException {
        Message message = Puncture.fromMap(m);
        assertEquals(message.getType(), 4);
        assertEquals(message.getPeerId(), "34");
        assertEquals(message.getDestination(), dest);
    }

}
