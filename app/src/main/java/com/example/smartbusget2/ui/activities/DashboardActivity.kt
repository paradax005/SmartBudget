package com.example.smartbusget2.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityDashboardBinding
import com.example.smartbusget2.domain.usecase.GetAccountsUseCase
import com.example.smartbusget2.domain.usecase.GetTransactionsUseCase
import com.example.smartbusget2.domain.usecase.GetMonthlyStatsUseCase
import com.example.smartbusget2.domain.usecase.GetBudgetStatusUseCase
import com.example.smartbusget2.ui.adapters.TransactionAdapter
import com.example.smartbusget2.ui.adapters.BudgetAlertAdapter
import com.example.smartbusget2.ui.util.Helpers
import com.example.smartbusget2.ui.util.TokenManager
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
class DashboardActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityDashboardBinding
    
    @Inject
    lateinit var getAccountsUseCase: GetAccountsUseCase
    
    @Inject
    lateinit var getTransactionsUseCase: GetTransactionsUseCase
    
    @Inject
    lateinit var getMonthlyStatsUseCase: GetMonthlyStatsUseCase
    
    @Inject
    lateinit var getBudgetStatusUseCase: GetBudgetStatusUseCase
    
    @Inject
    lateinit var tokenManager: TokenManager
    
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var budgetAlertAdapter: BudgetAlertAdapter
    
    private var selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        setupRecyclerViews()
        setupClickListeners()
        setupSwipeRefresh()
        updateMonthYearDisplay()
        loadData()
    }
    
    private fun setupRecyclerViews() {
        transactionAdapter = TransactionAdapter(
            onItemClick = { transaction ->
                // Navigate to transaction details or edit
            },
            onItemLongClick = { transaction ->
                // Handle long click if needed
            }
        )
        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTransactions.adapter = transactionAdapter
        
        budgetAlertAdapter = BudgetAlertAdapter { budgetStatus ->
            startActivity(Intent(this, BudgetsActivity::class.java))
        }
        binding.recyclerViewBudgetAlerts.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewBudgetAlerts.adapter = budgetAlertAdapter
    }
    
    private fun setupClickListeners() {
        binding.fabAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
        
        binding.btnSelectMonth.setOnClickListener {
            showMonthYearPicker()
        }
        
        binding.btnViewAllTransactions.setOnClickListener {
            startActivity(Intent(this, TransactionsActivity::class.java))
        }
        
        binding.btnViewAllCategories.setOnClickListener {
            startActivity(Intent(this, CategoriesActivity::class.java))
        }
        
        binding.btnViewAllBudgets.setOnClickListener {
            startActivity(Intent(this, BudgetsActivity::class.java))
        }
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true
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
                R.id.nav_analytics -> {
                    startActivity(Intent(this, AnalyticsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        binding.bottomNavigation.selectedItemId = R.id.nav_dashboard
    }
    
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadData()
        }
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
        binding.swipeRefreshLayout.isRefreshing = true
        
        lifecycleScope.launch {
            // Load accounts to calculate total balance
            getAccountsUseCase().collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        val totalBalance = response.data.sumOf { it.balance }
                        binding.tvTotalBalance.text = currencyFormat.format(totalBalance)
                    }
                    else -> {}
                }
            }
            
            // Load monthly statistics
            getMonthlyStatsUseCase(selectedMonth, selectedYear).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        val stats = response.data
                        binding.tvTotalIncome.text = currencyFormat.format(stats.totalIncome)
                        binding.tvTotalExpenses.text = currencyFormat.format(stats.totalExpenses)
                        binding.tvNetAmount.text = currencyFormat.format(stats.netAmount)
                        
                        // Update net amount color
                        binding.tvNetAmount.setTextColor(
                            resources.getColor(
                                if (stats.netAmount >= 0) R.color.emerald_500 else R.color.red_500,
                                null
                            )
                        )
                    }
                    else -> {}
                }
            }
            
            // Load budget status
            getBudgetStatusUseCase(selectedMonth, selectedYear).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        val budgetStatus = response.data
                        val exceededCount = budgetStatus.budgets.count { it.isExceeded }
                        binding.tvBudgetStatus.text = "$exceededCount exceeded"
                        
                        // Show budget alerts if any
                        val exceededBudgets = budgetStatus.budgets.filter { it.isExceeded }
                        if (exceededBudgets.isNotEmpty()) {
                            binding.cardBudgetAlerts.visibility = View.VISIBLE
                            budgetAlertAdapter.submitList(exceededBudgets)
                        } else {
                            binding.cardBudgetAlerts.visibility = View.GONE
                        }
                    }
                    else -> {}
                }
            }
            
            // Load recent transactions for current month
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.set(selectedYear, selectedMonth - 1, 1)
            val startDate = dateFormat.format(calendar.time)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            val endDate = dateFormat.format(calendar.time)
            
            getTransactionsUseCase(startDate, endDate, null).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        val transactions = response.data.sortedByDescending { it.date }.take(5)
                        transactionAdapter.submitList(transactions)
                        updatePieChart(response.data)
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    is com.example.smartbusget2.data.remote.ApiResponse.Error -> {
                        Helpers.showError(binding.root, response.message)
                        binding.progressBar.visibility = View.GONE
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    else -> {}
                }
            }
        }
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
                    resources.getColor(R.color.cyan_500, null)
                )
            }
            
            val pieData = PieData(dataSet)
            binding.pieChart.data = pieData
            binding.pieChart.description.isEnabled = false
            binding.pieChart.legend.isEnabled = true
            binding.pieChart.invalidate()
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_transactions -> {
                startActivity(Intent(this, TransactionsActivity::class.java))
                true
            }
            R.id.menu_accounts -> {
                startActivity(Intent(this, AccountsActivity::class.java))
                true
            }
            R.id.menu_budgets -> {
                startActivity(Intent(this, BudgetsActivity::class.java))
                true
            }
            R.id.menu_categories -> {
                startActivity(Intent(this, CategoriesActivity::class.java))
                true
            }
            R.id.menu_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun logout() {
        lifecycleScope.launch {
            tokenManager.clearToken()
            startActivity(Intent(this@DashboardActivity, LoginActivity::class.java))
            finish()
        }
    }
}

