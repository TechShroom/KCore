package k.core.util.netty;

public interface PacketSender {

    /**
     * Sends the packets {@link DataStruct}.
     * 
     * @param p
     *            - the packet to send
     * @return if the packet was sent.
     */
    public abstract boolean sendPacket(Packet p);
}
