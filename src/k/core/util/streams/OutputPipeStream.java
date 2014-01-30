package k.core.util.streams;

import java.io.IOException;
import java.io.OutputStream;

public class OutputPipeStream extends OutputStream {

    private SyncBuffer buf = null;

    public OutputPipeStream() {
    }

    public OutputPipeStream(InputPipeStream ips) {
        if (connected() || ips.connected()) {
            throw new IllegalStateException("Already connected");
        }
        buf = new SyncBuffer(new byte[4096]);
        ips.setbuf(buf);
    }

    public boolean connected() {
        return buf == null;
    }

    @Override
    public void write(int b) throws IOException {
        if (buf == null) {
            throw new IllegalStateException("Not connected");
        }
        buf.add((byte) b);
    }

    protected void setbuf(SyncBuffer b) {
        buf = b;
    }

}
