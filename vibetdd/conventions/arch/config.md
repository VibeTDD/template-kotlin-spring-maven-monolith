# Configuration and Constants Management

## Business Configuration

### Prefer Configuration Over Constants
Use external configuration instead of hardcoded constants for business rules and limits.

**Examples of configurable parameters:**
- Business limits (minimum/maximum amounts)
- Allowed value sets (currencies, statuses)
- Business rules thresholds
- Feature flags
- Validation constraints

### Configuration Interface Pattern
Create interfaces for configuration access that abstract the source of configuration values.

### Configuration Implementation Strategy
- **Environment-based**: Read from environment variables with sensible defaults
- **Database-driven**: Store configuration in database for runtime changes
- **Property files**: Use application.properties/yaml for static configuration

## Constants Management

### When to Use Constants
Use constants only for truly immutable values that will never change:

```kotlin
// ✅ Good examples - Values that never change
const val AUTHORIZATION = "Authorization"      // Standard value
const val MIN_CURRENCY_PRECISION = 0.01       // Currency precision is stable
const val BCRYPT_ROUNDS = 12                 // Security parameters
```

### When NOT to Use Constants
Avoid constants for business rules that might change:

```kotlin
// ❌ Bad examples - Should be configurable
const val MAX_PAYMENT_AMOUNT = 1000.0         // Business limit - should be configurable
val ALLOWED_CURRENCIES = setOf("EUR", "USD")  // Business rule - should be configurable
const val MAX_LOGIN_ATTEMPTS = 3             // Security policy - should be configurable
```

### Constants Organization
Organize constants in domain-specific objects:

```kotlin
// ✅ Good - Domain-specific constants
object Header {
    const val AUTHORIZATION = "Authorization"
}

object Security {
    const val BCRYPT_ROUNDS = 12
    
}
```

## Key Principles

**Configuration for Business Rules**: Any business limit, threshold, or rule should be externally configurable
**Constants for Technical Values**: Use constants only for truly immutable technical values
**Domain Organization**: Group constants by business domain or technical area
**Interface Abstraction**: Abstract configuration sources through interfaces for testability
**Default Values**: Always provide sensible defaults for configuration parameters

## Testing Configuration

### Mock Configuration for Tests
Configuration should be easily mockable for testing different business scenarios.

### Test Different Scenarios
- Test both within and outside configured limits
- Verify configuration is retrieved with correct parameters
- Test default value fallback behavior

## Application Configuration

TODO: to be defined