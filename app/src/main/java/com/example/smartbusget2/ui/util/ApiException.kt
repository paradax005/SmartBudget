package com.example.smartbusget2.ui.util

/**
 * Sealed class for API exceptions following the documentation pattern
 */
sealed class ApiException(message: String) : Exception(message) {
    class UnauthorizedException(message: String) : ApiException(message)
    class NotFoundException(message: String) : ApiException(message)
    class ValidationException(message: String) : ApiException(message)
    class ServerException(message: String) : ApiException(message)
    class NetworkException(message: String) : ApiException(message)
}

/**
 * Utility function to convert HTTP response code to ApiException
 */
fun handleApiError(responseCode: Int, errorMessage: String? = null): ApiException {
    return when (responseCode) {
        401 -> ApiException.UnauthorizedException(errorMessage ?: "Unauthorized")
        404 -> ApiException.NotFoundException(errorMessage ?: "Resource not found")
        400 -> ApiException.ValidationException(errorMessage ?: "Validation error")
        500 -> ApiException.ServerException(errorMessage ?: "Server error")
        else -> ApiException.NetworkException(errorMessage ?: "Unknown error")
    }
}

/**
 * Get user-friendly error message from ApiException
 */
fun getErrorMessage(error: ApiException): String {
    return when (error) {
        is ApiException.UnauthorizedException -> 
            "Your session has expired. Please login again."
        is ApiException.NotFoundException -> 
            "The requested item was not found."
        is ApiException.ValidationException -> 
            "Please check your input and try again."
        is ApiException.ServerException -> 
            "Server error. Please try again later."
        is ApiException.NetworkException -> 
            "Network error. Please check your internet connection."
    }
}


