package com.example.smartbusget2

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.lifecycle.lifecycleScope
import com.example.smartbusget2.databinding.ActivitySplashBinding
import com.example.smartbusget2.domain.usecase.GetCurrentUserUseCase
import com.example.smartbusget2.ui.activities.DashboardActivity
import com.example.smartbusget2.ui.activities.LoginActivity
import com.example.smartbusget2.ui.util.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    
    @Inject
    lateinit var tokenManager: TokenManager
    
    @Inject
    lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        animateSplash()
        checkAuthAndNavigate()
    }
    
    private fun animateSplash() {
        // Scale in animation for logo
        val scaleX = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_X, 0.8f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(binding.ivLogo, View.SCALE_Y, 0.8f, 1.0f)
        val alpha = ObjectAnimator.ofFloat(binding.ivLogo, View.ALPHA, 0f, 1.0f)
        
        val logoAnimator = AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 600
        }
        
        // Fade in for app name
        val nameAlpha = ObjectAnimator.ofFloat(binding.tvAppName, View.ALPHA, 0f, 1.0f).apply {
            startDelay = 300
            duration = 400
        }
        
        logoAnimator.start()
        nameAlpha.start()
    }
    
    private fun checkAuthAndNavigate() {
        lifecycleScope.launch {
            // Wait for animation + minimum splash duration (1.5 seconds total)
            kotlinx.coroutines.delay(1500)
            
            val token = tokenManager.getToken()
            
            if (token != null) {
                // Validate token by calling /api/auth/me with timeout
                try {
                    val response = withTimeoutOrNull(5000) { // 5 second timeout
                        getCurrentUserUseCase()
                            .firstOrNull { it !is com.example.smartbusget2.data.remote.ApiResponse.Loading }
                    }
                    
                    when (response) {
                        is com.example.smartbusget2.data.remote.ApiResponse.Success -> {
                            // Token is valid, navigate to dashboard
                            navigateToDashboard()
                        }
                        is com.example.smartbusget2.data.remote.ApiResponse.Error -> {
                            // Token is invalid or expired, clear and go to login
                            tokenManager.clearToken()
                            navigateToLogin()
                        }
                        null -> {
                            // Timeout or no response, assume token is invalid and go to login
                            tokenManager.clearToken()
                            navigateToLogin()
                        }
                        else -> {
                            // Unexpected state, go to login
                            navigateToLogin()
                        }
                    }
                } catch (e: Exception) {
                    // Network error or other exception, clear token and go to login
                    tokenManager.clearToken()
                    navigateToLogin()
                }
            } else {
                // No token, go to login
                navigateToLogin()
            }
        }
    }
    
    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        fadeOutAndNavigate(intent)
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        fadeOutAndNavigate(intent)
    }
    
    private fun fadeOutAndNavigate(intent: Intent) {
        val fadeOut = ObjectAnimator.ofFloat(binding.root, View.ALPHA, 1.0f, 0f).apply {
            duration = 200
            doOnEnd {
                startActivity(intent)
                finish()
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
        fadeOut.start()
    }
}
