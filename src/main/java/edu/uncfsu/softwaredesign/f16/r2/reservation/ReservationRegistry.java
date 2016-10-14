package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uncfsu.softwaredesign.f16.r2.reporting.Reportable;

/**
 * Keeps track of all reservations past, present, and future. Handles
 * new reservations as well. This should be the only class capable of creating a reservation.
 * 
 * @author phwhitin
 *
 */
@Repository
public class ReservationRegistry implements Reportable {
	
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
	public Reservation registerNewReservation(String name, LocalDate reservationDate, LocalDate registrationDate, int days, byte type) {
		return null;
	}
	
	private void addReservationToRegistry(Reservation reserve) {
		reservations.put(reserve.getReservationId(), reserve);
	}
	
	public List<Reservation> getReservations() {
		return Lists.newArrayList(reservations.values());
	}

	@Override
	public void writeReport(OutputStream out) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getReport() {
		// TODO Auto-generated method stub
		return null;
	}
}
