package com.uncfsu.softwaredesign.f16.r2

import java.util.Date
import java.time.{LocalDate, ZoneId}

abstract class Reservation(private var reservationDate: LocalDate, private var guestCount: Int, 
                           private val registeredDate: LocalDate, private var personName: String, 
                           private val baseCost: Float, private var room: Int = -1, private var days: Int = 1) {
  
  private var isCanceled = false
  
  private var isPaid = false
  
  def pay = {
    if (!isPaid) {
      isPaid = true
      getCost
    } else 0F
  }
  
  def getCost = (0 until days).foldLeft(0F)((total, i) => BaseCostRegistry.lookup(reservationDate plusDays i) + total)
 
  
  /**
   * Return a String identifying the type of reservation.
   * 
   */
  def getReservationType: String
  
  /**
   * Cancels a reservation. This cannot be undone.
   * 
   */
  def cancel: Unit = isCanceled = true;
  
  /**
   * Whether or not this type allows refunds
   * 
   */
  def canRefund: Boolean
  
  /**
   * Returns the fee for changing the reservation
   * 
   */
  def getChangeFee(date: LocalDate): Float
  
  def changeAndGetFee(date: LocalDate) = {
    
    reservationDate = date
    getChangeFee(date)
    
  }
  
  override def toString = s"Reservation by $personName for $guestCount guest(s), made on $registeredDate for date " + 
                          s"$reservationDate, for $days day(s)"
  
}

class PrepaidReservation(private var reservationDate: LocalDate, 
                         private var guestCount: Int, 
                         private val registeredDate: LocalDate, 
                         private var personName: String,
                         private val baseCost: Float,
                         private var room: Int = -1,
                         private var days: Int = 1) 
                         extends Reservation(reservationDate, guestCount, registeredDate, personName, baseCost, room, days) {
  
  assert(validDate, "Reservation date must be at least 90 days ahead of registration date")
  
  def getReservationType = "prepaid";
  
  def getChangeFee(date: LocalDate): Float = {
    return 0.0F;
  }
  
  def canRefund = false;

  private def validDate = (reservationDate compareTo (registeredDate plusDays 90)) >= 0    
  
  def doLogic = println(this.guestCount)
  
}