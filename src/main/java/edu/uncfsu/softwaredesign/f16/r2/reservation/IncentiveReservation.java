package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.time.LocalDate;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;

class IncentiveReservation extends Reservation {

	IncentiveReservation(long reservationId, float costModifier, String customer, LocalDate registrationDate,
			LocalDate reservationDate, int days, boolean hasPaid, CostRegistry costs, boolean canChange) {
		
		super(reservationId, costModifier, customer, registrationDate, reservationDate, days, hasPaid, costs, canChange);
	}

	@Override
	public float getChangeFeeModifier() {
		return 1.0F;
	}

}
