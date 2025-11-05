package com.aritradas.boxboxclubassignment.domain.usecase

import com.aritradas.boxboxclubassignment.data.model.Race
import com.aritradas.boxboxclubassignment.data.model.Session
import com.aritradas.boxboxclubassignment.data.repository.F1Repository
import com.aritradas.boxboxclubassignment.util.Result

/**
 * Use case for getting upcoming race information
 */
class GetUpcomingRaceUseCase(
    private val repository: F1Repository
) {
    suspend operator fun invoke(): Result<Race?> {
        return repository.getUpcomingRace()
    }
    
    /**
     * Gets the next upcoming session for a race
     */
    suspend fun getNextSession(race: Race): Result<Session?> {
        return repository.getNextUpcomingSession(race)
    }
}

