package com.uncfsu.softwaredesign.f16.r2

import java.util.Date
import java.time.LocalDate

object Main extends App {
  
  val reserver = new PrepaidReservation(LocalDate.of(2016, 12, 25), 1, LocalDate.now, "Bob", 455.00F, days = 3)
  
  
  println(reserver.getCost)
  
}