package edu.uncfsu.softwaredesign.f16.r2.util;

import java.time.LocalDate;
import java.util.List;

public class ReservationRegistryFullException extends ReservationException {

	private static final long serialVersionUID = -1337341867145142782L;

	private final List<LocalDate> dates;
	
	public ReservationRegistryFullException(List<LocalDate> dates) {
		super(Utils.createErrorMessageForFullRegistry(dates));
		this.dates = dates;
	}
	
	public List<LocalDate> getFullDays() {
		return dates;
	}
	
}
