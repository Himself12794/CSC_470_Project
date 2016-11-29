package edu.uncfsu.softwaredesign.f16.r2.components;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationRegistryFullException;

public class ModifyReservationForm extends ReservationForm {

	private static final Logger LOGGER 			= LoggerFactory.getLogger(ModifyReservationForm.class.getSimpleName());	
	private static final long serialVersionUID 	= 5719287999405904497L;
	
	private final ModifyReservationWindow parentWindow;
	
	public ModifyReservationForm(ModifyReservationWindow window, ReservationRegistry reservationRegistry) {
		super(reservationRegistry);
		dropDown.setEnabled(false);
		confirmButton.setEnabled(false);
		parentWindow = window;
		isViewing = false;
	}
	
	@Override
	public Optional<Reservation> doConfirm() {
		
		if (doCheck()) {
			
			builtReservation.setCreditCard(getCardFromFields());
			builtReservation.setCustomer(getCustomer());
			
			reservationRegistry.calculateChangeFee(builtReservation, getReservationDate(), getDays(), true);
			reservationRegistry.updateReservation(builtReservation);
			
			parentWindow.close();
		} 
		
		return Optional.ofNullable(builtReservation);
	}

	@Override
	public boolean doCheck() {
		super.checkAvailability();
		
		boolean canUpdate = false;
		
		if (builtReservation != null) {
			
			if (builtReservation.getDays() != getDays() || !builtReservation.getReservationDate().equals(getReservationDate())) {
				
				Optional<List<LocalDate>> conflicts = reservationRegistry.getConflicts(builtReservation.getReservationId(), getReservationDate(), getDays());
				
				if (!conflicts.isPresent()) {
					float newCost = reservationRegistry.calculateChangeFee(builtReservation, getReservationDate(), getDays(), false);
					totalCostLabel.setText("Change in cost");
					totalCost.setText(String.format("+$%.2f", newCost));
					canUpdate |= true;
				} else {
					showError(new ReservationRegistryFullException(conflicts.get()));
				}
				
			}
			
			canUpdate |= !getCustomer().equals(builtReservation.getCustomer()) 
					  || !getCardFromFields().equals(builtReservation.getCreditCard());
			
		}
		
		confirmButton.setEnabled(canUpdate);
		return canUpdate;
	}

}
