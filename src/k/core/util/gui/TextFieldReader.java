package k.core.util.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

public class TextFieldReader extends InputStream {
    JTextField in = null;
    ArrayList<Byte> buf = new ArrayList<Byte>();
    int ci = 0;

    public TextFieldReader(JTextField jtf) {
        in = jtf;
        Action a = new EnterAction(this);
        jtf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "enter_key");
        jtf.getActionMap().put("enter_key", a);
    }

    public class EnterAction extends AbstractAction {
        private static final long serialVersionUID = -2045859309599836915L;
        TextFieldReader handle;

        public EnterAction(TextFieldReader reader) {
            handle = reader;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            for (Byte b : handle.in.getText().getBytes()) {
                buf.add(ci++, b);
            }
            for (Byte b : "\n".getBytes()) {
                buf.add(b);
            }
            handle.in.setText("");
        }

    }

    @Override
    public int read() {
        ci--;
        if (ci < 0) {
            ci = 0;
        }
        while (buf.size() < 1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException is) {
            }
        }
        // easy on the memory
        buf.trimToSize();
        return buf.remove(0);
    }

    @Override
    public int read(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        int read = -1;
        for (int i = off; i - off < len && i < b.length; i++) {
            byte br = (byte) read();
            if (br == -1)
                break;
            read++;
            b[i] = br;
        }
        return read;
    }
}
