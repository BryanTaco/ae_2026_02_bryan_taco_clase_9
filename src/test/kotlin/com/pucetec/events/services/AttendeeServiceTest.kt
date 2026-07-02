package com.pucetec.events.services

import com.pucetec.events.dto.AttendeeRequest
import com.pucetec.events.entities.Attendee
import com.pucetec.events.exceptions.BlankFieldException
import com.pucetec.events.repositories.AttendeeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class AttendeeServiceTest {

    @Mock
    private lateinit var attendeeRepository: AttendeeRepository

    @InjectMocks
    private lateinit var attendeeService: AttendeeService

    // ─── createAttendee ───────────────────────────────────────────────────────

    @Test
    fun `createAttendee lanza BlankFieldException cuando el nombre esta en blanco`() {
        val request = AttendeeRequest(name = "  ", email = "test@puce.edu.ec")

        assertThrows(BlankFieldException::class.java) {
            attendeeService.createAttendee(request)
        }
    }

    @Test
    fun `createAttendee lanza BlankFieldException cuando el email esta en blanco y el nombre no`() {
        val request = AttendeeRequest(name = "Bryan Taco", email = "")

        assertThrows(BlankFieldException::class.java) {
            attendeeService.createAttendee(request)
        }
    }

    @Test
    fun `createAttendee retorna AttendeeResponse cuando los datos son validos`() {
        val request = AttendeeRequest(name = "Bryan Taco", email = "btaco@puce.edu.ec")
        val saved = Attendee(id = 1L, name = "Bryan Taco", email = "btaco@puce.edu.ec")
        `when`(attendeeRepository.save(any())).thenReturn(saved)

        val result = attendeeService.createAttendee(request)

        assertEquals(1L, result.id)
        assertEquals("Bryan Taco", result.name)
        assertEquals("btaco@puce.edu.ec", result.email)
    }

    // ─── getAllAttendees ──────────────────────────────────────────────────────

    @Test
    fun `getAllAttendees retorna lista de AttendeeResponse`() {
        val attendees = listOf(
            Attendee(id = 1L, name = "Alice", email = "alice@test.com"),
            Attendee(id = 2L, name = "Bob", email = "bob@test.com"),
        )
        `when`(attendeeRepository.findAll()).thenReturn(attendees)

        val result = attendeeService.getAllAttendees()

        assertEquals(2, result.size)
        assertEquals("Alice", result[0].name)
        assertEquals("Bob", result[1].name)
    }
}
