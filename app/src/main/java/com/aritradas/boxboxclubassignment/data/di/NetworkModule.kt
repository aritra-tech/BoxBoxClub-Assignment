package com.aritradas.boxboxclubassignment.data.di

import com.aritradas.boxboxclubassignment.data.api.ApiService
import com.aritradas.boxboxclubassignment.data.api.ApiServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkModule {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }
    }

    val apiService: ApiService = ApiServiceImpl(client)
}