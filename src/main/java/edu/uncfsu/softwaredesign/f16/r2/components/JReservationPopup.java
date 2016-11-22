package edu.uncfsu.softwaredesign.f16.r2.components;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import edu.uncfsu.softwaredesign.f16.r2.Application;
import edu.uncfsu.softwaredesign.f16.r2.components.card.IApplicationCard;
import edu.uncfsu.softwaredesign.f16.r2.components.card.ViewReservationsCard;
import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationType;
import edu.uncfsu.softwaredesign.f16.r2.transactions.TransactionController;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

public class JReservationPopup extends JRightClickMenu {
	
	private static final long serialVersionUID = -4350918459868320143L;
	
	private final IApplicationCard parent;
	
	public JReservationPopup(IApplicationCard parent, List<Reservation> reserves) {
		super();
		
		this.parent = parent;
		
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
	
			if (reserve.getType() == ReservationType.CONVENTIONAL && !reserve.isHasPaid() && !reserve.isCanceled()) {
				
				TransactionController controller = ((ViewReservationsCard) parent).getTransactionController();
				
				addMenuItem("Mark No-Show", e -> chargeNoShow(reserve, controller, parent));
				
				addMenuItem("Charge Full Stay", e -> {

					reserve.getCreditCard().ifPresent(c -> {
						controller.charge(c, reserve.getTotalCost());
						reserve.setHasPaid(true);
						Application.getApp().getReservationRegistry().updateReservation(reserve);
					});

					if (!reserve.getCreditCard().isPresent()) 
						Utils.generateErrorMessage("No credit card has been defined for this reservation", "Missing Credit Card");
					
					parent.reload();
				});
			}
				
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
	
	private void chargeNoShow(Reservation reserve, TransactionController controller, IApplicationCard parent) {
		reserve.getCreditCard().ifPresent(c -> {
			controller.charge(c, reserve.getFirstDayCost());
			reserve.setHasPaid(true);
			reserve.setCanceled(true);
			Application.getApp().getReservationRegistry().updateReservation(reserve);
		});
		
		if (!reserve.getCreditCard().isPresent()) 
			Utils.generateErrorMessage("No credit card has been defined for this reservation", "Missing Credit Card");
		
		parent.reload();
	}
	
	public synchronized void cancel(List<Reservation> reservations) {
		
		String preface = "cancel ";
		String message = reservations.size() == 1 ? "reservation #" + reservations.get(0) : reservations.size() + " reservations";
		String grammar = reservations.size() == 1 ? " has" : " have";
		
		if (!reservations.isEmpty() && Utils.confirmDialog(Application.getApp(), preface + message)) {
			reservations.forEach(r -> {
				if (r.getType() == ReservationType.CONVENTIONAL && LocalDate.now().minusDays(3).isBefore(r.getReservationDate())) {
					chargeNoShow(r, ((ViewReservationsCard) parent).getTransactionController(), parent);
				}
				r.setCanceled(true);
			});
			Application.getApp().getReservationRegistry().updateReservations(reservations);
			JOptionPane.showMessageDialog(this,  message.replaceFirst("r", "R") + grammar + " been canceled", "Canceled", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
}
