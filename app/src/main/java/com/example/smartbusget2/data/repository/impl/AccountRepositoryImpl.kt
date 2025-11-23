package com.example.smartbusget2.data.repository.impl

import android.util.Log
import com.example.smartbusget2.data.model.Account
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.remote.ApiService
import com.example.smartbusget2.data.remote.CreateAccountRequest
import com.example.smartbusget2.data.remote.ErrorResponse
import com.example.smartbusget2.data.remote.UpdateAccountRequest
import com.example.smartbusget2.data.repository.AccountRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AccountRepository {
    
    override suspend fun getAccounts(): Flow<ApiResponse<List<Account>>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getAccounts()
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { accounts ->
                    emit(ApiResponse.Success(accounts))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to fetch accounts (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AccountRepository", "Error fetching accounts", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun createAccount(name: String, type: String, balance: Double): Flow<ApiResponse<Account>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.createAccount(CreateAccountRequest(name, type, balance))
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { account ->
                    emit(ApiResponse.Success(account))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to create account (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AccountRepository", "Error creating account", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun updateAccount(id: String, name: String?, type: String?, balance: Double?): Flow<ApiResponse<Account>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.updateAccount(id, UpdateAccountRequest(name, type, balance))
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { account ->
                    emit(ApiResponse.Success(account))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to update account (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AccountRepository", "Error updating account", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun deleteAccount(id: String): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.deleteAccount(id)
            if (response.isSuccessful) {
                emit(ApiResponse.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to delete account (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("AccountRepository", "Error deleting account", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    private fun parseError(errorBody: String?): String? {
        return try {
            if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.error
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

