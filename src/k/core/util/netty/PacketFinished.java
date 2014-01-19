package k.core.util.netty;

public class PacketFinished extends Packet implements IReceivePacket,
        ISendPacket {

    protected PacketFinished() {
        super(new DataStruct(new Object[0]));
    }

    @Override
    public boolean send(NetHandler n) {
        return n.sendPacket(this);
    }

    @Override
    public ISendPacket receive(NetHandler n) {
        return null;
    }

}
