# Mobile Integration Guide

This guide provides detailed integration patterns and examples for Android and iOS mobile applications.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Network Layer Setup](#network-layer-setup)
3. [Data Models](#data-models)
4. [API Service Implementation](#api-service-implementation)
5. [Common Integration Patterns](#common-integration-patterns)
6. [Best Practices](#best-practices)

## Architecture Overview

### Recommended Architecture

```
┌─────────────────┐
│   UI Layer      │
│  (Activities/   │
│   ViewModels)   │
└────────┬────────┘
         │
┌────────▼────────┐
│  ViewModel/     │
│  Presenter     │
└────────┬────────┘
         │
┌────────▼────────┐
│  Repository     │
│  (Data Layer)   │
└────────┬────────┘
         │
┌────────▼────────┐
│  API Service    │
│  (Network)      │
└─────────────────┘
```

## Network Layer Setup

### Android (Kotlin with Retrofit)

#### 1. Dependencies

**build.gradle (Module: app)**

```gradle
dependencies {
    // Retrofit
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.6.2'
}
```

#### 2. API Service Interface

**ApiService.kt**

```kotlin
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Authentication
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserResponse>
    
    // Accounts
    @GET("accounts")
    suspend fun getAccounts(): Response<AccountsResponse>
    
    @POST("accounts")
    suspend fun createAccount(@Body request: CreateAccountRequest): Response<AccountResponse>
    
    @PUT("accounts/{id}")
    suspend fun updateAccount(@Path("id") id: String, @Body request: UpdateAccountRequest): Response<AccountResponse>
    
    @DELETE("accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String): Response<MessageResponse>
    
    // Categories
    @GET("categories")
    suspend fun getCategories(@Query("type") type: String? = null): Response<CategoriesResponse>
    
    @POST("categories")
    suspend fun createCategory(@Body request: CreateCategoryRequest): Response<CategoryResponse>
    
    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body request: UpdateCategoryRequest): Response<CategoryResponse>
    
    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<MessageResponse>
    
    // Transactions
    @GET("transactions")
    suspend fun getTransactions(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("accountId") accountId: String? = null
    ): Response<TransactionsResponse>
    
    @POST("transactions")
    suspend fun createTransaction(@Body request: CreateTransactionRequest): Response<TransactionResponse>
    
    @PUT("transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: String, @Body request: UpdateTransactionRequest): Response<TransactionResponse>
    
    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): Response<MessageResponse>
    
    @GET("transactions/stats/monthly")
    suspend fun getMonthlyStats(@Query("month") month: Int, @Query("year") year: Int): Response<MonthlyStatsResponse>
    
    // Budgets
    @GET("budgets")
    suspend fun getBudgets(@Query("month") month: Int? = null, @Query("year") year: Int? = null): Response<BudgetsResponse>
    
    @POST("budgets")
    suspend fun createBudget(@Body request: CreateBudgetRequest): Response<BudgetResponse>
    
    @PUT("budgets/{id}")
    suspend fun updateBudget(@Path("id") id: String, @Body request: UpdateBudgetRequest): Response<BudgetResponse>
    
    @DELETE("budgets/{id}")
    suspend fun deleteBudget(@Path("id") id: String): Response<MessageResponse>
    
    @GET("budgets/status")
    suspend fun getBudgetStatus(@Query("month") month: Int, @Query("year") year: Int): Response<BudgetStatusResponse>
}
```

#### 3. Retrofit Setup

**RetrofitClient.kt**

```kotlin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3000/api"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val authInterceptor = AuthInterceptor(TokenManager.getInstance())
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

#### 4. Auth Interceptor

**AuthInterceptor.kt**

```kotlin
import okhttp3.Interceptor
import okhttp3.Response

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
        
        val response = chain.proceed(newRequest)
        
        // Handle token expiration
        if (response.code == 401) {
            tokenManager.clearToken()
            // Post event to redirect to login
            EventBus.getDefault().post(LogoutEvent())
        }
        
        return response
    }
}
```

### iOS (Swift with URLSession)

#### 1. API Service Class

**ApiService.swift**

```swift
import Foundation

class ApiService {
    static let shared = ApiService()
    private let baseURL = ApiConfig.baseURL
    private let session = URLSession.shared
    
    private init() {}
    
    // MARK: - Authentication
    
    func register(email: String, password: String, fullName: String) async throws -> RegisterResponse {
        let url = URL(string: "\(baseURL)/auth/register")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body: [String: Any] = [
            "email": email,
            "password": password,
            "fullName": fullName
        ]
        request.httpBody = try JSONSerialization.data(withJSONObject: body)
        
        let (data, response) = try await session.data(for: request)
        try validateResponse(response)
        return try JSONDecoder().decode(RegisterResponse.self, from: data)
    }
    
    func login(email: String, password: String) async throws -> LoginResponse {
        let url = URL(string: "\(baseURL)/auth/login")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let body: [String: Any] = [
            "email": email,
            "password": password
        ]
        request.httpBody = try JSONSerialization.data(withJSONObject: body)
        
        let (data, response) = try await session.data(for: request)
        try validateResponse(response)
        return try JSONDecoder().decode(LoginResponse.self, from: data)
    }
    
    func getCurrentUser() async throws -> UserResponse {
        let url = URL(string: "\(baseURL)/auth/me")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.addAuthToken()
        
        let (data, response) = try await session.data(for: request)
        try validateResponse(response)
        return try JSONDecoder().decode(UserResponse.self, from: data)
    }
    
    // MARK: - Accounts
    
    func getAccounts() async throws -> AccountsResponse {
        let url = URL(string: "\(baseURL)/accounts")!
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.addAuthToken()
        
        let (data, response) = try await session.data(for: request)
        try validateResponse(response)
        return try JSONDecoder().decode(AccountsResponse.self, from: data)
    }
    
    func createAccount(request: CreateAccountRequest) async throws -> AccountResponse {
        let url = URL(string: "\(baseURL)/accounts")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addAuthToken()
        request.httpBody = try JSONEncoder().encode(request)
        
        let (data, response) = try await session.data(for: request)
        try validateResponse(response)
        return try JSONDecoder().decode(AccountResponse.self, from: data)
    }
    
    // MARK: - Helper Methods
    
    private func validateResponse(_ response: URLResponse) throws {
        guard let httpResponse = response as? HTTPURLResponse else {
            throw ApiError.invalidResponse
        }
        
        if httpResponse.statusCode == 401 {
            TokenManager.shared.clearToken()
            NotificationCenter.default.post(name: .userLoggedOut, object: nil)
            throw ApiError.unauthorized
        }
        
        guard (200...299).contains(httpResponse.statusCode) else {
            throw ApiError.httpError(httpResponse.statusCode)
        }
    }
}

// MARK: - URLRequest Extension

extension URLRequest {
    mutating func addAuthToken() {
        if let token = TokenManager.shared.getToken() {
            self.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
    }
}
```

## Data Models

### Android (Kotlin Data Classes)

**Models.kt**

```kotlin
// Authentication
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

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val createdAt: String? = null
)

// Accounts
data class Account(
    val _id: String,
    val userId: String,
    val name: String,
    val type: String, // "bank", "cash", "credit_card", "savings"
    val balance: Double,
    val currency: String,
    val createdAt: String,
    val updatedAt: String
)

data class CreateAccountRequest(
    val name: String,
    val type: String,
    val balance: Double,
    val currency: String
)

data class AccountsResponse(
    val data: List<Account>,
    val count: Int
)

// Transactions
data class Transaction(
    val _id: String,
    val userId: String,
    val accountId: Any, // Can be String or Account object
    val categoryId: Any, // Can be String or Category object
    val amount: Double,
    val type: String, // "expense" or "income"
    val description: String,
    val date: String,
    val createdAt: String,
    val updatedAt: String
)

data class CreateTransactionRequest(
    val accountId: String,
    val categoryId: String,
    val amount: Double,
    val type: String,
    val description: String,
    val date: String // ISO 8601 format
)

data class TransactionResponse(
    val message: String,
    val data: Transaction,
    val accountBalance: Double
)
```

### iOS (Swift Structs)

**Models.swift**

```swift
import Foundation

// Authentication
struct RegisterRequest: Codable {
    let email: String
    let password: String
    let fullName: String
}

struct RegisterResponse: Codable {
    let message: String
    let token: String
    let user: User
}

struct LoginRequest: Codable {
    let email: String
    let password: String
}

struct LoginResponse: Codable {
    let message: String
    let token: String
    let user: User
}

struct User: Codable {
    let id: String
    let email: String
    let fullName: String
    let createdAt: String?
}

// Accounts
struct Account: Codable {
    let _id: String
    let userId: String
    let name: String
    let type: String // "bank", "cash", "credit_card", "savings"
    let balance: Double
    let currency: String
    let createdAt: String
    let updatedAt: String
}

struct CreateAccountRequest: Codable {
    let name: String
    let type: String
    let balance: Double
    let currency: String
}

struct AccountsResponse: Codable {
    let data: [Account]
    let count: Int
}

// Transactions
struct Transaction: Codable {
    let _id: String
    let userId: String
    let accountId: Account
    let categoryId: Category
    let amount: Double
    let type: String // "expense" or "income"
    let description: String
    let date: String
    let createdAt: String
    let updatedAt: String
}

struct CreateTransactionRequest: Codable {
    let accountId: String
    let categoryId: String
    let amount: Double
    let type: String
    let description: String
    let date: String // ISO 8601 format
}

struct TransactionResponse: Codable {
    let message: String
    let data: Transaction
    let accountBalance: Double
}
```

## Common Integration Patterns

### 1. Repository Pattern

**Android Example**

```kotlin
class AccountRepository(private val apiService: ApiService) {
    suspend fun getAccounts(): Result<List<Account>> {
        return try {
            val response = apiService.getAccounts()
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception("Failed to fetch accounts"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createAccount(account: CreateAccountRequest): Result<Account> {
        return try {
            val response = apiService.createAccount(account)
            if (response.isSuccessful) {
                Result.success(response.body()?.data!!)
            } else {
                Result.failure(Exception("Failed to create account"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**iOS Example**

```swift
class AccountRepository {
    private let apiService = ApiService.shared
    
    func getAccounts() async throws -> [Account] {
        let response = try await apiService.getAccounts()
        return response.data
    }
    
    func createAccount(_ request: CreateAccountRequest) async throws -> Account {
        let response = try await apiService.createAccount(request: request)
        return response.data
    }
}
```

### 2. ViewModel Pattern

**Android Example**

```kotlin
class AccountsViewModel(private val repository: AccountRepository) : ViewModel() {
    private val _accounts = MutableStateFlow<List<Account>>(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadAccounts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getAccounts().fold(
                onSuccess = { accounts ->
                    _accounts.value = accounts
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
            
            _isLoading.value = false
        }
    }
    
    fun createAccount(account: CreateAccountRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.createAccount(account).fold(
                onSuccess = { account ->
                    _accounts.value = _accounts.value + account
                },
                onFailure = { exception ->
                    _error.value = exception.message
                }
            )
            
            _isLoading.value = false
        }
    }
}
```

**iOS Example**

```swift
@MainActor
class AccountsViewModel: ObservableObject {
    @Published var accounts: [Account] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    
    private let repository = AccountRepository()
    
    func loadAccounts() {
        Task {
            isLoading = true
            errorMessage = nil
            
            do {
                accounts = try await repository.getAccounts()
            } catch {
                errorMessage = error.localizedDescription
            }
            
            isLoading = false
        }
    }
    
    func createAccount(_ request: CreateAccountRequest) {
        Task {
            isLoading = true
            errorMessage = nil
            
            do {
                let account = try await repository.createAccount(request)
                accounts.append(account)
            } catch {
                errorMessage = error.localizedDescription
            }
            
            isLoading = false
        }
    }
}
```

## Best Practices

### 1. Error Handling

Always handle errors gracefully:

**Android:**
```kotlin
try {
    val response = apiService.getAccounts()
    if (response.isSuccessful) {
        // Handle success
    } else {
        when (response.code()) {
            401 -> // Handle unauthorized
            400 -> // Handle bad request
            404 -> // Handle not found
            500 -> // Handle server error
        }
    }
} catch (e: IOException) {
    // Handle network error
} catch (e: Exception) {
    // Handle other errors
}
```

**iOS:**
```swift
do {
    let response = try await apiService.getAccounts()
    // Handle success
} catch ApiError.unauthorized {
    // Handle unauthorized
} catch ApiError.httpError(let code) {
    // Handle HTTP error
} catch {
    // Handle other errors
}
```

### 2. Loading States

Always show loading indicators:

**Android:**
```kotlin
if (isLoading.value) {
    CircularProgressIndicator()
} else {
    // Show content
}
```

**iOS:**
```swift
if isLoading {
    ProgressView()
} else {
    // Show content
}
```

### 3. Retry Logic

Implement retry for failed requests:

**Android:**
```kotlin
suspend fun <T> retry(
    times: Int = 3,
    initialDelay: Long = 1000,
    maxDelay: Long = 10000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: Exception) {
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }
    return block()
}
```

### 4. Caching

Cache responses for offline support:

**Android:**
```kotlin
class CachedAccountRepository(
    private val apiService: ApiService,
    private val cache: Cache
) {
    suspend fun getAccounts(): List<Account> {
        return try {
            val accounts = apiService.getAccounts().body()?.data ?: emptyList()
            cache.saveAccounts(accounts)
            accounts
        } catch (e: Exception) {
            cache.getAccounts() // Return cached data
        }
    }
}
```

### 5. Date Formatting

Use ISO 8601 format for dates:

**Android:**
```kotlin
import java.time.format.DateTimeFormatter
import java.time.Instant

fun formatDateForApi(date: Date): String {
    return Instant.ofEpochMilli(date.time)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ISO_INSTANT)
}
```

**iOS:**
```swift
extension Date {
    func toISO8601String() -> String {
        let formatter = ISO8601DateFormatter()
        return formatter.string(from: self)
    }
}
```

## Next Steps

- Review error handling: **[ERROR_HANDLING.md](./ERROR_HANDLING.md)**
- Check endpoint details: **[ENDPOINTS.md](./ENDPOINTS.md)**
- See quick start: **[QUICKSTART.md](./QUICKSTART.md)**

