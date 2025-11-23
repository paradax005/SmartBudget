# API Endpoints Reference

Complete reference for all SmartBudget API endpoints with request/response examples.

## Base URL

- **Development**: `http://localhost:3000/api` (iOS Simulator)
- **Development**: `http://10.0.2.2:3000/api` (Android Emulator)
- **Production**: `https://api.smartbudget.com/api`

## Authentication

Most endpoints require authentication. Include the JWT token in the `Authorization` header:

```
Authorization: Bearer <your_jwt_token>
```

---

## Authentication Endpoints

### Register User

**POST** `/api/auth/register`

Register a new user account.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "John Doe"
}
```

**Response (201 Created):**
```json
{
  "message": "User registered successfully",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "email": "user@example.com",
    "fullName": "John Doe"
  }
}
```

**Error Responses:**
- `400` - Validation error or user already exists
- `500` - Server error

---

### Login User

**POST** `/api/auth/login`

Login with email and password.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "email": "user@example.com",
    "fullName": "John Doe"
  }
}
```

**Error Responses:**
- `400` - Validation error
- `401` - Invalid credentials
- `500` - Server error

---

### Get Current User

**GET** `/api/auth/me`

Get current authenticated user details.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "email": "user@example.com",
    "fullName": "John Doe",
    "createdAt": "2024-01-15T10:30:00.000Z"
  }
}
```

**Error Responses:**
- `401` - Unauthorized (invalid or expired token)
- `500` - Server error

---

## Account Endpoints

### Get All Accounts

**GET** `/api/accounts`

Get all accounts for the authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "data": [
    {
      "_id": "507f1f77bcf86cd799439011",
      "userId": "507f1f77bcf86cd799439012",
      "name": "Main Checking",
      "type": "bank",
      "balance": 5000.00,
      "currency": "USD",
      "createdAt": "2024-01-15T10:30:00.000Z",
      "updatedAt": "2024-01-15T10:30:00.000Z"
    }
  ],
  "count": 1
}
```

**Account Types:**
- `bank` - Bank account
- `cash` - Cash account
- `credit_card` - Credit card
- `savings` - Savings account

---

### Create Account

**POST** `/api/accounts`

Create a new account.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Main Checking",
  "type": "bank",
  "balance": 5000.00,
  "currency": "USD"
}
```

**Response (201 Created):**
```json
{
  "message": "Account created successfully",
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "userId": "507f1f77bcf86cd799439012",
    "name": "Main Checking",
    "type": "bank",
    "balance": 5000.00,
    "currency": "USD",
    "createdAt": "2024-01-15T10:30:00.000Z",
    "updatedAt": "2024-01-15T10:30:00.000Z"
  }
}
```

**Error Responses:**
- `400` - Validation error
- `401` - Unauthorized
- `500` - Server error

---

### Update Account

**PUT** `/api/accounts/:id`

Update an existing account.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body (all fields optional):**
```json
{
  "name": "Updated Account Name",
  "type": "savings",
  "balance": 6000.00,
  "currency": "EUR"
}
```

**Response (200 OK):**
```json
{
  "message": "Account updated successfully",
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "name": "Updated Account Name",
    "type": "savings",
    "balance": 6000.00,
    "currency": "EUR",
    "updatedAt": "2024-01-15T11:00:00.000Z"
  }
}
```

**Error Responses:**
- `400` - Validation error or invalid ID
- `401` - Unauthorized
- `404` - Account not found
- `500` - Server error

---

### Delete Account

**DELETE** `/api/accounts/:id`

Delete an account. This will also delete all related transactions.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "message": "Account deleted successfully"
}
```

**Error Responses:**
- `400` - Invalid ID
- `401` - Unauthorized
- `404` - Account not found
- `500` - Server error

---

## Category Endpoints

### Get All Categories

**GET** `/api/categories`

Get all categories for the authenticated user.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `type` (optional) - Filter by type: `expense` or `income`

**Example:**
```
GET /api/categories?type=expense
```

**Response (200 OK):**
```json
{
  "data": [
    {
      "_id": "507f1f77bcf86cd799439011",
      "userId": "507f1f77bcf86cd799439012",
      "name": "Groceries",
      "type": "expense",
      "icon": "🛒",
      "color": "#FF5733",
      "isDefault": false,
      "createdAt": "2024-01-15T10:30:00.000Z",
      "updatedAt": "2024-01-15T10:30:00.000Z"
    }
  ],
  "count": 1
}
```

---

### Create Category

**POST** `/api/categories`

Create a new category.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Groceries",
  "type": "expense",
  "icon": "🛒",
  "color": "#FF5733"
}
```

**Required Fields:**
- `name` - Category name
- `type` - `expense` or `income`

**Optional Fields:**
- `icon` - Icon emoji or identifier
- `color` - Hex color code (default: `#000000`)

