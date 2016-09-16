package com.uncfsu.softwaredesign.f16.r2

import java.time.LocalDate
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import scala.xml.Node
import scala.xml.Utility
import scala.xml.PrettyPrinter
import scala.xml.XML

/**
 * Holds the costs for each day
 */
object BaseCostRegistry extends XmlConvertable {
  
  val defaultCost = 500.0F
  
  val location = "./cost_registry.xml"
  
  private val costs: Map[LocalDate, Float] = {
     val temp = HashMap[LocalDate,Float]()
     temp.put(LocalDate.of(2016, 12, 25), 750.0F)
     temp.put(LocalDate.now, 350.0F)
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
  
  def toXml = {
    val xml = 
    <costEntries default={s"$defaultCost"}>
      {costs map { x => <costEntry><date>{x._1.toEpochDay}</date><cost>{x._2}</cost></costEntry>}}
    </costEntries>
      
    XML.loadString(new PrettyPrinter(80, 4).format(xml))
  }
  
  implicit def canModifyDate(date: LocalDate): Boolean = (date minusYears 1 compareTo LocalDate.now) >= 0

}