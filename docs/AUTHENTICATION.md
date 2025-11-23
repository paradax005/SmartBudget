# Authentication Guide

This guide explains how authentication works in the SmartBudget API and how to implement it in your mobile app.

## Overview

The SmartBudget API uses **JWT (JSON Web Tokens)** for authentication. After successful registration or login, you receive a JWT token that must be included in all subsequent API requests.

## Authentication Flow

```
┌─────────┐                    ┌─────────┐
│  App    │                    │  API    │
└────┬────┘                    └────┬────┘
     │                              │
     │  1. POST /auth/register      │
     │  or POST /auth/login         │
     ├─────────────────────────────>│
     │                              │
     │  2. JWT Token                │
     │<─────────────────────────────┤
     │                              │
     │  3. Store token securely     │
     │                              │
     │  4. Include in requests:     │
     │  Authorization: Bearer <token>│
     │                              │
     │  5. Protected API calls      │
     ├─────────────────────────────>│
     │                              │
     │  6. Response                 │
     │<─────────────────────────────┤
```

## Token Details

- **Token Type**: JWT (JSON Web Token)
- **Expiration**: 1 hour
- **Format**: `Bearer <token_string>`
- **Storage**: Store securely (Keychain for iOS, EncryptedSharedPreferences for Android)

## Registration

### Endpoint

```
POST /api/auth/register
```

### Request Body

```json
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

### Validation Rules

- **email**: Must be a valid email address
- **password**: Minimum 6 characters
- **fullName**: Required, cannot be empty

### Response (201 Created)

```json
{
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "email": "user@example.com",
    "fullName": "John Doe"
  }
}
```

### Error Responses

**400 Bad Request** - Validation error or user already exists:
```json
{
  "error": "User with this email already exists",
  "status": 400
}
```

### Android Implementation

```kotlin
// ApiService.kt
interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

data class RegisterResponse(
    val message: String,
    val token: String,
    val user: User
)

// Usage
lifecycleScope.launch {
    try {
        val request = RegisterRequest(
            email = "user@example.com",
            password = "password123",
            fullName = "John Doe"
        )
        val response = apiService.register(request)
        
        if (response.isSuccessful) {
            val token = response.body()?.token
            tokenManager.saveToken(token ?: "")
            // Navigate to main screen
        } else {
            // Handle error
            val errorBody = response.errorBody()?.string()
            // Parse error message
        }
    } catch (e: Exception) {
        // Handle network error
    }
}
```

### iOS Implementation

```swift
// ApiService.swift
struct ApiService {
    static func register(email: String, password: String, fullName: String) async throws -> RegisterResponse {
        let url = URL(string: "\(ApiConfig.baseURL)/auth/register")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body: [String: Any] = [
            "email": email,
            "password": password,
            "fullName": fullName
        ]
        request.httpBody = try JSONSerialization.data(withJSONObject: body)
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw ApiError.invalidResponse
        }
        
        if httpResponse.statusCode == 201 {
            return try JSONDecoder().decode(RegisterResponse.self, from: data)
        } else {
            let errorResponse = try JSONDecoder().decode(ErrorResponse.self, from: data)
            throw ApiError.serverError(errorResponse.error)
        }
    }
}

// Usage
Task {
    do {
        let response = try await ApiService.register(
            email: "user@example.com",
            password: "password123",
            fullName: "John Doe"
        )
        TokenManager.shared.saveToken(response.token)
        // Navigate to main screen
    } catch {
        // Handle error
        print("Registration failed: \(error)")
    }
}
```

## Login

### Endpoint

```
POST /api/auth/login
```

### Request Body

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### Response (200 OK)

```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "email": "user@example.com",
    "fullName": "John Doe"
  }
}
```

### Error Responses

**401 Unauthorized** - Invalid credentials:
```json
{
  "error": "Invalid email or password",
  "status": 401
}
```

### Android Implementation

```kotlin
@POST("auth/login")
suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

data class LoginRequest(
    val email: String,
    val password: String
)

