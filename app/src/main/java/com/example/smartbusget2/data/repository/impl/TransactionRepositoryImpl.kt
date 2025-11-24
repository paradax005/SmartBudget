package com.example.smartbusget2.data.repository.impl

import android.util.Log
import com.example.smartbusget2.data.model.Transaction
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.remote.ApiService
import com.example.smartbusget2.data.remote.CreateTransactionRequest
import com.example.smartbusget2.data.remote.ErrorResponse
import com.example.smartbusget2.data.remote.UpdateTransactionRequest
import com.example.smartbusget2.data.repository.TransactionRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TransactionRepository {
    
    override suspend fun getTransactions(startDate: String?, endDate: String?, categoryId: String?, accountId: String?): Flow<ApiResponse<List<Transaction>>> = flow {
        emit(ApiResponse.Loading)
        try {
            // API uses startDate and endDate
            val response = apiService.getTransactions(startDate, endDate, categoryId, accountId)
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { transactions ->
                    emit(ApiResponse.Success(transactions))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to fetch transactions (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error fetching transactions", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun createTransaction(amount: Double, description: String, date: String, type: String, accountId: String, categoryId: String?): Flow<ApiResponse<Transaction>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.createTransaction(CreateTransactionRequest(amount, description, date, type, accountId, categoryId))
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { dto ->
                    // Map DTO to Domain Model
                    val transaction = Transaction(
                        id = dto.id,
                        _id = dto._id,
                        amount = dto.amount,
                        description = dto.description,
                        date = dto.date,
                        type = dto.type,
                        // We don't have the full account/category objects here, but that's fine for creation response
                        // The UI typically refreshes the list or just closes the activity
                        accountIdString = dto.accountId,
                        categoryIdString = dto.categoryId,
                        account = null, 
                        category = null,
                        userId = dto.userId,
                        createdAt = dto.createdAt,
                        updatedAt = dto.updatedAt
                    )
                    emit(ApiResponse.Success(transaction))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to create transaction (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error creating transaction", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun updateTransaction(id: String, amount: Double?, description: String?, date: String?, type: String?, accountId: String?, categoryId: String?): Flow<ApiResponse<Transaction>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.updateTransaction(id, UpdateTransactionRequest(amount, description, date, type, accountId, categoryId))
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { dto ->
                    // Map DTO to Domain Model
                    val transaction = Transaction(
                        id = dto.id,
                        _id = dto._id,
                        amount = dto.amount,
                        description = dto.description,
                        date = dto.date,
                        type = dto.type,
                        accountIdString = dto.accountId,
                        categoryIdString = dto.categoryId,
                        account = null,
                        category = null,
                        userId = dto.userId,
                        createdAt = dto.createdAt,
                        updatedAt = dto.updatedAt
                    )
                    emit(ApiResponse.Success(transaction))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to update transaction (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error updating transaction", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun deleteTransaction(id: String): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.deleteTransaction(id)
            if (response.isSuccessful) {
                emit(ApiResponse.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to delete transaction (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error deleting transaction", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun getMonthlyStats(month: Int, year: Int): Flow<ApiResponse<com.example.smartbusget2.data.remote.MonthlyStatsResponse>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getMonthlyStats(month, year)
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { dto ->
                    // Map DTO to Domain Model
                    val stats = com.example.smartbusget2.data.remote.MonthlyStatsResponse(
                        month = dto.month,
                        year = dto.year,
                        totalIncome = dto.totalIncome,
                        totalExpenses = dto.totalExpense, // Map totalExpense -> totalExpenses
                        netAmount = dto.netAmount,
                        transactionCount = (dto.incomeCount ?: 0) + (dto.expenseCount ?: 0),
                        incomeByCategory = dto.byCategory?.income?.map { 
                            com.example.smartbusget2.data.remote.CategoryStats(
                                categoryId = it.categoryId,
                                categoryName = it.categoryName,
                                totalAmount = it.total,
                                transactionCount = it.count
                            )
                        } ?: emptyList(),
                        expensesByCategory = dto.byCategory?.expense?.map {
                            com.example.smartbusget2.data.remote.CategoryStats(
                                categoryId = it.categoryId,
                                categoryName = it.categoryName,
                                totalAmount = it.total,
                                transactionCount = it.count
                            )
                        } ?: emptyList()
                    )
                    emit(ApiResponse.Success(stats))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to fetch monthly stats (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("TransactionRepository", "Error fetching monthly stats", e)
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

