package com.example.smartbusget2.data.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("_id")
    val _id: String? = null, // MongoDB format
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("date")
    val date: String, // ISO 8601 format
    @SerializedName("type")
    val type: String, // "expense" or "income"
    @SerializedName("accountId")
    val account: Account? = null,
    @SerializedName("categoryId")
    val category: Category? = null,
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

    // Helper to get account ID
    val accountId: String?
        get() = account?.actualId()

    // Helper to get category ID
    val categoryId: String?
        get() = category?.actualId()
}

