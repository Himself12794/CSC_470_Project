package edu.uncfsu.softwaredesign.f16.r2.util;

import java.time.LocalDate;
import java.util.List;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

public class ReservationRegistryFullException extends ReservationException {

	private static final long serialVersionUID = -1337341867145142782L;

	private final List<LocalDate> dates;
	private final Reservation reserve;
	
	public ReservationRegistryFullException(Reservation reserve, List<LocalDate> dates) {
		super(Utils.createErrorMessageForFullRegistry(dates));
		this.dates = dates;
		this.reserve = reserve;
	}
	
	public List<LocalDate> getFullDays() {
		return dates;
	}
	
	public Reservation getReservation() {
		return reserve;
	}
	
}