**Response (201 Created):**
```json
{
  "message": "Category created successfully",
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "name": "Groceries",
    "type": "expense",
    "icon": "🛒",
    "color": "#FF5733",
    "isDefault": false
  }
}
```

---

### Update Category

**PUT** `/api/categories/:id`

Update an existing category.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body (all fields optional):**
```json
{
  "name": "Updated Category",
  "type": "income",
  "icon": "💰",
  "color": "#33FF57"
}
```

**Response (200 OK):**
```json
{
  "message": "Category updated successfully",
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "name": "Updated Category",
    "type": "income",
    "icon": "💰",
    "color": "#33FF57"
  }
}
```

---

### Delete Category

**DELETE** `/api/categories/:id`

Delete a category. Cannot delete if used in transactions or budgets.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "message": "Category deleted successfully"
}
```

**Error Responses:**
- `400` - Category is being used in transactions or budgets
- `404` - Category not found

---

## Transaction Endpoints

### Get All Transactions

**GET** `/api/transactions`

Get all transactions with optional filters.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `startDate` (optional) - ISO 8601 date format: `2024-01-01T00:00:00Z`
- `endDate` (optional) - ISO 8601 date format: `2024-01-31T23:59:59Z`
- `categoryId` (optional) - Filter by category ID
- `accountId` (optional) - Filter by account ID

**Example:**
```
GET /api/transactions?startDate=2024-01-01T00:00:00Z&endDate=2024-01-31T23:59:59Z&categoryId=507f1f77bcf86cd799439011
```

**Response (200 OK):**
```json
{
  "data": [
    {
      "_id": "507f1f77bcf86cd799439011",
      "userId": "507f1f77bcf86cd799439012",
      "accountId": {
        "_id": "507f1f77bcf86cd799439013",
        "name": "Main Checking",
        "type": "bank"
      },
      "categoryId": {
        "_id": "507f1f77bcf86cd799439014",
        "name": "Groceries",
        "type": "expense"
      },
      "amount": 150.50,
      "type": "expense",
      "description": "Weekly groceries",
      "date": "2024-01-15T10:30:00.000Z",
      "createdAt": "2024-01-15T10:30:00.000Z",
      "updatedAt": "2024-01-15T10:30:00.000Z"
    }
  ],
  "count": 1
}
```

---

### Create Transaction

**POST** `/api/transactions`

Create a new transaction. Account balance is automatically updated.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "accountId": "507f1f77bcf86cd799439013",
  "categoryId": "507f1f77bcf86cd799439014",
  "amount": 150.50,
  "type": "expense",
  "description": "Weekly groceries",
  "date": "2024-01-15T10:30:00Z"
}
```

**Required Fields:**
- `accountId` - Account ID (MongoDB ObjectId)
- `categoryId` - Category ID (MongoDB ObjectId)
- `amount` - Amount (positive number)
- `type` - `expense` or `income`
- `date` - ISO 8601 date format

**Optional Fields:**
- `description` - Transaction description

**Response (201 Created):**
```json
{
  "message": "Transaction created successfully",
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "accountId": "507f1f77bcf86cd799439013",
    "categoryId": "507f1f77bcf86cd799439014",
    "amount": 150.50,
    "type": "expense",
    "description": "Weekly groceries",
    "date": "2024-01-15T10:30:00.000Z"
  },
  "accountBalance": 4849.50
}
```

**Note:** Account balance is automatically updated:
- **Income**: Balance increases by amount
- **Expense**: Balance decreases by amount

---

### Update Transaction

**PUT** `/api/transactions/:id`

Update an existing transaction. Account balance is automatically adjusted.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body (all fields optional):**
```json
{
  "amount": 200.00,
  "description": "Updated description",
  "date": "2024-01-16T10:30:00Z"
}
```

**Response (200 OK):**
```json
{
  "message": "Transaction updated successfully",
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "amount": 200.00,
    "description": "Updated description",
    "date": "2024-01-16T10:30:00.000Z"
  },
  "accountBalance": 4800.00
}
```

---

### Delete Transaction

**DELETE** `/api/transactions/:id`

