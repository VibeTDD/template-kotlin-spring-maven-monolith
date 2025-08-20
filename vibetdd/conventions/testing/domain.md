# Domain Layer Testing Conventions

## Responsibility
**ALL business rules and validation** - isolated from external concerns.

## Testing Strategy

### Test with Mocks
Mock all external dependencies to focus purely on business logic:
- Storage interfaces (ports)
- Configuration sources
- External service ports
- Event publishers
- IdProvider and TimeProvider from commons

### Mock Strategy
Use MockK with direct mockk() calls for straightforward test setup. Always mock providers for deterministic testing.

### Relaxed Mocks for Storage
Use relaxed mockk() for storage mocks when you only care about verification, not return values.

## Testing Patterns

### Generic Validator Pattern
Use CommandValidator<T> with ValidationRule<T> implementations for consistent validation across all domains.

### Base Test Class Organization
Create abstract base test class with common setup, then specific test classes for:
- Valid scenarios: `CreateUserValidUseCaseTest`
- Invalid scenarios by rule: `CreateUserInvalidEmailUseCaseTest`, `CreatePayoutInvalidCountryUseCaseTest`

### Black Box Testing
Test use cases as entry points with real implementations of internal logic (e.q. validators, convertors, mappers), mocking only external dependencies.

### Provider Mocking
Always mock IdProvider and TimeProvider for deterministic testing of generated values.

## What to Test vs What NOT to Test

### ✅ Test in Domain Layer
- Business validation rules
- Domain logic and calculations
- Business workflows and state transitions
- Error scenarios with comprehensive error collection
- Configuration-driven business rules
- Use case orchestration
- Validator combinations and error aggregation

### ❌ Do NOT Test in Domain Layer
- Data persistence (mocked storage)
- External API calls (mocked services)
- HTTP request/response handling
- Database constraints
- Infrastructure concerns
- Input format validation (API layer responsibility)

## Testing Focus Areas

### Business Rule Validation
Focus on testing that business constraints are properly enforced through ValidationRule implementations.

### Use Case Workflows
Test that use cases properly orchestrate validation, business logic, and storage operations through black box testing.

### Configuration Integration
Verify that business rules correctly use configuration values rather than hardcoded limits.

### Error Scenarios
Test comprehensive error collection and proper error code assignment for all business rule violations.