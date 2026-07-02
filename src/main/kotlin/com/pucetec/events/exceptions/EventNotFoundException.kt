package com.pucetec.events.exceptions

class EventNotFoundException(id: Long) : RuntimeException("Event with id $id not found")
