package com.aritradas.boxboxclubassignment.data.api

import com.aritradas.boxboxclubassignment.data.model.DriversResponse
import com.aritradas.boxboxclubassignment.data.model.ScheduleResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class ApiServiceImpl(
    private val client: HttpClient
) : ApiService {
    
    override suspend fun getDrivers(): DriversResponse {
        return client.get("https://mocki.io/v1/e8616da8-220c-4aab-a670-ab2d43224ecb").body()
    }
    
    override suspend fun getSchedule(): ScheduleResponse {
        return client.get("https://mocki.io/v1/9086a3f1-f02b-4d24-8dd3-b63582f45e67").body()
    }
}

