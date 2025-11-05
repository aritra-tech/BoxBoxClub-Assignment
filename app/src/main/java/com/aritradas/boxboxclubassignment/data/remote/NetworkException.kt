package com.aritradas.boxboxclubassignment.data.remote

import java.io.IOException


sealed class NetworkException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NoInternetConnection(message: String = "No internet connection", cause: Throwable? = null) 
        : NetworkException(message, cause)
    
    class ServerError(message: String = "Server error", cause: Throwable? = null) 
        : NetworkException(message, cause)
    
    class TimeoutException(message: String = "Request timeout", cause: Throwable? = null) 
        : NetworkException(message, cause)
    
    class UnknownError(message: String = "Unknown error occurred", cause: Throwable? = null) 
        : NetworkException(message, cause)
}


fun Throwable.toNetworkException(): NetworkException {
    return when (this) {
        is IOException -> {
            if (message?.contains("Unable to resolve host") == true || 
                message?.contains("Network is unreachable") == true) {
                NetworkException.NoInternetConnection(cause = this)
            } else {
                NetworkException.UnknownError(message = message ?: "Network error", cause = this)
            }
        }
        is java.net.SocketTimeoutException -> {
            NetworkException.TimeoutException(cause = this)
        }
        is NetworkException -> this
        else -> NetworkException.UnknownError(message = message ?: "Unknown error", cause = this)
    }
}

