package com.pucetec.events.exceptions

class AttendeeNotFoundException(id: Long) : RuntimeException("Attendee with id $id not found")
