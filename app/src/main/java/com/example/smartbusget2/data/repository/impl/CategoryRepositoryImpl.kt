package com.example.smartbusget2.data.repository.impl

import android.util.Log
import com.example.smartbusget2.data.model.Category
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.remote.ApiService
import com.example.smartbusget2.data.remote.CreateCategoryRequest
import com.example.smartbusget2.data.remote.ErrorResponse
import com.example.smartbusget2.data.remote.UpdateCategoryRequest
import com.example.smartbusget2.data.repository.CategoryRepository
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : CategoryRepository {
    
    override suspend fun getCategories(type: String?): Flow<ApiResponse<List<Category>>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.getCategories(type)
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { categories ->
                    emit(ApiResponse.Success(categories))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to fetch categories (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error fetching categories", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun createCategory(name: String, type: String, icon: String?, color: String?): Flow<ApiResponse<Category>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.createCategory(CreateCategoryRequest(name, type, icon, color))
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { category ->
                    emit(ApiResponse.Success(category))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to create category (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error creating category", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun updateCategory(id: String, name: String?, type: String?, icon: String?, color: String?): Flow<ApiResponse<Category>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.updateCategory(id, UpdateCategoryRequest(name, type, icon, color))
            if (response.isSuccessful) {
                val body = response.body()
                body?.data?.let { category ->
                    emit(ApiResponse.Success(category))
                } ?: emit(ApiResponse.Error("Invalid response: data is null"))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to update category (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error updating category", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    override suspend fun deleteCategory(id: String): Flow<ApiResponse<Unit>> = flow {
        emit(ApiResponse.Loading)
        try {
            val response = apiService.deleteCategory(id)
            if (response.isSuccessful) {
                emit(ApiResponse.Success(Unit))
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = parseError(errorBody) ?: "Failed to delete category (HTTP ${response.code()})"
                emit(ApiResponse.Error(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("CategoryRepository", "Error deleting category", e)
            emit(ApiResponse.Error(e.message ?: "Network error"))
        }
    }
    
    private fun parseError(errorBody: String?): String? {
        return try {
            if (errorBody != null) {
                val gson = Gson()
                val errorResponse = gson.fromJson(errorBody, ErrorResponse::class.java)
                errorResponse.error
            } else null
        } catch (e: Exception) {
            null
        }
    }
}

