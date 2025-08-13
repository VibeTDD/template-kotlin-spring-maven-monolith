# API Layer Conventions

## Responsibility

The API module (e.g., `main-api`, `public-api`) handles HTTP interface concerns and orchestrates calls to domain modules through clients. Controllers have no business logic - they validate input format, call appropriate services, and return properly formatted responses.

## Common API Layer Patterns

Most detailed conventions are described in multiple files that should be also attached. Check them for specific implementation details.

## Architecture Patterns

### Controller Pattern
Controllers delegate all work to services and focus solely on HTTP concerns. Controllers should contain no business logic.

### Service Layer Pattern
API services orchestrate client calls and handle DTO mapping. Services handle two main scenarios:

- **Simple Case**: Single client call with direct DTO transformation
- **Complex Case**: Multiple client aggregation for presentation needs

### DTO Mapping Pattern
Use extension functions for clean DTO transformations between API and domain layers.

### Client Interface Pattern
API services depend on domain module clients, not direct domain services. This maintains proper layer separation.

### Aggregation Service Pattern
When combining data from multiple clients, keep aggregation logic simple and focused on presentation needs only.

## API Module Structure

Organize API components by feature or domain:

```
main-api/
├── controller/                 # HTTP endpoints and routing
├── service/                   # API orchestration and aggregation
├── dto/
│   ├── request/               # API request DTOs
│   └── response/              # API response DTOs
└── mapper/                    # DTO transformation logic
```

## Key Principles

**No Business Logic**: Controllers and API services contain no business rules or domain logic
**Client Dependency**: API services depend on domain clients, never directly on domain services
**DTO Transformation**: Always convert between API DTOs and domain models at the API boundary
**Simple Aggregation**: Combine data from multiple clients for presentation purposes only
**Clear Separation**: API layer handles HTTP concerns, domain handles business logic
**Format Validation Only**: API layer validates format and structure, not business rules

## Mapping Strategy

### Transformation Flow
Follow this transformation pipeline:
```
HTTP Request → API DTO → Domain DTO
Domain DTO → API DTO → HTTP Response
```

### Mapping Organization
- **API to Domain**: Transform API request DTOs to domain request DTOs
- **Domain to API**: Transform domain response DTOs to API response DTOs
- **Collection Handling**: Use extension functions for list transformations

## Service Patterns

### Single Client Services
For straightforward operations that involve a single domain module.

### Aggregation Services
For complex presentation needs that combine data from multiple domain modules. Keep aggregation logic focused on presentation requirements only.