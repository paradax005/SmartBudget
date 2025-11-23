package com.example.smartbusget2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityAddCategoryBinding
import com.example.smartbusget2.domain.usecase.CreateCategoryUseCase
import com.example.smartbusget2.domain.usecase.UpdateCategoryUseCase
import com.example.smartbusget2.ui.util.Helpers
import com.example.smartbusget2.ui.util.getTextString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddCategoryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAddCategoryBinding
    
    @Inject
    lateinit var createCategoryUseCase: CreateCategoryUseCase
    
    @Inject
    lateinit var updateCategoryUseCase: UpdateCategoryUseCase
    
    private var categoryId: String? = null
    private var selectedType: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        categoryId = intent.getStringExtra("category_id")
        
        if (categoryId != null) {
            binding.toolbar.title = getString(R.string.edit_category)
            // Load category data for editing
        }
        
        setupClickListeners()
    }
    
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_transaction, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveCategory()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupClickListeners() {
        binding.btnIncome.setOnClickListener {
            selectedType = "income"
            updateTypeUI()
        }
        
        binding.btnExpense.setOnClickListener {
            selectedType = "expense"
            updateTypeUI()
        }
        
        binding.btnSave.setOnClickListener {
            saveCategory()
        }
    }
    
    private fun updateTypeUI() {
        val isIncome = selectedType == "income"
        val activeColor = androidx.core.content.ContextCompat.getColor(this, if (isIncome) R.color.success_green else R.color.error_red)
        val inactiveColor = androidx.core.content.ContextCompat.getColor(this, R.color.text_secondary)
        val activeBg = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.bg_toggle_active)
        val inactiveBg = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.bg_toggle_inactive) // Ensure this drawable exists or use null/transparent
        
        binding.btnIncome.apply {
            setTextColor(if (isIncome) androidx.core.content.ContextCompat.getColor(context, R.color.white) else inactiveColor)
            background = if (isIncome) activeBg else null
            backgroundTintList = if (isIncome) android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(context, R.color.success_green)) else null
        }
        
        binding.btnExpense.apply {
            setTextColor(if (!isIncome) androidx.core.content.ContextCompat.getColor(context, R.color.white) else inactiveColor)
            background = if (!isIncome) activeBg else null
            backgroundTintList = if (!isIncome) android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(context, R.color.error_red)) else null
        }
    }
    
    private fun saveCategory() {
        val name = binding.etName.text.toString()
        val type = selectedType
        val icon = binding.etIcon.text.toString().takeIf { it.isNotEmpty() }
        val color = binding.etColor.text.toString().takeIf { it.isNotEmpty() }
        
        if (name.isEmpty() || type == null) {
            Helpers.showError(binding.root, getString(R.string.required_field))
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            val response = if (categoryId != null) {
                updateCategoryUseCase(categoryId!!, name, type, icon, color)
            } else {
                createCategoryUseCase(name, type, icon, color)
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



