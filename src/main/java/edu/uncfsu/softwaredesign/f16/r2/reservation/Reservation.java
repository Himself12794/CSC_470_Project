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
	
	protected final long reservationId;
	protected final float costModifier;
	protected final boolean canChange;
	protected String customer;
	protected LocalDate registrationDate;
	protected LocalDate reservationDate;
	protected int days;
	protected boolean hasPaid = false;
	protected float totalCost;
	protected CreditCard creditCard;
	
	Reservation(long reservationId, float costModifier, String customer, LocalDate registrationDate, LocalDate reservationDate, int days,
			boolean hasPaid, CostRegistry costs, boolean canChange) {
		this.costModifier = costModifier;
		this.reservationId = reservationId;
		this.customer = customer;
		this.registrationDate = registrationDate;
		this.reservationDate = reservationDate;
		this.days = days;
		this.canChange = canChange;
		
		calculateCost(costs);
	}
	
	public void markPaid() {
		
		if (creditCard == null) {
			throw new IllegalStateException("No credit card is on record for this reservation.");
		} else {
			hasPaid = true;
		}
		
	}
	
	protected float calculateCost(CostRegistry costs) {
		
		float total = 0.0F; 
		for (LocalDate start = getReservationDate(); start.isBefore(start.plusDays(getDays())); start = start.plusDays(1)) {
			total += costs.getCostForDay(start);
		}
		
		setTotalCost(total);
		
		return total;
	}
	
	public abstract float getChangeFeeModifier();
	
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
		
		newCost *= getChangeFeeModifier();

		return newCost > prevCost ? newCost - prevCost : 0.0F;
	}
	
	public long getReservationId() {
		return reservationId;
	}

	public float getCostModifier() {
		return costModifier;
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
	
}
