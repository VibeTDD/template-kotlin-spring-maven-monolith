## API Request

### Write Operations: Specific Parameter Objects
For write operations, create purpose-built parameter objects without metadata.

```kotlin
// Create operation parameters
data class CreateUserParamsV1(
    val name: String,
    val email: String
)

// Update operation parameters
data class UpdateUserParamsV1(
    val name: String,
    val email: String
)
```

### Request Validation Patterns

Add validation annotations to ensure data integrity and provide clear error messages.

```kotlin
data class CreateUserParamsV1(
    @field:NotBlank
    @field:Size(min = 1, max = 100)
    val name: String,

    @field:NotBlank
    @field:Email
    val email: String
)

data class UpdateUserParamsV1(
    @field:Size(min = 1, max = 100)
    val name: String?,

    @field:Email
    val email: String?
)
```

### Validation Guidelines

- Use `@field:` prefix for all validation annotations in Kotlin data classes
- `@NotBlank` for required string fields (handles null, empty, and whitespace)
- `@NotNull` for required non-string fields
- `@Size` for string length and collection size constraints
- `@Positive`/`@PositiveOrZero` for numeric constraints
- `@Email` for email format validation
- `@Pattern` for custom regex validation
- Make fields nullable in update DTOs when partial updates are allowed