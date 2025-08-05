# Modular Monolith Architecture

## 1. Project Structure Foundation

### 1.1 Multi-Module Organization

Structure the modular monolith using a multi-module build approach with clear separation of concerns:

```
project-root/
├── app/                        # Application assembly module
│   └── src/main/kotlin/...
├── commons/                    # Shared utilities and common types
│   └── src/main/kotlin/...
├── main-api/                        # API composition layer - combines data from multiple modules
│   └── src/main/kotlin/...
├── module-template/            # Template for domain module, e.q. users, orders (hexagonal architecture)
│   └── src/main/kotlin/...
```

### 1.2 Module Dependency Rules

**Dependency Flow (Upward Dependencies Only):**

```
app → api → commons
app → users/orders → commons
api → users/orders (through client interfaces)
```

**Key Principles:**

- **No circular dependencies** between modules
- **Commons module** contains only shared utilities, no business logic
- **API module** composes data from multiple domain modules through client interfaces
- **App module** is the assembly point that brings everything together
- **Domain modules** (e.q. users, orders) are isolated and only communicate through client interfaces
- **Domain modules never depend on each other directly**

### 1.3 Package Structure Within Modules

**Domain Module Structure (Hexagonal Architecture):**
Each domain module follows hexagonal architecture principles:

```
src/main/kotlin/com/company/users/
├── client/                    # Public client interfaces (suspend functions)
│   └── UsersClientV1.kt
├── client/internal/           # Internal client implementations  
│   └── UsersClientInternal.kt
├── dto/                       # Module-specific DTOs
│   ├── request/              # Input DTOs
│   └── response/UserV1.kt     # Output DTOs
├── domain/                    # Core business logic (domain layer)
│   ├── model/                # Domain entities
│   ├── service/              # Domain services
│   └── port/                 # Ports (interfaces for external dependencies)
├── adapter/                   # Adapters (implementations of ports)
│   ├── repository/           # Database adapters
│   └── external/             # External service adapters
├── config/                    # Module-specific configuration
└── UsersConfig.kt             # Spring configuration for module
```

**API Module Structure:**
The API module has a different structure focused on composition:

```
src/main/kotlin/com/company/api/
├── dto/                       # API-level composed DTOs
│   ├── request/              # API request DTOs
│   └── response/ApiOrderV1.kt         # API response DTOs (composed from multiple modules when needed)
├── mapper/                   # Mappers from domain to api dtos
├── service/                   # API composition services
├── rest/                      # REST controllers
├── config/                    # API configuration
└── ApiConfig.kt               # Spring configuration for API layer
```

## 2. Module Communication and Composition Setup

### 2.1 Domain Module Client Pattern

Each domain module exposes simple, focused functionality through client interfaces:

```kotlin
// Order module client - returns simple OrderV1
interface OrdersClientV1 {
    suspend fun create(params: CreateOrderParamsV1): ModelV1<OrderV1>
    suspend fun get(id: UUID): ModelV1<OrderV1>
}

// OrderV1 contains only order-specific data
data class OrderV1(
    val userId: UUID,
    val amount: BigDecimal
)

// User module client - returns simple UserV1  
interface UsersClientV1 {
    suspend fun get(id: UUID): ModelV1<UserV1>
}

// UserV1 contains only user-specific data
data class UserV1(
    val firstName: String,
    val lastName: String
)
```

### 2.2 API Composition Layer

The API module composes complex DTOs by requesting and combining data from multiple domain modules:

```kotlin
// API service that composes data from multiple modules
@Service
class ApiOrderService(
    private val ordersClient: OrdersClientV1,
    private val usersClient: UsersClientV1
) {

    suspend fun get(orderId: UUID): ModelV1<ApiOrderV1> {
        // Get order data
        val order = ordersClient.get(orderId)

        // Get user data
        val user = usersClient.get(order.data.userId)

        // Compose into API DTO
        val apiOrder = ApiOrderV1(
            amount = order.data.amount,
            user = ApiUserV1(
                firstName = user.data.firstName,
                lastName = user.data.lastName
            )
        )

        return ModelV1(
            data = apiOrder,
            version = order.version,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt
        )
    }
}

// Composed API DTO with data from multiple modules
data class ApiOrderV1(
    val amount: BigDecimal,
    val user: ApiUserV1        // Composed user data
)

data class ApiUserV1(
    val firstName: String,
    val lastName: String
)
```

### 2.3 REST Controller Integration

Controllers exist only in the API module and use composition services:

```kotlin
@RestController
@RequestMapping("/v1/orders")
class OrderControllerV1(
    private val apiOrderService: ApiOrderService
) {
    @GetMapping("/{id}")
    suspend fun get(@PathVariable id: UUID): ModelV1<ApiOrderV1> = apiOrderService.getOrder(id)
}
```

## 3. Spring Configuration Setup

### 3.1 Configuration

Each module provides its own Spring configuration when needed:

