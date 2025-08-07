## 1. API Versioning And Segregation Strategy

### 1.1 Comprehensive Versioning Approach
Apply versioning consistently across all API layers with version suffix pattern (`V1`, `V2`).

**DTOs**: All data transfer objects end with version suffix
**Controllers**: Controller classes include version
**URL Paths**: API endpoints include version prefix
**Packages**: Organize by version when necessary

```kotlin
// Request DTOs
data class CreateUserParamsV1(
    val name: String,
    val email: String
)

data class UpdateUserParamsV1(
    val name: String,
    val email: String
)

// Response DTOs
data class UserV1(
    val name: String,
    val email: String
)

// Controller classes
class UserControllerV1 {
    fun create(params: CreateUserParamsV1): ModelV1<UserV1> {}

    fun update(id: String, params: UpdateUserParamsV1): ModelV1<UserV1> {}
}
```

### 1.2 DTO Segregation by Purpose
Organize DTOs by their purpose and data flow direction in separate packages.

```kotlin
// dto.request package
data class CreateUserParamsV1(
    val name: String,
    val email: String
)

data class GetUserQueryV1(
    val includeInactive: Boolean = false
)

// dto.response package
data class UserV1(
    val name: String,
    val email: String
)

data class StatusV1(
    val name: String,
    val msg: String?
)
```

### 1.3 Package Organization Structure

Organize API packages by domain with clear separation between commons and domain-specific DTOs.

```markdown
// Package organization
api/
├── commons/dto/
│   ├── RangeV1                    # Shared between request and response
│   ├── request/                   # PageQueryV1, SortQueryV1
│   └── response/                  # ModelV1, PageV1, StatusV1, ErrorResponseV1
├── user/
│   ├── dto/request/               # CreateUserParamsV1, UpdateUserParamsV1, GetUserQueryV1
│   ├── dto/response/              # UserV1
│   └── UserControllerV1
├── payment/
│   ├── dto/request/               # CreatePaymentParamsV1, UpdatePaymentParamsV1
│   ├── dto/response/              # PaymentV1
│   └── PaymentControllerV1
```