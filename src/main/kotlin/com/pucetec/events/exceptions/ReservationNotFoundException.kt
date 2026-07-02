package com.pucetec.events.exceptions

class ReservationNotFoundException(id: Long) : RuntimeException("Reservation with id $id not found")
