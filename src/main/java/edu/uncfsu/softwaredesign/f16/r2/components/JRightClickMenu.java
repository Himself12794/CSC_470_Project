package edu.uncfsu.softwaredesign.f16.r2.components;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import com.google.common.collect.Lists;

public class JRightClickMenu extends JPopupMenu {

	private static final long serialVersionUID = 3882300236554687712L;

	private final List<JMenuItem> menuItems = Lists.newArrayList(); 
	
	public JRightClickMenu() {}
	
	public JRightClickMenu(List<JMenuItem> menuActions) {
		menuItems.addAll(menuActions);
	}
	
	public JMenuItem addMenuItem(String title, ActionListener action) {
		JMenuItem item = new JMenuItem(title);
		item.addActionListener(action);
		menuItems.add(item);
		add(item);
		return item;
	}
	
	public JMenuItem addMenuItem(JMenuItem item) {
		menuItems.add(item);
		add(item);
		return item;
	}
	
}
