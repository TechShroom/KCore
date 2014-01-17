package k.core.util.netty;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


public class NettyTest {

    public void init(String[] args) {

	// choose server port = 25566
	NetHandlerServer nhs = new NetHandlerServer(25566);
	NetHandlerClient nhc;
	try {
	    nhc = new NetHandlerClient(new InetSocketAddress(0),
		    new InetSocketAddress(InetAddress.getLocalHost(), 25566));
	} catch (UnknownHostException e1) {
	    e1.printStackTrace();
	    nhs.shutdown();
	    return;
	}
	Packet.registerPacket(TestPacket.class, 1);
	nhc.addPacketToSendQueue(Packet.newPacket(1, new Object[] { "a msg" }));
	for (int i = 0; i < 100; i++) {
	    nhc.processQueue();
	    nhs.processQueue();
	    try {
		Thread.sleep(100);
	    } catch (Exception e) {
	    }
	}
	nhc.shutdown();
	nhs.shutdown();
	System.err.println(nhc.sentPackets + ":" + nhs.sentPackets);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	new NettyTest().init(args);
    }

}
