package com.pucetec.events.mappers

import com.pucetec.events.dto.ReservationResponse
import com.pucetec.events.entities.Reservation

fun Reservation.toResponse(): ReservationResponse = ReservationResponse(
    id = id,
    status = status,
    createdAt = createdAt,
    attendee = attendee!!.toResponse(),
    event = event!!.toResponse(),
)
