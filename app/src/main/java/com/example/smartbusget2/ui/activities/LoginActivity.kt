package com.example.smartbusget2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityLoginBinding
import com.example.smartbusget2.domain.usecase.LoginUseCase
import com.example.smartbusget2.ui.util.Helpers
import com.example.smartbusget2.ui.util.getTextString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    
    @Inject
    lateinit var loginUseCase: LoginUseCase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            performHapticFeedback()
            login()
        }
        
        binding.btnRegister.setOnClickListener {
            performHapticFeedback()
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        
        // Password toggle
        binding.btnTogglePassword.setOnClickListener {
            performHapticFeedback()
            val isPasswordVisible = binding.etPassword.inputType == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            binding.etPassword.inputType = if (isPasswordVisible) {
                android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }
    }
    
    private fun performHapticFeedback() {
        binding.root.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY)
    }
    
    private fun login() {
        val email = binding.etEmail.getTextString()
        val password = binding.etPassword.getTextString()
        
        if (!validateInput(email, password)) {
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        
        lifecycleScope.launch {
            loginUseCase(email, password).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        finish()
                    }
                    is com.example.smartbusget2.data.remote.ApiResponse.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        Helpers.showError(binding.root, response.message)
                    }
                }
            }
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        if (!Helpers.isValidEmail(email)) {
            Helpers.showError(binding.root, getString(R.string.invalid_email))
            return false
        }
        
        if (!Helpers.isValidPassword(password)) {
            Helpers.showError(binding.root, getString(R.string.invalid_password))
            return false
        }
        
        return true
    }
}

