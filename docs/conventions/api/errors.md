## API Error Response Pattern

### Unified Error Structure
All error responses follow a consistent format with an array of errors, regardless of the HTTP status code or error type.
Use common `ErrorResponseV1` from the package `base_package.api.commons.dto.response`

```kotlin
data class ErrorResponseV1(
    val errors: List<ErrorV1>
)

data class ErrorV1(
    val code: String,
    val message: String,
    val attributes: Map<String, Any?> = mapOf()
)
```

For input validation errors, the `attributes` map will contain `field` and `value` properties to identify which field failed validation and what value was rejected.

### HTTP Status Code Mapping

Map each error type to appropriate HTTP status code:
- `400 Bad Request` - Invalid request format, malformed JSON
- `401 Unauthorized` - Authentication required or invalid credentials
- `403 Forbidden` - Authenticated but not authorized to perform this action
- `404 Not Found` - Resource doesn't exist
- `409 Conflict` - Resource conflicts, duplicate keys, version conflicts
- `422 Unprocessable Entity` - Input validation errors, business rule violations
- `429 Too Many Requests` - Rate limiting violations
- `500 Internal Server Error` - System failures, unexpected errors

### Standard Error Codes

#### Input Validation Codes (Spring Bean Validation)
- `NotNull` - Field cannot be null
- `NotEmpty` - Field cannot be empty (for collections, maps, arrays)
- `NotBlank` - Field cannot be blank (for strings - null, empty, or whitespace only)
- `Size` - Field size is outside the specified range
- `Min` - Numeric value is below minimum
- `Max` - Numeric value is above maximum
- `DecimalMin` - Decimal value is below minimum
- `DecimalMax` - Decimal value is above maximum
- `Positive` - Value must be positive (> 0)
- `PositiveOrZero` - Value must be positive or zero (>= 0)
- `Negative` - Value must be negative (< 0)
- `NegativeOrZero` - Value must be negative or zero (<= 0)
- `Email` - Invalid email format
- `Pattern` - Value doesn't match the specified regex pattern
- `Past` - Date must be in the past
- `PastOrPresent` - Date must be in the past or present
- `Future` - Date must be in the future
- `FutureOrPresent` - Date must be in the future or present

#### General Error Codes
```kotlin
object ErrorCode {
    const val NOT_FOUND = "NotFound"
    const val BAD_REQUEST = "BadRequest"
    const val INTERNAL_ERROR = "InternalError"
    const val FORBIDDEN_ACCESS = "ForbiddenAccess"
    const val OUTDATED_VERSION = "OutdatedVersion"
    const val DUPLICATED_KEY = "DuplicatedKey"
}
```

### Examples

#### Validation Errors (422)
```kotlin
ErrorResponseV1(
    errors = listOf(
        ErrorV1(
            code = "NOT_NULL",
            message = "Email is required",
            attributes = mapOf("field" to "email", "value" to null)
        ),
        ErrorV1(
            code = "INVALID_FORMAT", 
            message = "Invalid email format",
            attributes = mapOf("field" to "email", "value" to "invalid-email")
        )
    )
)
```

#### Business Logic Errors (409)
```kotlin
ErrorResponseV1(
    errors = listOf(
        ErrorV1(
            code = "DUPLICATE_EMAIL",
            message = "User with this email already exists",
            attributes = mapOf("email" to "john@example.com")
        )
    )
)
```