package edu.uncfsu.softwaredesign.f16.r2.components;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry.ReservationBuilder;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationType;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationException;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationRegistryFullException;

public class RegisterReservationForm extends ReservationForm {

	private static final long serialVersionUID 	= 5729618691732585588L;
	private static final Logger LOGGER 			= LoggerFactory.getLogger(RegisterReservationForm.class.getSimpleName());

	private Reservation builtReservation = null;
	
	public RegisterReservationForm(ReservationRegistry reservationRegistry) {
		super(reservationRegistry);
		isViewing = false;
		doTypeValidation(null);
		confirmButton.setEnabled(false);
	}
	
	/**
	 * Called when this form is submitted.
	 * 
	 */
	@Override
	public Optional<Reservation> doConfirm() {
		
		try {
			reservationRegistry.registerReservation(builtReservation);
			builtReservation = null;
			confirmButton.setEnabled(false);
		} catch (ReservationRegistryFullException re) {
			LOGGER.error("The registry is full for this reservation range");
			LOGGER.trace("Trace", re);
			showError(re);
		}
		
		return Optional.ofNullable(builtReservation);
	}
	
	/**
	 * Called to calculate the price
	 * 
	 * @param e
	 */
	@Override
	public boolean doCheck() {
		
		ReservationBuilder reservationBuilder = reservationRegistry.createReservationBuilder(ReservationType.values()[dropDown.getSelectedIndex()]);

		reservationBuilder.setDays(getDays())
			.setName(getGuestName())
			.setEmail(getGuestEmail())
			.setPayment(getCardFromFields())
			.setReservationDate(getReservationDate());
		
		try {
			builtReservation = reservationBuilder.createAndRegister(false);
			confirmButton.setEnabled(true);
			totalCost.setText(String.format("$%.2f", builtReservation.getTotalCost()));
			return true;
			
		} catch (ReservationException e1) {
			
			LOGGER.error("An error occured in attempting to create a reservation");
			LOGGER.trace("Trace", e1);
			showError(e1);
		}
		
		return false;
	}
}
