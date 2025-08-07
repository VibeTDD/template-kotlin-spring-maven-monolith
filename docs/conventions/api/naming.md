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

### Response DTO Naming Pattern
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

### URL Patterns and Endpoint Naming

#### Resource URL Structure

Follow standard REST conventions for resource naming and hierarchy.

```markdown
// Resource patterns
/v1/users                    # Collection operations
/v1/users/{id}              # Individual resource operations
/v1/users/{id}/orders       # Nested collection
/v1/users/{id}/activate     # Action on specific resource

// Search and complex operations
/v1/users/search            # Complex search (POST)
/v1/users/bulk              # Bulk operations (POST)
```

#### Version Prefix Rules

- All endpoints must start with version: `/v1/`, `/v2/`
- Exception: Public endpoints (if requested in spec): `/public/v1/`
- Use consistent versioning across all controllers

#### HTTP Method Usage Guidelines

- **GET**: Retrieve resources (list and individual)
- **POST**: Create new resources and complex operations (search, bulk operations)
- **PUT**: Update entire resource (full replacement)
- **PATCH**: Partial resource updates (if needed)
- **DELETE**: Remove resources

### Standard Controller Method Patterns

Implement consistent CRUD operations across all controllers.

```kotlin
@RestController
class UserControllerV1 {
    
    @GetMapping("/v1/users")
    fun list(@ModelAttribute query: GetUsersQueryV1): PageV1<ModelV1<UserV1>>
    
    @GetMapping("/v1/users/{id}")  
    fun get(@PathVariable id: UUID): ModelV1<UserV1>
    
    @PostMapping("/v1/users")
    fun create(@RequestBody @Valid params: CreateUserParamsV1): ModelV1<UserV1>
    
    @PutMapping("/v1/users/{id}")
    fun update(
        @PathVariable id: UUID, 
        @RequestBody @Valid params: UpdateUserParamsV1
    ): ModelV1<UserV1>
    
    @DeleteMapping("/v1/users/{id}")
    fun delete(@PathVariable id: UUID): StatusV1
    
    @PostMapping("/v1/users/search")
    fun search(@RequestBody @Valid params: SearchUsersParamsV1): PageV1<ModelV1<UserV1>>
}
```

### Annotation Requirements

- Always use `@Valid` for request body validation
- Use `@PathVariable` for URL path parameters
- Use `@RequestBody` for POST/PUT request bodies