package com.aritradas.boxboxclubassignment.data.di

import com.aritradas.boxboxclubassignment.data.api.ApiService
import com.aritradas.boxboxclubassignment.data.api.ApiServiceImpl
import com.aritradas.boxboxclubassignment.data.remote.configureNetwork
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Network module for dependency injection
 * Provides configured HttpClient and ApiService
 */
object NetworkModule {
    /**
     * Configured HTTP client with interceptors, timeouts, and logging
     */
    private val client = HttpClient(Android) {
        // Network configuration (timeouts, logging, headers)
        configureNetwork()
        
        // JSON serialization
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
                prettyPrint = false
            })
        }
    }

    /**
     * API service implementation
     */
    val apiService: ApiService = ApiServiceImpl(client)
}