package com.aritradas.boxboxclubassignment.domain.usecase

import com.aritradas.boxboxclubassignment.data.model.Driver
import com.aritradas.boxboxclubassignment.data.repository.F1Repository
import com.aritradas.boxboxclubassignment.util.Result

/**
 * Use case for getting drivers
 * This separates business logic from ViewModels
 * Makes code more testable and reusable
 */
class GetDriversUseCase(
    private val repository: F1Repository
) {
    suspend operator fun invoke(): Result<List<Driver>> {
        return repository.getDrivers()
    }
    
    /**
     * Gets drivers filtered by position
     */
    suspend fun getDriversByPosition(position: Int): Result<List<Driver>> {
        return repository.getDrivers().fold(
            onSuccess = { drivers ->
                val filteredDrivers = drivers.filter { it.position == position }
                Result.Success(filteredDrivers)
            },
            onError = { exception, message ->
                Result.Error(exception, message)
            },
            onLoading = {
                Result.Loading
            }
        )
    }
}

