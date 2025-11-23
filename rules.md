# Ultimate Cursor Prompt: SmartBudget Android App (Native, MVC, XML, Kotlin 2.2.21)

**Goal**: Generate a **complete, modular, production-ready Android app** named **SmartBudget** for personal finance management using **Kotlin**, **XML layouts**, **MVC architecture**, and **no Jetpack Compose**.

---

## ARCHITECTURE: MVC (Model-View-Controller)

- **Models**: Data classes in `data/model/`
- **Views**: XML layouts in `res/layout/`
- **Controllers**: `Activity` classes in `ui/activities/` — handle UI logic, business calls, coroutines

---

## PROJECT STRUCTURE (EXACT)
app/src/main/java/com/example/smartbudget/
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
│           └── ...
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
│   ├── activity_dashboard.xml
│   ├── item_transaction.xml
│   └── ...
├── values/
│   ├── themes.xml (Material3)
│   ├── strings.xml
│   └── colors.xml
└── drawable/


---

## BACKEND API

- Base URL: `http://10.0.2.2:3000/api/` (emulator)
- For physical device: Replace with local IP
- All endpoints return JSON wrapped in `{ "success": true, "data": ... }` or error
- JWT token in `Authorization: Bearer <token>`

### Endpoints (match exactly):

```kotlin
POST   /auth/register        → User
POST   /auth/login           → { token: String }
GET    /accounts             → List<Account>
POST   /accounts             → Account
PUT    /accounts/{id}        → Account
DELETE /accounts/{id}        → Unit
GET    /categories           → List<Category>
GET    /transactions         → List<Transaction>?dateFrom=&dateTo=&categoryId=
POST   /transactions         → Transaction
PUT    /transactions/{id}    → Transaction
DELETE /transactions/{id}    → Unit
GET    /budgets              → List<Budget>
POST   /budgets              → Budget

KEY REQUIREMENTS

Hilt Injection in all repositories and use cases
Coroutines + Flow for all network calls
DataStore for JWT token (TokenManager)
XML Layouts with View Binding
Material 3 Theme in themes.xml
RecyclerView + DiffUtil in adapters
SwipeRefreshLayout on list screens
MPAndroidChart for dashboard pie chart
Input Validation (email, password ≥8 chars)
Snackbars for errors
ProgressBar during loading
FAB on Dashboard → AddTransaction
Logout in menu → clear token → Login
Intents only for navigation (no NavComponent)
MainActivity checks token → redirect to Login or Dashboard

GENERATE ALL FILES
Generate every file exactly as structured above, including:

Full AndroidManifest.xml
All XML layouts (with IDs: etEmail, btnLogin, recyclerView, etc.)
View Binding in Activities
TokenManager with saveToken, getToken(), clearToken()
AuthInterceptor adding Bearer token
ApiResponse<T> sealed class
All UseCases with @Inject constructor
Repository interfaces + @ActivityScoped impls
Dashboard with pie chart, total balance, last 5 transactions
Transaction list with filters (date, category)
Add/Edit forms with validation
Long-click delete on list items
Budgets with progress bars and alerts

BEST PRACTICES (ENFORCE)

DRY, clean, readable code
Meaningful variable names
Inline comments for complex logic
Error handling (try/catch → ApiResponse.Error)
Loading states (ProgressBar)
No hardcoded strings
Use Constants.BASE_URL
Scalable, testable, maintainable

OUTPUT
Generate all files with complete, working code.
Include a README.md with:

Setup steps
How to change IP for physical device
Backend requirements
Screenshots (describe if needed)