package edu.uncfsu.softwaredesign.f16.r2.components;

import javax.swing.SwingUtilities;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

public class JReservationPopup extends JRightClickMenu {
	
	private static final long serialVersionUID = -4350918459868320143L;
	
	private final Reservation reservation;
	
	public JReservationPopup(Reservation reserve) {
		super();
		
		reservation = reserve;
		addMenuItem("Modify Reservation", e -> {
			new ModifyReservationWindow(reserve).setVisible(true);
		});
		
		addMenuItem("Cancel Reservation", e -> {
			ModifyReservationWindow mod = new ModifyReservationWindow(reserve);
			
			SwingUtilities.invokeLater(mod::cancel);
		});
	}
	
}
