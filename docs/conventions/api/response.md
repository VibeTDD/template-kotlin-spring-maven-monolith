## API Response 

### Read Operations: Universal Entity Wrapper
For read operations only, wrap business data with standardized metadata using generic `ModelV1<T>` from the package `base_package.api.commons.dto.response`

```kotlin
data class ModelV1<T>(
    val id: String,
    val version: Int?,
    val createdAt: String,
    val updatedAt: String,
    val data: T
)

// example
ModelV1(
    id = "272B2B03-3097-4FEF-AD7E-2EC3F7BF1E1B",
    version = 1,
    createdAt = "2025-06-19T07:49:29.546Z",
    updatedAt = "2025-06-20T07:24:20.531Z",
    data = UserV1(
        name = "John",
        email = "john@email.com"
    )
)
```

### Standard Pagination Response
All paginated endpoints return consistent structure. Use `PageV1<T>` from the package `base_package.api.commons.dto.response`

```kotlin
data class PageV1<T>(
    val totalElements: Int,
    val totalPages: Int,
    val items: List<T>
)

// example
PageV1<UserV1>(
    totalElements = 8,
    totalPages = 1,
    items = listOf(
        UserV1(
            name = "John",
            email = "john@email.com"
        )
    )
)
```

### Operation Status Pattern
For operations that need to communicate status information. Use `StatusV1` from the package `base_package.api.commons.dto.response`

```kotlin
data class StatusV1(
    val name: String,
    val msg: String? = null
)

// Simple status without additional context
StatusV1(
    name = "processing",
    msg = null
)

// Status with additional context message
StatusV1(
    name = "failed",
    msg = "Connection timeout after 30 seconds"
)
```
