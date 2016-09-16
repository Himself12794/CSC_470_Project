package com.uncfsu.softwaredesign.f16.r2

object ReservationRegistry {
  
  private val (reservation, ids) = {
    (List[Reservation](), List[Long]()) 
  }
  
  def getNextId = {
    
  }
  
  def getAndClaimNextFreeId(reservation: Reservation) = {
    null
  }
  
  private def loadSavedReservations = {}
  
}