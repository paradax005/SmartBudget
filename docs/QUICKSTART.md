# Quick Start Guide

This guide will help you quickly integrate the SmartBudget API into your mobile app with minimal setup.

## Prerequisites

- Backend server running (see main README.md for setup)
- Mobile development environment (Android Studio / Xcode)
- HTTP client library (OkHttp/Retrofit for Android, URLSession/Alamofire for iOS)

## Step 1: Configure Base URL

### Android (Kotlin/Java)

```kotlin
// Constants.kt or Config.kt
object ApiConfig {
    // For Android Emulator
    const val BASE_URL = "http://10.0.2.2:3000/api"
    
    // For Physical Device (use your computer's IP)
    // const val BASE_URL = "http://192.168.1.100:3000/api"
}
```

### iOS (Swift)

```swift
// ApiConfig.swift
struct ApiConfig {
    // For iOS Simulator
    static let baseURL = "http://localhost:3000/api"
    
    // For Physical Device (use your computer's IP)
    // static let baseURL = "http://192.168.1.100:3000/api"
}
```

## Step 2: Register a User

### Android Example (Kotlin with Retrofit)

```kotlin
// ApiService.kt
interface ApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}

// RegisterRequest.kt
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

// RegisterResponse.kt
data class RegisterResponse(
    val message: String,
    val token: String,
    val user: User
)

// Usage
val request = RegisterRequest(
    email = "user@example.com",
    password = "password123",
    fullName = "John Doe"
)

val response = apiService.register(request)
if (response.isSuccessful) {
    val token = response.body()?.token
    // Save token securely
}
```

### iOS Example (Swift with URLSession)

```swift
// ApiService.swift
struct ApiService {
    static let baseURL = ApiConfig.baseURL
    
    static func register(email: String, password: String, fullName: String) async throws -> RegisterResponse {
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
        
        let (data, _) = try await URLSession.shared.data(for: request)
        return try JSONDecoder().decode(RegisterResponse.self, from: data)
    }
}

// Models
struct RegisterResponse: Codable {
    let message: String
    let token: String
    let user: User
}

struct User: Codable {
    let id: String
    let email: String
    let fullName: String
}

// Usage
do {
    let response = try await ApiService.register(
        email: "user@example.com",
        password: "password123",
        fullName: "John Doe"
    )
    // Save token securely
    UserDefaults.standard.set(response.token, forKey: "auth_token")
} catch {
    print("Error: \(error)")
}
```

## Step 3: Login

### Android Example

```kotlin
// ApiService.kt
@POST("auth/login")
suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

data class LoginRequest(
    val email: String,
    val password: String
)

// Usage
val request = LoginRequest(
    email = "user@example.com",
    password = "password123"
)
val response = apiService.login(request)
```

### iOS Example

```swift
// ApiService.swift
static func login(email: String, password: String) async throws -> LoginResponse {
    let url = URL(string: "\(baseURL)/auth/login")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    
    let body: [String: Any] = [
        "email": email,
        "password": password
    ]
    request.httpBody = try JSONSerialization.data(withJSONObject: body)
    
    let (data, _) = try await URLSession.shared.data(for: request)
    return try JSONDecoder().decode(LoginResponse.self, from: data)
}
```

## Step 4: Make Authenticated Requests

### Android Example (with Interceptor)

```kotlin
// OkHttpClient setup with auth interceptor
val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${getStoredToken()}")
            .build()
        chain.proceed(request)
    }
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl(ApiConfig.BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

// Get accounts
@GET("accounts")
suspend fun getAccounts(): Response<AccountsResponse>
```

### iOS Example (with Auth Header)

```swift
// ApiService.swift
static func getAccounts() async throws -> AccountsResponse {
    let url = URL(string: "\(baseURL)/accounts")!
    var request = URLRequest(url: url)
    request.httpMethod = "GET"
    
    // Add auth token
    if let token = UserDefaults.standard.string(forKey: "auth_token") {
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
    }
    
    let (data, _) = try await URLSession.shared.data(for: request)
    return try JSONDecoder().decode(AccountsResponse.self, from: data)
}
```

## Step 5: Create a Transaction

### Android Example

```kotlin
@POST("transactions")
suspend fun createTransaction(@Body request: CreateTransactionRequest): Response<TransactionResponse>

data class CreateTransactionRequest(
    val accountId: String,
    val categoryId: String,
    val amount: Double,
    val type: String, // "expense" or "income"
    val description: String,
    val date: String // ISO 8601 format: "2024-01-15T10:30:00Z"
)

// Usage
val request = CreateTransactionRequest(
    accountId = "account_id_here",
    categoryId = "category_id_here",
    amount = 100.50,
    type = "expense",
    description = "Grocery shopping",
    date = "2024-01-15T10:30:00Z"
)
val response = apiService.createTransaction(request)
```

### iOS Example

```swift
static func createTransaction(
    accountId: String,
    categoryId: String,
    amount: Double,
    type: String,
    description: String,
    date: String
) async throws -> TransactionResponse {
    let url = URL(string: "\(baseURL)/transactions")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    
    if let token = UserDefaults.standard.string(forKey: "auth_token") {
        request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
    }
    
    let body: [String: Any] = [
        "accountId": accountId,
        "categoryId": categoryId,
        "amount": amount,
        "type": type,
        "description": description,
        "date": date
    ]
    request.httpBody = try JSONSerialization.data(withJSONObject: body)
    
    let (data, _) = try await URLSession.shared.data(for: request)
    return try JSONDecoder().decode(TransactionResponse.self, from: data)
}
```

## Complete Working Example

### Android (Kotlin)

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var apiService: ApiService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup Retrofit
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = getStoredToken()
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }
            .build()
        
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/api")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        apiService = retrofit.create(ApiService::class.java)
        
        // Login
        lifecycleScope.launch {
            try {
                val response = apiService.login(
                    LoginRequest("user@example.com", "password123")
                )
                if (response.isSuccessful) {
                    saveToken(response.body()?.token ?: "")
                    loadAccounts()
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            }
        }
    }
    
    private suspend fun loadAccounts() {
        try {
            val response = apiService.getAccounts()
            if (response.isSuccessful) {
                val accounts = response.body()?.data
                // Update UI with accounts
            }
        } catch (e: Exception) {
            Log.e("API", "Error: ${e.message}")
        }
    }
}
```

### iOS (Swift)

```swift
class ViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        Task {
            do {
                // Login
                let loginResponse = try await ApiService.login(
                    email: "user@example.com",
                    password: "password123"
                )
                UserDefaults.standard.set(loginResponse.token, forKey: "auth_token")
                
                // Load accounts
                let accountsResponse = try await ApiService.getAccounts()
                // Update UI with accounts
            } catch {
                print("Error: \(error)")
            }
        }
    }
}
```

## Next Steps

- Read **[AUTHENTICATION.md](./AUTHENTICATION.md)** for detailed auth flow
- Check **[ENDPOINTS.md](./ENDPOINTS.md)** for all available endpoints
- Review **[ERROR_HANDLING.md](./ERROR_HANDLING.md)** for error handling strategies
- See **[INTEGRATION.md](./INTEGRATION.md)** for advanced integration patterns

