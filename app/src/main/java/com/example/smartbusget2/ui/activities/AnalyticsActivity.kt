package com.example.smartbusget2.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityAnalyticsBinding
import com.example.smartbusget2.domain.usecase.GetMonthlyStatsUseCase
import com.example.smartbusget2.domain.usecase.GetTransactionsUseCase
import com.example.smartbusget2.ui.util.Helpers
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding

    @Inject
    lateinit var getMonthlyStatsUseCase: GetMonthlyStatsUseCase

    @Inject
    lateinit var getTransactionsUseCase: GetTransactionsUseCase

    private var selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupClickListeners()
        updateMonthYearDisplay()
        loadData()
    }

    private fun setupClickListeners() {
        binding.btnSelectMonth.setOnClickListener {
            showMonthYearPicker()
        }

        // SwipeRefreshLayout not implemented in layout yet
        // binding.swipeRefreshLayout?.setOnRefreshListener {
        //     loadData()
        // }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_transactions -> {
                    startActivity(Intent(this, TransactionsActivity::class.java))
                    true
                }
                R.id.nav_budgets -> {
                    startActivity(Intent(this, BudgetsActivity::class.java))
                    true
                }
                R.id.nav_accounts -> {
                    startActivity(Intent(this, AccountsActivity::class.java))
                    true
                }
                R.id.nav_analytics -> true
                else -> false
            }
        }
        binding.bottomNavigation.selectedItemId = R.id.nav_analytics
    }

    private fun showMonthYearPicker() {
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth - 1, 1)

        DatePickerDialog(
            this,
            { _, year, month, _ ->
                selectedYear = year
                selectedMonth = month + 1
                updateMonthYearDisplay()
                loadData()
            },
            selectedYear,
            selectedMonth - 1,
            1
        ).show()
    }

    private fun updateMonthYearDisplay() {
        val calendar = Calendar.getInstance()
        calendar.set(selectedYear, selectedMonth - 1, 1)
        binding.tvMonthYear.text = monthYearFormat.format(calendar.time)
    }

    private fun loadData() {
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            // Load monthly statistics
            launch {
                getMonthlyStatsUseCase(selectedMonth, selectedYear).collect { response ->
                    when (response) {
                        is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                            val stats = response.data
                            binding.tvTotalIncome.text = currencyFormat.format(stats.totalIncome)
                            binding.tvTotalExpense.text = currencyFormat.format(stats.totalExpenses)
                            binding.tvNetSavings.text = currencyFormat.format(stats.netAmount)
                            
                            // Calculate Avg Daily
                            val daysInMonth = getDaysInMonth(selectedMonth, selectedYear)
                            val avgDaily = if (daysInMonth > 0) stats.totalExpenses / daysInMonth else 0.0
                            binding.tvAvgDaily.text = currencyFormat.format(avgDaily)
                            
                            // Hide progress bar when stats are loaded (at least partial data is shown)
                            binding.progressBar.visibility = View.GONE
                        }
                        is com.example.smartbusget2.data.remote.ApiResponse.Error -> {
                            Helpers.showError(binding.root, response.message)
                            binding.progressBar.visibility = View.GONE
                        }
                        else -> {}
                    }
                }
            }

            // Load transactions for chart
            launch {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.set(selectedYear, selectedMonth - 1, 1)
                val startDate = dateFormat.format(calendar.time)
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                val endDate = dateFormat.format(calendar.time)

                getTransactionsUseCase(startDate, endDate, null).collect { response ->
                    when (response) {
                        is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                            updatePieChart(response.data)
                            // Also hide progress bar here in case this finishes first or stats failed
                            binding.progressBar.visibility = View.GONE
                        }
                        is com.example.smartbusget2.data.remote.ApiResponse.Error -> {
                            // Don't show error again if stats already showed it, or maybe log it
                            binding.progressBar.visibility = View.GONE
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun updatePieChart(transactions: List<com.example.smartbusget2.data.model.Transaction>) {
        val expenseTransactions = transactions.filter { it.type.lowercase() == "expense" }
        val categoryMap = expenseTransactions.groupBy { it.category?.name ?: "Other" }

        val entries = categoryMap.map { (category, trans) ->
            PieEntry(trans.sumOf { it.amount }.toFloat(), category)
        }

        if (entries.isNotEmpty()) {
            val dataSet = PieDataSet(entries, "").apply {
                colors = listOf(
                    resources.getColor(R.color.indigo_600, null),
                    resources.getColor(R.color.violet_600, null),
                    resources.getColor(R.color.fuchsia_400, null),
                    resources.getColor(R.color.cyan_500, null),
                    resources.getColor(R.color.emerald_500, null)
                )
                valueTextColor = resources.getColor(R.color.white, null)
                valueTextSize = 12f
            }

            val pieData = PieData(dataSet)
            binding.pieChart.data = pieData
            binding.pieChart.description.isEnabled = false
            binding.pieChart.legend.isEnabled = true
            binding.pieChart.legend.textColor = resources.getColor(R.color.text_secondary_dark, null)
            binding.pieChart.setHoleColor(resources.getColor(R.color.surface_dark, null))
            binding.pieChart.invalidate()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
