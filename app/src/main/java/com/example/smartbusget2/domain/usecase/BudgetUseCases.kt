package com.example.smartbusget2.domain.usecase

import com.example.smartbusget2.data.model.Budget
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetsUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(month: Int? = null, year: Int? = null): Flow<ApiResponse<List<Budget>>> {
        return budgetRepository.getBudgets(month, year)
    }
}

class GetBudgetStatusUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(month: Int, year: Int): Flow<ApiResponse<com.example.smartbusget2.data.remote.BudgetStatusResponse>> {
        return budgetRepository.getBudgetStatus(month, year)
    }
}

class CreateBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(categoryId: String, amount: Double, month: Int, year: Int): Flow<ApiResponse<Budget>> {
        return budgetRepository.createBudget(categoryId, amount, month, year)
    }
}

class UpdateBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(id: String, amount: Double? = null, month: Int? = null, year: Int? = null): Flow<ApiResponse<Budget>> {
        return budgetRepository.updateBudget(id, amount, month, year)
    }
}

class DeleteBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(id: String): Flow<ApiResponse<Unit>> {
        return budgetRepository.deleteBudget(id)
    }
}

