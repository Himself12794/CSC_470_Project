package edu.uncfsu.softwaredesign.f16.r2.util;

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
	
}
