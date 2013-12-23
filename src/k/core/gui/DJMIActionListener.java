package k.core.gui;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

public class DJMIActionListener extends JMIActionListener {

    public DJMIActionListener() {
	super("Debug?", SideConsole.DEBUG_JMIKEY, SideConsole.OPTION_MENU);
    }

    @Override
    public void onAction(ActionEvent e) {
	SideConsole.console.error(((JCheckBoxMenuItem) e.getSource())
		.isSelected());
    }

}
