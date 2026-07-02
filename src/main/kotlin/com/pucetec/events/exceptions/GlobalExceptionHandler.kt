package com.pucetec.events.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ExceptionResponse(val message: String, val source: String)

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BlankFieldException::class)
    fun handleBlankField(ex: BlankFieldException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(ex.message ?: "Blank field", "BlankFieldException"))

    @ExceptionHandler(InvalidCapacityException::class)
    fun handleInvalidCapacity(ex: InvalidCapacityException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(ex.message ?: "Invalid capacity", "InvalidCapacityException"))

    @ExceptionHandler(AttendeeNotFoundException::class)
    fun handleAttendeeNotFound(ex: AttendeeNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(ex.message ?: "Attendee not found", "AttendeeNotFoundException"))

    @ExceptionHandler(EventNotFoundException::class)
    fun handleEventNotFound(ex: EventNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(ex.message ?: "Event not found", "EventNotFoundException"))

    @ExceptionHandler(ReservationNotFoundException::class)
    fun handleReservationNotFound(ex: ReservationNotFoundException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ExceptionResponse(ex.message ?: "Reservation not found", "ReservationNotFoundException"))

    @ExceptionHandler(SoldOutException::class)
    fun handleSoldOut(ex: SoldOutException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(ex.message ?: "Event sold out", "SoldOutException"))

    @ExceptionHandler(ReservationLimitExceededException::class)
    fun handleReservationLimitExceeded(ex: ReservationLimitExceededException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(ex.message ?: "Reservation limit exceeded", "ReservationLimitExceededException"))

    @ExceptionHandler(ReservationAlreadyCancelledException::class)
    fun handleReservationAlreadyCancelled(ex: ReservationAlreadyCancelledException): ResponseEntity<ExceptionResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ExceptionResponse(ex.message ?: "Reservation already cancelled", "ReservationAlreadyCancelledException"))
}
