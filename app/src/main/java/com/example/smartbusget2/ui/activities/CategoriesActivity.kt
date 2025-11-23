package com.example.smartbusget2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityCategoriesBinding
import com.example.smartbusget2.domain.usecase.DeleteCategoryUseCase
import com.example.smartbusget2.domain.usecase.GetCategoriesUseCase
import com.example.smartbusget2.ui.adapters.CategoryAdapter
import com.example.smartbusget2.ui.util.Helpers
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCategoriesBinding
    
    @Inject
    lateinit var getCategoriesUseCase: GetCategoriesUseCase
    
    @Inject
    lateinit var deleteCategoryUseCase: DeleteCategoryUseCase
    
    private lateinit var categoryAdapter: CategoryAdapter
    private var filterType: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            binding = ActivityCategoriesBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            
            setupRecyclerView()
            setupClickListeners()
            setupFilterChips()
            loadCategories()
        } catch (e: Exception) {
            e.printStackTrace()
            // Use application context to show toast in case activity is not fully created
            android.widget.Toast.makeText(applicationContext, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private fun setupFilterChips() {
        binding.chipAll.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                filterType = null
                loadCategories()
            }
        }
        
        binding.chipIncome.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                filterType = "income"
                loadCategories()
            }
        }
        
        binding.chipExpense.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                filterType = "expense"
                loadCategories()
            }
        }
    }
    
    private fun setupRecyclerView() {
        categoryAdapter = CategoryAdapter(
            onItemClick = { category ->
                val intent = Intent(this, AddCategoryActivity::class.java)
                intent.putExtra("category_id", category.actualId())
                startActivity(intent)
            },
            onItemLongClick = { category ->
                showDeleteDialog(category.actualId())
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = categoryAdapter
    }
    
    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }
        
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadCategories()
        }
    }
    
    private fun loadCategories() {
        binding.progressBar.visibility = View.VISIBLE
        
        val exceptionHandler = kotlinx.coroutines.CoroutineExceptionHandler { _, throwable ->
            android.util.Log.e("CategoriesActivity", "Error loading categories", throwable)
            runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
                Helpers.showError(binding.root, "Error: ${throwable.message}")
            }
        }

        lifecycleScope.launch(exceptionHandler) {
            getCategoriesUseCase(filterType).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        categoryAdapter.submitList(response.data)
                        
                        if (response.data.isEmpty()) {
                            binding.layoutEmptyState.root.visibility = View.VISIBLE
                            binding.layoutEmptyState.tvEmptyTitle.text = "No Categories"
                            binding.layoutEmptyState.tvEmptyMessage.text = "Add categories to organize your finances."
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
    
    private fun showDeleteDialog(categoryId: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage("Are you sure you want to delete this category?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteCategory(categoryId)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun deleteCategory(categoryId: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            deleteCategoryUseCase(categoryId).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        Helpers.showSuccess(binding.root, getString(R.string.success))
                        loadCategories()
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



