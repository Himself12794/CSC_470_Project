package edu.uncfsu.softwaredesign.f16.r2.util;

import java.util.NoSuchElementException;

/**
 * Thrown when a reservation type has not been registered
 * 
 * @author phwhitin
 *
 */
public class NoSuchReservationTypeException extends NoSuchElementException {

	private static final long serialVersionUID = -5543188807126418383L;

	public NoSuchReservationTypeException(String type) {
		super("No such reservation with identifier \'" + type +"\' found");
	}
}
