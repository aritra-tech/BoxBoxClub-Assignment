package com.aritradas.boxboxclubassignment.data.api

import com.aritradas.boxboxclubassignment.data.model.DriversResponse
import com.aritradas.boxboxclubassignment.data.model.ScheduleResponse

interface ApiService {
    suspend fun getDrivers(): DriversResponse
    suspend fun getSchedule(): ScheduleResponse
}

