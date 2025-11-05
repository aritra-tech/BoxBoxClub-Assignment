package com.aritradas.boxboxclubassignment.util

/**
 * Sealed class representing the result of an operation.
 * This is better than using Result<T> directly as it provides more control.
 */
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onError: (Throwable, String?) -> R,
        noinline onLoading: (() -> R)? = null
    ): R {
        return when (this) {
            is Success -> onSuccess(data)
            is Error -> onError(exception, message)
            is Loading -> onLoading?.invoke() ?: onError(Exception("Loading"), null)
        }
    }

    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable, String?) -> Unit): Result<T> {
        if (this is Error) action(exception, message)
        return this
    }
}

/**
 * Extension function to convert Kotlin's Result to our custom Result
 */
fun <T> kotlin.Result<T>.toResult(): Result<T> {
    return fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(it, it.message) }
    )
}