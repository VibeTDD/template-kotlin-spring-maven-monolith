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