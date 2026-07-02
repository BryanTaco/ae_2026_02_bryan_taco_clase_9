package com.pucetec.events.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "events")
class Event(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val name: String = "",

    val venue: String = "",

    val totalTickets: Int = 0,

    var availableTickets: Int = 0,
)
