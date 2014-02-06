package k.core.util.streams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * ChainedStream allows us to keep the stream to default out and/or error while
 * also writing to a new stream. It can be chained as many times as needed.
 * 
 */
public class ChainedStream extends PrintStream {

    private OutputStream chained = null;

    public ChainedStream(OutputStream newStream, OutputStream chainTo,
            boolean autoFlush) {
        super(newStream, autoFlush);
        chained = chainTo;
    }

    @Override
    public void flush() {
        super.flush();
        try {
            chained.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(int b) {
        super.write(b);
        try {
            chained.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
        try {
            chained.write(buf, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        super.write(b);
        chained.write(b);
    }

    @Override
    public void close() {
        super.close();
        try {
            chained.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}