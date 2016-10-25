package edu.uncfsu.softwaredesign.f16.r2.util;

public abstract class ReservationException extends Exception {

	private static final long serialVersionUID = 2892880155948776775L;

	public ReservationException() {
		super();
	}
	
	public ReservationException(String msg) {
		super(msg);
	}
	
	public ReservationException(Throwable throwable) {
		super(throwable);
	}
	
	public ReservationException(String msg, Throwable throwable) {
		super(msg, throwable);
	}
	
}
