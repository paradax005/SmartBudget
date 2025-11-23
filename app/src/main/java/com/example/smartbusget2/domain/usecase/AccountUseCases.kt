package com.example.smartbusget2.domain.usecase

import com.example.smartbusget2.data.model.Account
import com.example.smartbusget2.data.remote.ApiResponse
import com.example.smartbusget2.data.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(): Flow<ApiResponse<List<Account>>> {
        return accountRepository.getAccounts()
    }
}

class CreateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(name: String, type: String, balance: Double): Flow<ApiResponse<Account>> {
        return accountRepository.createAccount(name, type, balance)
    }
}

class UpdateAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(id: String, name: String?, type: String?, balance: Double?): Flow<ApiResponse<Account>> {
        return accountRepository.updateAccount(id, name, type, balance)
    }
}

class DeleteAccountUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(id: String): Flow<ApiResponse<Unit>> {
        return accountRepository.deleteAccount(id)
    }
}

