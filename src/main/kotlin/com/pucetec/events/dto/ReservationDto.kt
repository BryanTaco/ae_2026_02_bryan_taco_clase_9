package com.pucetec.events.dto

import java.time.LocalDateTime

data class ReservationRequest(
    val attendeeId: Long,
    val eventId: Long,
)

data class ReservationResponse(
    val id: Long,
    val status: String,
    val createdAt: LocalDateTime,
    val attendee: AttendeeResponse,
    val event: EventResponse,
)
