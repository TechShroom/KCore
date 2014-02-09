package k.core.util.math;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.swing.JTextField;

import k.core.util.gui.TextFieldReader;

class SpecialReader extends Reader {

    /**
     * The {@link JTextField} reader
     */
    private TextFieldReader in;

    /**
     * Creates a new reader that prints out to STDOUT what is read via
     * {@link #readLine()}
     * 
     * @param is
     *            - the input stream this is backed by
     */
    public SpecialReader(InputStream is) {
        try {
            in = (TextFieldReader) is;
        } catch (ClassCastException ce) {
            System.err
                    .println("Attempt to use SpecialReader with an input stream"
                            + " that is NOT a TextFieldReader!");
        }
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        byte[] b = new byte[cbuf.length];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) cbuf[i];
        }
        int read = in.read(b, off, len);
        for (int i = 0; i < b.length; i++) {
            cbuf[i] = (char) b[i];
        }
        return read;
    }

    public String readLine() {
        String str = "";
        while (!str.contains("\n")) {
            str += new String(new byte[] { (byte) in.read() });
        }
        str = str.replaceAll("\\n", "");
        System.out.println(str);
        return str;
    }

    @Override
    public void close() throws IOException {
    }

}
