package com.example.smartbusget2.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("_id")
    val _id: String? = null, // MongoDB format
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("type")
    val type: String? = null, // "expense" or "income"
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("isDefault")
    val isDefault: Boolean? = false,
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

