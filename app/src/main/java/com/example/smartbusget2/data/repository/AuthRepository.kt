package com.example.smartbusget2.data.repository

import com.example.smartbusget2.data.model.User
import com.example.smartbusget2.data.remote.ApiResponse
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(email: String, password: String, name: String?): Flow<ApiResponse<User>>
    suspend fun login(email: String, password: String): Flow<ApiResponse<String>> // Returns token
    suspend fun getCurrentUser(): Flow<ApiResponse<User>>
}

