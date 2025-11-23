package com.example.smartbusget2.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartbusget2.domain.usecase.GetBudgetStatusUseCase
import com.example.smartbusget2.ui.util.NotificationHelper
import com.example.smartbusget2.data.remote.ApiResponse
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.util.Calendar

class BudgetCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BudgetCheckWorkerEntryPoint {
        fun getBudgetStatusUseCase(): GetBudgetStatusUseCase
    }

    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(applicationContext, BudgetCheckWorkerEntryPoint::class.java)
        val getBudgetStatusUseCase = entryPoint.getBudgetStatusUseCase()

        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)

        try {
            getBudgetStatusUseCase(month, year).collect { response ->
                if (response is ApiResponse.Success) {
                    val exceededBudgets = response.data.budgets.filter { it.isExceeded }
                    if (exceededBudgets.isNotEmpty()) {
                        val notificationHelper = NotificationHelper(applicationContext)
                        val count = exceededBudgets.size
                        val title = "Budget Alert"
                        val message = if (count == 1) {
                            "You have exceeded your budget for ${exceededBudgets[0].category.name}"
                        } else {
                            "You have exceeded $count budgets"
                        }
                        notificationHelper.showBudgetAlert(title, message)
                    }
                }
            }
            return Result.success()
        } catch (e: Exception) {
            return Result.failure()
        }
    }
}
