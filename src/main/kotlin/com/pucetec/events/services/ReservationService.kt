package com.pucetec.events.services

import com.pucetec.events.dto.ReservationRequest
import com.pucetec.events.dto.ReservationResponse
import com.pucetec.events.entities.Reservation
import com.pucetec.events.exceptions.AttendeeNotFoundException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.ReservationAlreadyCancelledException
import com.pucetec.events.exceptions.ReservationLimitExceededException
import com.pucetec.events.exceptions.ReservationNotFoundException
import com.pucetec.events.exceptions.SoldOutException
import com.pucetec.events.mappers.toResponse
import com.pucetec.events.repositories.AttendeeRepository
import com.pucetec.events.repositories.EventRepository
import com.pucetec.events.repositories.ReservationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val attendeeRepository: AttendeeRepository,
    private val eventRepository: EventRepository,
) {
    private val logger = LoggerFactory.getLogger(ReservationService::class.java)

    fun createReservation(request: ReservationRequest): ReservationResponse {
        val attendee = attendeeRepository.findById(request.attendeeId)
            .orElseThrow { AttendeeNotFoundException(request.attendeeId) }

        val event = eventRepository.findById(request.eventId)
            .orElseThrow { EventNotFoundException(request.eventId) }

        if (event.availableTickets <= 0) {
            throw SoldOutException("Event '${event.name}' is sold out")
        }

        val activeCount = reservationRepository.countByAttendeeIdAndStatus(attendee.id, "ACTIVE")
        if (activeCount >= 4) {
            throw ReservationLimitExceededException("Attendee has reached the limit of 4 active reservations")
        }

        event.availableTickets--
        eventRepository.save(event)

        logger.info("Creating reservation: attendee=${attendee.id} event=${event.id}")
        val reservation = Reservation(
            attendee = attendee,
            event = event,
            status = "ACTIVE",
            createdAt = LocalDateTime.now(),
        )
        return reservationRepository.save(reservation).toResponse()
    }

    fun cancelReservation(id: Long): ReservationResponse {
        val reservation = reservationRepository.findById(id)
            .orElseThrow { ReservationNotFoundException(id) }

        if (reservation.status == "CANCELLED") {
            throw ReservationAlreadyCancelledException("Reservation $id is already cancelled")
        }

        reservation.status = "CANCELLED"
        val event = reservation.event!!
        event.availableTickets++
        eventRepository.save(event)

        logger.info("Cancelling reservation: $id")
        return reservationRepository.save(reservation).toResponse()
    }

    fun getAllReservations(): List<ReservationResponse> {
        logger.info("Getting all reservations")
        return reservationRepository.findAll().map { it.toResponse() }
    }
}
