package com.example.smartbusget2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityAccountsBinding
import com.example.smartbusget2.domain.usecase.DeleteAccountUseCase
import com.example.smartbusget2.domain.usecase.GetAccountsUseCase
import com.example.smartbusget2.ui.adapters.AccountAdapter
import com.example.smartbusget2.ui.util.Helpers
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AccountsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAccountsBinding
    
    @Inject
    lateinit var getAccountsUseCase: GetAccountsUseCase
    
    @Inject
    lateinit var deleteAccountUseCase: DeleteAccountUseCase
    
    private lateinit var accountAdapter: AccountAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        setupRecyclerView()
        setupClickListeners()
        loadAccounts()
    }
    
    private fun setupRecyclerView() {
        accountAdapter = AccountAdapter(
            onItemClick = { account ->
                val intent = Intent(this, AddAccountActivity::class.java)
                intent.putExtra("account_id", account.actualId())
                startActivity(intent)
            },
            onItemLongClick = { account ->
                showDeleteDialog(account.actualId())
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = accountAdapter
    }
    
    private fun setupClickListeners() {
        binding.fabAddAccount.setOnClickListener {
            startActivity(Intent(this, AddAccountActivity::class.java))
        }
        
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadAccounts()
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
                R.id.nav_budgets -> {
                    startActivity(Intent(this, BudgetsActivity::class.java))
                    true
                }
                R.id.nav_accounts -> true
                R.id.nav_analytics -> {
                    startActivity(Intent(this, AnalyticsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        binding.bottomNavigation.selectedItemId = R.id.nav_accounts
    }
    
    private fun loadAccounts() {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            getAccountsUseCase().collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        accountAdapter.submitList(response.data)
                        
                        if (response.data.isEmpty()) {
                            binding.layoutEmptyState.root.visibility = View.VISIBLE
                            binding.layoutEmptyState.tvEmptyTitle.text = "No Accounts"
                            binding.layoutEmptyState.tvEmptyMessage.text = "Add an account to start tracking."
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
    
    private fun showDeleteDialog(accountId: String) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage("Are you sure you want to delete this account?")
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                deleteAccount(accountId)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
    
    private fun deleteAccount(accountId: String) {
        binding.progressBar.visibility = View.VISIBLE
        
        lifecycleScope.launch {
            deleteAccountUseCase(accountId).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        Helpers.showSuccess(binding.root, getString(R.string.success))
                        loadAccounts()
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

