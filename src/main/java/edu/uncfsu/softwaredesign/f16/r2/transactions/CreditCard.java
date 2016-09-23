package edu.uncfsu.softwaredesign.f16.r2.transactions;

import java.math.BigInteger;
import java.time.YearMonth;

public class CreditCard {

	private final String    nameOnCard;
	private final String    cardNumber;
	private final YearMonth expirationDate;
	private final short     securityCode;

	public CreditCard(String nameOnCard, String cardNumber, YearMonth expirationDate, short securityCode) {
		
		// Making sure the number is valid
		new BigInteger(cardNumber);
		
		this.nameOnCard = nameOnCard;
		this.cardNumber = cardNumber;
		this.expirationDate = expirationDate;
		this.securityCode = securityCode;
	}
	
	public String getNameOnCard() {
		return nameOnCard;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public YearMonth getExpirationDate() {
		return expirationDate;
	}

	public short getSecurityCode() {
		return securityCode;
	}
	
}
