package com.example.smartbusget2.ui.util

object ApiConfig {
    // Build variants - Base URLs
    const val BASE_URL_DEV = "http://10.0.2.2:3000/api"
    const val BASE_URL_STAGING = "https://staging-api.smartbudget.com/api"
    const val BASE_URL_PROD = "https://api.smartbudget.com/api"
    
    // Get base URL based on build variant
    // For now, using Constants.BASE_URL, but this can be extended to use BuildConfig
    fun getBaseUrl(): String {
        // TODO: Use BuildConfig.BUILD_TYPE when build variants are configured
        // return when (BuildConfig.BUILD_TYPE) {
        //     "debug" -> BASE_URL_DEV
        //     "staging" -> BASE_URL_STAGING
        //     "release" -> BASE_URL_PROD
        //     else -> BASE_URL_DEV
        // }
        return Constants.BASE_URL
    }
}


