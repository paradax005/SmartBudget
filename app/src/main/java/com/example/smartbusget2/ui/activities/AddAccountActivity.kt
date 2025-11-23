package com.example.smartbusget2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityAddAccountBinding
import com.example.smartbusget2.domain.usecase.CreateAccountUseCase
import com.example.smartbusget2.domain.usecase.UpdateAccountUseCase
import com.example.smartbusget2.ui.util.Helpers
import com.example.smartbusget2.ui.util.getTextString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddAccountActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddAccountBinding
    
    @Inject
    lateinit var createAccountUseCase: CreateAccountUseCase
    
    @Inject
    lateinit var updateAccountUseCase: UpdateAccountUseCase
    
    private var accountId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        accountId = intent.getStringExtra("account_id")
        
        setupTypeSelectors()
        setupAmountInput()
        
        if (accountId != null) {
            binding.toolbar.title = getString(R.string.edit_account)
            // Load account data for editing
            // For now, just simulate loading
        } else {
            // Default selection
            selectType("bank")
        }
        
        setupClickListeners()
    }
    
    private var selectedType: String = "bank"
    
    private fun setupTypeSelectors() {
        // Initialize selectors
        setupSelector(binding.typeChecking.root, "Checking", R.drawable.ic_type_checking, "bank")
        setupSelector(binding.typeSavings.root, "Savings", R.drawable.ic_type_savings, "savings")
        setupSelector(binding.typeCredit.root, "Credit", R.drawable.ic_type_credit, "credit_card")
        setupSelector(binding.typeCash.root, "Cash", R.drawable.ic_type_cash, "cash")
        setupSelector(binding.typeInvestment.root, "Invest", R.drawable.ic_type_investment, "investment")
    }
    
    private fun setupSelector(view: View, label: String, iconRes: Int, type: String) {
        val tvLabel = view.findViewById<android.widget.TextView>(R.id.tvLabel)
        val ivIcon = view.findViewById<android.widget.ImageView>(R.id.ivIcon)
        
        tvLabel.text = label
        ivIcon.setImageResource(iconRes)
        
        view.setOnClickListener {
            selectType(type)
        }
        view.tag = type
    }
    
    private fun selectType(type: String) {
        selectedType = type
        
        // Update UI for all selectors
        updateSelectorState(binding.typeChecking.root, type == "bank", R.color.account_checking)
        updateSelectorState(binding.typeSavings.root, type == "savings", R.color.account_savings)
        updateSelectorState(binding.typeCredit.root, type == "credit_card", R.color.account_credit)
        updateSelectorState(binding.typeCash.root, type == "cash", R.color.account_cash)
        updateSelectorState(binding.typeInvestment.root, type == "investment", R.color.account_investment)
        
        // Update amount color
        val colorRes = when (type) {
            "credit_card" -> R.color.error_red
            "savings", "investment" -> R.color.success_green
            else -> R.color.purple_primary
        }
        val etAmount = binding.layoutAmount.etAmount
        etAmount.setTextColor(androidx.core.content.ContextCompat.getColor(this, colorRes))
    }
    
    private fun updateSelectorState(view: View, isSelected: Boolean, colorRes: Int) {
        val card = view as androidx.cardview.widget.CardView
        val iconContainer = view.findViewById<View>(R.id.iconContainer)
        val ivIcon = view.findViewById<android.widget.ImageView>(R.id.ivIcon)
        val tvLabel = view.findViewById<android.widget.TextView>(R.id.tvLabel)
        
        if (isSelected) {
            card.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.surface_dark))
            iconContainer.backgroundTintList = android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(this, colorRes)
            )
            ivIcon.setColorFilter(androidx.core.content.ContextCompat.getColor(this, R.color.white))
            tvLabel.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_primary))
        } else {
            card.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(this, R.color.surface_dark))
            iconContainer.backgroundTintList = android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(this, R.color.surface_darker)
            )
            ivIcon.setColorFilter(androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary))
            tvLabel.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary))
        }
    }
    
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
    
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_transaction, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveAccount()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        
        binding.btnSave.setOnClickListener {
            saveAccount()
        }
    }
    
    private fun saveAccount() {
        val name = binding.etName.text.toString()
        val etAmount = binding.layoutAmount.etAmount
        val balance = etAmount.text.toString().toDoubleOrNull()
        
        if (name.isEmpty() || balance == null) {
            Helpers.showError(binding.root, getString(R.string.required_field))
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val response = if (accountId != null) {
                updateAccountUseCase(accountId!!, name, selectedType, balance)
            } else {
                createAccountUseCase(name, selectedType, balance)
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

