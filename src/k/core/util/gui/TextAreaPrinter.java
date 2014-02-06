package k.core.util.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextAreaPrinter extends ByteArrayOutputStream implements
        DocumentListener {
    private JTextArea out = null;
    private String pre = ">";
    private String buffer = "";

    public TextAreaPrinter(JTextArea jta, String prefix) {
        super();
        out = jta;
        jta.getDocument().addDocumentListener(this);
        pre = prefix;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            super.flush();
            String record = this.toString();
            super.reset();
            if (record.length() == 0) {
                return;
            }
            buffer = buffer.concat(record);
            if (buffer.contains("\n")) {
                String[] nl_spl = buffer.split("\\n");
                String reconcat_buf = "";
                for (String s : nl_spl) {
                    reconcat_buf += pre + s + "\n";
                }
                out.append(reconcat_buf);
                buffer = "";
            }
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        out.setCaretPosition(out.getDocument().getLength());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        out.setCaretPosition(out.getDocument().getLength());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        out.setCaretPosition(out.getDocument().getLength());
    }

}
