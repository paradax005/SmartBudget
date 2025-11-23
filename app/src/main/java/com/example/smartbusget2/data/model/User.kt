package com.example.smartbusget2.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("_id")
    val _id: String? = null, // MongoDB format
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String? = null,
    @SerializedName("name")
    val name: String? = null, // Fallback for compatibility
    @SerializedName("createdAt")
    val createdAt: String? = null
) {
    // Helper to get the actual ID (handles both formats)
    fun actualId(): String {
        return id ?: _id ?: ""
    }
    
    // Helper to get the name (handles both fullName and name)
    fun actualName(): String {
        return fullName ?: name ?: ""
    }
}

