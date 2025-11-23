package com.example.smartbusget2.domain.usecase

import com.example.smartbusget2.data.model.User
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String, name: String?): Flow<ApiResponse<User>> {
        return authRepository.register(email, password, name)
    }
}

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Flow<ApiResponse<String>> {
        return authRepository.login(email, password)
    }
}

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Flow<ApiResponse<User>> {
        return authRepository.getCurrentUser()
    }
}

