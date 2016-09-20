package edu.uncfsu.softwaredesign.f16.r2.reservation;

import java.time.LocalDate;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import edu.uncfsu.softwaredesign.f16.r2.cost.CostRegistry;

public abstract class Reservation {

	@Id
	private ObjectId id;
	
	protected long reservationId;
	protected float costModifier;
	protected String customer;
	protected LocalDate registrationDate;
	protected LocalDate reservationDate;
	protected int days;
	protected boolean hasPaid = false;
	protected float totalCost;
	
	public Reservation(float costModifier, String customer, LocalDate registrationDate, LocalDate reservationDate, int days,
			boolean hasPaid, CostRegistry costs) {
		this.costModifier = costModifier;
		this.customer = customer;
		this.registrationDate = registrationDate;
		this.reservationDate = reservationDate;
		this.days = days;
		
		calculateCost(costs);
	}
	
	public float calculateCost(CostRegistry costs) {
		
		float total = 0.0F; 
		for (LocalDate start = getReservationDate(); start.isBefore(start.plusDays(getDays())); start = start.plusDays(1)) {
			total += costs.getCostForDay(start);
		}
		
		setTotalCost(total);
		
		return total;
	}
	
	public long getReservationId() {
		return reservationId;
	}

	public void setReservationId(long reservationId) {
		this.reservationId = reservationId;
	}

	public float getCostModifier() {
		return costModifier;
	}

	public void setCostModifier(float costModifier) {
		this.costModifier = costModifier;
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

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public LocalDate getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(LocalDate reservationDate) {
		this.reservationDate = reservationDate;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
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

	public void setTotalCost(float totalCost) {
		this.totalCost = totalCost;
	}
	
}
