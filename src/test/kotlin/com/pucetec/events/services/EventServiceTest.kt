package com.pucetec.events.services

import com.pucetec.events.dto.EventRequest
import com.pucetec.events.entities.Event
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.exceptions.EventNotFoundException
import com.pucetec.events.exceptions.InvalidCapacityException
import com.pucetec.events.repositories.EventRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class EventServiceTest {

    @Mock
    private lateinit var eventRepository: EventRepository

    @InjectMocks
    private lateinit var eventService: EventService

    // ─── createEvent ──────────────────────────────────────────────────────────

    @Test
    fun `createEvent lanza BlankFieldException cuando el nombre esta en blanco`() {
        val request = EventRequest(name = "  ", venue = "Arena", totalTickets = 100)

        assertThrows(BlankFieldException::class.java) {
            eventService.createEvent(request)
        }
    }

    @Test
    fun `createEvent lanza BlankFieldException cuando el venue esta en blanco y el nombre no`() {
        val request = EventRequest(name = "Rock Fest", venue = "", totalTickets = 100)

        assertThrows(BlankFieldException::class.java) {
            eventService.createEvent(request)
        }
    }

    @Test
    fun `createEvent lanza InvalidCapacityException cuando totalTickets es menor que 1`() {
        val request = EventRequest(name = "Rock Fest", venue = "Arena", totalTickets = 0)

        assertThrows(InvalidCapacityException::class.java) {
            eventService.createEvent(request)
        }
    }

    @Test
    fun `createEvent retorna EventResponse con availableTickets igual a totalTickets cuando los datos son validos`() {
        val request = EventRequest(name = "Rock Fest", venue = "Arena", totalTickets = 200)
        val saved = Event(id = 1L, name = "Rock Fest", venue = "Arena", totalTickets = 200, availableTickets = 200)
        `when`(eventRepository.save(any())).thenReturn(saved)

        val result = eventService.createEvent(request)

        assertEquals(1L, result.id)
        assertEquals("Rock Fest", result.name)
        assertEquals("Arena", result.venue)
        assertEquals(200, result.totalTickets)
        assertEquals(200, result.availableTickets)
    }

    // ─── getAllEvents ─────────────────────────────────────────────────────────

    @Test
    fun `getAllEvents retorna lista de EventResponse`() {
        val events = listOf(
            Event(id = 1L, name = "Rock Fest", venue = "Arena", totalTickets = 100, availableTickets = 80),
            Event(id = 2L, name = "Jazz Night", venue = "Club", totalTickets = 50, availableTickets = 50),
        )
        `when`(eventRepository.findAll()).thenReturn(events)

        val result = eventService.getAllEvents()

        assertEquals(2, result.size)
        assertEquals("Rock Fest", result[0].name)
        assertEquals("Jazz Night", result[1].name)
    }

    // ─── getEventById ─────────────────────────────────────────────────────────

    @Test
    fun `getEventById retorna EventResponse cuando el evento existe`() {
        val event = Event(id = 5L, name = "Concert", venue = "Stadium", totalTickets = 500, availableTickets = 300)
        `when`(eventRepository.findById(5L)).thenReturn(Optional.of(event))

        val result = eventService.getEventById(5L)

        assertEquals(5L, result.id)
        assertEquals("Concert", result.name)
    }

    @Test
    fun `getEventById lanza EventNotFoundException cuando el evento no existe`() {
        `when`(eventRepository.findById(99L)).thenReturn(Optional.empty())

        assertThrows(EventNotFoundException::class.java) {
            eventService.getEventById(99L)
        }
    }
}
