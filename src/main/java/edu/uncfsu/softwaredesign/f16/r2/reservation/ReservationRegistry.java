package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.io.OutputStream;
import java.lang.reflect.Field;
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
import com.google.common.math.DoubleMath;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reporting.Reportable;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;
import edu.uncfsu.softwaredesign.f16.r2.util.InvalidReservationException;
import edu.uncfsu.softwaredesign.f16.r2.util.NoSuchReservationTypeException;
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
		
	}

	@Override
	public String getReport() {
		return "";
	}
	
	private long getNextFreeId() {
		return reservations.keySet().stream().max(Long::compare).orElse(1L);
	}
	
	public void registerReservation(Reservation reserve) {
		if (reserve.getReservationId() == -1) {
			
			long id = getNextFreeId();
					
			try {
				Field field = Reservation.class.getField("reservationId");
				field.setAccessible(true);
				field.set(reserve, id);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				LOGGER.error("An unexpected error has occured", e);
			}
		}
		
		reservations.put(reserve.getReservationId(), reserve);
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
		
		List<Integer> dayCounts = Lists.newArrayList();
		
		for (LocalDate start = startDate; start.isBefore(startDate.plusDays(days+1)); start = start.plusDays(1)) {
			dayCounts.add(getReservationsForDate(start).size());
		}
		return (float) DoubleMath.mean(dayCounts);
	}
	
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
		 */
		public Reservation createAndRegister(boolean register) throws InvalidReservationException {
			
			errorCheck();
			
			Reservation reserve = new Reservation(register ? getNextFreeId() : -1, name, email, registrationDate, reservationDate, days, false, costRegistry, false, type.changeFee, type.costModifier, card) {
 
				@Override
				float calculateCost(CostRegistry costs)  {
					
					float total = 0.0F; 
					for (LocalDate start = getReservationDate(); start.isBefore(getReservationDate().plusDays(getDays()+1)); start = start.plusDays(1)) {
						total += costs.getCostForDay(start) * getCostModifier();
					}
					if (getReservationDate().isBefore(getRegistrationDate().plusDays(30))) {
						if (averageOccupancyForRange(getRegistrationDate(), getDays()) / MAX_ROOMS <= 0.6F) {
							total *= 0.8F;
						}
						
					}

					LOGGER.info("This total has been gotten {}", total);
					
					setTotalCost(total);
					
					return total;
				}
				
			};
			
			if (register) reservations.put(reserve.getReservationId(), reserve);
			
			invalid = true;
			
			return reserve;
		}
		
		public boolean isValid() {
			return !invalid;
		}
		
	}
	
}
