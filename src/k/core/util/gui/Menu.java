package k.core.util.gui;

import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * A menu system that allows easy usage via reference names, instead of
 * remembering titles and dealing with 100s of vars.
 * 
 * @author Kenzie Togami
 */
public class Menu {

    /**
     * The mapping from {@link Menu} reference names to instances.
     */
    private static HashMap<String, Menu> menus = new HashMap<String, Menu>();
    /**
     * The mapping from {@link JMenu} reference names to instances.
     */
    private HashMap<String, JMenu> map = new HashMap<String, JMenu>();
    /**
     * The mapping from {@link JMenu} reference names to titles.
     */
    private HashMap<String, String> titleToRef = new HashMap<String, String>();
    /**
     * The bar used to hold everything.
     */
    private JMenuBar bar = new JMenuBar();
    /**
     * The mapping from {@link JMenuItem} reference names to instances.
     */
    private HashMap<String, JMenuItem> jmimap = new HashMap<String, JMenuItem>();
    /**
     * The mapping from {@link JMenuItem} reference names to titles.
     */
    private HashMap<String, String> jmiTitleToRef = new HashMap<String, String>();
    /**
     * A special key used to combine certain strings where needed. If ANYTHING
     * gets named this, things will break.
     */
    private static final String mjmiakey = "_unbreakable_keys_";

    /**
     * Concatenates the two strings together with the {@link #mjmiakey} key in
     * the center.
     * 
     * @param m
     *            - the first string
     * @param jmi
     *            - the second string
     * @return the concatenated string
     */
    private String a(String m, String jmi) {
        return m + mjmiakey + jmi;
    }

    /**
     * Creates a new Menu, private due to the fact that we cache the instances.
     */
    private Menu() {

    }

    /**
     * Creates a new Menu with the given reference name.
     * 
     * @param key
     *            - the reference name
     * @return a menu instance.
     */
    public static Menu create(String key) {
        if (menus.containsKey(key)) {
            return get(key);
        }
        menus.put(key, new Menu());
        return menus.get(key);
    }

    /**
     * Returns the menu registered under the given reference name. May be null!
     * 
     * @param key
     *            - the reference name
     * @return the registered Menu instance.
     */
    public static Menu get(String key) {
        return menus.get(key);
    }

    /**
     * Adds a new {@link JMenu}, the reference name is the title.
     * 
     * @param title
     *            - the title to give the JMenu
     */
    public void addMenu(String title) {
        addMenuByName(title, title);
    }

    /**
     * Adds a new {@link JMenu} with the given reference name and title.
     * 
     * @param menu_ref_name
     *            - the reference name
     * @param ctitle
     *            - the title
     */
    public void addMenuByName(String menu_ref_name, String ctitle) {
        map.put(menu_ref_name, new JMenu(ctitle));
        bar.add(map.get(menu_ref_name));
        titleToRef.put(ctitle, menu_ref_name);
    }

    /**
     * Adds a new {@link JMenuItem} to the {@link JMenu} represented by the
     * given reference name. The reference name for the JMI is the title.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param title
     *            - the title to give the JMI
     */
    public void addMenuItemToMenu(String menu_ref_name, String title) {
        addMenuItemToMenuByName(menu_ref_name, title, title);
    }

    /**
     * Adds a new {@link JMenuItem} with the given reference name to the
     * {@link JMenu} represented by the given reference name.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param title
     *            - the title to give the JMI
     * @param jmi_ref_name
     *            - the JMI reference name
     */
    public void addMenuItemToMenuByName(String menu_ref_name,
            String jmi_ref_name, String title) {
        JMenu menu = map.get(menu_ref_name);
        if (menu == null) {
            System.err.println("No menu accesible from refname "
                    + menu_ref_name + "!");
            return;
        }
        jmimap.put(a(menu_ref_name, jmi_ref_name),
                menu.add(new JMenuItem(title)));
        jmiTitleToRef.put(a(menu_ref_name, title),
                a(menu_ref_name, jmi_ref_name));
    }

    /**
     * Adds the given {@link JMenuItem} to the {@link JMenu} represented by the
     * given reference name.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param item
     *            - the JMI to add
     */
    public void addGenericMenuItemToMenu(String menu_ref_name, JMenuItem item) {
        addGenericMenuItemToMenuByName(menu_ref_name, item.getText(), item);
    }

