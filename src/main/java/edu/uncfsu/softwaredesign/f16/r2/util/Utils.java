package edu.uncfsu.softwaredesign.f16.r2.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.NoSuchElementException;

public final class Utils {

	private Utils() {}
	
	public static <K, V> K getFirstKeyOfType(Map<K, V> map, V value) {
		return map.entrySet().stream().reduce((r1,r2) -> r1.getValue().equals(value) ? r1 : r2).get().getKey();
	}
	
	/**
	 * Rethrows as a {@link NoSuchReservationTypeException}
	 * 
	 * @param ex
	 * @param type
	 * @throws NoSuchReservationTypeException
	 */
	public static void rethrowException(NoSuchElementException ex, String type) throws NoSuchReservationTypeException {
		throw new NoSuchReservationTypeException(type);
	}
	
	public static Date todayPlus(int days) {
		return Date.from(LocalDate.now().plusDays(days).atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public static Integer[] genRange(int start, int end) {
		
		Integer[] range = new Integer[end - start + 1];
		
		for (int i = 0; i < end; i++) range[i] = start + i;
		
		return range;
	}
	
	public static boolean isInt(String text) {
		
		try {
			Integer.valueOf(text);
		} catch (NumberFormatException nfe) {
			return false;
		}
		
		return true;
	}
	
	public static String createErrorMessageForInvalidValues(Map<String, Object> values) {
		StringBuilder msg = new StringBuilder("Invalid values: ");
		
		values.forEach((k,v) -> msg.append("attribute \"").append(k).append("\" has invalid value \"").append(v).append("\"; ")	);
		
		return msg.toString();
	}
		
}
