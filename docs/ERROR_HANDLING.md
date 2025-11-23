# Error Handling Guide

This guide explains how to handle errors from the SmartBudget API in your mobile application.

## Error Response Format

All error responses follow a consistent format:

```json
{
  "error": "Error message description",
  "status": 400
}
```

## HTTP Status Codes

### Success Codes

- **200 OK** - Request successful
- **201 Created** - Resource created successfully

### Client Error Codes

- **400 Bad Request** - Validation error or invalid request
- **401 Unauthorized** - Authentication required or token expired
- **404 Not Found** - Resource not found

### Server Error Codes

- **500 Internal Server Error** - Server error

## Common Error Scenarios

### 1. Authentication Errors

#### 401 Unauthorized - No Token

**Response:**
```json
{
  "error": "No token provided. Authorization header must be in format: Bearer <token>",
  "status": 401
}
```

**Android Handling:**
```kotlin
if (response.code() == 401) {
    // Redirect to login screen
    tokenManager.clearToken()
    navigateToLogin()
}
```

**iOS Handling:**
```swift
if httpResponse.statusCode == 401 {
    TokenManager.shared.clearToken()
    // Navigate to login screen
    NotificationCenter.default.post(name: .userLoggedOut, object: nil)
}
```

#### 401 Unauthorized - Invalid Token

**Response:**
```json
{
  "error": "Invalid token",
  "status": 401
}
```

**Handling:**
- Clear stored token
- Redirect user to login screen
- Show appropriate message

#### 401 Unauthorized - Token Expired

**Response:**
```json
{
  "error": "Token expired",
  "status": 401
}
```

**Handling:**
- Clear stored token
- Prompt user to login again
- Optionally attempt automatic re-login if credentials are stored securely

### 2. Validation Errors

#### 400 Bad Request - Validation Error

**Response:**
```json
{
  "error": "Email is required, Password must be at least 6 characters long",
  "status": 400
}
```

**Android Handling:**
```kotlin
if (response.code() == 400) {
    val errorBody = response.errorBody()?.string()
    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
    
    // Parse error message
    val errors = errorResponse.error.split(", ")
    errors.forEach { error ->
        when {
            error.contains("email", ignoreCase = true) -> {
                // Show email error
                emailError.value = error
            }
            error.contains("password", ignoreCase = true) -> {
                // Show password error
                passwordError.value = error
            }
        }
    }
}
```

**iOS Handling:**
```swift
if httpResponse.statusCode == 400 {
    let errorResponse = try JSONDecoder().decode(ErrorResponse.self, from: data)
    let errors = errorResponse.error.components(separatedBy: ", ")
    
    for error in errors {
        if error.lowercased().contains("email") {
            // Show email error
            emailError = error
        } else if error.lowercased().contains("password") {
            // Show password error
            passwordError = error
        }
    }
}
```

### 3. Resource Not Found

#### 404 Not Found

**Response:**
```json
{
  "error": "Account not found",
  "status": 404
}
```

**Handling:**
- Show user-friendly message
- Optionally refresh the list
- Navigate back if appropriate

**Android Example:**
```kotlin
if (response.code() == 404) {
    showError("Account not found. It may have been deleted.")
    // Refresh accounts list
    loadAccounts()
}
```

**iOS Example:**
```swift
if httpResponse.statusCode == 404 {
    showError("Account not found. It may have been deleted.")
    // Refresh accounts list
    await loadAccounts()
}
```

### 4. Duplicate Resource

#### 400 Bad Request - User Already Exists

**Response:**
```json
{
  "error": "User with this email already exists",
  "status": 400
}
```

**Handling:**
- Show specific error message
- Suggest user to login instead
- Pre-fill email field

### 5. Business Logic Errors

#### 400 Bad Request - Category in Use

**Response:**
```json
{
  "error": "Cannot delete category. It is being used in 5 transaction(s)",
  "status": 400
}
```

**Handling:**
- Show informative message
- Explain why the action cannot be performed
- Suggest alternative actions

### 6. Network Errors

#### Connection Timeout

**Android:**
```kotlin
catch (e: SocketTimeoutException) {
    showError("Connection timeout. Please check your internet connection.")
}
```

**iOS:**
```swift
catch URLError.timedOut {
    showError("Connection timeout. Please check your internet connection.")
}
```

#### No Internet Connection

**Android:**
```kotlin
catch (e: UnknownHostException) {
    showError("No internet connection. Please check your network settings.")
}
```

**iOS:**
```swift
catch URLError.notConnectedToInternet {
    showError("No internet connection. Please check your network settings.")
}
```

## Error Handling Patterns

### 1. Centralized Error Handler

**Android Example:**

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val code: Int) : ApiResult<Nothing>()
    object NetworkError : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            ApiResult.Success(response.body()!!)
        } else {
            val errorBody = response.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ApiResult.Error(errorResponse.error, response.code())
        }
    } catch (e: IOException) {
        ApiResult.NetworkError
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Unknown error", 0)
    }
}

// Usage
val result = safeApiCall { apiService.getAccounts() }
when (result) {
    is ApiResult.Success -> {
        // Handle success
        accounts.value = result.data.data
    }
    is ApiResult.Error -> {
        // Handle error
        showError(result.message)
    }
    is ApiResult.NetworkError -> {
        // Handle network error
        showError("Network error. Please check your connection.")
    }
}
```

**iOS Example:**

```swift
enum ApiResult<T> {
    case success(T)
    case error(String, Int)
    case networkError
}

