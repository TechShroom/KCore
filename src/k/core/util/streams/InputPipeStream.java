package k.core.util.streams;

import java.io.IOException;
import java.io.InputStream;

public class InputPipeStream extends InputStream {

    private SyncBuffer buf = null;

    public InputPipeStream() {
    }

    public InputPipeStream(OutputPipeStream ops) {
        if (connected() || ops.connected()) {
            throw new IllegalStateException("Already connected");
        }
        buf = new SyncBuffer(new byte[4096]);
        ops.setbuf(buf);
    }

    public boolean connected() {
        return buf != null;
    }

    @Override
    public int read() throws IOException {
        return buf.get();
    }

    protected void setbuf(SyncBuffer b) {
        buf = b;
    }

}
