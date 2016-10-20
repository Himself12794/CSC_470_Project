package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.uncfsu.softwaredesign.f16.r2.reporting.Reportable;
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
	
	private final Map<String, Class<? extends Reservation>> reservationTypes = Maps.newHashMap();
	@SuppressWarnings("deprecation")
	private final Map<Class<? extends Reservation>, ReservationMeta> reservationMetadata = Maps.newHashMap();
	
	
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
	
	public boolean typeIsPrepaid(Reservation reserve) {
		return typeIsPrepaid(reserve.getClass());
	}
	
	public boolean typeIsPrepaid(Class<? extends Reservation> type) {
		return typeIsPrepaid(Utils.getFirstKeyOfType(reservationTypes, type));
	}
	
	@SuppressWarnings("deprecation")
	public boolean typeIsPrepaid(String type) {
		
		try {
			return reservationMetadata.get(reservationTypes.get(type)).isPrepaid();
		} catch (NoSuchElementException e) {
			Utils.rethrowException(e, type);
		}
		
		return false;
	}
	
	public String[] getTypeNames() {
		return reservationTypes.keySet().toArray(new String[reservationTypes.size()]);
	}
	
	public ReservationBuilder createReservation(String type) {
		return new ReservationBuilder(type);
		
	}
	
	@SuppressWarnings("deprecation")
	@PostConstruct
	@Deprecated
	private void scanTypes() throws BeanInstantiationException {
		
		Reflections reflect = new Reflections("edu.uncfsu.softwaredesign.f16.r2.reservation");
		
		reflect.getSubTypesOf(Reservation.class).stream()
			.filter(c -> c.getDeclaredAnnotation(ReservationMeta.class) != null)
			.forEach(clazz -> {
				
				ReservationMeta meta = clazz.getDeclaredAnnotation(ReservationMeta.class);
				
				if (reservationTypes.containsKey(meta.value())) {
					
					throw new BeanInstantiationException(ReservationRegistry.class, String.format("Conflicting definition for Reservation type name '%s' for type '%s'. Name has already been registered for " +
						"type '%s'.", meta.value(), clazz, reservationTypes.get(meta.value())));
					
				} else {
					reservationTypes.put(meta.value(), clazz);
					reservationMetadata.put(clazz, meta);
				}
				
			});
		
	}
	
	public class ReservationBuilder {
		
		private String name;
		private LocalDate reservationdDate;
		private LocalDate registrationDate;
		
		private ReservationBuilder(String type) throws NoSuchReservationTypeException {
			
		}
		
	} 
	
}
