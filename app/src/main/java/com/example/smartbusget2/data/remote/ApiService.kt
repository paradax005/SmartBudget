package com.example.smartbusget2.data.remote

import com.example.smartbusget2.data.model.*
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @GET("auth/me")
    suspend fun getCurrentUser(): Response<UserResponse>
    
    // Accounts
    @GET("accounts")
    suspend fun getAccounts(): Response<AccountsResponse>
    
    @POST("accounts")
    suspend fun createAccount(@Body account: CreateAccountRequest): Response<AccountResponse>
    
    @PUT("accounts/{id}")
    suspend fun updateAccount(@Path("id") id: String, @Body account: UpdateAccountRequest): Response<AccountResponse>
    
    @DELETE("accounts/{id}")
    suspend fun deleteAccount(@Path("id") id: String): Response<MessageResponse>
    
    // Categories
    @GET("categories")
    suspend fun getCategories(@Query("type") type: String? = null): Response<CategoriesResponse>
    
    @POST("categories")
    suspend fun createCategory(@Body category: CreateCategoryRequest): Response<CategoryResponse>
    
    @PUT("categories/{id}")
    suspend fun updateCategory(@Path("id") id: String, @Body category: UpdateCategoryRequest): Response<CategoryResponse>
    
    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: String): Response<MessageResponse>
    
    // Transactions
    @GET("transactions")
    suspend fun getTransactions(
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("categoryId") categoryId: String? = null,
        @Query("accountId") accountId: String? = null
    ): Response<TransactionsResponse>
    
    @POST("transactions")
    suspend fun createTransaction(@Body transaction: CreateTransactionRequest): Response<TransactionCreationResponse>
    
    @PUT("transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: String, @Body transaction: UpdateTransactionRequest): Response<TransactionCreationResponse>
    
    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: String): Response<MessageResponse>
    
    // Budgets
    @GET("budgets")
    suspend fun getBudgets(
        @Query("month") month: Int? = null,
        @Query("year") year: Int? = null
    ): Response<BudgetsResponse>
    
    @POST("budgets")
    suspend fun createBudget(@Body budget: CreateBudgetRequest): Response<BudgetResponse>
    
    @PUT("budgets/{id}")
    suspend fun updateBudget(@Path("id") id: String, @Body budget: UpdateBudgetRequest): Response<BudgetResponse>
    
    @DELETE("budgets/{id}")
    suspend fun deleteBudget(@Path("id") id: String): Response<MessageResponse>
    
    @GET("budgets/status")
    suspend fun getBudgetStatus(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<BudgetStatusResponse>
    
    // Statistics
    @GET("transactions/stats/monthly")
    suspend fun getMonthlyStats(
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<MonthlyStatsContainer>
}

// Request/Response DTOs
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

// Registration Response
data class RegisterResponse(
    val message: String,
    val token: String,
    val user: User
)

// Login Response
data class LoginResponse(
    val message: String,
    val token: String,
    val user: User
)

data class CreateAccountRequest(
    val name: String,
    val type: String, // "bank", "cash", "credit_card", "savings"
    val balance: Double,
    val currency: String = "USD"
)

data class UpdateAccountRequest(
    val name: String? = null,
    val type: String? = null,
    val balance: Double? = null
)

data class CreateTransactionRequest(
    val amount: Double,
    val description: String,
    val date: String,
    val type: String,
    val accountId: String,
    val categoryId: String? = null
)

data class UpdateTransactionRequest(
    val amount: Double? = null,
    val description: String? = null,
    val date: String? = null,
    val type: String? = null,
    val accountId: String? = null,
    val categoryId: String? = null
)

data class CreateBudgetRequest(
    val categoryId: String,
    val amount: Double,
    val month: Int, // 1-12
    val year: Int // e.g., 2024
)

data class UpdateBudgetRequest(
    val amount: Double? = null,
    val month: Int? = null,
    val year: Int? = null
)

data class CreateCategoryRequest(
    val name: String,
    val type: String, // "expense" or "income"
    val icon: String? = null,
    val color: String? = null
)

data class UpdateCategoryRequest(
    val name: String? = null,
    val type: String? = null,
    val icon: String? = null,
    val color: String? = null
)

// API Response Models (matching actual API format)
data class AccountsResponse(
    val data: List<Account>,
    val count: Int? = null,
    val message: String? = null
)

data class AccountResponse(
    val message: String? = null,
    val data: Account
)

data class CategoriesResponse(
    val data: List<Category>,
    val count: Int? = null,
    val message: String? = null
)

data class CategoryResponse(
    val message: String? = null,
    val data: Category
)

data class TransactionsResponse(
    val data: List<Transaction>,
    val count: Int? = null,
    val message: String? = null
)

data class TransactionResponse(
    val message: String? = null,
    val data: Transaction,
    val accountBalance: Double? = null
)

data class BudgetsResponse(
    val data: List<Budget>,
    val count: Int? = null,
    val message: String? = null
)

data class BudgetResponse(
    val message: String? = null,
    val data: Budget
)

data class BudgetStatusResponse(
    val month: Int,
    val year: Int,
    val budgets: List<BudgetStatusItem>,
    val alerts: List<String>? = null,
    val alertCount: Int? = 0
)

data class BudgetStatusItem(
    val id: String,
    val category: Category,
    val amount: Double,
    val spent: Double,
    val remaining: Double,
    val percentage: String,
    val isExceeded: Boolean
)

data class MonthlyStatsResponse(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpenses: Double,
    val netAmount: Double,
    val transactionCount: Int? = 0,
    val incomeByCategory: List<CategoryStats>? = null,
    val expensesByCategory: List<CategoryStats>? = null
)

data class CategoryStats(
    val categoryId: String,
    val categoryName: String? = null,
    val category: Category? = null,
    val totalAmount: Double,
    val transactionCount: Int? = 0
)

data class MessageResponse(
    val message: String
)

data class UserResponse(
    val user: User
)

// Error Response Model
data class ErrorResponse(
    val error: String,
    val status: Int
)

// Response for Create/Update Transaction (where accountId/categoryId are Strings)
data class TransactionCreationResponse(
    val message: String? = null,
    val data: TransactionDto,
    val accountBalance: Double? = null
)

data class TransactionDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("_id")
    val _id: String? = null,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("date")
    val date: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("accountId")
    val accountId: String,
    @SerializedName("categoryId")
    val categoryId: String? = null,
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

// Wrapper for Monthly Stats API Response
data class MonthlyStatsContainer(
    val data: MonthlyStatsDto,
    val message: String? = null
)

// DTO for Monthly Stats (matching backend structure)
data class MonthlyStatsDto(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double, // Backend uses singular 'totalExpense'
    val netAmount: Double,
    val incomeCount: Int? = 0,
    val expenseCount: Int? = 0,
    val byCategory: ByCategoryDto? = null
)

data class ByCategoryDto(
    val income: List<CategoryStatsDto>? = null,
    val expense: List<CategoryStatsDto>? = null
)

data class CategoryStatsDto(
    val categoryId: String,
    val categoryName: String? = null,
    val total: Double,
    val count: Int? = 0
)
