package edu.uncfsu.softwaredesign.f16.r2.components;

import javax.swing.JButton;
import javax.swing.JDialog;

import edu.uncfsu.softwaredesign.f16.r2.components.card.IApplicationCard;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

public class ChargeConventionalWindow extends JDialog {

	private static final long serialVersionUID = 2742541878430028730L;

	private static final String TITLE = "Charge for resrevation #";
	
	private final JButton chargeFirstDay = new JButton("Charge First Day");
	
	private final IApplicationCard parent;
	private final Reservation reservation;
	
	public ChargeConventionalWindow(IApplicationCard parent, Reservation reserve) {
		super();
		this.parent = parent;
		this.reservation = reserve;
		
		setTitle(TITLE + reserve.getReservationId());
	}

}