Delete a transaction. Account balance is automatically reverted.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "message": "Transaction deleted successfully"
}
```

---

### Get Monthly Statistics

**GET** `/api/transactions/stats/monthly`

Get monthly transaction statistics.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `month` (required) - Month (1-12)
- `year` (required) - Year (e.g., 2024)

**Example:**
```
GET /api/transactions/stats/monthly?month=1&year=2024
```

**Response (200 OK):**
```json
{
  "data": {
    "month": 1,
    "year": 2024,
    "totalIncome": 5000.00,
    "totalExpense": 2500.00,
    "incomeCount": 5,
    "expenseCount": 10,
    "netAmount": 2500.00,
    "byCategory": {
      "income": [
        {
          "categoryId": "507f1f77bcf86cd799439011",
          "categoryName": "Salary",
          "total": 5000.00,
          "count": 1
        }
      ],
      "expense": [
        {
          "categoryId": "507f1f77bcf86cd799439012",
          "categoryName": "Groceries",
          "total": 1500.00,
          "count": 5
        }
      ]
    }
  }
}
```

---

## Budget Endpoints

### Get All Budgets

**GET** `/api/budgets`

Get all budgets, optionally filtered by month/year.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `month` (optional) - Month (1-12)
- `year` (optional) - Year (e.g., 2024)

**Example:**
```
GET /api/budgets?month=1&year=2024
```

**Response (200 OK):**
```json
{
  "data": [
    {
      "_id": "507f1f77bcf86cd799439011",
      "userId": "507f1f77bcf86cd799439012",
      "categoryId": {
        "_id": "507f1f77bcf86cd799439013",
        "name": "Groceries",
        "type": "expense",
        "icon": "🛒",
        "color": "#FF5733"
      },
      "amount": 1000.00,
      "month": 1,
      "year": 2024,
      "spent": 750.00,
      "createdAt": "2024-01-15T10:30:00.000Z",
      "updatedAt": "2024-01-15T10:30:00.000Z"
    }
  ],
  "count": 1
}
```

**Note:** `spent` is automatically calculated from transactions.

---

### Create Budget

**POST** `/api/budgets`

Create a new budget for a category and month/year.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "categoryId": "507f1f77bcf86cd799439013",
  "amount": 1000.00,
  "month": 1,
  "year": 2024
}
```

**Required Fields:**
- `categoryId` - Category ID (MongoDB ObjectId)
- `amount` - Budget amount (positive number)
- `month` - Month (1-12)
- `year` - Year (e.g., 2024)

**Response (201 Created):**
```json
{
  "message": "Budget created successfully",
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "categoryId": {
      "_id": "507f1f77bcf86cd799439013",
      "name": "Groceries",
      "type": "expense"
    },
    "amount": 1000.00,
    "month": 1,
    "year": 2024,
    "spent": 0
  }
}
```

**Error Responses:**
- `400` - Budget already exists for this category/month/year

---

### Update Budget

**PUT** `/api/budgets/:id`

Update an existing budget.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body (all fields optional):**
```json
{
  "amount": 1500.00,
  "month": 2,
  "year": 2024
}
```

**Response (200 OK):**
```json
{
  "message": "Budget updated successfully",
  "data": {
    "_id": "507f1f77bcf86cd799439011",
    "amount": 1500.00,
    "month": 2,
    "year": 2024,
    "spent": 0
  }
}
```

---

### Delete Budget

**DELETE** `/api/budgets/:id`

Delete a budget.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
{
  "message": "Budget deleted successfully"
}
```

---

### Get Budget Status

**GET** `/api/budgets/status`

Get budget status with alerts for a specific month/year.

**Headers:**
```
Authorization: Bearer <token>
```

**Query Parameters:**
- `month` (required) - Month (1-12)
- `year` (required) - Year (e.g., 2024)

**Example:**
```
GET /api/budgets/status?month=1&year=2024
```

**Response (200 OK):**
```json
{
  "month": 1,
  "year": 2024,
  "budgets": [
    {
      "id": "507f1f77bcf86cd799439011",
      "category": {
        "_id": "507f1f77bcf86cd799439013",
        "name": "Groceries",
        "type": "expense",
        "icon": "🛒",
        "color": "#FF5733"
      },
      "amount": 1000.00,
      "spent": 1200.00,
      "remaining": -200.00,
      "percentage": "120.00",
      "isExceeded": true
    }
  ],
  "alerts": [
    "Budget exceeded for Groceries"
  ],
  "alertCount": 1
}
```

**Response Fields:**
- `spent` - Amount spent in the month
- `remaining` - Remaining budget (can be negative)
- `percentage` - Percentage of budget used
- `isExceeded` - Whether budget is exceeded
- `alerts` - Array of alert messages for exceeded budgets

---

## Date Format

All dates should be in **ISO 8601** format:

```
2024-01-15T10:30:00Z
```

**Examples:**
- `2024-01-15T10:30:00Z` - UTC time
- `2024-01-15T10:30:00+05:00` - With timezone offset

---

## Object IDs

All IDs are MongoDB ObjectIds (24-character hexadecimal strings):

```
507f1f77bcf86cd799439011
```

---

## Next Steps

- Learn integration patterns: **[INTEGRATION.md](./INTEGRATION.md)**
- Handle errors properly: **[ERROR_HANDLING.md](./ERROR_HANDLING.md)**
- Review authentication: **[AUTHENTICATION.md](./AUTHENTICATION.md)**

