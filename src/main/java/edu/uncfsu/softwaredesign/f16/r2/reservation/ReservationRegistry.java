package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.reporting.Reportable;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;
import edu.uncfsu.softwaredesign.f16.r2.transactions.TransactionController;
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
	private static final String SAVE_LOCATION = "data/";
	private static final File SAVE_FILE	= new File(SAVE_LOCATION, "reservation-registry.dat");
	
	public static final short MAX_ROOMS = 45;
	
	final Map<Long, Reservation> reservations = Maps.newHashMap();
	
	@Autowired
	private CostRegistry costRegistry;
	
	@Autowired
	private TransactionController transactionController;
	
	private final File saveFile;
	
	public ReservationRegistry() {
		this(SAVE_FILE);
	}
	
	public ReservationRegistry(File file) {
		saveFile = file;
	}
	
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

	public List<Reservation> getForReservationDate(LocalDate date) {
		return getByFilter(r -> r.getReservationDate().equals(date));
	}
	
	public List<Reservation> getByFilter(Predicate<Reservation> filter) {
		return Lists.newArrayList(reservations.values()).stream().filter(filter).collect(Collectors.toList());
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
		
		Optional<List<LocalDate>> conflicts = getConflicts(reserve.getReservationDate(), reserve.getDays());
		System.out.println(conflicts.orElse(Lists.newArrayList()).size());
		if (conflicts.isPresent()) {
			throw new ReservationRegistryFullException(reserve, conflicts.get());
		}
		
		if (reserve.getReservationId() == -1 || reservations.containsKey(reserve.reservationId)) {
			reserve.reservationId = getNextFreeId();
		} 

		reservations.put(reserve.getReservationId(), reserve);
		
		if (reserve.theType.isPrepaid) {
			transactionController.doPayment(reserve);
		}
		
		LOGGER.info("Registered reservation with id {}", reserve.reservationId);
		saveToDisk();
	}
	
	/**
	 * Checks if the reservation can be inserted into the registry.
	 * 
	 * @param reserve
	 * @return a list of the full days, or None if no conflicts
	 * 
	 */
	public Optional<List<LocalDate>> getConflicts(LocalDate date, int days) {
		return getConflicts(-1, date, days);
	}
	
	/**
	 * Gets conflicts for a certain day, ignoring reservations with the specific type.
	 * This is useful for modifying a reservation.
	 * 
	 * @param id
	 * @param date
	 * @param days
	 * @return
	 */
	public Optional<List<LocalDate>> getConflicts(long id, LocalDate date, int days) {
		
		List<LocalDate> conflicts = Lists.newArrayList();
		
		Utils.dateStream(date, days)
			.filter(d -> getReservationsForDate(d).stream()
					.filter(c -> c.reservationId != id 
							&& !c.isCanceled()).toArray().length >= MAX_ROOMS)
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
	

	public int getMaxRooms() {
		return MAX_ROOMS;
	}

	/**
	 * Calculates the fee for changing a reservation. Optionally updates the entry.
	 * 
	 * @param reservation
	 * @param date
	 * @param days
	 * @param update
	 * @return
	 */
	public float calculateChangeFee(Reservation reservation, LocalDate date, int days, boolean update) {
		
		float newCost = reservation.calculateCost(date, days, costRegistry, this);
		newCost *= reservation.theType.changeFee;
		
		if (update) {
			float diff = reservation.updateCost(date, days, this, costRegistry);
			if (reservation.getType().isPrepaid && diff > 0.0F) {
				transactionController.charge(reservation.getCreditCard().get(), diff);
			}
		}
		
		return newCost;
	}
	
	/**
	 * Updates the entry in the registry and on the disk. If this is not called,
	 * reservation data will not be persisted between sessions. 
	 * 
	 * 
	 * @param reservation
	 */
	public synchronized void updateReservation(Reservation reservation) {
		reservations.put(reservation.reservationId, reservation);
		saveToDisk();
	}
	
	public synchronized void updateReservations(Collection<Reservation> reservations) {
		reservations.forEach(reservation -> this.reservations.put(reservation.reservationId, reservation));
		saveToDisk();
	}
	
	/**
	 * Used to build a reservation
	 * 
	 * @author phwhitin
	 *
	 */
	public class ReservationBuilder {
		
		private final ReservationType type;
		private Customer customer;
		private LocalDate reservationDate;
		private LocalDate registrationDate = LocalDate.now();
		private CreditCard card;
		private int days = 1;
		
		private boolean invalid = false;
		
		private ReservationBuilder(ReservationType type) throws NoSuchReservationTypeException {
			System.out.println("new builder");
			this.type = type;
		}
		
		public ReservationBuilder setCustomer(Customer name) {
			this.customer = name;
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
		
		public ReservationBuilder setRegistrationDate(LocalDate date) {
			registrationDate = date;
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
		
		/**
		 * Checks for invalid state and throws errors if it encounters problems
		 * 
		 * @throws InvalidReservationException
		 */
		private void errorCheck() throws InvalidReservationException {
			
			if (invalid) {
				throw new IllegalStateException("This builder cannot be reused");
			}
			
			Map<String, Object> errors = Maps.newHashMap();
			
			if (reservationDate == null) {
				errors.put("reservationDate", reservationDate);
			}
			
			if (customer == null) {
				errors.put("customer", customer);
			} else if (customer != null) {
				if (Strings.isNullOrEmpty(customer.getAddress())) {
					errors.put("street", customer.getAddress());
				} if (Strings.isNullOrEmpty(customer.getCity())) {
					errors.put("city", customer.getCity());
				} if (Strings.isNullOrEmpty(customer.getEmail())) {
					errors.put("email", customer.getEmail());
				} if (Strings.isNullOrEmpty(customer.getName())) {
					errors.put("name", customer.getName());
				} if (String.valueOf(customer.getPhone()).length() < 10) {
					errors.put("phone", customer.getPhone());
				} if (customer.getZipCode() == 0) {
					errors.put("zipCode", customer.getZipCode());
				}
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
			
			Reservation reserve = new Reservation(register ? getNextFreeId() : -1, customer, registrationDate, reservationDate, days, costRegistry, ReservationRegistry.this, type.changeFee, card, type);
			
			if (register) registerReservation(reserve);
			
			invalid = true;
			
			return reserve;
		}
		
		public boolean isValid() {
			return !invalid;
		}
		
	}

	void saveToDisk() {
		
		try {

			FileUtils.forceMkdir(saveFile.getParentFile());
			
			List<Reservation> items = Lists.newArrayList(reservations.values());
			
			FileOutputStream fos = new FileOutputStream(saveFile, false);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(items);
			oos.flush();
			oos.close();
			
		} catch (IOException e) {
			LOGGER.error("An unexpected exception has occurred during saving data to disk", e);
		}
	}
	
	/**
	 * Note: this will overwrite any unsaved data.
	 */
	@PostConstruct
	@SuppressWarnings("unchecked")
	void loadFromDisk() {

		if (saveFile.exists()) {
			try {
				
				FileInputStream fis = new FileInputStream(saveFile);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Collection<Reservation> items = (Collection<Reservation>) ois.readObject();
				ois.close();
				
				reservations.clear();
				
				items.stream().forEach(r -> reservations.put(r.reservationId, r));
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
}
