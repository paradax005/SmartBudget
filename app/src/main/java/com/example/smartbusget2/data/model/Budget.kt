package com.example.smartbusget2.data.model

import com.google.gson.annotations.SerializedName

data class Budget(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("_id")
    val _id: String? = null, // MongoDB format
    @SerializedName("categoryId")
    val categoryId: String? = null,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("month")
    val month: Int? = null, // 1-12
    @SerializedName("year")
    val year: Int? = null,
    @SerializedName("spent")
    val spent: Double? = 0.0, // Automatically calculated
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

