package com.example.smartbusget2.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartbusget2.R
import com.example.smartbusget2.databinding.ActivityRegisterBinding
import com.example.smartbusget2.domain.usecase.RegisterUseCase
import com.example.smartbusget2.ui.util.Helpers
import com.example.smartbusget2.ui.util.getTextString
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    
    @Inject
    lateinit var registerUseCase: RegisterUseCase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            performHapticFeedback()
            register()
        }
        
        binding.btnLogin.setOnClickListener {
            performHapticFeedback()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
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
    
    private fun register() {
        val name = binding.etName.getTextString()
        val email = binding.etEmail.getTextString()
        val password = binding.etPassword.getTextString()
        
        if (!validateInput(email, password, name)) {
            return
        }
        
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
        
        lifecycleScope.launch {
            // API requires fullName, but we'll use name or email prefix as fallback
            val fullName = name.takeIf { it.isNotEmpty() } ?: email.split("@").first()
            registerUseCase(email, password, fullName).collect { response ->
                when (response) {
                    is com.example.smartbusget2.data.remote.ApiResponse.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        Helpers.showSuccess(binding.root, getString(R.string.success))
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                    is com.example.smartbusget2.data.remote.ApiResponse.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        Helpers.showError(binding.root, response.message)
                    }
                }
            }
        }
    }
    
    private fun validateInput(email: String, password: String, name: String): Boolean {
        if (name.isEmpty()) {
            Helpers.showError(binding.root, "Name is required")
            return false
        }
        
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

