package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

@Service
public class RegistrationRegistry {

	private final Map<Long, Reservation> reservation = Maps.newHashMap();
	
	public Optional<Reservation> getReservationById(long id) {
		return Optional.ofNullable(reservation.getOrDefault(id, null));
	}
	
}
