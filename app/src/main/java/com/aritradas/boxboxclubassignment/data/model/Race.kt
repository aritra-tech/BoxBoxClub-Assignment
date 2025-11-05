package com.aritradas.boxboxclubassignment.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val sessionId: String,
    val sessionType: String,
    val sessionName: String,
    val startTime: Long,
    val endTime: Long,
    val sessionState: String,
    val _id: String
)

@Serializable
data class Race(
    val raceId: String,
    val circuitId: String,
    val isSprint: Boolean,
    val raceEndTime: Long,
    val raceName: String,
    val raceStartTime: Long,
    val raceState: String,
    val round: Int,
    val sessions: List<Session>,
    val podium: List<String>? = null
)

@Serializable
data class ScheduleResponse(
    val schedule: List<Race>
)

