## Naming Conventions

### Request DTO Naming Pattern
Use consistent verb-first naming for request objects with domain context clear from class name.

```kotlin
// ✅ Preferred Pattern - verb-first naming
data class CreateUserParamsV1()
data class UpdateUserParamsV1()
data class DeleteUserParamsV1()
data class GetUserQueryV1()
```

### 3.2 Response DTO Naming Pattern
Keep response objects simple and descriptive without redundant domain repetition.

```kotlin
// Simple entity response
data class UserV1(
    val name: String,
    val email: String
)
```

### Query Parameter Naming
Use consistent suffixes for different query types.

```kotlin
// Request body parameters
data class CreateUserParamsV1(
    val name: String,
    val email: String
)

// URL query parameters
data class GetUserQueryV1(
    val includeInactive: Boolean = false,
)

// Complex filtering objects
data class UserFilterV1(
    val status: List<String> = emptyList(),
    val dateRange: RangeV1<LocalDate>? = null
)

// Complete request wrapper
data class SearchUserParamsV1(
    val page: PageQueryV1,
    val sort: SortQueryV1,
    val filters: UserFilterV1
)
```

### Endpoint Naming

- Follow REST standards
- Use versions: `/v1/users`, `/v2/users`
- All endpoints must start with version: `/v1/`, except public endpoint (if requested in spec): `/public/v1/`