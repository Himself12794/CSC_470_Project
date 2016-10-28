package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.time.LocalDate;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;
import edu.uncfsu.softwaredesign.f16.r2.util.Utils;

/**
 * Object representing a reservation. To ensure consistent ids, only the
 * ReservationRegistry will be allowed to create them. 
 * 
 * @author phwhitin
 *
 */
public final class Reservation {

	private String customer;
	private String email;
	private LocalDate registrationDate;
	private LocalDate reservationDate;
	private int days;
	private CreditCard creditCard;
	
	ReservationType theType;
	long reservationId;
	boolean hasPaid = false;
	float totalCost;

	Reservation(long reservationId, String customer, String email, LocalDate registrationDate,
			LocalDate reservationDate, int days, CostRegistry costs, ReservationRegistry registry,
			float changeFeeModifier, CreditCard card, ReservationType type) {
		this.reservationId = reservationId;
		this.customer = customer;
		this.email = email;
		this.registrationDate = registrationDate;
		this.reservationDate = reservationDate;
		this.days = days;
		this.creditCard = card;
		this.theType = type;

		calculateCost(costs, registry);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public float getChangeFeeModifier() {
		return theType.changeFee;
	}

	public void markPaid() {

		if (creditCard == null) {
			throw new IllegalStateException("No credit card is on record for this reservation.");
		} else {
			hasPaid = true;
		}

	}

	public float calculateCost(CostRegistry costs, ReservationRegistry registry)  {
		
		float[] total = {0.0F}; 
		Utils.dateStream(this).forEach(d -> total[0] += costs.getCostForDay(d) * getCostModifier());
		if (getReservationDate().isBefore(getRegistrationDate().plusDays(30))) {
			if (registry.averageOccupancyForRange(getRegistrationDate(), getDays()) / registry.getMaxRooms() <= 0.6F) {
				total[0] *= 0.8F;
			}
		}

		setTotalCost(total[0]);
		
		return total[0];
	}

	/**
	 * Gets the updated cost and returns the change in amount owed.
	 * 
	 * @param date
	 * @param cost
	 * @return
	 */
	float updateCost(LocalDate date, int days, ReservationRegistry registry, CostRegistry cost) {

		float prevCost = totalCost, newCost;

		setReservationDate(date);
		setDays(days);
		newCost = calculateCost(cost, registry);

		newCost *= getChangeFeeModifier();

		return newCost > prevCost ? newCost - prevCost : 0.0F;
	}

	public long getReservationId() {
		return reservationId;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public LocalDate getReservationDate() {
		return reservationDate;
	}

	void setReservationDate(LocalDate reservationDate) {
		this.reservationDate = reservationDate;
	}

	public int getDays() {
		return days;
	}

	void setDays(int days) {
		this.days = days;
	}

	public boolean isHasPaid() {
		return hasPaid;
	}

	public void setHasPaid(boolean hasPaid) {
		this.hasPaid = hasPaid;
	}

	public float getTotalCost() {
		return totalCost;
	}

	void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}

	public float getCostModifier() {
		return theType.costModifier;
	}

	/**
	 * The only reason it is created this way is to have metadata about reservations without needing to 
	 * create a new instance.
	 * 
	 * @return
	 */
	public ReservationType getType() {
		return theType;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(getChangeFeeModifier());
		result = prime * result + Float.floatToIntBits(getCostModifier());
		result = prime * result + ((creditCard == null) ? 0 : creditCard.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + days;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (hasPaid ? 1231 : 1237);
		result = prime * result + ((registrationDate == null) ? 0 : registrationDate.hashCode());
		result = prime * result + ((reservationDate == null) ? 0 : reservationDate.hashCode());
		result = prime * result + (int) (reservationId ^ (reservationId >>> 32));
		result = prime * result + ((theType == null) ? 0 : theType.hashCode());
		result = prime * result + Float.floatToIntBits(totalCost);
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
		Reservation other = (Reservation) obj;
		if (Float.floatToIntBits(getChangeFeeModifier()) != Float.floatToIntBits(other.getChangeFeeModifier()))
			return false;
		if (Float.floatToIntBits(getCostModifier()) != Float.floatToIntBits(other.getCostModifier()))
			return false;
		if (creditCard == null) {
			if (other.creditCard != null)
				return false;
		} else if (!creditCard.equals(other.creditCard))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (days != other.days)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (hasPaid != other.hasPaid)
			return false;
		if (registrationDate == null) {
			if (other.registrationDate != null)
				return false;
		} else if (!registrationDate.equals(other.registrationDate))
			return false;
		if (reservationDate == null) {
			if (other.reservationDate != null)
				return false;
		} else if (!reservationDate.equals(other.reservationDate))
			return false;
		if (reservationId != other.reservationId)
			return false;
		if (theType != other.theType)
			return false;
		if (Float.floatToIntBits(totalCost) != Float.floatToIntBits(other.totalCost))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Reservation [changeFeeModifier=" + getChangeFeeModifier() + ", costModifier="
				+ getCostModifier() + ", customer=" + customer + ", email=" + email + ", registrationDate="
				+ registrationDate + ", reservationDate=" + reservationDate + ", days=" + days + ", creditCard="
				+ creditCard + ", reservationId=" + reservationId + ", hasPaid=" + hasPaid + ", totalCost=" + totalCost
				+ "]";
	}

}
