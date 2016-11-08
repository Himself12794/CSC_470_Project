package edu.uncfsu.softwaredesign.f16.r2.components;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.components.card.IApplicationCard;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

public class JReservationPopup extends JRightClickMenu {
	
	private static final long serialVersionUID = -4350918459868320143L;
	
	public JReservationPopup(IApplicationCard parent, List<Reservation> reserves) {
		super();
		
		if (reserves.size() == 1) {
			Reservation reserve = reserves.get(0);
			addMenuItem("Modify Reservation", e -> {
				new ModifyReservationWindow(parent, reserve).setVisible(true);
			});
			
			JMenuItem item = !reserve.isHasCheckedIn() ? ( 
					
				addMenuItem("Check-In Reservation", e -> {
					reserve.setHasCheckedIn(true);
					Application.getApp().getReservationRegistry().updateReservation(reserve);
					parent.reload();
				})
				
			 ) : (
					 
				addMenuItem("Check-Out Reservation", e -> {
					reserve.setHasCheckedOut(true);
					Application.getApp().getReservationRegistry().updateReservation(reserve);
					parent.reload();
				})
				
			);
	
			item.setEnabled(!reserve.isCanceled() && reserve.getRoomNumber() > 0 && !reserve.isHasCheckedOut());
			
			addMenuItem("Assign Room", e -> {
				int room = selectRoomNumber(reserve);
				reserve.setRoomNumber(room);
				Application.getApp().getReservationRegistry().updateReservation(reserve);
				parent.reload();
			}).setEnabled(!reserve.isCanceled() && !reserve.isHasCheckedIn());
		}
		
		addMenuItem("Cancel Reservation", e -> {
			cancel(reserves);
			parent.reload();
		});
		
	}
	
	public int selectRoomNumber(Reservation reserve) {
		
		List<Integer> rooms = Application.getApp().getReservationRegistry().getForReservationDate(reserve.getReservationDate())
				.stream().filter(r -> r.getReservationId() != reserve.getReservationId()).mapToInt(Reservation::getRoomNumber)
				.boxed().collect(Collectors.toList());
		
		Object[] available = IntStream.range(1, ReservationRegistry.MAX_ROOMS+1).filter(i -> !rooms.contains(i)).boxed().toArray();
		
		int s = (int)JOptionPane.showInputDialog(
                Application.getApp(),
                "Select a room for this reservation:\n",
                "Select Room", JOptionPane.PLAIN_MESSAGE,
                null, available, null);
		
		return s;
		
	}
	
	public synchronized void cancel(List<Reservation> reservations) {
		
		String preface = "cancel ";
		String message = reservations.size() == 1 ? "reservation #" + reservations.get(0) : reservations.size() + " reservations";
		String grammar = reservations.size() == 1 ? " has" : " have";
		
		if (!reservations.isEmpty() && Utils.confirmDialog(Application.getApp(), preface + message)) {
			reservations.forEach(r -> r.setCanceled(true));
			Application.getApp().getReservationRegistry().updateReservations(reservations);
			JOptionPane.showMessageDialog(this,  message.replaceFirst("r", "R") + grammar + " been canceled", "Canceled", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
}
