package com.example.smartbusget2.domain.usecase

import com.example.smartbusget2.data.model.Transaction
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(startDate: String? = null, endDate: String? = null, categoryId: String? = null, accountId: String? = null): Flow<ApiResponse<List<Transaction>>> {
        return transactionRepository.getTransactions(startDate, endDate, categoryId, accountId)
    }
}

class GetMonthlyStatsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(month: Int, year: Int): Flow<ApiResponse<com.example.smartbusget2.data.remote.MonthlyStatsResponse>> {
        return transactionRepository.getMonthlyStats(month, year)
    }
}

class CreateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(amount: Double, description: String, date: String, type: String, accountId: String, categoryId: String?): Flow<ApiResponse<Transaction>> {
        return transactionRepository.createTransaction(amount, description, date, type, accountId, categoryId)
    }
}

class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(id: String, amount: Double?, description: String?, date: String?, type: String?, accountId: String?, categoryId: String?): Flow<ApiResponse<Transaction>> {
        return transactionRepository.updateTransaction(id, amount, description, date, type, accountId, categoryId)
    }
}

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(id: String): Flow<ApiResponse<Unit>> {
        return transactionRepository.deleteTransaction(id)
    }
}

