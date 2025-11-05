package com.aritradas.boxboxclubassignment.data.repository

import com.aritradas.boxboxclubassignment.data.api.ApiService
import com.aritradas.boxboxclubassignment.data.model.Driver
import com.aritradas.boxboxclubassignment.data.model.Race
import com.aritradas.boxboxclubassignment.data.model.Session
import com.aritradas.boxboxclubassignment.data.remote.NetworkException
import com.aritradas.boxboxclubassignment.data.remote.toNetworkException
import com.aritradas.boxboxclubassignment.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class F1Repository(
    private val apiService: ApiService
) {

    suspend fun getDrivers(): Result<List<Driver>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getDrivers()
            Result.Success(response.drivers)
        } catch (e: Exception) {
            val networkException = e.toNetworkException()
            Result.Error(
                exception = networkException,
                message = getErrorMessage(networkException)
            )
        }
    }

    suspend fun getSchedule(): Result<List<Race>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getSchedule()
            Result.Success(response.schedule)
        } catch (e: Exception) {
            val networkException = e.toNetworkException()
            Result.Error(
                exception = networkException,
                message = getErrorMessage(networkException)
            )
        }
    }

    suspend fun getUpcomingRace(): Result<Race?> = withContext(Dispatchers.IO) {
        try {
            val scheduleResult = getSchedule()
            scheduleResult.fold(
                onSuccess = { schedule ->
                    val upcomingRace = schedule
                        .filter { it.raceState == "upcoming" }
                        .minByOrNull { it.raceStartTime }
                    Result.Success(upcomingRace)
                },
                onError = { exception, message ->
                    Result.Error(exception, message)
                },
                onLoading = {
                    Result.Error(
                        NetworkException.UnknownError("Loading schedule"),
                        "Loading schedule"
                    )
                }
            )
        } catch (e: Exception) {
            val networkException = e.toNetworkException()
            Result.Error(
                exception = networkException,
                message = getErrorMessage(networkException)
            )
        }
    }

    suspend fun getNextUpcomingSession(race: Race): Result<Session?> = withContext(Dispatchers.IO) {
        try {
            val nextSession = race.sessions
                .filter { it.sessionState == "upcoming" }
                .minByOrNull { it.startTime }
            Result.Success(nextSession)
        } catch (e: Exception) {
            val networkException = e.toNetworkException()
            Result.Error(
                exception = networkException,
                message = getErrorMessage(networkException)
            )
        }
    }

    suspend fun getRaceById(raceId: String): Result<Race?> = withContext(Dispatchers.IO) {
        try {
            val scheduleResult = getSchedule()
            scheduleResult.fold(
                onSuccess = { schedule ->
                    val race = schedule.find { it.raceId == raceId }
                    Result.Success(race)
                },
                onError = { exception, message ->
                    Result.Error(exception, message)
                },
                onLoading = {
                    Result.Error(
                        NetworkException.UnknownError("Loading schedule"),
                        "Loading schedule"
                    )
                }
            )
        } catch (e: Exception) {
            val networkException = e.toNetworkException()
            Result.Error(
                exception = networkException,
                message = getErrorMessage(networkException)
            )
        }
    }

    private fun getErrorMessage(exception: NetworkException): String {
        return when (exception) {
            is NetworkException.NoInternetConnection -> 
                "No internet connection. Please check your network and try again."
            is NetworkException.TimeoutException -> 
                "Request timed out. Please try again."
            is NetworkException.ServerError -> 
                "Server error. Please try again later."
            is NetworkException.UnknownError -> 
                exception.message ?: "An unexpected error occurred. Please try again."
        }
    }
}
