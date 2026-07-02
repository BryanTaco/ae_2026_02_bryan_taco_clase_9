package com.pucetec.events.mappers

import com.pucetec.events.dto.AttendeeRequest
import com.pucetec.events.dto.AttendeeResponse
import com.pucetec.events.entities.Attendee

fun AttendeeRequest.toEntity(): Attendee = Attendee(
    name = name,
    email = email,
)

fun Attendee.toResponse(): AttendeeResponse = AttendeeResponse(
    id = id,
    name = name,
    email = email,
)
