package com.example.smartbusget2.data.repository.impl

import android.util.Log
import com.example.smartbusget2.data.model.Budget
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.remote.ApiService
import com.example.smartbusget2.data.remote.CreateBudgetRequest
import com.example.smartbusget2.data.remote.ErrorResponse
import com.example.smartbusget2.data.remote.UpdateBudgetRequest
import com.example.smartbusget2.data.repository.BudgetRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : BudgetRepository {
    
    override suspend fun getBudgets(month: Int?, year: Int?): Flow<ApiResponse<List<Budget>>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getBudgets(month, year)
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { budgets ->
                    emit(ApiResponse.Success(budgets))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to fetch budgets (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error fetching budgets", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun createBudget(categoryId: String, amount: Double, month: Int, year: Int): Flow<ApiResponse<Budget>> = flow {
        emit(ApiResponse.Loading)
        try {
            val request = CreateBudgetRequest(categoryId, amount, month, year)
            val response = apiService.createBudget(request)
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { budget ->
                    emit(ApiResponse.Success(budget))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to create budget (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error creating budget", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun updateBudget(id: String, amount: Double?, month: Int?, year: Int?): Flow<ApiResponse<Budget>> = flow {
        emit(ApiResponse.Loading)
        try {
            val request = UpdateBudgetRequest(amount, month, year)
            val response = apiService.updateBudget(id, request)
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { budget ->
                    emit(ApiResponse.Success(budget))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to update budget (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error updating budget", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun deleteBudget(id: String): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.deleteBudget(id)
            if (response.isSuccessful) {
                emit(ApiResponse.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to delete budget (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error deleting budget", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun getBudgetStatus(month: Int, year: Int): Flow<ApiResponse<com.example.smartbusget2.data.remote.BudgetStatusResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getBudgetStatus(month, year)
            if (response.isSuccessful) {
                val body = response.body()
                body?.let { status ->
                    emit(ApiResponse.Success(status))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to fetch budget status (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Error fetching budget status", e)
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

