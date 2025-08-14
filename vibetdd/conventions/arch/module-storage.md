# Adapter Implementation Conventions

## Responsibility

The adapter module implements hexagonal architecture ports to provide concrete implementations for external dependencies. Adapters handle data transformation between domain models and external systems, manage infrastructure-specific concerns, and translate between different data formats while preserving domain boundaries.

## Architecture Patterns

### Repository Pattern
Use Spring Data repositories for database operations with custom query methods when needed. Repository interfaces define data access contracts.

### Adapter Pattern
Implement domain ports with infrastructure-specific adapters that handle external system integration and data transformation.

### Document Mapping Pattern
Transform between domain models and persistence documents using extension functions for clean separation of concerns.

### Exception Translation Pattern
Translate infrastructure exceptions to domain exceptions to maintain clean boundaries and provide meaningful error information.

### Configuration Implementation Pattern
Implement domain configuration ports with concrete implementations that fetch from databases, external services, or configuration files.

## Adapter Module Structure

Organize adapter components by responsibility:

```
adapter/
├── [Entity]StorageAdapter.kt      # Port implementation
├── mapper/                       # Data transformation
│   └── [Entity]DocMapper.kt      # Document mapping extensions
└── repository/                   # Data access layer
    ├── [Entity]Repository.kt     # Spring Data repository
    └── doc/                      # Document models
        └── [Entity]Doc.kt        # MongoDB document class
```

## Key Principles

**Port Implementation**: Adapters implement domain ports without exposing infrastructure details
**Data Transformation**: Clean mapping between domain and persistence models
**Exception Translation**: Convert infrastructure exceptions to domain exceptions
**Index Optimization**: Add database indexes for all queried fields
**Version Management**: Use optimistic locking with @Version annotation

## Storage Adapter Implementation

### Port Implementation Pattern
Implement storage ports using Spring Data repositories with proper exception handling and data transformation.

### Exception Handling Strategy
Catch infrastructure exceptions and translate to domain exceptions with meaningful context and error information.

### Version Management
Use Spring Data's @Version annotation for optimistic locking and automatic version incrementing.

## Document Design

### Document Structure
MongoDB documents follow wrapper pattern with embedded domain data and metadata fields.

### Index Strategy
Always add compound indexes for fields involved in queries to optimize database performance.

### Version Control
Use @Version annotation for automatic version management and optimistic locking support.

### Collection Naming
Use plural names for MongoDB collections that clearly identify the stored entity type. For example: domain `user` - collection `users`

## Mapping Implementation

### Extension Function Pattern
Use extension functions for bidirectional mapping between domain models and documents.

### Transformation Consistency
Maintain consistent transformation patterns across all mappers for predictable behavior.

### UUID Handling
Convert between String and UUID types using commons utilities for type safety.

## Repository Design

### Spring Data Integration
Extend MongoRepository with entity-specific query methods following Spring Data naming conventions.

### Custom Query Methods
Add business-specific query methods using Spring Data method naming or custom queries when needed.

### Query Method Naming
Follow Spring Data conventions for automatic query generation from method names.

## Configuration Implementation

### Database-Backed Configuration
Implement configuration ports with database implementations for dynamic business rule management.

### Configuration Caching
Consider caching strategies for frequently accessed configuration values to improve performance.

### Default Value Strategy
Provide sensible defaults for configuration values to ensure system stability.

## Exception Translation

### Infrastructure to Domain
Translate database exceptions to domain exceptions while preserving relevant error context.

### Error Context Preservation
Include relevant parameters and context information when translating exceptions for debugging.

### Exception Type Mapping
Map specific infrastructure exceptions to appropriate domain exception types.