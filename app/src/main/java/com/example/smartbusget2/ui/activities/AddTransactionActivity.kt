package com.example.smartbusget2.ui.activities

import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.lifecycle.lifecycleScope
import com.example.smartbusget2.R
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.databinding.ActivityAddTransactionBinding
import com.example.smartbusget2.domain.usecase.CreateTransactionUseCase
import com.example.smartbusget2.domain.usecase.GetAccountsUseCase
import com.example.smartbusget2.domain.usecase.GetCategoriesUseCase
import com.example.smartbusget2.domain.usecase.UpdateTransactionUseCase
import com.example.smartbusget2.ui.util.Helpers
import com.example.smartbusget2.ui.util.getTextString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddTransactionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddTransactionBinding
    
    @Inject
    lateinit var createTransactionUseCase: CreateTransactionUseCase
    
    @Inject
    lateinit var updateTransactionUseCase: UpdateTransactionUseCase
    
    @Inject
    lateinit var getAccountsUseCase: GetAccountsUseCase
    
    @Inject
    lateinit var getCategoriesUseCase: GetCategoriesUseCase
    
    private var transactionId: String? = null
    private val accounts = mutableListOf<com.example.smartbusget2.data.model.Account>()
    private val categories = mutableListOf<com.example.smartbusget2.data.model.Category>()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private var selectedAccount: com.example.smartbusget2.data.model.Account? = null
    private var selectedTransactionType: String = "expense" // Default to expense
    private var selectedDate: Calendar = Calendar.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        
        transactionId = intent.getStringExtra("transaction_id")
        
        setupUI()
        setupClickListeners()
        loadAccountsAndCategories()
        
        if (transactionId != null) {
            binding.toolbar.title = getString(R.string.edit_transaction)
            // Load transaction logic here if needed
        } else {
            updateDateDisplay()
            updateTransactionTypeUI()
        }
    }
    
    private fun setupUI() {
        // Setup Quick Amount Buttons
        val quickAmounts = listOf(5, 10, 20, 50, 100)
        val container = binding.layoutQuickAmounts.quickAmountContainer
        
        quickAmounts.forEach { amount ->
            val button = TextView(this).apply {
                text = "$$amount"
                setTextAppearance(R.style.Widget_SmartBudget_QuickAmountButton)
                setBackgroundResource(R.drawable.bg_quick_amount_button)
                setPadding(32, 16, 32, 16)
                layoutParams = android.widget.LinearLayout.LayoutParams(
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
                    android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 16
                }
                setOnClickListener {
                    binding.layoutAmount.etAmount.setText(amount.toString())
                    binding.layoutAmount.etAmount.setSelection(amount.toString().length)
                }
            }
            container.addView(button)
        }
    }
    
    private fun setupClickListeners() {
        // Toggle Switch
        binding.layoutToggle.btnExpense.setOnClickListener {
            selectedTransactionType = "expense"
            updateTransactionTypeUI()
        }
        
        binding.layoutToggle.btnIncome.setOnClickListener {
            selectedTransactionType = "income"
            updateTransactionTypeUI()
        }
        
        // Selectors
        binding.cardDate.setOnClickListener { showDatePicker() }
        binding.cardAccount.setOnClickListener { showAccountDialog() }
        binding.cardCategory.setOnClickListener { showCategoryDialog() }
        
        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
    }
    
    private fun updateTransactionTypeUI() {
        val isExpense = selectedTransactionType == "expense"
        val activeColor = ContextCompat.getColor(this, if (isExpense) R.color.error_red else R.color.success_green)
        val inactiveColor = ContextCompat.getColor(this, R.color.text_secondary)
        val activeBg = ContextCompat.getDrawable(this, R.drawable.bg_toggle_active)
        val inactiveBg = ContextCompat.getDrawable(this, R.drawable.bg_toggle_inactive)
        
        // Update Toggle Buttons
        binding.layoutToggle.btnExpense.apply {
            setTextColor(if (isExpense) ContextCompat.getColor(context, R.color.white) else inactiveColor)
            background = if (isExpense) activeBg else inactiveBg
            backgroundTintList = if (isExpense) ColorStateList.valueOf(ContextCompat.getColor(context, R.color.error_red)) else null
        }
        
        binding.layoutToggle.btnIncome.apply {
            setTextColor(if (!isExpense) ContextCompat.getColor(context, R.color.white) else inactiveColor)
            background = if (!isExpense) activeBg else inactiveBg
            backgroundTintList = if (!isExpense) ColorStateList.valueOf(ContextCompat.getColor(context, R.color.success_green)) else null
        }
        
        // Update Amount Color
        binding.layoutAmount.etAmount.setTextColor(activeColor)
        
        // Filter categories if already loaded
        if (categories.isNotEmpty()) {
            // Optional: Clear category if type changes?
            // binding.tvSelectedCategory.text = "Select Category"
            // binding.cardCategory.tag = null
        }
    }
    
    private fun updateDateDisplay() {
        binding.tvSelectedDate.text = displayDateFormat.format(selectedDate.time)
    }
    
    private fun showDatePicker() {
        DatePickerDialog(
            this,
            R.style.Theme_SmartBudget_DatePicker,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateDateDisplay()
            },
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
    
    private fun showAccountDialog() {
        if (accounts.isEmpty()) {
            Helpers.showError(binding.root, "No accounts found")
            return
        }
        
        val accountNames = accounts.map { it.name }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.account))
            .setItems(accountNames.toTypedArray()) { _, which ->
                selectedAccount = accounts[which]
                binding.tvSelectedAccount.text = selectedAccount?.name
                binding.cardAccount.tag = selectedAccount?.actualId()
                
                // Update icon tint based on account type if possible
                // binding.ivAccountIcon.imageTintList = ...
            }
            .show()
    }
    
    private fun showCategoryDialog() {
        val filteredCategories = categories.filter { 
            it.type?.lowercase() == selectedTransactionType.lowercase() 
        }
        
        if (filteredCategories.isEmpty()) {
            Helpers.showError(binding.root, "No categories found for $selectedTransactionType")
            return
        }
        
        val categoryNames = filteredCategories.map { "${it.icon ?: "📁"} ${it.name}" }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.category))
            .setItems(categoryNames.toTypedArray()) { _, which ->
                val category = filteredCategories[which]
                binding.tvSelectedCategory.text = "${category.icon ?: ""} ${category.name}"
                binding.cardCategory.tag = category.actualId()
            }
            .show()
    }
    
    private fun loadAccountsAndCategories() {
        lifecycleScope.launch {
            launch {
                getAccountsUseCase().collect { response ->
                    if (response is com.example.smartbusget2.data.remote.ApiResponse.Success) {
                        accounts.clear()
                        accounts.addAll(response.data)
                    }
                }
            }
            
            launch {
                getCategoriesUseCase().collect { response ->
                    if (response is ApiResponse.Success) {
                        categories.clear()
                        categories.addAll(response.data)
                    }
                }
            }
        }
    }
    
    private fun saveTransaction() {
        val amount = binding.layoutAmount.etAmount.text.toString().toDoubleOrNull()
        val description = binding.etDescription.text.toString()
        val selectedAccountId = binding.cardAccount.tag as? String
        val selectedCategoryId = binding.cardCategory.tag as? String
        
        if (amount == null || amount <= 0) {
            Helpers.showError(binding.root, "Please enter a valid amount")
            return
        }
        
        if (selectedAccountId == null) {
            Helpers.showError(binding.root, "Please select an account")
            return
        }

        if (selectedCategoryId == null) {
            Helpers.showError(binding.root, "Please select a category")
            return
        }
        
        // Format date to ISO 8601
        val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val dateString = isoFormat.format(selectedDate.time)
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val response = if (transactionId != null) {
                updateTransactionUseCase(
                    transactionId!!,
                    amount,
                    description,
                    dateString,
                    selectedTransactionType,
                    selectedAccountId,
                    selectedCategoryId
                )
            } else {
                createTransactionUseCase(
                    amount,
                    description,
                    dateString,
                    selectedTransactionType,
                    selectedAccountId,
                    selectedCategoryId
                )
            }
            
            response.collect { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        Helpers.showSuccess(binding.root, getString(R.string.success))
                        finish()
                    }
                    is ApiResponse.Error -> {
                        Helpers.showError(binding.root, result.message)
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

