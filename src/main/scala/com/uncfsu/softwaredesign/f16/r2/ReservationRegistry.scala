package com.uncfsu.softwaredesign.f16.r2

import java.io.File
import java.io.FileFilter
import java.io.FilenameFilter
import scala.xml.XML
import scala.xml.PrettyPrinter

object ReservationRegistry {
  
  private val location = "./reservations" 
  
  private val (reservations, ids) = {
    val file = new File(location)
    
    if (!file.exists) file.mkdirs
    
    val reservations = file.listFiles.filter { _.getName.endsWith("xml") } map { x => Reservation.fromXml(XML.loadFile(x)) } 
    
    (reservations.flatten, reservations.flatten.map { _.reservationId } )
  }
  
  def getReservations = reservations.map { x => x }
  
  def getNextId = {
    if (ids != null && !ids.isEmpty) ids.max + 1
    else 1
  }
  
  def registerReservation(reservation: Reservation): Boolean = {
    if (!(ids contains reservation.reservationId)) {
      ids :+ reservation.reservationId
      reservations :+ reservation; true 
    }
    else false
  }
  
  def saveReservationToFile(reserve: Reservation) = {
    val id = reserve.reservationId
    val p = new PrettyPrinter(80, 4)
    XML.save(s"$location/r-$id.xml", XML.loadString(p.format(reserve.toXml)))
  }
  
}