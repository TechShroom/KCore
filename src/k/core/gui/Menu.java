package k.core.gui;

import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu {

    private static HashMap<String, Menu> menus = new HashMap<String, Menu>();
    private HashMap<String, JMenu> map = new HashMap<String, JMenu>();
    private HashMap<String, String> titleToRef = new HashMap<String, String>();
    private JMenuBar bar = new JMenuBar();
    private HashMap<String, JMenuItem> jmimap = new HashMap<String, JMenuItem>();
    private HashMap<String, String> jmiTitleToRef = new HashMap<String, String>();
    private static final String mjmiakey = "_unbreakable_keys_";

    private String a(String m, String jmi) {
	return m + mjmiakey + jmi;
    }

    private Menu() {

    }

    public static Menu create(String key) {
	if (menus.containsKey(key)) {
	    return get(key);
	}
	menus.put(key, new Menu());
	return menus.get(key);
    }

    public static Menu get(String key) {
	return menus.get(key);
    }

    public void addMenu(String title) {
	addMenuByName(title, title);
    }

    public void addMenuByName(String menu_ref_name, String ctitle) {
	map.put(menu_ref_name, new JMenu(ctitle));
	bar.add(map.get(menu_ref_name));
	titleToRef.put(ctitle, menu_ref_name);
    }

    public void addMenuItemToMenu(String menu_ref_name, String title) {
	addMenuItemToMenuByName(menu_ref_name, title, title);
    }

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

    public void addGenericMenuItemToMenu(String menu_ref_name, JMenuItem item) {
	addGenericMenuItemToMenuByName(menu_ref_name, item.getText(), item);
    }

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

    public void addMenuToMenu(String menu_ref_name, String title) {
	addMenuToMenuByName(menu_ref_name, title, title);
    }

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

    public JMenu getMenuByTitle(String menu_title) {
	return map.get(titleToRef.get(menu_title));
    }

    public JMenu getMenuByRef(String menu_ref_name) {
	return map.get(menu_ref_name);
    }

    public JMenuItem getItemInMenuByTitle(String menu_ref_name, String jmi_title) {
	return jmimap.get(jmiTitleToRef.get(a(menu_ref_name, jmi_title)));
    }

    public JMenuItem getItemInMenuByRef(String menu_ref_name,
	    String jmi_ref_name) {
	return jmimap.get(a(menu_ref_name, jmi_ref_name));
    }

    public String translateMenuTitleToRef(String menu_title) {
	return titleToRef.get(menu_title);
    }

    public String translateJMITitleToRef(String menu_ref_name, String jmi_title) {
	String jmi = jmiTitleToRef.get(a(menu_ref_name, jmi_title));
	if (jmi == null) {
	    return null;
	}
	return jmi.split(mjmiakey)[1];
    }

    public void display(JFrame frame) {
	frame.setJMenuBar(bar);
    }

    public void setActionListenerAll(ActionListener inst) {
	for (JMenuItem jmi : jmimap.values()) {
	    jmi.addActionListener(inst);
	}
    }

    public static String unbreakableKey() {
	return mjmiakey;
    }

}
