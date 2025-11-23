package com.example.smartbusget2.data.remote

import android.content.Context
import android.content.Intent
import com.example.smartbusget2.ui.activities.LoginActivity
import com.example.smartbusget2.ui.util.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get token synchronously for interceptor
        val token = runBlocking {
            tokenManager.getToken()
        }
        
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        val response = chain.proceed(newRequest)
        
        // Handle token expiration (401 Unauthorized)
        if (response.code == 401 && !originalRequest.url.encodedPath.contains("auth/login") 
            && !originalRequest.url.encodedPath.contains("auth/register")) {
            // Clear token on 401 error
            runBlocking {
                tokenManager.clearToken()
            }
            
            // Redirect to login screen (only if not already on login/register)
            try {
                val intent = Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                // Ignore if context is not available (e.g., in background)
            }
        }
        
        return response
    }
}

