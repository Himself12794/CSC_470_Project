package edu.uncfsu.softwaredesign.f16.r2.components.card;

import java.awt.Container;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("serial")
public abstract class AbstractCard extends JPanel implements IApplicationCard {

	@Autowired
	protected CardRegistry cardRegistry;

	public AbstractCard(String name) {
		super();
		setName(name);
	}
	
	@Override
	public Container getComponent() {
		return this;
	}
	
	@Override
	public boolean onNavigateAway(IApplicationCard target) {
		return true;
	}
	
	@Override
	public boolean canBeSelected() {
		return true;
	}
	
	@PostConstruct
	public final void register() {
		cardRegistry.registerCard(this);
	}
	
}
