package com.example.smartbusget2.domain.usecase

import com.example.smartbusget2.data.model.Category
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(type: String? = null): Flow<ApiResponse<List<Category>>> {
        return categoryRepository.getCategories(type)
    }
}

class CreateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(name: String, type: String, icon: String? = null, color: String? = null): Flow<ApiResponse<Category>> {
        return categoryRepository.createCategory(name, type, icon, color)
    }
}

class UpdateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: String, name: String?, type: String?, icon: String? = null, color: String? = null): Flow<ApiResponse<Category>> {
        return categoryRepository.updateCategory(id, name, type, icon, color)
    }
}

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(id: String): Flow<ApiResponse<Unit>> {
        return categoryRepository.deleteCategory(id)
    }
}

