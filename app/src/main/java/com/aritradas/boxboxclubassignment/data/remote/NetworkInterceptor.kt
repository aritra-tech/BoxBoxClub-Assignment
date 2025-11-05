package com.aritradas.boxboxclubassignment.data.remote

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.AndroidEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders

/**
 * Configuration for HTTP client with proper interceptors and timeouts
 */
fun HttpClientConfig<AndroidEngineConfig>.configureNetwork() {
    // Timeout configuration
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000 // 30 seconds
        connectTimeoutMillis = 10_000 // 10 seconds
        socketTimeoutMillis = 30_000 // 30 seconds
    }

    // Logging interceptor (only in debug builds)
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                android.util.Log.d("Network", message)
            }
        }
        level = LogLevel.INFO
    }

    // Default headers
    defaultRequest {
        header(HttpHeaders.ContentType, ContentType.Application.Json)
        header(HttpHeaders.Accept, ContentType.Application.Json)
    }
}