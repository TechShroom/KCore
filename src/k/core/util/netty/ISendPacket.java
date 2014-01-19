package k.core.util.netty;

/**
 * Provides the outline of a send packet.
 * 
 * @author Kenzie Togami
 * 
 */
public interface ISendPacket {

    /**
     * Allows packets to make changes before sending over the NetHandler.
     * Implementations should return {@link NetHandler#sendPacket(Packet)}'s
     * result.
     * 
     * @param n
     *            - a {@link NetHandler}
     * @return {@link NetHandler#sendPacket(Packet)}
     */
    public boolean send(NetHandler n);

}
