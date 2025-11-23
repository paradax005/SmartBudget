package com.example.smartbusget2

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.smartbusget2.worker.BudgetCheckWorker
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class SmartBudgetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupWorker()
    }

    private fun setupWorker() {
        val workRequest = PeriodicWorkRequestBuilder<BudgetCheckWorker>(6, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "BudgetCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}

