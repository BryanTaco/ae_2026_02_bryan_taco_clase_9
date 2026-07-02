package com.pucetec.events.services

import com.pucetec.events.dto.ReservationRequest
import com.pucetec.events.entities.Attendee
import com.pucetec.events.entities.Event
import com.pucetec.events.entities.Reservation
import com.pucetec.events.exceptions.AttendeeNotFoundException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.ReservationAlreadyCancelledException
import com.pucetec.events.exceptions.ReservationLimitExceededException
import com.pucetec.events.exceptions.ReservationNotFoundException
import com.pucetec.events.exceptions.SoldOutException
import com.pucetec.events.repositories.AttendeeRepository
import com.pucetec.events.repositories.EventRepository
import com.pucetec.events.repositories.ReservationRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ReservationServiceTest {

    @Mock
    private lateinit var reservationRepository: ReservationRepository

    @Mock
    private lateinit var attendeeRepository: AttendeeRepository

    @Mock
    private lateinit var eventRepository: EventRepository

    @InjectMocks
    private lateinit var reservationService: ReservationService

    private val attendee = Attendee(id = 1L, name = "Bryan Taco", email = "btaco@puce.edu.ec")
    private val event = Event(id = 1L, name = "Rock Fest", venue = "Arena", totalTickets = 100, availableTickets = 50)

    // ─── createReservation ────────────────────────────────────────────────────

    @Test
    fun `createReservation lanza AttendeeNotFoundException cuando el asistente no existe`() {
        val request = ReservationRequest(attendeeId = 99L, eventId = 1L)
        `when`(attendeeRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(AttendeeNotFoundException::class.java) {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation lanza EventNotFoundException cuando el evento no existe`() {
        val request = ReservationRequest(attendeeId = 1L, eventId = 99L)
        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        `when`(eventRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(EventNotFoundException::class.java) {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation lanza SoldOutException cuando el evento no tiene entradas disponibles`() {
        val soldOutEvent = Event(id = 2L, name = "Sold Out Concert", venue = "Club", totalTickets = 10, availableTickets = 0)
        val request = ReservationRequest(attendeeId = 1L, eventId = 2L)
        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        `when`(eventRepository.findById(2L)).thenReturn(Optional.of(soldOutEvent))

        assertThrows(SoldOutException::class.java) {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation lanza ReservationLimitExceededException cuando el asistente ya tiene 4 reservas activas`() {
        val request = ReservationRequest(attendeeId = 1L, eventId = 1L)
        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        `when`(eventRepository.findById(1L)).thenReturn(Optional.of(event))
        `when`(reservationRepository.countByAttendeeIdAndStatus(1L, "ACTIVE")).thenReturn(4L)

        assertThrows(ReservationLimitExceededException::class.java) {
            reservationService.createReservation(request)
        }
    }

    @Test
    fun `createReservation crea la reserva y decrementa availableTickets cuando los datos son validos`() {
        val request = ReservationRequest(attendeeId = 1L, eventId = 1L)
        val now = LocalDateTime.now()
        val savedReservation = Reservation(id = 100L, attendee = attendee, event = event, status = "ACTIVE", createdAt = now)

        `when`(attendeeRepository.findById(1L)).thenReturn(Optional.of(attendee))
        `when`(eventRepository.findById(1L)).thenReturn(Optional.of(event))
        `when`(reservationRepository.countByAttendeeIdAndStatus(1L, "ACTIVE")).thenReturn(2L)
        `when`(eventRepository.save(any())).thenReturn(event)
        `when`(reservationRepository.save(any())).thenReturn(savedReservation)

        val result = reservationService.createReservation(request)

        assertEquals(100L, result.id)
        assertEquals("ACTIVE", result.status)
        assertEquals(1L, result.attendee.id)
        assertEquals(1L, result.event.id)
        assertEquals(49, event.availableTickets)
        verify(eventRepository).save(event)
    }

    // ─── cancelReservation ────────────────────────────────────────────────────

    @Test
    fun `cancelReservation lanza ReservationNotFoundException cuando la reserva no existe`() {
        `when`(reservationRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(ReservationNotFoundException::class.java) {
            reservationService.cancelReservation(99L)
        }
    }

    @Test
    fun `cancelReservation lanza ReservationAlreadyCancelledException cuando la reserva ya esta cancelada`() {
        val cancelledReservation = Reservation(id = 1L, attendee = attendee, event = event, status = "CANCELLED", createdAt = LocalDateTime.now())
        `when`(reservationRepository.findById(1L)).thenReturn(Optional.of(cancelledReservation))

        assertThrows(ReservationAlreadyCancelledException::class.java) {
            reservationService.cancelReservation(1L)
        }
    }

    @Test
    fun `cancelReservation cancela la reserva e incrementa availableTickets cuando los datos son validos`() {
        val activeEvent = Event(id = 1L, name = "Rock Fest", venue = "Arena", totalTickets = 100, availableTickets = 49)
        val activeReservation = Reservation(id = 1L, attendee = attendee, event = activeEvent, status = "ACTIVE", createdAt = LocalDateTime.now())
        val cancelledReservation = Reservation(id = 1L, attendee = attendee, event = activeEvent, status = "CANCELLED", createdAt = activeReservation.createdAt)

        `when`(reservationRepository.findById(1L)).thenReturn(Optional.of(activeReservation))
        `when`(eventRepository.save(any())).thenReturn(activeEvent)
        `when`(reservationRepository.save(any())).thenReturn(cancelledReservation)

        val result = reservationService.cancelReservation(1L)

        assertEquals("CANCELLED", result.status)
        assertEquals(50, activeEvent.availableTickets)
        verify(eventRepository).save(activeEvent)
    }

    // ─── getAllReservations ───────────────────────────────────────────────────

    @Test
    fun `getAllReservations retorna lista de ReservationResponse`() {
        val now = LocalDateTime.now()
        val reservations = listOf(
            Reservation(id = 1L, attendee = attendee, event = event, status = "ACTIVE", createdAt = now),
            Reservation(id = 2L, attendee = attendee, event = event, status = "CANCELLED", createdAt = now),
        )
        `when`(reservationRepository.findAll()).thenReturn(reservations)

        val result = reservationService.getAllReservations()

        assertEquals(2, result.size)
        assertEquals("ACTIVE", result[0].status)
        assertEquals("CANCELLED", result[1].status)
    }
}
