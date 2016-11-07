package edu.uncfsu.softwaredesign.f16.r2.transactions;

import org.springframework.stereotype.Service;

import edu.uncfsu.softwaredesign.f16.r2.reservation.Reservation;

@Service
public class TransactionController {

	public boolean doPayment(Reservation reservation) {
		reservation.markPaid();
		return true;
	}
	
	public boolean charge(CreditCard card, float amount) {
		return true;
	}
	
}
