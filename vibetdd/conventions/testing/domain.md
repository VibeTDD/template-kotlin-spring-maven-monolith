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
Use MockK with annotations for clean test setup. Always mock providers for deterministic testing.

### Relaxed Mocks for Storage
Use RelaxedMockk for storage mocks when you only care about verification, not return values.

## Testing Patterns

### Individual Validator Testing
Test each validator component in isolation with mocked configuration to verify business rules.

### Service Testing with Error Collection
Test use cases with multiple validators to verify error collection and processing workflow.

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
Focus on testing that business constraints are properly enforced through validator components.

### Use Case Workflows
Test that use cases properly orchestrate validation, business logic, and storage operations.

### Configuration Integration
Verify that business rules correctly use configuration values rather than hardcoded limits.

### Error Scenarios
Test comprehensive error collection and proper error code assignment for all business rule violations.