package com.uncfsu.softwaredesign.f16.r2

import java.time.LocalDate
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap

/**
 * Holds the costs for each day
 */
object BaseCostRegistry {
  
  val defaultCost = 500.0F
  
  private val costs: Map[LocalDate, Float] = {
     val temp = HashMap[LocalDate,Float]()
     temp.put(LocalDate.of(2016, 12, 25), 750.0F)
     temp
  }
  
  def lookup(date: LocalDate) = costs.getOrElse(date, defaultCost)
    
  def setCostForDay(cost: Float, date: LocalDate) = {
    
    if (!date) {
      println("The date is less than a year away and cannot be changed")
    } else {
      costs.put(date, cost)
    }
    
  }
  
  implicit def canModifyDate(date: LocalDate): Boolean = (date minusYears 1 compareTo LocalDate.now) >= 0

}