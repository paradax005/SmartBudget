package com.example.smartbusget2.data.repository

import com.example.smartbusget2.data.model.Transaction
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.remote.MonthlyStatsResponse
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun getTransactions(startDate: String? = null, endDate: String? = null, categoryId: String? = null, accountId: String? = null): Flow<ApiResponse<List<Transaction>>>
    suspend fun createTransaction(amount: Double, description: String, date: String, type: String, accountId: String, categoryId: String?): Flow<ApiResponse<Transaction>>
    suspend fun updateTransaction(id: String, amount: Double?, description: String?, date: String?, type: String?, accountId: String?, categoryId: String?): Flow<ApiResponse<Transaction>>
    suspend fun deleteTransaction(id: String): Flow<ApiResponse<Unit>>
    suspend fun getMonthlyStats(month: Int, year: Int): Flow<ApiResponse<MonthlyStatsResponse>>
}

