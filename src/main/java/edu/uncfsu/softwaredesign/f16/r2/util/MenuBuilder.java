package edu.uncfsu.softwaredesign.f16.r2.util;

import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * This is just so we can build menus without adding same JMenu texts twice.
 * 
 * @author phwhitin
 *
 */
public class MenuBuilder {

	private final Map<String, JMenu> menuMappings	= Maps.newHashMap();
	private final List<JMenu> menuOrder				= Lists.newArrayList();
	
	private JMenu lastMenu = null;
	
	private JMenuItem lastItem = null;
	
	public MenuBuilder addMenu(String menu) {
		return addMenu(new JMenu(menu));
	}
	
	public MenuBuilder addMenu(JMenu menu) {
		
		JMenu res = menuMappings.putIfAbsent(menu.getText(), menu);
		lastMenu = res == null ? menu : res;
		if (res == null) { menuOrder.add(menu); }
		
		return this;
	}
	
	public MenuBuilder addMenuAction(ActionListener listener) {
	
		if (lastMenu != null) {
			lastMenu.addActionListener(listener);
		}
		
		return this;
	}
	
	public MenuBuilder addSeparator() {
		if (lastMenu != null) {
			lastMenu.addSeparator();
		}
		
		return this;
	}
	
	public MenuBuilder addMenuItem(String option) {
		return addMenuItem(new JMenuItem(option));
	}
	
	/**
	 * Adds an option to last JMenu item, if it exists.
	 * 
	 * @param option
	 * @return
	 */
	public MenuBuilder addMenuItem(JMenuItem option) {
		
		if (lastMenu != null) {
			lastMenu.add(option);
			lastItem = option;
		}
		
		return this;
	}
	
	/**
	 * Adds action to last item if it exists.
	 * 
	 * @param listener
	 * @return
	 */
	public MenuBuilder addItemAction(ActionListener listener) {
		
		if (lastItem != null) {
			lastItem.addActionListener(listener);
		}
		
		return this;
	}
	
	/**
	 * Builds a menu from current values.
	 * 
	 * @return
	 */
	public JMenuBar build() {
		JMenuBar bar = new JMenuBar();
		
		menuOrder.forEach(bar::add);
		
		return bar;
	}
	
}
