package com.pucetec.events.services

import com.pucetec.events.dto.EventRequest
import com.pucetec.events.dto.EventResponse
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.InvalidCapacityException
import com.pucetec.events.mappers.toEntity
import com.pucetec.events.mappers.toResponse
import com.pucetec.events.repositories.EventRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EventService(
    private val eventRepository: EventRepository,
) {
    private val logger = LoggerFactory.getLogger(EventService::class.java)

    fun createEvent(request: EventRequest): EventResponse {
        if (request.name.isBlank() || request.venue.isBlank()) {
            throw BlankFieldException("name and venue must not be blank")
        }
        if (request.totalTickets < 1) {
            throw InvalidCapacityException("totalTickets must be at least 1")
        }
        logger.info("Creating event: ${request.name} at ${request.venue}")
        return eventRepository.save(request.toEntity()).toResponse()
    }

    fun getAllEvents(): List<EventResponse> {
        logger.info("Getting all events")
        return eventRepository.findAll().map { it.toResponse() }
    }

    fun getEventById(id: Long): EventResponse {
        logger.info("Getting event by id: $id")
        return eventRepository.findById(id)
            .orElseThrow { EventNotFoundException(id) }
            .toResponse()
    }
}
