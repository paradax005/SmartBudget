package com.example.smartbusget2.data.repository

import com.example.smartbusget2.data.model.Budget
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.remote.BudgetStatusResponse
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun getBudgets(month: Int? = null, year: Int? = null): Flow<ApiResponse<List<Budget>>>
    suspend fun createBudget(categoryId: String, amount: Double, month: Int, year: Int): Flow<ApiResponse<Budget>>
    suspend fun updateBudget(id: String, amount: Double? = null, month: Int? = null, year: Int? = null): Flow<ApiResponse<Budget>>
    suspend fun deleteBudget(id: String): Flow<ApiResponse<Unit>>
    suspend fun getBudgetStatus(month: Int, year: Int): Flow<ApiResponse<BudgetStatusResponse>>
}

