package k.core.util.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import k.core.util.streams.InputPipeStream;
import k.core.util.streams.OutputPipeStream;

public class SwingAWTUtils {

    /**
     * Centers the given {@link Window} on the screen using
     * {@link Window#setLocationRelativeTo(Component)}.
     * 
     * @param frame
     *            - the window to center
     */
    public static void drop(Window frame) {
        frame.setLocationRelativeTo(null);
    }

    /**
     * Sets the background color on the given {@link JFrame}.
     * 
     * @param c
     *            - the color to set the background to
     * @param fr
     *            - the frame to apply the color to
     */
    public static void setBackground(Color c, JFrame fr) {
        Container frame = fr.getContentPane();
        frame.setBackground(c);
    }

    /**
     * Closes the given {@link JFrame}.
     * 
     * @param win
     *            - the frame to close
     */
    public static void kill(JFrame win) {
        if (win == null) {
            return;
        }
        WindowEvent close = new WindowEvent(win, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(close);
    }

    /**
     * Safe handling for the {@link SwingUtilities#invokeAndWait(Runnable)}
     * method. It is unsafe to call invokeAndWait on the dispatch thread due to
     * deadlocks. This method simply runs the given {@link Runnable} if this is
     * called in the dispatch thread.
     * 
     * @param r
     *            - the runnable to run
     * @throws Exception
     *             - any exceptions propagate, the possible
     *             {@link InvocationTargetException} is unwrapped.
     */
    public static void runOnDispatch(Runnable r) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (InvocationTargetException iie) {
                if (iie.getCause() instanceof Exception) {
                    throw (Exception) iie.getCause();
                } else {
                    throw iie;
                }
            }
        }
    }

    /**
     * Removes all the Components from the container, then validates it.
     * 
     * @param c
     *            - the container to remove from
     */
    public static void removeAll(Container c) {
        c.removeAll();
        validate(c);
    }

    /**
     * Adds all of the specified {@link Component components} with the given
     * constraints, then calls {@link #validate(Container)} on <tt>to</tt>.
     * 
     * @param to
     *            - the {@link Container} to add to
     * @param comps
     *            - the components to add
     * @param constraints
     *            - the constraints. If <tt>null</tt>, a new array of size
     *            <tt>comps.length</tt> is created.
     * @see #validate(Container)
     */
    public static void addAllAndValidate(Container to, Component[] comps,
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

    /**
     * Indicates that you wish to set the preferred size in
     * {@link #setAllSize(Component, Dimension, int)}.
     * 
     * @deprecated Use enums instead now.
     */
    @Deprecated
    public static final byte SETPREFERREDSIZE = 1 << 0;

    /**
     * Indicates that you wish to set the maximum size in
     * {@link #setAllSize(Component, Dimension, int)}.
     * 
     * @deprecated Use enums instead now.
     */
    @Deprecated
    public static final byte SETMAXIMUMSIZE = 1 << 1;

    /**
     * Indicates that you wish to set the minimum size in
     * {@link #setAllSize(Component, Dimension, int)}.
     * 
     * @deprecated Use enums instead now.
     */
    @Deprecated
    public static final byte SETMINIMUMSIZE = 1 << 2;

    /**
     * Indicates that you wish to set the size in
     * {@link #setAllSize(Component, Dimension, int)}.
     * 
     * @deprecated Use enums instead now.
     */
    @Deprecated
    public static final byte SETSIZE = 1 << 3;

    /**
     * Indicates that you wish to set all sizes in
     * {@link #setAllSize(Component, Dimension, int)}.
     * 
     * @deprecated Use enums instead now.
     */
    @Deprecated
    public static final byte SETALL = SETPREFERREDSIZE | SETMAXIMUMSIZE
            | SETMINIMUMSIZE | SETSIZE;

    /**
     * Calls the setXSize methods on the given {@link Component}.
     * 
     * @param c
     *            - the component to change
     * @param size
     *            - the size to set to
     * @param flags
     *            - a bitwise OR of {@link #SETPREFERREDSIZE},
     *            {@link #SETMAXIMUMSIZE}, {@link #SETMINIMUMSIZE}, or
     *            {@link #SETSIZE}.
     * @deprecated Use enums instead now.
     */
    @Deprecated
    public static void setAllSize(Component c, Dimension size, int flags) {
        if ((flags & SETPREFERREDSIZE) != 0) {
            c.setPreferredSize(size);
        }
        if ((flags & SETMAXIMUMSIZE) != 0) {
            c.setMaximumSize(size);
        }
        if ((flags & SETMINIMUMSIZE) != 0) {
            c.setMinimumSize(size);
        }
        if ((flags & SETSIZE) != 0) {
            c.setSize(size);
        }
        if (c instanceof Container) {
            validate((Container) c);
        } else {
            validate(c.getParent());
        }
    }

    /**
     * An enum representing the different setXSize methods.
     * 
     * @author Kenzie Togami
     */
    public static enum Size {
        SETPREFFERED, SETMAX, SETMIN, SET, SETALL;
    }

    /**
     * Calls the setXSize methods on the given {@link Component}.
     * 
     * @param c
     *            - the component to change
     * @param size
     *            - the size to set to
     * @param flags
     *            - a set of the requested size changes. {@link EnumSet} is
     *            preferred.
     */
    public static void setAllSize(Component c, Dimension size, Set<Size> flags) {
        if (flags.contains(Size.SETPREFFERED) || flags.contains(Size.SETALL)) {
            c.setPreferredSize(size);
        }
        if (flags.contains(Size.SETMAX) || flags.contains(Size.SETALL)) {
            c.setMaximumSize(size);
        }
        if (flags.contains(Size.SETMIN) || flags.contains(Size.SETALL)) {
            c.setMinimumSize(size);
        }
        if (flags.contains(Size.SET) || flags.contains(Size.SETALL)) {
            c.setSize(size);
        }
        if (c instanceof Container) {
            validate((Container) c);
        } else {
            validate(c.getParent());
        }
    }

    /**
     * Clones the given Component via {@link Serializable} interfacing.
     * 
     * @param c
     *            - the component to clone
     * @return a clone of the original component, done via serialization
     */
    public static <T extends Component> T cloneLikeSerial(T c) {
        try {
            InputPipeStream ips = new InputPipeStream();
            OutputPipeStream ops = new OutputPipeStream(ips);
            ObjectOutputStream oos = new ObjectOutputStream(ops);
            oos.writeObject(c);
            oos.close();
            ObjectInputStream ois = new ObjectInputStream(ips);
            @SuppressWarnings("unchecked")
            T inst = (T) ois.readObject();
            ois.close();
            return inst;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
