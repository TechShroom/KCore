package k.core.util.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class InputPipeStream extends InputStream {
    private static class PipeRunnable implements Runnable {
        private PipedInputStream in = null;
        private AtomicIntegerArray aia = new AtomicIntegerArray(4096);
        private AtomicInteger reqdbytes = new AtomicInteger(-1);

        public PipeRunnable(PipedInputStream i) {
            in = i;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                if (reqdbytes.get() > 0) {
                    for (int i = 0; i < reqdbytes.get(); i++) {
                        try {
                            aia.set(i, in.read());
                        } catch (Exception e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    reqdbytes.set(-1);
                }
            }
        }

        public int read() {
            reqdbytes.set(1);
            while (reqdbytes.get() > 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ie) {
                }
            }
            return aia.get(0);
        }

        public int read(byte[] b, int off, int len) {
            reqdbytes.set(len);
            while (reqdbytes.get() > 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ie) {
                }
            }
            for (int i = 0, j = off; i < len; i++, j++) {
                b[j] = (byte) aia.get(i);
            }
            return len;
        }
    }

    private PipeRunnable running = null;
    private PipedInputStream i = new PipedInputStream(1024 * 16);
    private boolean conn = false;
    private Thread runningt;

    public InputPipeStream() {
    }

    public InputPipeStream(OutputPipeStream ops) {
        if (connected() || ops.connected()) {
            throw new IllegalStateException("Already connected");
        }
        try {
            i.connect(ops.getSrc());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        setup();
        conn = true;
    }

    public boolean connected() {
        return conn;
    }

    @Override
    public int read() throws IOException {
        return running.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return running.read(b, off, len);
    }

    PipedInputStream getSnk() {
        setup();
        return i;
    }

    @Override
    public void close() {
        try {
            i.close();
            runningt.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        running = new PipeRunnable(i);
        runningt = new Thread(running, "PipeIn");
        runningt.setDaemon(true);
        runningt.start();
    }

}
