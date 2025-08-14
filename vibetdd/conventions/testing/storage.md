# Storage Layer Testing Conventions

## Responsibility
**Data persistence and retrieval** - test storage operations against real database.

## Testing Strategy

### Use Real Database
Test against actual database instance, not mocks, to verify persistence behavior and data integrity.

### Base Test Class Pattern
`lib-testing` has `StorageTestBase` with automatic cleanup to ensure test isolation and consistent database state. It also has spring boot configuration. So no need to create it, just extend it for a certain test class.

### Database Cleanup
Drop collections after each test method using base class cleanup to maintain test independence.

## Testing Patterns

### Storage Operation Testing
Test entity creation, version management, and constraint enforcement at persistence level.

### Retrieval Operation Testing
Test query operations, existence checks, and null handling for non-existent entities.

### Port Interface Testing
Test through storage port interfaces to maintain hexagonal architecture boundaries.

## What to Test vs What NOT to Test

### ✅ Test in Storage Layer
- Data persistence and retrieval operations
- Version incrementing and optimistic locking
- Database constraints and uniqueness violations
- Query operations by business fields
- Entity existence checking
- Null returns for non-existent entities
- Storage-specific exception scenarios

### ❌ Do NOT Test in Storage Layer
- Business validation rules (domain layer responsibility)
- Input format validation (API layer responsibility)
- Domain logic and calculations
- Configuration-driven business rules
- Use case orchestration

## MongoDB-Specific Patterns

### Version Management Testing
Verify that storage operations properly increment version numbers for optimistic locking scenarios.

## Test Organization

### Test Class Separation
Separate storage operations from retrieval operations into different test classes for clarity and focused testing.

### Exception Testing
Test duplicate entity scenarios and constraint violations specific to storage layer operations.

### Data Transformation Testing
Verify proper mapping between domain models and persistence format in adapter layer.