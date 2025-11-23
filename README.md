# SmartBudget Android App

A comprehensive personal finance management Android application built with Kotlin, XML layouts, and MVC architecture.

## Features

- **User Authentication**: Register and login with JWT token-based authentication
- **Account Management**: Create, view, update, and delete financial accounts (Checking, Savings, Credit Card)
- **Transaction Tracking**: Record income and expenses with categories and dates
- **Budget Planning**: Set budgets by category with period tracking
- **Dashboard**: View total balance, expense breakdown (pie chart), and recent transactions
- **Material Design 3**: Modern UI with Material 3 components

## Architecture

- **MVC (Model-View-Controller)**: Clean separation of concerns
- **Hilt Dependency Injection**: For dependency management
- **Retrofit + OkHttp**: For network requests
- **Coroutines + Flow**: For asynchronous operations
- **DataStore**: For secure token storage
- **View Binding**: For type-safe view references
- **RecyclerView + DiffUtil**: For efficient list rendering

## Project Structure

```
app/src/main/java/com/example/smartbusget2/
├── MainActivity.kt
├── SmartBudgetApplication.kt
├── di/
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   └── RepositoryModule.kt
├── data/
│   ├── model/
│   │   ├── User.kt
│   │   ├── Account.kt
│   │   ├── Category.kt
│   │   ├── Transaction.kt
│   │   └── Budget.kt
│   ├── remote/
│   │   ├── ApiService.kt
│   │   ├── AuthInterceptor.kt
│   │   └── ApiResponse.kt
│   └── repository/
│       ├── AuthRepository.kt
│       ├── AccountRepository.kt
│       ├── CategoryRepository.kt
│       ├── TransactionRepository.kt
│       ├── BudgetRepository.kt
│       └── impl/
│           ├── AuthRepositoryImpl.kt
│           ├── AccountRepositoryImpl.kt
│           ├── CategoryRepositoryImpl.kt
│           ├── TransactionRepositoryImpl.kt
│           └── BudgetRepositoryImpl.kt
├── domain/
│   └── usecase/
│       ├── AuthUseCases.kt
│       ├── AccountUseCases.kt
│       ├── CategoryUseCases.kt
│       ├── TransactionUseCases.kt
│       └── BudgetUseCases.kt
├── ui/
│   ├── activities/
│   │   ├── LoginActivity.kt
│   │   ├── RegisterActivity.kt
│   │   ├── DashboardActivity.kt
│   │   ├── TransactionsActivity.kt
│   │   ├── AddTransactionActivity.kt
│   │   ├── AccountsActivity.kt
│   │   ├── AddAccountActivity.kt
│   │   ├── BudgetsActivity.kt
│   │   └── AddBudgetActivity.kt
│   ├── adapters/
│   │   ├── TransactionAdapter.kt
│   │   ├── AccountAdapter.kt
│   │   └── BudgetAdapter.kt
│   └── util/
│       ├── TokenManager.kt
│       ├── Constants.kt
│       └── Helpers.kt
└── res/
    ├── layout/
    │   ├── activity_login.xml
    │   ├── activity_register.xml
    │   ├── activity_dashboard.xml
    │   ├── activity_transactions.xml
    │   ├── activity_add_transaction.xml
    │   ├── activity_accounts.xml
    │   ├── activity_add_account.xml
    │   ├── activity_budgets.xml
    │   ├── activity_add_budget.xml
    │   ├── item_transaction.xml
    │   ├── item_account.xml
    │   └── item_budget.xml
    ├── values/
    │   ├── themes.xml
    │   ├── strings.xml
    │   └── colors.xml
    └── menu/
        └── main_menu.xml
```

## Setup Instructions

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+ (Android 7.0+)
- Kotlin 1.9.0+
- Gradle 8.3+

### Backend Requirements

The app requires a backend API running on `http://10.0.2.2:3000/api/` (for Android Emulator).

**API Endpoints:**
- `POST /auth/register` - User registration
- `POST /auth/login` - User login (returns JWT token)
- `GET /accounts` - Get all accounts
- `POST /accounts` - Create account
- `PUT /accounts/{id}` - Update account
- `DELETE /accounts/{id}` - Delete account
- `GET /categories` - Get all categories
- `GET /transactions` - Get transactions (with optional filters: dateFrom, dateTo, categoryId)
- `POST /transactions` - Create transaction
- `PUT /transactions/{id}` - Update transaction
- `DELETE /transactions/{id}` - Delete transaction
- `GET /budgets` - Get all budgets
- `POST /budgets` - Create budget

**Response Format:**
All endpoints return JSON in the format:
```json
{
  "success": true,
  "data": {...}
}
```

**Authentication:**
Include JWT token in request headers:
```
Authorization: Bearer <token>
```

### Configuration

1. **For Android Emulator:**
   - The base URL is already configured: `http://10.0.2.2:3000/api/`
   - This maps to `localhost:3000` on your development machine

2. **For Physical Device:**
   - Find your computer's local IP address (e.g., `192.168.1.100`)
   - Update `Constants.kt`:
     ```kotlin
     const val BASE_URL = "http://192.168.1.100:3000/api/"
     ```
   - Ensure your device and computer are on the same network
   - Make sure your backend allows connections from your device's IP

3. **Build and Run:**
   ```bash
   ./gradlew build
   ./gradlew installDebug
   ```

## Key Features Implementation

### Authentication
- JWT token stored securely using DataStore
- Automatic token injection via AuthInterceptor
- Token validation on app launch (MainActivity)

### Dashboard
- Total balance calculation from all accounts
- Pie chart showing expense breakdown by category (MPAndroidChart)
- Recent 5 transactions displayed
- FAB to quickly add new transaction

### Transactions
- List view with SwipeRefreshLayout
- Filter by date range and category
- Long-click to delete
- Click to edit

### Accounts
- CRUD operations for accounts
- Support for multiple account types
- Balance tracking

### Budgets
- Create budgets by category
- Period selection (Weekly, Monthly, Yearly)
- Progress tracking (simplified implementation)

## Dependencies

- **Hilt**: Dependency injection
- **Retrofit**: HTTP client
- **OkHttp**: Network interceptor
- **Coroutines**: Asynchronous operations
- **DataStore**: Secure data storage
- **MPAndroidChart**: Chart library
- **Material Components**: UI components
- **View Binding**: Type-safe view access

## Best Practices

- ✅ DRY, clean, readable code
- ✅ Meaningful variable names
- ✅ Error handling with try/catch
- ✅ Loading states with ProgressBar
- ✅ No hardcoded strings (all in strings.xml)
- ✅ Scalable, testable, maintainable architecture
- ✅ Input validation
- ✅ User feedback with Snackbars

## Notes

- The app uses `usesCleartextTraffic="true"` in AndroidManifest for HTTP connections. For production, use HTTPS.
- Budget progress calculation is simplified - in a production app, calculate from actual transaction spending.
- Date formatting assumes ISO 8601 format from backend.

## License

This project is created for educational purposes.

