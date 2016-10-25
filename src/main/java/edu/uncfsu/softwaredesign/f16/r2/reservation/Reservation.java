package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.time.LocalDate;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;

/**
 * Object representing a reservation. To ensure consistent ids, only the
 * CostRegistry will be allowed to create them.
 * 
 * @author phwhitin
 *
 */
public abstract class Reservation {

	// @Id
	// private ObjectId id;

	private final boolean canChange;
	private final float changeFeeModifier;
	private final float costModifier;

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
			LocalDate reservationDate, int days, boolean hasPaid, CostRegistry costs, boolean canChange,
			float changeFeeModifier, float costModifier, CreditCard card) {
		this.reservationId = reservationId;
		this.customer = customer;
		this.email = email;
		this.registrationDate = registrationDate;
		this.reservationDate = reservationDate;
		this.days = days;
		this.canChange = canChange;
		this.changeFeeModifier = changeFeeModifier;
		this.costModifier = costModifier;
		this.creditCard = card;

		calculateCost(costs);
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
		return changeFeeModifier;
	}

	public void markPaid() {

		if (creditCard == null) {
			throw new IllegalStateException("No credit card is on record for this reservation.");
		} else {
			hasPaid = true;
		}

	}

	abstract float calculateCost(CostRegistry costs);

	/**
	 * Gets the updated cost and returns the change in amount owed.
	 * 
	 * @param date
	 * @param cost
	 * @return
	 */
	public float updateCost(LocalDate date, CostRegistry cost) {

		float prevCost = totalCost, newCost;

		setReservationDate(date);
		newCost = calculateCost(cost);

		newCost *= changeFeeModifier;

		return newCost > prevCost ? newCost - prevCost : 0.0F;
	}

	public long getReservationId() {
		return reservationId;
	}

	public String getCustomer() {
		return customer;
	}

	void setCustomer(String customer) {
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

	void setHasPaid(boolean hasPaid) {
		this.hasPaid = hasPaid;
	}

	public float getTotalCost() {
		return totalCost;
	}

	void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}

	public boolean getCanChange() {
		return canChange;
	}

	public float getCostModifier() {
		return costModifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (canChange ? 1231 : 1237);
		result = prime * result + Float.floatToIntBits(changeFeeModifier);
		result = prime * result + Float.floatToIntBits(costModifier);
		result = prime * result + ((creditCard == null) ? 0 : creditCard.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + days;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (hasPaid ? 1231 : 1237);
		result = prime * result + ((registrationDate == null) ? 0 : registrationDate.hashCode());
		result = prime * result + ((reservationDate == null) ? 0 : reservationDate.hashCode());
		result = prime * result + (int) (reservationId ^ (reservationId >>> 32));
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
		if (canChange != other.canChange)
			return false;
		if (Float.floatToIntBits(changeFeeModifier) != Float.floatToIntBits(other.changeFeeModifier))
			return false;
		if (Float.floatToIntBits(costModifier) != Float.floatToIntBits(other.costModifier))
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
		if (Float.floatToIntBits(totalCost) != Float.floatToIntBits(other.totalCost))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Reservation [canChange=" + canChange + ", changeFeeModifier=" + changeFeeModifier + ", costModifier="
				+ costModifier + ", customer=" + customer + ", email=" + email + ", registrationDate="
				+ registrationDate + ", reservationDate=" + reservationDate + ", days=" + days + ", creditCard="
				+ creditCard + ", reservationId=" + reservationId + ", hasPaid=" + hasPaid + ", totalCost=" + totalCost
				+ "]";
	}

}
