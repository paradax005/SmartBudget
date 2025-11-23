# Setup and Configuration Guide

This guide explains how to configure your mobile app to connect to the SmartBudget backend API.

## Environment Configuration

### Base URLs

The API base URL varies depending on your development environment:

#### Development
- **Android Emulator**: `http://10.0.2.2:3000/api`
- **iOS Simulator**: `http://localhost:3000/api`
- **Physical Device**: `http://<your-computer-ip>:3000/api`

#### Staging
- `https://staging-api.smartbudget.com/api`

#### Production
- `https://api.smartbudget.com/api`

### Finding Your Computer's IP Address

**macOS/Linux:**
```bash
ifconfig | grep "inet " | grep -v 127.0.0.1
```

**Windows:**
```bash
ipconfig
```

Look for IPv4 address (usually starts with `192.168.x.x` or `10.0.x.x`)

## Android Configuration

### 1. Add Internet Permission

In `AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application
        android:usesCleartextTraffic="true"
        ...>
        <!-- Your app components -->
    </application>
</manifest>
```

> **Note**: `usesCleartextTraffic="true"` is required for HTTP connections. For production, use HTTPS and remove this.

### 2. Network Security Configuration (For HTTP in Production)

Create `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">localhost</domain>
    </domain-config>
</network-security-config>
```

Reference in `AndroidManifest.xml`:

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
</application>
```

### 3. Build Configuration

**build.gradle (Module: app)**

```gradle
dependencies {
    // HTTP Client
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.11.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.11.0'
    
    // Coroutines for async
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
}
```

### 4. API Configuration Class

Create `ApiConfig.kt`:

```kotlin
object ApiConfig {
    // Build variants
    const val BASE_URL_DEV = "http://10.0.2.2:3000/api"
    const val BASE_URL_STAGING = "https://staging-api.smartbudget.com/api"
    const val BASE_URL_PROD = "https://api.smartbudget.com/api"
    
    // Get base URL based on build variant
    fun getBaseUrl(): String {
        return when (BuildConfig.BUILD_TYPE) {
            "debug" -> BASE_URL_DEV
            "staging" -> BASE_URL_STAGING
            "release" -> BASE_URL_PROD
            else -> BASE_URL_DEV
        }
    }
}
```

### 5. Secure Token Storage

Use `EncryptedSharedPreferences` for storing JWT tokens:

```kotlin
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(private val context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    
    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }
    
    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
    
    fun clearToken() {
        sharedPreferences.edit().clear().apply()
    }
}
```

## iOS Configuration

### 1. Info.plist Configuration

Add to `Info.plist` for HTTP connections (development only):

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSAllowsArbitraryLoads</key>
    <true/>
    <!-- For production, use specific domains instead -->
</dict>
```

For production, use specific domains:

```xml
<key>NSAppTransportSecurity</key>
<dict>
    <key>NSExceptionDomains</key>
    <dict>
        <key>api.smartbudget.com</key>
        <dict>
            <key>NSExceptionAllowsInsecureHTTPLoads</key>
            <false/>
            <key>NSIncludesSubdomains</key>
            <true/>
        </dict>
    </dict>
</dict>
```

### 2. Build Configuration

**Podfile** (if using CocoaPods):

```ruby
platform :ios, '13.0'

target 'YourApp' do
  use_frameworks!
  
  pod 'Alamofire', '~> 5.8'
end
```

Or use Swift Package Manager:

1. File → Add Packages
2. Add: `https://github.com/Alamofire/Alamofire.git`

### 3. API Configuration

Create `ApiConfig.swift`:

```swift
import Foundation

struct ApiConfig {
    #if DEBUG
    static let baseURL = "http://localhost:3000/api"
    #elseif STAGING
    static let baseURL = "https://staging-api.smartbudget.com/api"
    #else
    static let baseURL = "https://api.smartbudget.com/api"
    #endif
}
```

### 4. Secure Token Storage

Use Keychain for storing JWT tokens:

```swift
import Security

class TokenManager {
    private let service = "com.yourapp.smartbudget"
    private let tokenKey = "auth_token"
    
    func saveToken(_ token: String) {
        let data = token.data(using: .utf8)!
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: tokenKey,
            kSecValueData as String: data
        ]
        
        SecItemDelete(query as CFDictionary)
        SecItemAdd(query as CFDictionary, nil)
    }
    
    func getToken() -> String? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: tokenKey,
            kSecReturnData as String: true
        ]
        
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        
        if status == errSecSuccess,
           let data = result as? Data,
           let token = String(data: data, encoding: .utf8) {
            return token
        }
        return nil
    }
    
    func clearToken() {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: tokenKey
        ]
        SecItemDelete(query as CFDictionary)
    }
}
```

## Build Variants / Schemes

### Android Build Variants

In `build.gradle (Module: app)`:

```gradle
android {
    buildTypes {
        debug {
            buildConfigField "String", "API_BASE_URL", '"http://10.0.2.2:3000/api"'
        }
        staging {
            buildConfigField "String", "API_BASE_URL", '"https://staging-api.smartbudget.com/api"'
        }
        release {
            buildConfigField "String", "API_BASE_URL", '"https://api.smartbudget.com/api"'
        }
    }
}
```

### iOS Schemes

1. Product → Scheme → Manage Schemes
2. Create schemes: `Debug`, `Staging`, `Release`
3. Set different base URLs per scheme using build configurations

## Testing Connection

### Health Check Endpoint

Test if the server is reachable:

**Request:**
```
GET http://localhost:3000/health
```

**Response:**
```json
{
  "status": "OK",
  "message": "Server is running",
  "timestamp": "2024-01-15T10:30:00.000Z"
}
```

### Android Test

```kotlin
val client = OkHttpClient()
val request = Request.Builder()
    .url("http://10.0.2.2:3000/health")
    .build()

client.newCall(request).enqueue(object : Callback {
    override fun onResponse(call: Call, response: Response) {
        Log.d("API", "Server is reachable: ${response.body?.string()}")
    }
    
    override fun onFailure(call: Call, e: IOException) {
        Log.e("API", "Cannot reach server: ${e.message}")
    }
})
```

### iOS Test

```swift
let url = URL(string: "http://localhost:3000/health")!
URLSession.shared.dataTask(with: url) { data, response, error in
    if let data = data {
        print("Server is reachable: \(String(data: data, encoding: .utf8) ?? "")")
    } else {
        print("Cannot reach server: \(error?.localizedDescription ?? "")")
    }
}.resume()
```

## Common Issues

### Android Emulator Connection Issues

**Problem**: Cannot connect to `localhost:3000`

**Solution**: Use `10.0.2.2` instead of `localhost`

### iOS Simulator Connection Issues

**Problem**: Connection refused

**Solution**: 
1. Ensure server is running
2. Check firewall settings
3. For physical device, use computer's IP address

### CORS Errors

**Problem**: CORS policy blocking requests

**Solution**: Backend should have CORS enabled. If issues persist, check backend CORS configuration.

### SSL/Certificate Errors

**Problem**: Certificate validation errors in production

**Solution**: 
- Ensure backend uses valid SSL certificate
- For development, allow cleartext traffic (see Android config above)
- Never disable SSL validation in production

## Next Steps

- Configure authentication: **[AUTHENTICATION.md](./AUTHENTICATION.md)**
- Review API endpoints: **[ENDPOINTS.md](./ENDPOINTS.md)**
- Start integration: **[QUICKSTART.md](./QUICKSTART.md)**

