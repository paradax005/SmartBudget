# SmartBudget Backend API Documentation

Welcome to the SmartBudget Backend API documentation for mobile developers. This guide will help you integrate the SmartBudget REST API into your Android and iOS applications.

## 📚 Documentation Index

1. **[QUICKSTART.md](./QUICKSTART.md)** - Get started quickly with minimal examples
2. **[SETUP.md](./SETUP.md)** - Environment setup and configuration
3. **[AUTHENTICATION.md](./AUTHENTICATION.md)** - Authentication flow and token management
4. **[ENDPOINTS.md](./ENDPOINTS.md)** - Complete API endpoint reference
5. **[INTEGRATION.md](./INTEGRATION.md)** - Mobile integration guide with examples
6. **[ERROR_HANDLING.md](./ERROR_HANDLING.md)** - Error codes and handling strategies

## 🚀 Quick Overview

### Base URL

- **Development**: `http://localhost:3000/api`
- **Production**: `https://your-production-domain.com/api`

> **Note for Android Emulator**: Use `http://10.0.2.2:3000/api` instead of `localhost`
> 
> **Note for iOS Simulator**: Use `http://localhost:3000/api`

### Authentication

The API uses **JWT (JSON Web Tokens)** for authentication. Most endpoints require a valid JWT token in the `Authorization` header.

**Format**: `Authorization: Bearer <your_jwt_token>`

### Response Format

All API responses follow a consistent format:

**Success Response:**
```json
{
  "data": { ... },
  "message": "Success message (optional)",
  "count": 10 (optional)
}
```

**Error Response:**
```json
{
  "error": "Error message",
  "status": 400
}
```

## 📋 API Endpoints Summary

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/auth/me` - Get current user (protected)

### Accounts
- `GET /api/accounts` - List all accounts (protected)
- `POST /api/accounts` - Create account (protected)
- `PUT /api/accounts/:id` - Update account (protected)
- `DELETE /api/accounts/:id` - Delete account (protected)

### Categories
- `GET /api/categories` - List categories (protected)
- `POST /api/categories` - Create category (protected)
- `PUT /api/categories/:id` - Update category (protected)
- `DELETE /api/categories/:id` - Delete category (protected)

### Transactions
- `GET /api/transactions` - List transactions with filters (protected)
- `POST /api/transactions` - Create transaction (protected)
- `PUT /api/transactions/:id` - Update transaction (protected)
- `DELETE /api/transactions/:id` - Delete transaction (protected)
- `GET /api/transactions/stats/monthly` - Monthly statistics (protected)

### Budgets
- `GET /api/budgets` - List budgets (protected)
- `POST /api/budgets` - Create budget (protected)
- `PUT /api/budgets/:id` - Update budget (protected)
- `DELETE /api/budgets/:id` - Delete budget (protected)
- `GET /api/budgets/status` - Budget status with alerts (protected)

## 🔑 Key Features

- ✅ JWT-based authentication
- ✅ Automatic account balance updates on transactions
- ✅ Budget tracking with automatic spent calculation
- ✅ Monthly statistics and analytics
- ✅ Category-based filtering
- ✅ Date range filtering for transactions
- ✅ Budget alerts when exceeded

## 📱 Mobile-Specific Notes

### Android
- Use `10.0.2.2` instead of `localhost` for emulator
- Add `INTERNET` permission in `AndroidManifest.xml`
- Use `OkHttp` or `Retrofit` for HTTP requests
- Store JWT token securely using `SharedPreferences` or `EncryptedSharedPreferences`

### iOS
- Use `localhost` for simulator
- Use actual device IP for physical device testing
- Use `URLSession` or `Alamofire` for HTTP requests
- Store JWT token securely using `Keychain`

## 🔗 Next Steps

1. Start with **[QUICKSTART.md](./QUICKSTART.md)** for a quick integration example
2. Read **[SETUP.md](./SETUP.md)** to configure your environment
3. Review **[AUTHENTICATION.md](./AUTHENTICATION.md)** for auth implementation
4. Check **[ENDPOINTS.md](./ENDPOINTS.md)** for detailed endpoint documentation
5. Follow **[INTEGRATION.md](./INTEGRATION.md)** for mobile-specific integration patterns

## 📞 Support

For issues or questions:
- Check the error handling guide: **[ERROR_HANDLING.md](./ERROR_HANDLING.md)**
- Review the Swagger documentation at: `http://localhost:3000/api-docs` (when server is running)

---

**Version**: 1.0.0  
**Last Updated**: 2024

