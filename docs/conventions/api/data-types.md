## Data Types in DTOs

### Use Proper Types Instead of Strings

Don't default to String for standard types. Use appropriate Kotlin/Java types based on the specification.

#### Standard Type Mappings

```kotlin
// ✅ Use proper types
data class UserV1(
    val name: String,
    val email: String,
    val birthDate: LocalDate,        // Not String
    val isActive: Boolean,           // Not String
    val age: Int,                    // Not String
    val type: String               // String for enums to avoid version conflicts
)
```

#### Common Type Guidelines

- **UUID**: For identifiers (id, userId, orderId, etc.) - Note: id is part of ModelV1 wrapper
- **LocalDate**: For dates without time (birthDate, startDate, etc.)
- **LocalDateTime**: For dates with time in local timezone
- **Instant**: For timestamps (processedAt, scheduledAt, etc.) - Note: createdAt/updatedAt are in ModelV1 wrapper
- **Boolean**: For flags and boolean properties (isActive, isVerified, etc.)
- **Int/Long**: For numeric values (age, count, version, etc.)
- **BigDecimal**: For monetary values and precise decimal calculations
- **String**: For enums (status, type, category) to avoid version conflicts between services

#### Request Parameters with Proper Types

```kotlin
data class CreateUserParamsV1(
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val name: String,

    @field:NotBlank
    @field:Email
    val email: String,

    @field:Past
    val birthDate: LocalDate,

    @field:Positive
    val age: Int
)

data class CreatePaymentParamsV1(
    @field:NotNull
    val userId: UUID,

    @field:Positive
    @field:DecimalMax("10000.00")
    val amount: BigDecimal,

    @field:NotBlank
    @field:Size(min = 3, max = 3)
    @field:Pattern(regexp = "^[A-Z]{3}$")
    val currency: String,            // ISO 4217 currency code format

    val scheduledAt: Instant?
)
```

#### Path Variables with Proper Types

```kotlin
@RestController
class UserControllerV1 {

    @GetMapping("/v1/users/{id}")
    fun get(@PathVariable id: UUID): ModelV1<UserV1>  // UUID, not String

    @PutMapping("/v1/users/{id}")
    fun update(
        @PathVariable id: UUID,                        // UUID, not String
        @RequestBody @Valid params: UpdateUserParamsV1
    ): ModelV1<UserV1>
}
```

### JSON Serialization Notes

- Spring Boot automatically handles serialization/deserialization for standard types
- UUID serializes to string in JSON but maintains type safety in code
- Instant serializes to ISO-8601 string format
- LocalDate serializes to ISO date format (YYYY-MM-DD)
- BigDecimal serializes to numeric value in JSON

### When to Use String

Use String for:

- Actual text content (name, description, notes)
- **Enums represented as strings in DTOs** (status, type, category) - **Always use strings for enums to avoid version
  conflicts between services**
- External system identifiers that are genuinely strings
- Free-form text fields
- Currency codes, country codes, and other standardized string formats