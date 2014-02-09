package k.core.util.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JMenuItem;

/**
 * An {@link ActionListener} that should be used in combination with
 * {@link Menu}.
 * 
 * @author Kenzie Togami
 */
public abstract class JMIActionListener implements ActionListener {
    /**
     * Variables for mappings....not really needed.
     */
    private String cmd, jmi_ref, menu;

    /**
     * The fake instance used to register on a menu.
     * 
     * @author Kenzie Togami
     */
    private static class NonAbstractJMIActionListener extends JMIActionListener {

        public NonAbstractJMIActionListener(String command,
                String jmi_ref_name, String mtitle) {
            super(command, jmi_ref_name, mtitle);
        }

        @Override
        public void onAction(ActionEvent e) {
            throw new IllegalStateException(
                    "Only registered listener is a dummy class");
        }

    }

    /**
     * The mapping from the reference name to the action listener.
     */
    private static HashMap<String, JMIActionListener> hm = new HashMap<String, JMIActionListener>();
    /**
     * The mapping from the command to the menu reference name.
     */
    private static HashMap<String, String> reg_refs = new HashMap<String, String>();
    /**
     * The {@link Menu} instance this is registered under.
     */
    private Menu cmenu;

    /**
     * "Registers" a new JMIActionListener for callbacks.
     * 
     * @param command
     *            - the command string that a call to
     *            {@link ActionEvent#getActionCommand()} returns (usually the
     *            name).
     * @param jmi_ref_name
     *            - the reference name used by {@link Menu} for the associated
     *            {@link JMenuItem}.
     * @param mtitle
     *            - the reference name used by {@link Menu} for the menu.
     */
    public JMIActionListener(String command, String jmi_ref_name, String mtitle) {
        hm.put(jmi_ref_name, this);
        reg_refs.put(command, mtitle);
        cmd = command;
        jmi_ref = jmi_ref_name;
        menu = mtitle;
        System.err.println("registered new listener: " + hm.get(jmi_ref_name)
                + " on menu ref " + reg_refs.get(command));
    }

    /**
     * Creates a new dummy listener to register via
     * {@link Menu#setActionListenerAll(ActionListener)}.
     * 
     * @param menuKey
     *            - the String used to get a menu instance via
     *            {@link Menu#get(String)}.
     * @return a new instance to do the callbacks on this menu.
     */
    public static JMIActionListener instForMenu(String menuKey) {
        JMIActionListener nl = new NonAbstractJMIActionListener("3.14159",
                null, "bigmagic v35.0");
        nl.cmenu = Menu.get(menuKey);
        return nl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String translatedKey = cmenu.translateJMITitleToRef(
                reg_refs.get(e.getActionCommand()), e.getActionCommand());
        JMIActionListener listener = hm.get(translatedKey);
        if (listener == null && !(translatedKey == null)) {
            System.err.println("Warning: No registered listener for item: "
                    + translatedKey);
            return;
        } else if (listener instanceof NonAbstractJMIActionListener) {
            System.err.println("Invoking class "
                    + listener.getClass().getName() + " with action "
                    + e.toString());
            System.err.println("Null entry for title " + e.getActionCommand()
                    + " (ref is " + translatedKey
                    + "[retrived via cmenu.translateJMITitleToRef("
                    + reg_refs.get(e.getActionCommand())
                    + "(call to reg_refs with '" + e.getActionCommand()
                    + "'), " + e.getActionCommand() + ")!");
            return;
        }
        /*
         * System.err.println("Invoking class " + listener.getClass().getName()
         * + " with action " + e.toString());
         * System.err.println("entry for title " + e.getActionCommand() +
         * " (ref is " + translatedKey +
         * " [retrived via cmenu.translateJMITitleToRef(" +
         * reg_refs.get(e.getActionCommand()) + ", " + e.getActionCommand() +
         * ")!");
         */
        listener.onAction(e);
    }

    /**
     * Returns the command string this listener is registered with.
     * 
     * @return the command string.
     */
    public String getCommand() {
        return cmd;
    }

    /**
     * Returns the reference key this listener uses.
     * 
     * @return the reference key.
     */
    public String getReferenceKey() {
        return jmi_ref;
    }

    /**
     * Returns the menu reference key this listener was registered with.
     * 
     * @return the menu reference key.
     */
    public String getMenuReferenceKey() {
        return menu;
    }

    /**
     * Gets called by the main listener when the appropriate conditions are met.
     * 
     * @param e
     *            - the event.
     */
    public abstract void onAction(ActionEvent e);

}
