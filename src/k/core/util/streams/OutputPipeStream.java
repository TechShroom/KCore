package k.core.util.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class OutputPipeStream extends OutputStream {
    private static class PipeRunnable implements Runnable {
        private PipedOutputStream out = null;
        private AtomicIntegerArray aia = new AtomicIntegerArray(4096);
        private AtomicInteger reqdbytes = new AtomicInteger(-1);

        public PipeRunnable(PipedOutputStream o) {
            out = o;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                if (reqdbytes.get() > 0) {
                    for (int i = 0; i < reqdbytes.get(); i++) {
                        try {
                            out.write(aia.get(i));
                        } catch (IOException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    if (Thread.interrupted()) {
                        break;
                    }
                    reqdbytes.set(-1);
                }
            }
        }

        public void write(int b) {
            aia.addAndGet(0, b);
            reqdbytes.set(1);
        }

    }

    private PipeRunnable running = null;
    private PipedOutputStream o = new PipedOutputStream();
    private boolean conn = false;
    private Thread runningt;

    public OutputPipeStream() {
    }

    public OutputPipeStream(InputPipeStream ips) {
        if (connected() || ips.connected()) {
            throw new IllegalStateException("Already connected");
        }
        try {
            o.connect(ips.getSnk());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        conn = true;
    }

    public boolean connected() {
        return conn;
    }

    @Override
    public void write(int b) throws IOException {
        running.write(b);
    }

    PipedOutputStream getSrc() {
        return o;
    }

    void setup() {
        running = new PipeRunnable(o);
        runningt = new Thread(running, "PipeOut");
        runningt.setDaemon(true);
        runningt.start();
    }
}
