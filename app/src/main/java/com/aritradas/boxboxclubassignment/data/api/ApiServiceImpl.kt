package com.aritradas.boxboxclubassignment.data.api

import com.aritradas.boxboxclubassignment.data.model.DriversResponse
import com.aritradas.boxboxclubassignment.data.model.ScheduleResponse
import com.aritradas.boxboxclubassignment.data.remote.toNetworkException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * API Service implementation using KTOR
 * Handles network calls and error propagation
 */
class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {
    
    companion object {
        private const val BASE_URL = "https://mocki.io/v1"
        private const val DRIVERS_ENDPOINT = "$BASE_URL/e8616da8-220c-4aab-a670-ab2d43224ecb"
        private const val SCHEDULE_ENDPOINT = "$BASE_URL/9086a3f1-f02b-4d24-8dd3-b63582f45e67"
    }
    
    override suspend fun getDrivers(): DriversResponse {
        return try {
            client.get(DRIVERS_ENDPOINT).body()
        } catch (e: Exception) {
            throw e.toNetworkException()
        }
    }
    
    override suspend fun getSchedule(): ScheduleResponse {
        return try {
            client.get(SCHEDULE_ENDPOINT).body()
        } catch (e: Exception) {
            throw e.toNetworkException()
        }
    }
}

