package edu.uncfsu.softwaredesign.f16.r2.components;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reservation.ReservationRegistry.ReservationBuilder;

public class ModifyReservationForm extends ReservationForm {

	private static final Logger LOGGER 			= LoggerFactory.getLogger(ModifyReservationForm.class.getSimpleName());	
	private static final long serialVersionUID 	= 5719287999405904497L;

	private ReservationBuilder reservationBuilder = null;
	private Reservation builtReservation = null;
	
	public ModifyReservationForm(ReservationRegistry reservationRegistry) {
		super(reservationRegistry);
	}
	
	@Override
	public Optional<Reservation> doSubmit() {
		return Optional.ofNullable(builtReservation);
	}

	@Override
	public void doCalculate() {
		
		LOGGER.info("Calculate button has been pressed");

	}

}
