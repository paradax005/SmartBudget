package com.example.smartbusget2.data.repository.impl

import android.util.Log
import com.example.smartbusget2.data.model.User
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.remote.ApiService
import com.example.smartbusget2.data.remote.CreateAccountRequest
import com.example.smartbusget2.data.remote.LoginRequest
import com.example.smartbusget2.data.remote.RegisterRequest
import com.example.smartbusget2.data.repository.AuthRepository
import com.example.smartbusget2.ui.util.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {
    
    override suspend fun register(email: String, password: String, name: String?): Flow<ApiResponse<User>> = flow {
        emit(ApiResponse.Loading)
        try {
            // API expects fullName, not name
            val fullName = name ?: email.split("@").first() // Use email prefix if name is null
            val request = RegisterRequest(email, password, fullName)
            Log.d("AuthRepository", "Registering user: email=$email, fullName=$fullName")
            val response = apiService.register(request)
            
            Log.d("AuthRepository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("AuthRepository", "Response body: $body")
                
                if (body != null && body.user != null && body.token.isNotEmpty()) {
                    // Save token after successful registration
                    tokenManager.saveToken(body.token)
                    Log.d("AuthRepository", "Registration successful: ${body.user}, token saved")
                    emit(ApiResponse.Success(body.user))
                } else {
                    val errorMessage = body?.message ?: "Registration failed: Invalid response format"
                    Log.e("AuthRepository", "Registration failed: $errorMessage, body: $body")
                    emit(ApiResponse.Error(errorMessage))
                }
            } else {
                // Handle HTTP error responses (4xx, 5xx)
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                
                Log.e("AuthRepository", "HTTP Error ${response.code()}: ${response.message()}, errorBody: $errorBody")
                
                val errorMessage = try {
                    // Parse error response: { "error": "...", "status": 400 }
                    if (errorBody != null) {
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.example.smartbusget2.data.remote.ErrorResponse::class.java)
                        errorResponse.error
                    } else {
                        "Registration failed (HTTP ${response.code()})"
                    }
                } catch (e: Exception) {
                    "Registration failed: ${response.message()} (HTTP ${response.code()})"
                }
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("AuthRepository", "UnknownHostException: ${e.message}")
            emit(ApiResponse.Error("Network error: Cannot reach server. Check your connection and ensure the backend is running."))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("AuthRepository", "SocketTimeoutException: ${e.message}")
            emit(ApiResponse.Error("Network error: Request timeout. Please try again."))
        } catch (e: java.io.IOException) {
            Log.e("AuthRepository", "IOException: ${e.message}", e)
            emit(ApiResponse.Error("Network error: ${e.message ?: "Connection failed"}"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Exception during registration: ${e.message}", e)
            emit(ApiResponse.Error("Error: ${e.message ?: "Unknown error occurred"}"))
        }
    }
    
    override suspend fun login(email: String, password: String): Flow<ApiResponse<String>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.login(LoginRequest(email, password))
            Log.d("AuthRepository", "Login response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("AuthRepository", "Login response body: $body")
                
                if (body != null && body.token.isNotEmpty()) {
                    tokenManager.saveToken(body.token)
                    Log.d("AuthRepository", "Login successful, token saved")
                    emit(ApiResponse.Success(body.token))
                } else {
                    val errorMessage = body?.message ?: "Login failed: Invalid response format"
                    Log.e("AuthRepository", "Login failed: $errorMessage")
                    emit(ApiResponse.Error(errorMessage))
                }
            } else {
                // Handle HTTP error responses
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                
                Log.e("AuthRepository", "HTTP Error ${response.code()}: ${response.message()}, errorBody: $errorBody")
                
                val errorMessage = try {
                    if (errorBody != null) {
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.example.smartbusget2.data.remote.ErrorResponse::class.java)
                        errorResponse.error
                    } else {
                        "Login failed (HTTP ${response.code()})"
                    }
                } catch (e: Exception) {
                    "Login failed: ${response.message()} (HTTP ${response.code()})"
                }
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("AuthRepository", "UnknownHostException: ${e.message}")
            emit(ApiResponse.Error("Network error: Cannot reach server. Check your connection."))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("AuthRepository", "SocketTimeoutException: ${e.message}")
            emit(ApiResponse.Error("Network error: Request timeout. Please try again."))
        } catch (e: java.io.IOException) {
            Log.e("AuthRepository", "IOException: ${e.message}", e)
            emit(ApiResponse.Error("Network error: ${e.message ?: "Connection failed"}"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Exception during login: ${e.message}", e)
            emit(ApiResponse.Error("Error: ${e.message ?: "Unknown error occurred"}"))
        }
    }
    
    override suspend fun getCurrentUser(): Flow<ApiResponse<User>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getCurrentUser()
            Log.d("AuthRepository", "GetCurrentUser response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")
            
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("AuthRepository", "GetCurrentUser response body: $body")
                
                if (body != null && body.user != null) {
                    emit(ApiResponse.Success(body.user))
                } else {
                    val errorMessage = "Failed to get user: Invalid response format"
                    Log.e("AuthRepository", errorMessage)
                    emit(ApiResponse.Error(errorMessage))
                }
            } else {
                // Handle HTTP error responses
                val errorBody = try {
                    response.errorBody()?.string()
                } catch (e: Exception) {
                    null
                }
                
                Log.e("AuthRepository", "HTTP Error ${response.code()}: ${response.message()}, errorBody: $errorBody")
                
                val errorMessage = try {
                    if (errorBody != null) {
                        val gson = com.google.gson.Gson()
                        val errorResponse = gson.fromJson(errorBody, com.example.smartbusget2.data.remote.ErrorResponse::class.java)
                        errorResponse.error
                    } else {
                        "Failed to get user (HTTP ${response.code()})"
                    }
                } catch (e: Exception) {
                    "Failed to get user: ${response.message()} (HTTP ${response.code()})"
                }
                
                // If 401, clear token
                if (response.code() == 401) {
                    tokenManager.clearToken()
                }
                
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: java.net.UnknownHostException) {
            Log.e("AuthRepository", "UnknownHostException: ${e.message}")
            emit(ApiResponse.Error("Network error: Cannot reach server. Check your connection."))
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("AuthRepository", "SocketTimeoutException: ${e.message}")
            emit(ApiResponse.Error("Network error: Request timeout. Please try again."))
        } catch (e: java.io.IOException) {
            Log.e("AuthRepository", "IOException: ${e.message}", e)
            emit(ApiResponse.Error("Network error: ${e.message ?: "Connection failed"}"))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Exception during getCurrentUser: ${e.message}", e)
            emit(ApiResponse.Error("Error: ${e.message ?: "Unknown error occurred"}"))
        }
    }
}

