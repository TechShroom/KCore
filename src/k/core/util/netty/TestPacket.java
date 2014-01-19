package k.core.util.netty;

public class TestPacket extends Packet implements ISendPacket, IReceivePacket {

    public TestPacket(String msg) {
        this(new DataStruct(new Object[] { msg }));
    }

    public TestPacket(DataStruct dataStruct) {
        super(dataStruct);
    }

    @Override
    public ISendPacket receive(NetHandler n) {
        return new TestPacket((String) data.get(0));
    }

    @Override
    public boolean send(NetHandler n) {
        return n.sendPacket(this);
    }

}
