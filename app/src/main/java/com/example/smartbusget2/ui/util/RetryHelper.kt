package com.example.smartbusget2.ui.util

import kotlinx.coroutines.delay

/**
 * Retry logic with exponential backoff as per documentation
 * @param times Number of retry attempts
 * @param initialDelay Initial delay in milliseconds
 * @param maxDelay Maximum delay in milliseconds
 * @param factor Exponential backoff factor
 * @param block The suspend function to retry
 */
suspend fun <T> retryWithBackoff(
    times: Int = 3,
    initialDelay: Long = 1000,
    maxDelay: Long = 10000,
    factor: Double = 2.0,
    block: suspend () -> Result<T>
): Result<T> {
    var currentDelay = initialDelay
    repeat(times - 1) {
        val result = block()
        if (result.isSuccess) {
            return result
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block()
}


