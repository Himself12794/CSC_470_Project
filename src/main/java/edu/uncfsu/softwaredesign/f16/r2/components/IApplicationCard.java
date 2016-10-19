package edu.uncfsu.softwaredesign.f16.r2.components;

import java.awt.Container;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.util.MenuBuilder;

public interface IApplicationCard {
	
	/**
	 * A name by which to identify this component.
	 * 
	 * @return
	 */
	String getName();
	
	/**
	 * Called after building.
	 * 
	 * @return
	 */
	Container getComponent();
	
	/**
	 * This will be called before {@link IApplicationCard#getComponent()}, and it
	 * will only be called once.
	 */
	void buildComponent();
	
	/**
	 * This determines the menu this item can be activated from.
	 * 
	 * @return
	 */
	String getMenuName();
	
	/**
	 * The option the can be clicked to open this card.
	 * 
	 * @return
	 */
	String getMenuItemName();
	
	/**
	 * Determines whether or not this card can currently be selected.
	 * 
	 * @return
	 */
	boolean canBeSelected();
	
	default void buildMenu(MenuBuilder builder, Application app){
		builder.addMenu(getMenuName()).addMenuItem(getMenuItemName()).addItemAction(l -> app.setCurrentCard(getName()));
	}
	
	/**
	 * Called every time this card is opened.
	 * 
	 */
	void reload();
	
	/**
	 * Whether or not we should navigate form this card.
	 * 
	 * @param target
	 * @return
	 */
	boolean onNavigateAway(IApplicationCard target);
}
