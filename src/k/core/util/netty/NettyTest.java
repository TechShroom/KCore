package k.core.util.netty;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class NettyTest {

    private NetHandlerServer nhs;
    private NetHandlerClient nhc;

    public void init(String[] args) {

        // choose server port = 25566
        nhs = new NetHandlerServer(25566);
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
        Runnable r = new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep((long) (Math.random() * 100000));
                } catch (InterruptedException is) {
                }
                nhc.addPacketToSendQueue(Packet
                        .newPacket(Packet.TERMINATION_PACKET_ID));
            }
        };
        new Thread(r, "Termination Thread").start();
        while (!nhc.isShutdown() && !nhs.isShutdown()) {
            nhc.processQueue();
            nhs.processQueue();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        System.err.println(nhc.sentPackets + ":" + nhs.sentPackets);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new NettyTest().init(args);
    }

}
