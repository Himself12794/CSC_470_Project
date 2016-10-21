package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.time.LocalDate;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;
import edu.uncfsu.softwaredesign.f16.r2.transactions.CreditCard;

/**
 * Object representing a reservation. To ensure consistent ids, only the CostRegistry will be allowed to create them.
 * 
 * @author phwhitin
 *
 */
public abstract class Reservation {
	
	//@Id
	//private ObjectId id;
	
	long reservationId;
	private final boolean canChange;
	private final float changeFeeModifier;
	private final float costModifier;
	private String customer;
	private String email;
	private LocalDate registrationDate;
	private LocalDate reservationDate;
	private int days;
	private boolean hasPaid = false;
	private float totalCost;
	private CreditCard creditCard;
	
	Reservation(long reservationId, String customer, String email, LocalDate registrationDate, LocalDate reservationDate, int days,
			boolean hasPaid, CostRegistry costs, boolean canChange, float changeFeeModifier, float costModifier, CreditCard card) {
		this.reservationId = reservationId;
		this.customer = customer;
		this.email = email;
		this.registrationDate = registrationDate;
		this.reservationDate = reservationDate;
		this.days = days;
		this.canChange = canChange;
		this.changeFeeModifier = changeFeeModifier;
		this.costModifier = costModifier;
		
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
	
}
