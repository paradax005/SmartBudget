package com.example.smartbusget2.data.repository

import com.example.smartbusget2.data.model.Category
import com.example.smartbusget2.data.remote.ApiResponse
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(type: String? = null): Flow<ApiResponse<List<Category>>>
    suspend fun createCategory(name: String, type: String, icon: String? = null, color: String? = null): Flow<ApiResponse<Category>>
    suspend fun updateCategory(id: String, name: String?, type: String?, icon: String? = null, color: String? = null): Flow<ApiResponse<Category>>
    suspend fun deleteCategory(id: String): Flow<ApiResponse<Unit>>
}

