package edu.uncfsu.softwaredesign.f16.r2.reservation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Holds meta-data about Reservation types.
 * 
 * @deprecated Uses {@link Reservation.Type} to keep track now
 * 
 * @author phwhitin
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Deprecated
public @interface ReservationMeta {

	/**
	 * The name of the type
	 * @return
	 */
	String value();

	/**
	 * Minimum day limit
	 * 
	 * @return
	 */
	short minimumDays() default 1;
	
	/**
	 * Whether or not payment is required on registration
	 * 
	 * @return
	 */
	boolean isPrepaid() default true;
	
}