// Usage
lifecycleScope.launch {
    try {
        val response = apiService.login(
            LoginRequest("user@example.com", "password123")
        )
        if (response.isSuccessful) {
            tokenManager.saveToken(response.body()?.token ?: "")
        }
    } catch (e: Exception) {
        // Handle error
    }
}
```

### iOS Implementation

```swift
static func login(email: String, password: String) async throws -> LoginResponse {
    let url = URL(string: "\(ApiConfig.baseURL)/auth/login")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    
    let body: [String: Any] = [
        "email": email,
        "password": password
    ]
    request.httpBody = try JSONSerialization.data(withJSONObject: body)
    
    let (data, response) = try await URLSession.shared.data(for: request)
    
    guard let httpResponse = response as? HTTPURLResponse else {
        throw ApiError.invalidResponse
    }
    
    if httpResponse.statusCode == 200 {
        return try JSONDecoder().decode(LoginResponse.self, from: data)
    } else {
        let errorResponse = try JSONDecoder().decode(ErrorResponse.self, from: data)
        throw ApiError.serverError(errorResponse.error)
    }
}
```

## Get Current User

### Endpoint

```
GET /api/auth/me
```

### Headers

```
Authorization: Bearer <your_jwt_token>
```

### Response (200 OK)

```json
{
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "email": "user@example.com",
    "fullName": "John Doe",
    "createdAt": "2024-01-15T10:30:00.000Z"
  }
}
```

### Use Cases

- Verify token is still valid
- Get user information on app startup
- Refresh user profile data

### Android Implementation

```kotlin
@GET("auth/me")
suspend fun getCurrentUser(): Response<UserResponse>

// Usage
lifecycleScope.launch {
    try {
        val response = apiService.getCurrentUser()
        if (response.isSuccessful) {
            val user = response.body()?.user
            // Update UI with user info
        } else if (response.code() == 401) {
            // Token expired, redirect to login
            tokenManager.clearToken()
            // Navigate to login screen
        }
    } catch (e: Exception) {
        // Handle error
    }
}
```

## Adding Authentication to Requests

### Android (OkHttp Interceptor)

```kotlin
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        val token = tokenManager.getToken()
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}

// Setup OkHttpClient
val client = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor(tokenManager))
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    })
    .build()
```

### iOS (URLRequest Extension)

```swift
extension URLRequest {
    mutating func addAuthToken() {
        if let token = TokenManager.shared.getToken() {
            self.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
    }
}

// Usage
var request = URLRequest(url: url)
request.httpMethod = "GET"
request.addAuthToken()
```

## Token Expiration Handling

### Token Expiration

JWT tokens expire after **1 hour**. When a token expires, you'll receive a `401 Unauthorized` response.

### Handling Expired Tokens

**Android:**

```kotlin
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        
        // If token expired, clear and redirect to login
        if (response.code == 401) {
            tokenManager.clearToken()
            // Post event to redirect to login
            EventBus.getDefault().post(LogoutEvent())
        }
        
        return response
    }
}
```

**iOS:**

```swift
func handleResponse(_ response: HTTPURLResponse) {
    if response.statusCode == 401 {
        TokenManager.shared.clearToken()
        // Post notification to redirect to login
        NotificationCenter.default.post(name: .userLoggedOut, object: nil)
    }
}
```

### Token Refresh Strategy

Since tokens expire after 1 hour, implement one of these strategies:

1. **Re-login on expiration**: Clear token and redirect to login screen
2. **Proactive refresh**: Refresh token before expiration (if refresh endpoint available)
3. **Background refresh**: Check token validity periodically

## Logout

### Implementation

Logout is handled client-side by clearing the stored token:

**Android:**

```kotlin
fun logout() {
    tokenManager.clearToken()
    // Clear user data
    // Navigate to login screen
}
```

**iOS:**

```swift
func logout() {
    TokenManager.shared.clearToken()
    // Clear user data
    // Navigate to login screen
}
```

## Security Best Practices

1. **Never store tokens in plain text**
   - Use Keychain (iOS) or EncryptedSharedPreferences (Android)

2. **Always use HTTPS in production**
   - Never send tokens over HTTP

3. **Validate token on app startup**
   - Call `/api/auth/me` to verify token validity

4. **Handle token expiration gracefully**
   - Show appropriate message and redirect to login

5. **Clear tokens on logout**
   - Remove tokens from secure storage

6. **Don't log tokens**
   - Avoid logging tokens in debug logs

## Next Steps

- Review all endpoints: **[ENDPOINTS.md](./ENDPOINTS.md)**
- Learn error handling: **[ERROR_HANDLING.md](./ERROR_HANDLING.md)**
- See integration examples: **[INTEGRATION.md](./INTEGRATION.md)**

