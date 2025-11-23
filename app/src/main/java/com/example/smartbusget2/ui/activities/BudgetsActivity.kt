package com.example.smartbusget2.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityBudgetsBinding
import com.example.smartbusget2.domain.usecase.GetBudgetsUseCase
import com.example.smartbusget2.domain.usecase.GetBudgetStatusUseCase
import com.example.smartbusget2.ui.adapters.BudgetAdapter
import com.example.smartbusget2.ui.util.Helpers
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BudgetsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityBudgetsBinding
    
    @Inject
    lateinit var getBudgetsUseCase: GetBudgetsUseCase
    
    @Inject
    lateinit var getBudgetStatusUseCase: GetBudgetStatusUseCase

    @Inject
    lateinit var deleteBudgetUseCase: com.example.smartbusget2.domain.usecase.DeleteBudgetUseCase
    
    private lateinit var budgetAdapter: BudgetAdapter
    
    private var selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYear = Calendar.getInstance().get(Calendar.YEAR)
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        setupRecyclerView()
        setupClickListeners()
        updateMonthYearDisplay()
        loadBudgets()
    }
    
    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter(
            onItemClick = { budget ->
                val intent = Intent(this, AddBudgetActivity::class.java)
                intent.putExtra("budget_id", budget.actualId())
                intent.putExtra("amount", budget.amount)
                intent.putExtra("category_id", budget.categoryId)
                intent.putExtra("month", budget.month)
                intent.putExtra("year", budget.year)
                startActivity(intent)
            },
            onItemLongClick = { budget ->
                showDeleteDialog(budget.actualId())
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = budgetAdapter
    }
    
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddBudgetActivity::class.java))
        }
        
        binding.btnSelectMonth.setOnClickListener {
            showMonthYearPicker()
        }
        
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadBudgets()
        }
        
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
                R.id.nav_budgets -> true
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
        binding.bottomNavigation.selectedItemId = R.id.nav_budgets
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
                loadBudgets()
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
    
    private fun loadBudgets() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            // Load budget status for summary
            getBudgetStatusUseCase(selectedMonth, selectedYear).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        val budgetStatus = response.data
                        val exceededCount = budgetStatus.budgets.count { it.isExceeded }
                        binding.tvExceededCount.text = "$exceededCount exceeded"
                        
                        val totalBudgeted = budgetStatus.budgets.sumOf { it.amount }
                        val totalSpent = budgetStatus.budgets.sumOf { it.spent }
                        
                        binding.tvTotalBudgeted.text = currencyFormat.format(totalBudgeted as Double)
                        binding.tvTotalSpent.text = currencyFormat.format(totalSpent as Double)
                    }
                    else -> {}
                }
            }
            
            // Load budgets
            getBudgetsUseCase(selectedMonth, selectedYear).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        budgetAdapter.submitList(response.data)
                        
                        if (response.data.isEmpty()) {
                            binding.layoutEmptyState.root.visibility = View.VISIBLE
                            binding.layoutEmptyState.tvEmptyTitle.text = "No Budgets"
                            binding.layoutEmptyState.tvEmptyMessage.text = "Set up a budget to track your spending."
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            binding.layoutEmptyState.root.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                        }
                        
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
    
    private fun showDeleteDialog(budgetId: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage("Are you sure you want to delete this budget?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteBudget(budgetId)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun deleteBudget(budgetId: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            deleteBudgetUseCase(budgetId).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        Helpers.showSuccess(binding.root, getString(R.string.success))
                        loadBudgets()
                    }
                    is com.example.smartbusget2.data.remote.ApiResponse.Error -> {
                        Helpers.showError(binding.root, response.message)
                        binding.progressBar.visibility = View.GONE
                    }
                    else -> {}
                }
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

