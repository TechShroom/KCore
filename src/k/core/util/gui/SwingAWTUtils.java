package k.core.util.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SwingAWTUtils {

    public static void drop(JFrame frame) {
        frame.setLocationRelativeTo(null);
    }

    public static void setBackground(Color c, JFrame fr) {
        Container frame = fr.getContentPane();
        frame.setBackground(c);
    }

    public static void kill(JFrame win) {
        if (win == null) {
            return;
        }
        WindowEvent close = new WindowEvent(win, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(close);
    }

    public void runOnDispatch(Runnable r) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeAndWait(r);
        }
    }
}
