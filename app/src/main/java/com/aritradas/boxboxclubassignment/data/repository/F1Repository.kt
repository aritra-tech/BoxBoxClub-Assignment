package com.aritradas.boxboxclubassignment.data.repository

import com.aritradas.boxboxclubassignment.data.api.ApiService
import com.aritradas.boxboxclubassignment.data.model.Driver
import com.aritradas.boxboxclubassignment.data.model.Race
import com.aritradas.boxboxclubassignment.data.model.Session

class F1Repository(
    private val apiService: ApiService
) {
    suspend fun getDrivers(): Result<List<Driver>> {
        return try {
            val response = apiService.getDrivers()
            Result.success(response.drivers)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSchedule(): Result<List<Race>> {
        return try {
            val response = apiService.getSchedule()
            Result.success(response.schedule)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getDriverByPosition(position: Int): Result<Driver?> {
        return try {
            val drivers = getDrivers().getOrThrow()
            val driver = drivers.find { it.position == position }
            Result.success(driver)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUpcomingRace(): Result<Race?> {
        return try {
            val schedule = getSchedule().getOrThrow()
            val currentTime = System.currentTimeMillis() / 1000 // Unix timestamp in seconds
            val upcomingRace = schedule
                .filter { it.raceState == "upcoming" }
                .minByOrNull { it.raceStartTime }
            Result.success(upcomingRace)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getNextUpcomingSession(race: Race): Result<Session?> {
        return try {
            val currentTime = System.currentTimeMillis() / 1000
            val nextSession = race.sessions
                .filter { it.sessionState == "upcoming" }
                .minByOrNull { it.startTime }
            Result.success(nextSession)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

