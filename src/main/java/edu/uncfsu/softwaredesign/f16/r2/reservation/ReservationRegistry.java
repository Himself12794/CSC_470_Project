package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import edu.uncfsu.softwaredesign.f16.r2.reporting.Reportable;

/**
 * Keeps track of all reservations past, present, and future. Handles
 * new reservations as well. This should be the only class capable of creating a reservation.
 * 
 * @author phwhitin
 *
 */
@Service
public abstract class ReservationRegistry implements Reportable {

	private final Map<Long, Reservation> reservations = Maps.newHashMap();
	
	/**
	 * Retrives the specified reservation, if it exists.
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Reservation> getReservationById(long id) {
		return Optional.ofNullable(reservations.get(id));
	}
	
	/**
	 * Creates a new reservation and returns it.
	 * 
	 * @return
	 */
	public abstract Reservation registerNewReservation(String name, LocalDate reservationDate, LocalDate registrationDate, int days, byte type);
	
	private void addReservationToRegistry(Reservation reserve) {
		reservations.put(reserve.getReservationId(), reserve);
	}
}
