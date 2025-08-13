# Basic Testing Conventions

## VibeTDD Batching Principle

**Classic TDD (red-green-refactor) is incompatible with AI collaboration** due to context explosion and session limits.

**VibeTDD Solution**: Write small, focused sets of related tests first, then implement them together.

**Benefits:**
- Reduces context switching overhead
- Keeps AI sessions manageable
- Maintains test-first discipline
- Allows better validation of test completeness

## Object Mother Pattern

### Single Factory Method with Valid Defaults
Create one factory method that always produces valid objects by default using `Rand` utility for realistic test data.

### Usage Principles
- **Valid by default**: `EntityMother.of()` always creates valid entity
- **Override when you care**: Only specify values that matter for the test
- **Random defaults**: Use `Rand` utility for realistic test data

## Random Data Utility

### Domain-Specific Valid Values
`Rand` object provides generic utilities and is located in testing library at `base_package.libs.testing` package.

**Rules:**
- No business rules in `Rand` (don't enforce amount limits here)
- Valid values only - `Rand` should never generate invalid data

## Test Structure and Naming

### Test Method Naming
Use descriptive names with backticks following pattern:
```
`should [expected behavior] when [condition]`
```

### Variable Naming Convention
Use descriptive prefixes for test variables:
- **given** prefix for input data (e.g., `givenUser`, `givenPayment`)
- **expected** prefix for expected outcomes (e.g., `expectedResult`)
- **actual** prefix for actual results (e.g., `actualResult`)

**Note**: In `ParameterizedTest`, add `given` prefix for parameter names (e.g., `givenAmount`, `givenName`)

### Test Structure
Always use Given-When-Then structure with comments to divide blocks.

## Error Collection Strategy

### Collect All Errors Before Failing
Prefer comprehensive error collection over fast-fail for better user experience.

### Testing Error Collection
Test that all validation errors are collected and reported together.

## Error Code Testing

**CRITICAL**: Always test error codes, NEVER test error messages.

**Reasons:**
- Error messages change for i18n/localization
- Error codes are stable API contracts
- Error codes are used by client applications
- Messages are for human display only

## Testing Layer Responsibilities

### Clear Layer Boundaries
Each layer tests only its responsibility:

- **API Layer**: Input validation (formats, required fields, basic ranges)
- **Domain Layer**: Business rules and logic (isolated with mocks)
- **Storage Layer**: Data persistence and retrieval (real database)
- **Integration**: Complete workflows (real components)

### What NOT to Test
- **API Layer**: ❌ Business logic, domain rules
- **Domain Layer**: ❌ Database persistence, external API calls, input validation
- **Storage Layer**: ❌ Business validation, domain logic
- **Integration**: ❌ Individual component details

## Parameterized Tests

Use parameterized tests to reduce duplication for similar test scenarios:

### ValueSource Examples
```kotlin
@ParameterizedTest
@ValueSource(doubles = [0.0, -5.0, -100.0])
fun `should reject payment when amount is invalid`(givenAmount: Double) {
    // Test implementation
}

@ParameterizedTest
@ValueSource(strings = ["", "   ", "INVALID"])
fun `should reject payment when currency is invalid`(givenCurrency: String) {
    // Test implementation
}

@ParameterizedTest
@ValueSource(ints = [0, -1, -100])
fun `should reject when count is not positive`(givenCount: Int) {
    // Test implementation
}
```

### CsvSource Examples
```kotlin
@ParameterizedTest
@CsvSource(
    "user1, 25.0, EUR, true",
    "user2, 30.0, USD, true", 
    "'', 25.0, EUR, false",      // Empty user ID
    "user3, -10.0, EUR, false",  // Negative amount
    "user4, 25.0, JPY, false"    // Invalid currency
)
fun `should validate payment with various inputs`(
    givenUserId: String,
    givenAmount: Double, 
    givenCurrency: String,
    shouldBeValid: Boolean
) {
    // Test implementation
}
```

### EnumSource Examples
```kotlin
@ParameterizedTest
@EnumSource(PaymentStatus::class)
fun `should handle all payment statuses`(givenStatus: PaymentStatus) {
    // Test implementation
}

@ParameterizedTest
@EnumSource(value = UserRole::class, names = ["ADMIN", "MODERATOR"])
fun `should allow admin operations for privileged roles`(givenRole: UserRole) {
    // Test implementation
}
```

## Data Class Verification

### Verify Whole Objects When Possible
Prefer testing entire data classes over individual fields for better test reliability.

**Benefits:**
- **Robust**: Catches missed fields in assertions
- **Maintainable**: Adding fields doesn't break existing tests
- **Clear**: Expresses complete expected state
- **Reliable**: Prevents partial verification bugs

### When to Use Field-by-Field Verification
Use individual field checks only when:
- Testing partial updates
- Verifying generated values (IDs, timestamps)
- Asserting specific field transformations

## Test Independence

### Independent Test Execution
- Tests should not depend on each other
- Each test should be able to run in isolation
- Avoid shared mutable state between tests

### One Concept Per Test
- Each test should verify one specific behavior
- Avoid testing multiple scenarios in a single test
- Keep tests focused and clear