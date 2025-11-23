package com.example.smartbusget2.data.model

import com.google.gson.annotations.SerializedName

data class Account(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("_id")
    val _id: String? = null, // MongoDB format
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String, // "bank", "cash", "credit_card", "savings"
    @SerializedName("balance")
    val balance: Double,
    @SerializedName("currency")
    val currency: String? = "USD",
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    // Helper to get the actual ID (handles both formats)
    fun actualId(): String {
        return id ?: _id ?: ""
    }
}

