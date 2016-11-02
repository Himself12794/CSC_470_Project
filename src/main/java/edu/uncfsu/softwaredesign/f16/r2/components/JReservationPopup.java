package edu.uncfsu.softwaredesign.f16.r2.components;

import javax.swing.SwingUtilities;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.components.card.IApplicationCard;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

public class JReservationPopup extends JRightClickMenu {
	
	private static final long serialVersionUID = -4350918459868320143L;
	
	public JReservationPopup(IApplicationCard parent, Reservation reserve) {
		super();
		
		addMenuItem("Modify Reservation", e -> {
			new ModifyReservationWindow(parent, reserve).setVisible(true);
		});
		
		if (!reserve.isCanceled() && reserve.getRoomNumber() > 0) {
			
			if (!reserve.isHasCheckedIn()) {
				
				addMenuItem("Check-In Reservation", e -> {
					reserve.setHasCheckedIn(true);
					Application.getApp().getReservationRegistry().updateReservation(reserve);
				});
				
			} else {
				
				addMenuItem("Check-Out Reservation", e -> {
					reserve.setHasCheckedOut(true);
					Application.getApp().getReservationRegistry().updateReservation(reserve);
				});
				
			}
			
		} else if (!reserve.isCanceled() && reserve.getRoomNumber() < 0) {
			
			addMenuItem("Assign Room", e -> {
				
			});
			
		}
		
		addMenuItem("Cancel Reservation", e -> {
			ModifyReservationWindow mod = new ModifyReservationWindow(parent, reserve);
			
			SwingUtilities.invokeLater(mod::cancel);
		});
	}
	
}
