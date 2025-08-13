# Domain Module Conventions

## Responsibility

The domain module (e.g., `users`, `orders`, `payments`) contains core business logic, validation rules, and domain entities. It is accessed only through client interfaces and operates independently of infrastructure concerns. Domain modules handle business operations, enforce business rules, and manage domain state through use cases and domain services.

## Architecture Patterns

### Client Interface Pattern
Domain modules expose functionality through client interfaces that serve as the entry point for other modules. Client implementations coordinate between use cases and handle DTO transformations.

### Use Case Pattern
Business operations are implemented as use cases that orchestrate domain logic. Use cases handle validation, business rule enforcement, and state management.

### Domain Model Pattern
Domain entities are simple data classes focused on business concepts. Commands represent operations and carry the necessary data for business operations.

### Validation Pattern
Business validation is handled by validator components that implement domain rules. Use comprehensive error collection instead of fast-fail for better user experience.

### Port Pattern (Hexagonal Architecture)
External dependencies are abstracted through ports. Domain layer depends only on abstractions, never on infrastructure.

### Configuration Pattern
Business configuration is injected through interfaces to keep business rules externally configurable.

## Domain Module Structure

Organize domain components by responsibility:

```
domain/
├── handler/                    # Use cases and business operations
├── model/                      # Domain entities and value objects
│   └── command/                # Command objects for operations
├── validator/                  # Business validation rules
├── port/                       # Hexagonal architecture ports
├── mapper/                     # Domain object transformations
└── constant/                   # Domain error codes and constants
```

## Key Principles

**Business Logic Focus**: Domain layer contains only business rules and domain concepts
**Port Dependency**: External dependencies are abstracted through port interfaces
**Use Case Orchestration**: Complex business operations are coordinated by use cases
**Validation Strategy**: Business rules are enforced through composable validator pattern
**Command Pattern**: Operations are represented as command objects for better testability
**Clean Dependencies**: Domain layer depends only on abstractions, never on infrastructure

## Mapping Strategy

### Transformation Flow
Follow this transformation pipeline:
```
Request DTO → Domain Command → Domain Entity → Response DTO → API Response
```

### Mapping Organization
- **DTO to Command**: Use extension functions in `domain/mapper/`
- **Domain to Response**: Use extension functions in root `mapper/`
- **Commons Integration**: Always use `DtoMapper` from commons for Model wrapping

## Provider Integration

### ID Generation
Always use `IdProvider<T>` from commons module for testable ID generation.

### Time Management
Always use `TimeProvider` from commons module for testable time operations.

## Exception Handling

### Validation Exceptions
Use common `ValidationException` from commons module. Create domain-specific error codes only when common codes are insufficient.

### Error Code Naming
Follow CamelCase format for domain-specific error codes when extending beyond common error codes.

### Error Collection Strategy
Collect all validation errors before throwing exceptions to provide comprehensive feedback to users.