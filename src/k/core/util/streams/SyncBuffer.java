package k.core.util.streams;

public class SyncBuffer {

    private byte[] buf = null;
    private int index = 0;

    public SyncBuffer(byte[] buffer) {
        buf = buffer;
    }

    public void add(byte b) {
        if (index >= buf.length) {
            return;
        }
        buf[index] = b;
        index++;
    }

    public byte get() {
        index--;
        if (index < 0) {
            return -1;
        }
        byte b = buf[index];
        buf[index] = 0;
        return b;
    }
}
