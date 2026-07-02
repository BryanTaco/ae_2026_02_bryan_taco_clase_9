package com.pucetec.events.controllers

import com.pucetec.events.dto.AttendeeRequest
import com.pucetec.events.dto.AttendeeResponse
import com.pucetec.events.services.AttendeeService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/attendees")
class AttendeeController(
    private val attendeeService: AttendeeService,
) {
    private val logger = LoggerFactory.getLogger(AttendeeController::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAttendee(@RequestBody request: AttendeeRequest): AttendeeResponse {
        logger.info("POST /api/attendees - name=${request.name}")
        return attendeeService.createAttendee(request)
    }

    @GetMapping
    fun getAllAttendees(): List<AttendeeResponse> {
        logger.info("GET /api/attendees")
        return attendeeService.getAllAttendees()
    }
}
