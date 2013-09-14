package k.core.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

public abstract class JMIActionListener implements ActionListener {
	protected String cmd, jmi_ref, menu;

	public static class NonAbstractJMIActionListener extends JMIActionListener {

		public NonAbstractJMIActionListener(String command,
				String jmi_ref_name, String mtitle) {
			super(command, jmi_ref_name, mtitle);
		}

		@Override
		public void onAction(ActionEvent e) {
			System.err
					.println("NonAbstract JMIListener was used! Bad idea for you!");
		}

	}

	private static HashMap<String, JMIActionListener> hm = new HashMap<String, JMIActionListener>();
	private static HashMap<String, String> reg_refs = new HashMap<String, String>();
	private Menu cmenu;

	public JMIActionListener(String command, String jmi_ref_name, String mtitle) {
		hm.put(jmi_ref_name, this);
		reg_refs.put(command, mtitle);
		cmd = command;
		jmi_ref = jmi_ref_name;
		menu = mtitle;
		System.err.println("registered new listener: " + hm.get(jmi_ref_name)
				+ " on menu ref " + reg_refs.get(command));
	}

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
		System.err.println("Invoking class " + listener.getClass().getName()
				+ " with action " + e.toString());
		System.err.println("entry for title " + e.getActionCommand()
				+ " (ref is " + translatedKey
				+ " [retrived via cmenu.translateJMITitleToRef("
				+ reg_refs.get(e.getActionCommand()) + ", "
				+ e.getActionCommand() + ")!");
		listener.onAction(e);
	}

	public abstract void onAction(ActionEvent e);

}
