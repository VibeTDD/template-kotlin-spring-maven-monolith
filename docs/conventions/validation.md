# Kotlin Validation Conventions

This document provides concrete Kotlin validation patterns for hexagonal architecture with Spring Boot.

## 1. Two-Layer Validation Strategy

### 1.1 Validation Layers
1. **Input Validation**: Basic data validation at controller level using Bean Validation annotations
2. **Business Validation**: Domain-specific rules validation in the domain layer

### 1.2 Layer Responsibilities
- **Input layer**: Format, type, basic constraints (email format, string length, number ranges)
- **Domain layer**: Business rules, user limits, complex constraints, cross-field validation

## 2. Input Validation (Controller Layer)

### 2.1 Bean Validation Annotations
Use Spring's Bean Validation annotations on DTOs for basic input validation:

```kotlin
data class CreateUserParamsV1(
    @field:NotBlank
    @field:Size(min = 2, max = 100)
    val name: String,
    
    @field:NotBlank
    @field:Email
    val email: String,
    
    @field:Min(18)
    @field:Max(150)
    val age: Int?,
    
    @field:Pattern(regexp = "^[A-Z]{3}$")
    val currency: String
)
```

### 2.2 Controller Validation
Enable validation using `@Valid` annotation in controller methods:

```kotlin
@RestController
@RequestMapping("/v1/users")
class UserControllerV1(
    private val userService: UserService
) {
    
    @PostMapping
    fun create(@RequestBody @Valid params: CreateUserParamsV1): UserV1 {
        val user = userService.create(params.toModel())
        return user.toV1()
    }
}
```

### 2.3 Common Validation Annotations
```kotlin
// Required fields
@field:NotBlank      // String cannot be null, empty, or whitespace
@field:NotEmpty      // Collection/Map/Array cannot be empty

// String constraints
@field:Size(min = 2, max = 100)     // String length
@field:Pattern(regexp = "^[A-Z]+$") // Regex pattern
@field:Email                         // Email format

// Number constraints  
@field:Min(0)           // Minimum value
@field:Max(100)         // Maximum value
@field:Positive         // Must be positive
@field:PositiveOrZero   // Must be positive or zero
@field:Digits(integer = 3, fraction = 2) // Number format

// Date constraints
@field:Past             // Date in the past
@field:Future           // Date in the future
@field:PastOrPresent    // Date in past or today
```

## 3. Business Validation (Domain Layer)

### 3.1 Validation Exception Structure
Define typed exceptions for business validation errors:

```kotlin
class ValidationException(
    val errors: List<ValidationError>
) : Exception(errors.joinToString("; ") { it.message })

data class ValidationError(
    val code: String,              // CamelCase format, e.g., "LimitExceeded"
    val message: String,            // Human-readable message
    val attributes: Map<String, Any?> = emptyMap() // Context attributes
)
```

### 3.2 Validator Pattern
Create separate validator classes for each business rule:

```kotlin
interface UserValidator {
    fun validate(user: User): List<ValidationError>
}

class UserLimitValidator(
    private val userRepository: UserRepository,
    private val configuration: UserConfiguration
) : UserValidator {
    
    override fun validate(user: User): List<ValidationError> {
        val currentCount = userRepository.countByEmail(user.email)
        val maxAccounts = configuration.getMaxAccountsPerEmail()

        return listOf(
            ValidationError(
                code = "AccountLimitExceeded",
                message = "Maximum $maxAccounts accounts allowed per email",
                attributes = mapOf(
                    "currentCount" to currentCount,
                    "maxAllowed" to maxAccounts
                )
            )
        )
    }
}
```

### 3.3 Collect All Errors Pattern
Validate all rules and collect errors before throwing exception:

```kotlin
class CreateUserUseCase(
    private val validators: List<UserValidator>,
    private val userRepository: UserRepositoryPort
) {
    
    fun create(userData: CreateUserData): User {
        // Collect all validation errors
        val errors = validators.flatMap { it.validate(user) }

        // Throw if any errors found
        if (errors.isNotEmpty()) {
            throw ValidationException(errors)
        }
        
        val user = User(
            id = UUID.randomUUID(),
            name = userData.name,
            email = userData.email,
            age = userData.age
        )
        
        return userRepository.save(user)
    }
}
```

## 4. Validation Best Practices

### 4.1 Error Code Conventions
- Use **CamelCase** for error codes (e.g., `LimitExceeded`, `InvalidFormat`)
- Make codes specific and searchable
- Group related codes with common prefixes (e.g., `PaymentLimitExceeded`, `PaymentCurrencyInvalid`)

### 4.2 Error Message Guidelines
- Provide clear, actionable messages
- Include relevant limits or constraints in the message
- Avoid technical jargon in user-facing messages

### 4.3 Attributes Usage
Include context that helps debugging and user understanding:
```kotlin
ValidationError(
    code = "LimitExceeded",
    message = "Daily limit of $1000 exceeded",
    attributes = mapOf(
        "currentTotal" to 950.00,
        "requestedAmount" to 100.00,
        "dailyLimit" to 1000.00,
        "resetTime" to "2024-01-15T00:00:00Z"
    )
)
```

### 4.4 Validation Organization
- **One validator per business rule** for maintainability
- **Compose validators** in services for complete validation
- **Test validators independently** for focused testing
- **Mock external dependencies** in validator tests

## 5. Common Anti-Patterns to Avoid

### 5.1 Validation Anti-Patterns
- ❌ Throwing on first error instead of collecting all errors
- ❌ Mixing input validation with business validation
- ❌ Hardcoding limits in validators instead of using configuration
- ❌ Generic error messages without context
- ❌ Validation logic in controllers or repositories