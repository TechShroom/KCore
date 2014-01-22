package k.core.util.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SideConsole extends JFrame {
    private static final long serialVersionUID = 3733784846766341735L;
    private boolean error;
    public JScrollPane scroller;
    private Menu m;
    private static PrintStream oldO, oldE, newO, newE;
    private static String[] exceptions = { "Error creating the OutputStreams",
            "Error setting System.out", "Error setting System.err" };
    protected static PrintStream log;
    static {
        log = new PrintStream(System.err);
    }
    private static String earlyBufferE = null, earlyBufferO = null;
    private static boolean chained = false;

    public static final String OPTION_MENU = "options",
            DEBUG_JMIKEY = "debug_checkbox";

    public static final DJMIActionListener DEBUG_LISTENER = new DJMIActionListener();
    private static OutputStream earlyPOS = new OutputStream() {

        @Override
        public void write(int b) throws IOException {
            if (b == 0) {
                return;
            }
            if (earlyBufferE == null) {
                earlyBufferE = "";
            }
            earlyBufferE += new String(new byte[] { (byte) b });
            // log.println(b); //DEBUG
        }
    };
    private static OutputStream earlyPOS_ = new OutputStream() {

        @Override
        public void write(int b) throws IOException {
            if (b == 0) {
                return;
            }
            if (earlyBufferO == null) {
                earlyBufferO = "";
            }
            earlyBufferO += new String(new byte[] { (byte) b });
            // log.println(b); //DEBUG
        }
    };
    /**
     * The only console instance. Multiple consoles can be created, but only one
     * will be held here.
     */
    public static SideConsole console;

    public static void setOut(PrintStream temp) {
        oldO = temp;
    }

    public static void setErr(PrintStream temp_) {
        oldE = temp_;
    }

    public SideConsole(boolean debug) {
    	super("Console");
    	this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        console = this;
        error = debug;
        JPanel jp = new JPanel();
        JTextArea jta = new JTextArea("Loading console...\n", 25, 65);
        scroller = new JScrollPane(jta);
        jta.setBackground(new Color(100, 0, 0));
        jta.setForeground(new Color(255, 255, 255));
        jta.setFont(Font.decode("Terminal-BOLD-14"));
        jta.setEditable(false);
        jp.setBackground(jta.getBackground());
        jp.setLayout(new BorderLayout());
        jp.add(scroller, BorderLayout.NORTH);
        this.add(jp);
        addMenu();
        pack();
        setVisible(true);
        int state = -1;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        if (!chained) {
            try {
                state = 0;
                OutputStream jtaOStream = new TextAreaPrinter(jta, "[STDOUT] ");
                OutputStream jtaEStream = new TextAreaPrinter(jta, "[STDERR] ");
                oldO = oldO == null ? System.out : oldO;
                oldE = oldE == null ? System.err : oldE;
                newO = new ChainedStream(jtaOStream, oldO, true);
                newE = new ChainedStream(jtaEStream, oldE, true);
                state = 1;
                System.setOut(newO);
                System.out.println("Chain to stdout: success!");
                if (error) {
                    state = 2;
                    System.setErr(newE);
                    System.err.println("Chain to stderr: success!");
                }
                chained = true;
            } catch (Exception e) {
                System.err.println(exceptions[state] + ": cause below");
                e.printStackTrace();
            }
        }
        if (earlyBufferO != null && !earlyBufferO.equalsIgnoreCase("null")
                && !earlyBufferO.equalsIgnoreCase("")) {
            System.out.println(earlyBufferO);
        }
        if (earlyBufferE != null && !earlyBufferE.equalsIgnoreCase("null")
                && !earlyBufferE.equalsIgnoreCase("") && error) {
            System.err.println(earlyBufferE);
        }
    }

    private void addMenu() {
        // Create menu creator //
        m = Menu.create("console");

        // Add menu items on bar //
        m.addMenuByName(OPTION_MENU, "Options");

        // Add menu items inside bar-visible ones //
        m.addGenericMenuItemToMenuByName(OPTION_MENU, DEBUG_JMIKEY,
                new JCheckBoxMenuItem("Debug?"));
        JMenuItem jmi = m.getItemInMenuByRef(OPTION_MENU, DEBUG_JMIKEY);
        jmi.setSelected(error);

        // Add listeners //
        m.setActionListenerAll(JMIActionListener.instForMenu("console"));

        // Display //
        m.display(this);
    }

    /**
     * Writes a message out before a console is created and bound, it will be
     * printed when a console is created.
     * 
     * @param s
     *            - a message
     * @param stdout
     *            - if this should go to STDOUT or STDERR
     */
    public static void earlyMessage(String s, boolean stdout) {
        if (stdout) {
            earlyBufferO += s;
        } else {
            earlyBufferE += s;
        }
    }

    /**
     * Returns a new {@link PrintStream} wrapping the early error stream. Write
     * to this stream to write a message to the next console to be created on
     * the error stream.
     * 
     * @param defE
     *            - the original error stream
     * @return a new stream wrapping the early error stream
     */
    public static PrintStream earlyChainE(PrintStream defE) {
        setErr(defE);
        return new PrintStream(earlyPOS);
    }

    /**
     * Returns a new {@link PrintStream} wrapping the early output stream. Write
     * to this stream to write a message to the next console to be created on
     * the output stream.
     * 
     * @param defO
     *            - the original output stream
     * @return a new stream wrapping the early output stream
     */
    public static PrintStream earlyChainO(PrintStream defO) {
        setOut(defO);
        return new PrintStream(earlyPOS_);
    }

    /**
     * Sets the error value, determining if this console prints the err stream
     * or not.
     * 
     * @param value
     *            - true for error on, false for error off
     */
    public void error(boolean value) {
        error = value;
        JMenuItem jmi = m.getItemInMenuByRef(OPTION_MENU, DEBUG_JMIKEY);
        jmi.setSelected(error);
        if (error) {
            System.setErr(newE);
        } else {
            System.setErr(oldE);
        }
    }

    /**
     * ChainedStream allows us to keep the stream to default out and/or error
     * while also writing to a new stream. It can be chained as many times as
     * needed.
     * 
     */
    public static class ChainedStream extends PrintStream {

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
}
