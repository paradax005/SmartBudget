package com.example.smartbusget2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityTransactionsBinding
import com.example.smartbusget2.domain.usecase.DeleteTransactionUseCase
import com.example.smartbusget2.domain.usecase.GetTransactionsUseCase
import com.example.smartbusget2.ui.adapters.TransactionAdapter
import com.example.smartbusget2.ui.util.Helpers
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TransactionsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTransactionsBinding
    
    @Inject
    lateinit var getTransactionsUseCase: GetTransactionsUseCase
    
    @Inject
    lateinit var deleteTransactionUseCase: DeleteTransactionUseCase
    
    private lateinit var transactionAdapter: TransactionAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        setupRecyclerView()
        setupClickListeners()
        loadTransactions()
    }
    
    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(
            onItemClick = { transaction ->
                // Navigate to edit transaction
                val intent = Intent(this, AddTransactionActivity::class.java)
                intent.putExtra("transaction_id", transaction.actualId())
                startActivity(intent)
            },
            onItemLongClick = { transaction ->
                showDeleteDialog(transaction.actualId())
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = transactionAdapter
    }
    
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddTransactionActivity::class.java))
        }
        
        binding.btnFilter.setOnClickListener {
            showFilterDialog()
        }
        
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadTransactions()
        }
        
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_transactions -> true
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
        binding.bottomNavigation.selectedItemId = R.id.nav_transactions
    }
    
    private var filterStartDate: String? = null
    private var filterEndDate: String? = null
    private var filterCategoryId: String? = null
    private var filterAccountId: String? = null
    private var filterType: String? = null
    
    private fun loadTransactions() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            var transactions = listOf<com.example.smartbusget2.data.model.Transaction>()
            
            getTransactionsUseCase(filterStartDate, filterEndDate, filterCategoryId, filterAccountId).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        transactions = response.data
                        
                        // Apply type filter if set
                        if (!filterType.isNullOrEmpty() && filterType != "All") {
                            transactions = transactions.filter { it.type.lowercase() == filterType!!.lowercase() }
                        }
                        
                        // Sort by date descending
                        transactions = transactions.sortedByDescending { it.date }
                        
                        transactionAdapter.submitList(transactions)
                        updateSummary(transactions)
                        
                        if (transactions.isEmpty()) {
                            binding.layoutEmptyState.root.visibility = View.VISIBLE
                            binding.layoutEmptyState.tvEmptyTitle.text = getString(R.string.no_data)
                            binding.layoutEmptyState.tvEmptyMessage.text = "No transactions found. Tap + to add one."
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
    
    private fun updateSummary(transactions: List<com.example.smartbusget2.data.model.Transaction>) {
        val totalIncome = transactions.filter { it.type.lowercase() == "income" }.sumOf { it.amount }
        val totalExpenses = transactions.filter { it.type.lowercase() == "expense" }.sumOf { it.amount }
        val netAmount = totalIncome - totalExpenses
        
        val currencyFormat = java.text.NumberFormat.getCurrencyInstance(java.util.Locale.getDefault())
        binding.tvSummaryIncome.text = currencyFormat.format(totalIncome)
        binding.tvSummaryExpenses.text = currencyFormat.format(totalExpenses)
        binding.tvSummaryNet.text = currencyFormat.format(netAmount)
        
        // Update net amount color
        binding.tvSummaryNet.setTextColor(
            resources.getColor(
                if (netAmount >= 0) R.color.green else R.color.red,
                null
            )
        )
    }
    
    private fun showFilterDialog() {
        val bottomSheet = com.google.android.material.bottomsheet.BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
        bottomSheet.setContentView(view)
        
        val etStartDate = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etStartDate)
        val etEndDate = view.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etEndDate)
        val chipAll = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipAll)
        val chipIncome = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipIncome)
        val chipExpense = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipExpense)
        val btnApply = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnApplyFilters)
        val btnClear = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnClearFilters)
        
        // Set current filter values
        etStartDate.setText(filterStartDate ?: "")
        etEndDate.setText(filterEndDate ?: "")
        
        when (filterType) {
            "All", null -> chipAll.isChecked = true
            "income" -> chipIncome.isChecked = true
            "expense" -> chipExpense.isChecked = true
        }
        
        // Date pickers
        etStartDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(java.util.Calendar.getInstance().apply { set(year, month, day) }.time)
                    etStartDate.setText(date)
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        etEndDate.setOnClickListener {
            val calendar = java.util.Calendar.getInstance()
            android.app.DatePickerDialog(
                this,
                { _, year, month, day ->
                    val date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(java.util.Calendar.getInstance().apply { set(year, month, day) }.time)
                    etEndDate.setText(date)
                },
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH),
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            ).show()
        }
        
        btnApply.setOnClickListener {
            filterStartDate = etStartDate.text?.toString()?.takeIf { it.isNotEmpty() }
            filterEndDate = etEndDate.text?.toString()?.takeIf { it.isNotEmpty() }
            
            filterType = when {
                chipAll.isChecked -> "All"
                chipIncome.isChecked -> "income"
                chipExpense.isChecked -> "expense"
                else -> null
            }
            
            bottomSheet.dismiss()
            loadTransactions()
        }
        
        btnClear.setOnClickListener {
            filterStartDate = null
            filterEndDate = null
            filterCategoryId = null
            filterAccountId = null
            filterType = null
            bottomSheet.dismiss()
            loadTransactions()
        }
        
        bottomSheet.show()
    }
    
    private fun showDeleteDialog(transactionId: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage("Are you sure you want to delete this transaction?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteTransaction(transactionId)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun deleteTransaction(transactionId: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            deleteTransactionUseCase(transactionId).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        Helpers.showSuccess(binding.root, getString(R.string.success))
                        loadTransactions()
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

