package com.pucetec.events.services

import com.pucetec.events.dto.AttendeeRequest
import com.pucetec.events.dto.AttendeeResponse
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.mappers.toEntity
import com.pucetec.events.mappers.toResponse
import com.pucetec.events.repositories.AttendeeRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AttendeeService(
    private val attendeeRepository: AttendeeRepository,
) {
    private val logger = LoggerFactory.getLogger(AttendeeService::class.java)

    fun createAttendee(request: AttendeeRequest): AttendeeResponse {
        if (request.name.isBlank() || request.email.isBlank()) {
            throw BlankFieldException("name and email must not be blank")
        }
        logger.info("Creating attendee: ${request.name}")
        return attendeeRepository.save(request.toEntity()).toResponse()
    }

    fun getAllAttendees(): List<AttendeeResponse> {
        logger.info("Getting all attendees")
        return attendeeRepository.findAll().map { it.toResponse() }
    }
}
