package com.aritradas.boxboxclubassignment.domain.usecase

import com.aritradas.boxboxclubassignment.data.model.Race
import com.aritradas.boxboxclubassignment.data.repository.F1Repository
import com.aritradas.boxboxclubassignment.util.Result

/**
 * Use case for getting race details by ID
 */
class GetRaceDetailsUseCase(
    private val repository: F1Repository
) {
    suspend operator fun invoke(raceId: String): Result<Race?> {
        return repository.getRaceById(raceId)
    }
}

