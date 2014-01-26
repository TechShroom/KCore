package k.core.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
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

    public static void runOnDispatch(Runnable r) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeAndWait(r);
        }
    }

    public static void removeAll(JComponent c) {
        c.removeAll();
        c.validate();
        c.repaint();
    }

    /**
     * Adds all of the specified {@link Component components} with the given
     * constraints, then calls {@link SwingAWTUtils#validate(JPanel) validate()}
     * on <tt>to</tt>.
     * 
     * @param to
     *            - the {@link JPanel} to add to
     * @param comps
     *            - the components to add
     * @param constraints
     *            - the constraints. If <tt>null</tt>, a new array of size
     *            <tt>comps.length</tt> is created.
     * @see SwingAWTUtils#validate(JPanel)
     */
    public static void addAllAndValidate(JPanel to, Component[] comps,
            Object[] constraints) {
        if (constraints == null) {
            constraints = new Object[comps.length];
        } else if (constraints.length < comps.length) {
            constraints = Arrays.copyOf(constraints, comps.length);
        }
        for (int i = 0; i < comps.length; i++) {
            to.add(comps[i], constraints[i]);
        }
        validate(to);
    }

    /**
     * Calls {@link Container#validate()} then {@link Container#repaint()} on
     * the given {@link Container} to fully reload the Container.
     * 
     * @param c
     *            - the Container to use
     * @see Container#repaint()
     * @see Container#validate()
     */
    public static void validate(Container c) {
        c.validate();
        c.repaint();
    }

    /**
     * Returns the font used by the L&F on the given {@link Component} modded
     * with the given attributes.
     * 
     * @param style
     *            - the style, see {@link Font#Font(String, int, int)}
     * @param size
     *            - the size, see {@link Font#Font(String, int, int)}
     * @return a new {@link Font}
     */
    public static Font getDefaultModdedFont(Component c, int style, int size) {
        Font f = c.getFont();
        return new Font(f.getFontName(), style, size);
    }
}