func safeApiCall<T: Decodable>(
    _ apiCall: () async throws -> T
) async -> ApiResult<T> {
    do {
        let result = try await apiCall()
        return .success(result)
    } catch URLError.notConnectedToInternet {
        return .networkError
    } catch URLError.timedOut {
        return .networkError
    } catch {
        if let apiError = error as? ApiError {
            return .error(apiError.message, apiError.code)
        }
        return .error(error.localizedDescription, 0)
    }
}

// Usage
let result = await safeApiCall { try await apiService.getAccounts() }
switch result {
case .success(let response):
    accounts = response.data
case .error(let message, let code):
    showError(message)
case .networkError:
    showError("Network error. Please check your connection.")
}
```

### 2. Error Response Model

**Android:**

```kotlin
data class ErrorResponse(
    val error: String,
    val status: Int
)
```

**iOS:**

```swift
struct ErrorResponse: Codable {
    let error: String
    let status: Int
}
```

### 3. Custom Exception Classes

**Android:**

```kotlin
sealed class ApiException(message: String) : Exception(message) {
    class UnauthorizedException(message: String) : ApiException(message)
    class NotFoundException(message: String) : ApiException(message)
    class ValidationException(message: String) : ApiException(message)
    class ServerException(message: String) : ApiException(message)
    class NetworkException(message: String) : ApiException(message)
}

fun handleApiError(response: Response<*>): ApiException {
    return when (response.code()) {
        401 -> ApiException.UnauthorizedException("Unauthorized")
        404 -> ApiException.NotFoundException("Resource not found")
        400 -> ApiException.ValidationException("Validation error")
        500 -> ApiException.ServerException("Server error")
        else -> ApiException.NetworkException("Unknown error")
    }
}
```

**iOS:**

```swift
enum ApiError: Error {
    case unauthorized(String)
    case notFound(String)
    case validationError(String)
    case serverError(String)
    case networkError(String)
    
    var message: String {
        switch self {
        case .unauthorized(let msg), .notFound(let msg),
             .validationError(let msg), .serverError(let msg),
             .networkError(let msg):
            return msg
        }
    }
}
```

## User-Friendly Error Messages

### Mapping Technical Errors to User Messages

**Android:**

```kotlin
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
```

**iOS:**

```swift
extension ApiError {
    var userMessage: String {
        switch self {
        case .unauthorized:
            return "Your session has expired. Please login again."
        case .notFound:
            return "The requested item was not found."
        case .validationError:
            return "Please check your input and try again."
        case .serverError:
            return "Server error. Please try again later."
        case .networkError:
            return "Network error. Please check your internet connection."
        }
    }
}
```

## Error Logging

### Android Logging

```kotlin
import android.util.Log

fun logError(tag: String, error: ApiException) {
    Log.e(tag, "API Error: ${error.message}", error)
    // Optionally send to crash reporting service
    // Crashlytics.recordException(error)
}
```

### iOS Logging

```swift
import os.log

func logError(_ error: ApiError) {
    os_log("API Error: %@", log: .default, type: .error, error.message)
    // Optionally send to crash reporting service
    // Crashlytics.crashlytics().record(error: error)
}
```

## Retry Logic

### Android Retry with Exponential Backoff

```kotlin
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
```

### iOS Retry with Exponential Backoff

```swift
func retryWithBackoff<T>(
    times: Int = 3,
    initialDelay: TimeInterval = 1.0,
    maxDelay: TimeInterval = 10.0,
    factor: Double = 2.0,
    block: () async throws -> T
) async throws -> T {
    var currentDelay = initialDelay
    
    for _ in 0..<(times - 1) {
        do {
            return try await block()
        } catch {
            try await Task.sleep(nanoseconds: UInt64(currentDelay * 1_000_000_000))
            currentDelay = min(currentDelay * factor, maxDelay)
        }
    }
    
    return try await block()
}
```

## Best Practices

1. **Always handle errors gracefully** - Never let unhandled exceptions crash the app
2. **Show user-friendly messages** - Translate technical errors to user-friendly language
3. **Log errors for debugging** - Log errors for development but don't expose technical details to users
4. **Implement retry logic** - Retry transient errors (network, timeout)
5. **Handle offline scenarios** - Show cached data when offline
6. **Validate input client-side** - Reduce server errors by validating before sending
7. **Handle token expiration** - Automatically redirect to login when token expires
8. **Provide feedback** - Always show loading states and error messages

## Common Error Messages Reference

| Status Code | Error Message | User Message |
|------------|---------------|--------------|
| 400 | Validation error | "Please check your input and try again." |
| 400 | User already exists | "An account with this email already exists." |
| 400 | Category in use | "Cannot delete category. It is being used in transactions." |
| 401 | No token provided | "Please login to continue." |
| 401 | Invalid token | "Your session is invalid. Please login again." |
| 401 | Token expired | "Your session has expired. Please login again." |
| 404 | Resource not found | "The requested item was not found." |
| 500 | Server error | "Server error. Please try again later." |

## Next Steps

- Review integration patterns: **[INTEGRATION.md](./INTEGRATION.md)**
- Check endpoint details: **[ENDPOINTS.md](./ENDPOINTS.md)**
- See authentication flow: **[AUTHENTICATION.md](./AUTHENTICATION.md)**

