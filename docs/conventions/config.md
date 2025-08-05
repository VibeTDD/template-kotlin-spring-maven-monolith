## 1. Configuration and Constants Management

### 1.1 Prefer Configuration Over Constants
Use external configuration instead of hardcoded constants:

```kotlin
// ✅ Good - Configuration-driven values
interface PaymentConfiguration {
    fun getMaxAmount(): Double
    fun getAllowedCurrencies(): Set<String>
    fun getRetryAttempts(): Int
}

class DefaultPaymentConfiguration : PaymentConfiguration {
    override fun getMaxAmount(): Double = environmentConfig.getDouble("payment.max.amount", 1000.0)
    override fun getAllowedCurrencies(): Set<String> = environmentConfig.getStringSet("payment.currencies", setOf("EUR", "USD"))
    override fun getRetryAttempts(): Int = environmentConfig.getInt("payment.retry.attempts", 3)
}

// ✅ Good - Use constants only when truly constant
class PaymentConstants {
    companion object {
        const val PAYMENT_ID_LENGTH = 36  // UUID length never changes
        const val MIN_AMOUNT_PRECISION = 0.01  // Currency precision is stable
    }
}

// ❌ Bad -  Business rules as constants
class PaymentConstants {
    companion object {
        const val MAX_PAYMENT_AMOUNT = 1000.0  // This should be configurable
        val ALLOWED_CURRENCIES = setOf("EUR", "USD")  // This should be configurable
    }
}
```

### 2. Constants Organization
When constants are needed, organize them in separate files:

```kotlin
// ✅ Good - PaymentConstants.kt - Domain-specific constants
object PaymentConstants {
    const val PAYMENT_ID_LENGTH = 36
    const val TRANSACTION_TIMEOUT_SECONDS = 30
}

// ❌ Bad - Global Constants file 
object GlobalConstants {
    const val DEFAULT_PAGE_SIZE = 20
    const val MAX_RETRY_ATTEMPTS = 3
    const val UUID_PATTERN = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
}
```