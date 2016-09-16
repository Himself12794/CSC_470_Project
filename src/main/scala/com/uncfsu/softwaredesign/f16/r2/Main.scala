package com.uncfsu.softwaredesign.f16.r2

import java.util.Date
import java.time.LocalDate

object Main extends App {
  
  val reserver: Reservation = new PrepaidReservation(LocalDate.of(2016, 12, 25), 2, LocalDate.now, "Bob", days = 3, id = 1L)
  
  println(LocalDate.ofEpochDay(LocalDate.of(2016, 12, 25).toEpochDay))
  
  ReservationRegistry.getReservations foreach {println}
  
  ReservationRegistry.saveReservationToFile(reserver)
  
  println(BaseCostRegistry.toXml)
  
  println(reserver.getBaseCostDue())
  
}