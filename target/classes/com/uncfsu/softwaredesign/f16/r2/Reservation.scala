package com.uncfsu.softwaredesign.f16.r2

import java.util.Date
import java.time.{LocalDate, ZoneId}
import scala.xml.Node

abstract class Reservation(protected var reservationDate: LocalDate, protected var guestCount: Int, 
                           val registeredDate: LocalDate, protected var personName: String, 
                           val baseCost: Float, protected var room: Int = -1, protected var days: Int = 1, 
                           val costMultiplier: Float) extends XmlConvertable {

  //val reservationId: Long
  
  protected var isCanceled = false
  
  // For if the reservation gets changed
  protected var modifiedDate: LocalDate = reservationDate
  
  private var isPaid = false
  
  /**
   * Pays with the initial fees
   * 
   */
  def pay = {
    if (!isPaid) {
      val totalDue = getTotalDue
      isPaid = true
      totalDue
    } else 0F
  }
  
  /**
   * Returns the cost for the length of the stay without multipliers
   * 
   */
  def getBaseCostDue(date: LocalDate = registeredDate) = (0 until days).foldLeft(0F) { (total, i) => BaseCostRegistry.lookup(date plusDays i) + total }
  
  /**
   * Returns the total amount due, with multipliers.
   * 
   */
  def getTotalDue = if (!isPaid) getBaseCostDue() * costMultiplier else 0.0F
 
  /**
   * Return a String identifying the type of reservation.
   * 
   */
  def getReservationType: String
  
  /**
   * Cancels a reservation. Optionally returns a fee. If overridden, the function must mark
   * the reservation as canceled.
   * 
   */
  def cancel: Option[Float] = {
    isCanceled = true;
    None
  }
  
  /**
   * Optionally returns the change fee, or None if no change possible
   * 
   */
  def getChangeFee(date: LocalDate): Option[Float]
  
  def changeAndGetFee(date: LocalDate) = {
    
    modifiedDate = date
    getChangeFee(date)
    
  }
  
  def toXml: Node = {
    null
    
  }
  
  override def toString = s"Reservation by $personName for $guestCount guest(s), made on $registeredDate for date " + 
                          s"$reservationDate, for $days day(s)"
  
}

class PrepaidReservation(reservationDate: LocalDate, guestCount: Int, registeredDate: LocalDate, 
                         personName: String, baseCost: Float, room: Int = -1, days: Int = 1) 
                         extends Reservation(reservationDate, guestCount, registeredDate, personName, baseCost, room, days, 0.75F) {
  
  assert(validDate, "Reservation date must be at least 90 days ahead of registration date")
  
  def getReservationType = "prepaid";
  
  def getChangeFee(date: LocalDate): Option[Float] = {
    if (date isAfter reservationDate) {
      val total = getBaseCostDue(date) * 1.1F - getBaseCostDue() * costMultiplier
      Some(if (total > 0) total else 0)
    } else None
  }
  
  private def validDate = (reservationDate compareTo (registeredDate plusDays 90)) >= 0    
  
  def doLogic = println(this.guestCount)
  
}