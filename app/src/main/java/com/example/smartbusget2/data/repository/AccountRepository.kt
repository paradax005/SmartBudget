package com.example.smartbusget2.data.repository

import com.example.smartbusget2.data.model.Account
import com.example.smartbusget2.data.remote.ApiResponse
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    suspend fun getAccounts(): Flow<ApiResponse<List<Account>>>
    suspend fun createAccount(name: String, type: String, balance: Double): Flow<ApiResponse<Account>>
    suspend fun updateAccount(id: String, name: String?, type: String?, balance: Double?): Flow<ApiResponse<Account>>
    suspend fun deleteAccount(id: String): Flow<ApiResponse<Unit>>
}