    /**
     * Adds the given {@link JMenuItem} to the {@link JMenu} represented by the
     * given reference name.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param jmi_ref_name
     *            - the JMI reference name
     * @param item
     *            - the JMI to add
     */
    public void addGenericMenuItemToMenuByName(String menu_ref_name,
            String jmi_ref_name, JMenuItem item) {
        JMenu menu = map.get(menu_ref_name);
        if (menu == null) {
            System.err.println("No menu accesible from refname "
                    + menu_ref_name + "!");
            return;
        }
        jmimap.put(a(menu_ref_name, jmi_ref_name), menu.add(item));
        jmiTitleToRef.put(a(menu_ref_name, item.getText()),
                a(menu_ref_name, jmi_ref_name));
    }

    /**
     * Adds a {@link JMenu} to the JMenu represented by the given reference
     * name. The reference name is the title.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param title
     *            - the title to give the JMenu
     */
    public void addMenuToMenu(String menu_ref_name, String title) {
        addMenuToMenuByName(menu_ref_name, title, title);
    }

    /**
     * Adds a {@link JMenu} to the JMenu represented by the given reference
     * name. The reference name is the title.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param menu_ref_name2
     *            - the reference name to give the JMenu
     * @param title
     *            - the title to give the JMenu
     */
    public void addMenuToMenuByName(String menu_ref_name,
            String menu_ref_name2, String title) {
        JMenu menu = map.get(menu_ref_name);
        if (menu == null) {
            System.err.println("No menu accesible from refname "
                    + menu_ref_name + "!");
            return;
        }
        map.put(menu_ref_name2, new JMenu(title));
        menu.add(map.get(menu_ref_name2));
        titleToRef.put(title, menu_ref_name2);
    }

    /**
     * Returns the {@link JMenu} from the given title.
     * 
     * @param menu_title
     *            - the title of the JMenu
     * @return the menu, or null if there is none
     */
    public JMenu getMenuByTitle(String menu_title) {
        return map.get(titleToRef.get(menu_title));
    }

    /**
     * Returns the {@link JMenu} from the given reference name.
     * 
     * @param menu_ref_name
     *            - the reference name of the JMenu
     * @return the menu, or null if there is none
     */
    public JMenu getMenuByRef(String menu_ref_name) {
        return map.get(menu_ref_name);
    }

    /**
     * Returns the {@link JMenuItem} from the given title, under the given
     * reference name.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param jmi_title
     *            - the title of the JMenuItem
     * @return the item, or null if there is none
     */
    public JMenuItem getItemInMenuByTitle(String menu_ref_name, String jmi_title) {
        return jmimap.get(jmiTitleToRef.get(a(menu_ref_name, jmi_title)));
    }

    /**
     * Returns the {@link JMenuItem} from the given reference name, under the
     * given reference name.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param jmi_ref_name
     *            - the reference name of the JMenuItem
     * @return the item, or null if there is none
     */
    public JMenuItem getItemInMenuByRef(String menu_ref_name,
            String jmi_ref_name) {
        return jmimap.get(a(menu_ref_name, jmi_ref_name));
    }

    /**
     * Translates the given menu title to it's reference name
     * 
     * @param menu_title
     *            - the menu title
     * @return the registered reference name, or null if there is none.
     */
    public String translateMenuTitleToRef(String menu_title) {
        return titleToRef.get(menu_title);
    }

    /**
     * Translates the given JMI title to it's reference name.
     * 
     * @param menu_ref_name
     *            - the menu reference name
     * @param jmi_title
     *            - the JMI title
     * @return the registered reference name, or null if there is none.
     */
    public String translateJMITitleToRef(String menu_ref_name, String jmi_title) {
        String jmi = jmiTitleToRef.get(a(menu_ref_name, jmi_title));
        if (jmi == null) {
            return null;
        }
        return jmi.split(mjmiakey)[1];
    }

    /**
     * Adds this Menu's {@link JMenuBar} to the given {@link JFrame}.
     * 
     * @param frame
     *            - the frame to attach to
     */
    public void display(JFrame frame) {
        frame.setJMenuBar(bar);
    }

    /**
     * Sets the given {@link ActionListener} on all the {@link JMenuItem
     * JMenuItems} in this Menu.
     * 
     * @param inst
     *            - the ActionListener to add
     */
    public void setActionListenerAll(ActionListener inst) {
        for (JMenuItem jmi : jmimap.values()) {
            jmi.addActionListener(inst);
        }
    }

    /**
     * Returns the "unbreakable" key used to combine JMI reference names and
     * their parent menu reference names.
     * 
     * @return the unbreakable key, {@link #mjmiakey}
     */
    public static String unbreakableKey() {
        return mjmiakey;
    }

}
