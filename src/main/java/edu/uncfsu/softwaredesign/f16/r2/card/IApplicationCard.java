package edu.uncfsu.softwaredesign.f16.r2.card;

import java.awt.Container;

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
	 * This will be called before {@link IApplicationCard#getComponent()}
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
	String getMenuOptionName();
	
	/**
	 * Determines whether or not this card can currently be selected.
	 * 
	 * @return
	 */
	boolean canBeSelected();
	
	/**
	 * Called every time this card is open.
	 * 
	 */
	void reload();
}