```kotlin
class OrdersConfig {
    // specific config
}

class ApiConfig {
    // specific config
}
```

### 3.3 Application Assembly Configuration

The app module brings all modules together:

```kotlin
@Configuration
@Import(
    ApiConfig::class,
    ModuleTemplateConfig::class,
)
class AppConfig

@SpringBootApplication(scanBasePackages = ["com.company"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
```

## 4. Hexagonal Architecture in Domain Modules

### 4.1 Domain Module Data Ownership

Each domain module owns and manages its own data following hexagonal architecture:

```kotlin
// Domain entity in users module
@Entity
@Table(name = "users")
data class UserEntity(
    val id: UUID,
    val firstName: String,
    val lastName: String
)

// Orders module owns order data but references users by userId only
@Entity
@Table(name = "orders")
data class OrderEntity(
    val id: UUID,
    val userId: UUID,
    val amount: BigDecimal
)
```

### 4.2 Hexagonal Architecture Layers

Domain modules follow hexagonal architecture:

```kotlin
// Domain layer - core business logic
@Service
class OrderService(
    private val userPort: UserPort,
    private val orderRepository: OrderRepository
) {

    suspend fun create(command: CreateOrderCommand): ModelV1<OrderV1> {
        // Validate through port
        userPort.get(command.userId)

        // Create through domain service
        val orderEntity = OrderEntity(
            userId = command.userId,
            amount = command.amount
        )

        // Save and return
        return orderRepository.save(orderEntity).toV1()
    }
}

// Port - interface for external dependencies
interface UserPort {
    suspend fun get(userId: UUID): ModelV1<UserV1>
}

// Adapter - implementation of port
@Component
class UserClientAdapter(
    private val usersClient: UsersClientV1
) : UserPort {

    override suspend fun get(userId: UUID): ModelV1<UserV1> = usersClient.get(userId)
}
```

## 5. Build Configuration Principles

### 5.1 Parent Build Configuration

Configure dependency management and common build settings in the parent build file:

**Key Elements:**

- **Parent configuration** with Spring Boot parent or BOM imports
- **Module declarations** listing all submodules
- **Dependency management** for internal module dependencies
- **Common plugin configuration** shared across modules

### 5.2 Module Build Configuration

Each module has its own build configuration with specific dependencies:

**Library Module Pattern:**

- Internal modules (commons, api, business modules) are library modules
- No executable artifacts, just JAR files for consumption by other modules
- Dependencies on other internal modules through dependency management

**Application Module Pattern:**

- App module includes executable application plugin
- Depends on all necessary internal modules
- Produces deployable artifact (executable JAR/WAR)
- Contains main application class and configuration

### 5.3 Dependency Management

Structure dependencies to maintain clean module boundaries:

**Internal Dependencies:**

- Reference internal modules through dependency management
- Use consistent versioning across all internal modules
- Avoid direct cross-dependencies between business modules

**External Dependencies:**

- Manage external library versions in parent configuration
- Business modules depend only on what they actually use
- App module brings together all necessary runtime dependencies

## 5. Best Practices

### 5.1 Module Design Principles

- **Clear Separation**: Domain modules handle domain logic, API module handles composition
- **Hexagonal Architecture**: Domain modules follow ports and adapters pattern
- **Simple DTOs**: Domain modules return focused, single-domain DTOs
- **Composed DTOs**: API module creates rich DTOs by combining multiple domain DTOs
- **Client Interfaces**: Communication through versioned client interfaces with suspend functions
- **No Controllers in Domain Modules**: REST controllers exist only in the API module

### 5.2 Communication Setup

- **Domain-to-Domain**: Minimal, only when necessary, through client interfaces or ports
- **API-to-Domain**: API module composes data from multiple domain modules
- **Consistent API**: All client methods use suspend functions and versioned DTOs
- **Hexagonal Boundaries**: External dependencies accessed through ports and adapters

### 5.3 Data Composition Strategy

- **Reference by ID**: Domain modules store only IDs of entities from other modules
- **Compose at API Level**: Rich data structures are built in the API module by calling multiple clients
- **Avoid Data Duplication**: Don't copy data between domain modules, compose it when needed
- **Performance Considerations**: API services may need to optimize multiple client calls

### 5.4 Build Organization

- **Multi-Module Build**: Clean dependency management and build isolation
- **Assembly Pattern**: App module brings everything together for deployment
- **Upward Dependencies**: Dependencies flow upward, never circular
- **API Layer**: API module depends on domain modules, never the reverse

### 5.5 Controller and API Design

- **Single API Module**: All REST controllers in the API module only
- **Rich API DTOs**: API DTOs contain composed data from multiple domains
- **Simple Domain DTOs**: Domain module DTOs are focused and single-domain
- **Consistent Patterns**: All API endpoints follow same composition approach
- **Configuration Naming**: Use short config names (ApiConfig, UsersConfig, OrdersConfig)