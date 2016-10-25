package edu.uncfsu.softwaredesign.f16.r2.transactions;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.YearMonth;

public class CreditCard implements Serializable {

	private static final long serialVersionUID = -6087472615380073870L;
	
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

	@Override
	public String toString() {
		return "CreditCard [nameOnCard=" + nameOnCard + ", cardNumber=" + cardNumber + ", expirationDate="
				+ expirationDate + ", securityCode=" + securityCode + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardNumber == null) ? 0 : cardNumber.hashCode());
		result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
		result = prime * result + ((nameOnCard == null) ? 0 : nameOnCard.hashCode());
		result = prime * result + securityCode;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreditCard other = (CreditCard) obj;
		if (cardNumber == null) {
			if (other.cardNumber != null)
				return false;
		} else if (!cardNumber.equals(other.cardNumber))
			return false;
		if (expirationDate == null) {
			if (other.expirationDate != null)
				return false;
		} else if (!expirationDate.equals(other.expirationDate))
			return false;
		if (nameOnCard == null) {
			if (other.nameOnCard != null)
				return false;
		} else if (!nameOnCard.equals(other.nameOnCard))
			return false;
		if (securityCode != other.securityCode)
			return false;
		return true;
	}
	
}
