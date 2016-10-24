package edu.uncfsu.softwaredesign.f16.r2.reservation;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.YearMonth;

import org.junit.Test;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;
import edu.uncfsu.softwaredesign.f16.r2.util.ReservationException;

public class TestReservationRegistry {
	
	private static final File SAVE_LOCATION = new File("data/");
	private static final File SAVE_FILE	= new File(SAVE_LOCATION, "reservation-registry-test.dat");

	@Test
	public void testReadWrite() throws ReservationException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		
		CostRegistry costs = new CostRegistry();
		ReservationRegistry registry = new ReservationRegistry();
		
		Field field = registry.getClass().getDeclaredField("costRegistry");
		field.setAccessible(true);
		field.set(registry, costs);
		
		Reservation reserve = registry.createReservationBuilder(ReservationType.CONVENTIONAL)
			.setDays(1).setName("Bob").setEmail("sfsdf")
			.setPayment(new CreditCard("bob", "3123412341", YearMonth.now(), (short)123))
			.setReservationDate(LocalDate.now().plusDays(2))
			.createAndRegister(false);
		
		registry.registerReservation(reserve);
		
		registry.saveToDisk(SAVE_FILE, SAVE_LOCATION);
		registry.reservations.clear();
		registry.loadFromDisk(SAVE_FILE);
		
		assertTrue(!registry.reservations.isEmpty());
		
		System.out.println(reserve);
		registry.reservations.values().forEach(System.out::println);
		assertTrue(registry.getReservationById(reserve.reservationId).get().equals(reserve));
		
	}
	
}
