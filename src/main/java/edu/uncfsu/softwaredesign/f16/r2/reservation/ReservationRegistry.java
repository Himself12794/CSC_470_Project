package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reporting.Reportable;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;
import edu.uncfsu.softwaredesign.f16.r2.util.InvalidReservationException;
import edu.uncfsu.softwaredesign.f16.r2.util.NoSuchReservationTypeException;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationException;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationRegistryFullException;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

/**
 * Keeps track of all reservations past, present, and future. Handles
 * new reservations as well. This should be the only class capable of creating a reservation.
 * 
 * @author phwhitin
 *
 */
@Repository
public class ReservationRegistry implements Reportable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReservationRegistry.class.getSimpleName());
	
	public static final short MAX_ROOMS = 45;
	
	private final Map<Long, Reservation> reservations = Maps.newHashMap();
	
	@Autowired
	private CostRegistry costRegistry;
	
	/**
	 * Retrives the specified reservation, if it exists.
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Reservation> getReservationById(long id) {
		return Optional.ofNullable(reservations.get(id));
	}
	
	public List<Reservation> getReservations() {
		return Lists.newArrayList(reservations.values());
	}

	@Override
	public void writeReport(OutputStream out) {
		
	}

	@Override
	public String getReport() {
		return "";
	}
	
	private long getNextFreeId() {
		return reservations.keySet().stream().max(Long::compare).orElse(0L) + 1;
	}
	
	/**
	 * Adds a reservation to the registry only if it does not already exist
	 * 
	 * @param reserve
	 * @throws ReservationRegistryFullException if the registry is full
	 */
	public void registerReservation(Reservation reserve) throws ReservationRegistryFullException {
		
		Optional<List<LocalDate>> dates = getConflicts(reserve);
		
		if (dates.isPresent()) {
			throw new ReservationRegistryFullException(reserve, dates.get());
		}
		
		if (reserve.getReservationId() == -1 || reservations.containsKey(reserve.reservationId)) {
			reserve.reservationId = getNextFreeId();
		} 

		reservations.put(reserve.getReservationId(), reserve);
		
		LOGGER.info("Registered reservation with id {}", reserve.reservationId);
	}
	
	/**
	 * Checks if the reservation can be inserted into the registry.
	 * 
	 * @param reserve
	 * @return a list of the full days, or None if no conflicts
	 * 
	 */
	public Optional<List<LocalDate>> getConflicts(Reservation reserve) {
		
		List<LocalDate> conflicts = Lists.newArrayList();
		
		Utils.dateStream(reserve)
			.filter(d -> getReservationsForDate(d).size() >= MAX_ROOMS)
			.forEach(conflicts::add);

		return Optional.ofNullable(conflicts.isEmpty() ? null : conflicts);
	}
	
	public ReservationBuilder createReservationBuilder(ReservationType type) {
		return new ReservationBuilder(type);
		
	}
	
	public Range<LocalDate> generateRangeForStay(Reservation r) {
		return Range.closed(r.getReservationDate(), r.getReservationDate().plusDays(r.getDays()-1));
	}
	
	public List<Reservation> getReservationsForDate(LocalDate date) {
		return reservations.values().stream().filter(r -> generateRangeForStay(r).contains(date)).collect(Collectors.toList());
	}
	
	public float averageOccupancyForRange(LocalDate startDate, int days) {
		return (float)Utils.dateStream(startDate, days).mapToInt(d -> getReservationsForDate(d).size()).average().orElse(0.0);
	}
	
	/**
	 * Used to build a reservation
	 * 
	 * @author phwhitin
	 *
	 */
	public class ReservationBuilder {
		
		private final ReservationType type;
		private String name;
		private String email;
		private LocalDate reservationDate;
		private LocalDate registrationDate = LocalDate.now();
		private CreditCard card;
		private int days = 1;
		
		private boolean invalid = false;
		
		private ReservationBuilder(ReservationType type) throws NoSuchReservationTypeException {
			this.type = type;
		}
		
		public ReservationBuilder setName(String name) {
			this.name = name;
			return this;
		}
		
		public ReservationBuilder setEmail(String email) {
			this.email = email;
			return this;
		}
		
		/**
		 * Sets the reservation date if it is valid.
		 * 
		 * @param date
		 * @return
		 */
		public ReservationBuilder setReservationDate(LocalDate date) {
			if (date.isAfter(registrationDate.plusDays(type.minimumDays-1))) {
				reservationDate = date;
			}
			return this;
		}
		
		public ReservationBuilder setPayment(CreditCard card) {
			this.card = card;
			return this;
		}
		
		/**
		 * Sets days, default is 1.
		 * 
		 * @param days
		 * @return
		 */
		public ReservationBuilder setDays(int days) {
			this.days = days;
			return this;
		}
		
		private void errorCheck() throws InvalidReservationException {
			
			if (invalid) {
				throw new IllegalStateException("This builder cannot be reused");
			}
			
			Map<String, Object> errors = Maps.newHashMap();
			
			if (reservationDate == null) {
				errors.put("reservationDate", reservationDate);
			} else if (name == null) {
				errors.put("name", name);
			} else if (email == null) {
				errors.put("email", email);
			}
			
			if (!errors.isEmpty()) {
				throw new InvalidReservationException(Utils.createErrorMessageForInvalidValues(errors));
			}
		}
		
		/**
		 * Creates a reservation and optionally registers it in the registry.
		 * 
		 * @return
		 * @throws InvalidReservationException
		 * @throws ReservationRegistryFullException 
		 */
		public Reservation createAndRegister(boolean register) throws ReservationException {
			
			errorCheck();
			
			Reservation reserve = new Reservation(register ? getNextFreeId() : -1, name, email, registrationDate, reservationDate, days, false, costRegistry, false, type.changeFee, type.costModifier, card) {
 
				@Override
				float calculateCost(CostRegistry costs)  {
					
					float[] total = {0.0F}; 
					Utils.dateStream(this).forEach(d -> total[0] += costs.getCostForDay(d) * getCostModifier());
					if (getReservationDate().isBefore(getRegistrationDate().plusDays(30))) {
						if (averageOccupancyForRange(getRegistrationDate(), getDays()) / MAX_ROOMS <= 0.6F) {
							total[0] *= 0.8F;
						}
					}

					setTotalCost(total[0]);
					
					return total[0];
				}
				
			};
			
			if (register) registerReservation(reserve);
			
			invalid = true;
			
			return reserve;
		}
		
		public boolean isValid() {
			return !invalid;
		}
		
	}
	
}
