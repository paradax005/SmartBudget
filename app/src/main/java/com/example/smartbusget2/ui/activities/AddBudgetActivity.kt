package com.example.smartbusget2.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityAddBudgetBinding
import com.example.smartbusget2.domain.usecase.CreateBudgetUseCase
import com.example.smartbusget2.domain.usecase.GetCategoriesUseCase
import com.example.smartbusget2.ui.util.Helpers
import com.example.smartbusget2.ui.util.getTextString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddBudgetActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddBudgetBinding
    
    @Inject
    lateinit var createBudgetUseCase: CreateBudgetUseCase
    
    @Inject
    lateinit var updateBudgetUseCase: com.example.smartbusget2.domain.usecase.UpdateBudgetUseCase

    @Inject
    lateinit var getCategoriesUseCase: GetCategoriesUseCase
    
    private val categories = mutableListOf<com.example.smartbusget2.data.model.Category>()
    
    private var budgetId: String? = null
    private var selectedMonth: Int? = null
    private var selectedYear: Int? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        budgetId = intent.getStringExtra("budget_id")
        
        setupClickListeners()
        setupAmountInput()
        setupSlider()
        loadCategories()
        
        if (budgetId != null) {
            binding.toolbar.title = getString(R.string.edit_budget)
            populateData()
        }
    }
    
    private fun populateData() {
        val amount = intent.getDoubleExtra("amount", 0.0)
        val categoryId = intent.getStringExtra("category_id")
        selectedMonth = intent.getIntExtra("month", -1).takeIf { it != -1 }
        selectedYear = intent.getIntExtra("year", -1).takeIf { it != -1 }
        
        binding.layoutAmount.etAmount.setText(amount.toString())
        
        // Category selection will be handled after categories are loaded
        // We store the categoryId to select it later
        binding.cardCategory.root.tag = categoryId
    }
    
    // ... (setupAmountInput, setupSlider, setupClickListeners, showCategoryDialog remain same) ...
    
    private fun setupAmountInput() {
        val etAmount = binding.layoutAmount.etAmount
        etAmount.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val text = etAmount.text.toString()
                if (text.isNotEmpty()) {
                    try {
                        val value = text.toDouble()
                        etAmount.setText(String.format("%.2f", value))
                    } catch (e: NumberFormatException) {
                        // Ignore
                    }
                }
            }
        }
    }
    
    private fun setupSlider() {
        binding.sliderAlert.addOnChangeListener { _, value, _ ->
            binding.tvAlertPercent.text = "Alert at ${value.toInt()}% exceeded"
        }
        
        binding.switchAlert.setOnCheckedChangeListener { _, isChecked ->
            binding.sliderAlert.isEnabled = isChecked
            binding.tvAlertPercent.alpha = if (isChecked) 1.0f else 0.5f
        }
    }
    
    private fun setupClickListeners() {
        binding.cardCategory.root.setOnClickListener {
            showCategoryDialog()
        }
        
        binding.btnSave.setOnClickListener {
            saveBudget()
        }
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun showCategoryDialog() {
        val categoryNames = categories.map { it.name }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.category))
            .setItems(categoryNames.toTypedArray()) { _, which ->
                val selectedCategory = categories[which]
                val tvLabel = binding.cardCategory.root.findViewById<android.widget.TextView>(R.id.tvLabel)
                val tvValue = binding.cardCategory.root.findViewById<android.widget.TextView>(R.id.tvValue)
                
                tvLabel.text = getString(R.string.category)
                tvValue.text = selectedCategory.name
                
                binding.cardCategory.root.tag = selectedCategory.actualId()
            }
            .show()
    }
    
    private fun loadCategories() {
        lifecycleScope.launch {
            getCategoriesUseCase().collect { response ->
                if (response is com.example.smartbusget2.data.remote.ApiResponse.Success) {
                    categories.clear()
                    categories.addAll(response.data)
                    
                    // If editing, update category display
                    val currentCategoryId = binding.cardCategory.root.tag as? String
                    if (currentCategoryId != null) {
                        val category = categories.find { it.actualId() == currentCategoryId }
                        if (category != null) {
                            val tvLabel = binding.cardCategory.root.findViewById<android.widget.TextView>(R.id.tvLabel)
                            val tvValue = binding.cardCategory.root.findViewById<android.widget.TextView>(R.id.tvValue)
                            tvLabel.text = getString(R.string.category)
                            tvValue.text = category.name
                        }
                    }
                }
            }
        }
    }
    
    private fun saveBudget() {
        val categoryId = binding.cardCategory.root.tag as? String
        val etAmount = binding.layoutAmount.etAmount
        val amount = etAmount.text.toString().toDoubleOrNull()
        
        // Use selected month/year if editing, otherwise current
        val calendar = Calendar.getInstance()
        val month = selectedMonth ?: (calendar.get(Calendar.MONTH) + 1)
        val year = selectedYear ?: calendar.get(Calendar.YEAR)
        
        if (categoryId == null || amount == null) {
            Helpers.showError(binding.root, getString(R.string.required_field))
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false
        
        lifecycleScope.launch {
            val response = if (budgetId != null) {
                updateBudgetUseCase(budgetId!!, amount, month, year)
            } else {
                createBudgetUseCase(categoryId, amount, month, year)
            }
            
            response.collect { result ->
                when (result) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        Helpers.showSuccess(binding.root, getString(R.string.success))
                        finish()
                    }
                    is com.example.smartbusget2.data.remote.ApiResponse.Error -> {
                        Helpers.showError(binding.root, result.message)
                        binding.progressBar.visibility = View.GONE
                        binding.btnSave.isEnabled = true
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

