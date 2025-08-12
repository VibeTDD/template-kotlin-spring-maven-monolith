# Basic Testing Conventions

## VibeTDD Batching Principle

**Classic TDD (red-green-refactor) is incompatible with AI collaboration** due to context explosion and session limits.

**VibeTDD Solution**: Write small, focused sets of related tests first, then implement them together.

```kotlin
// ❌ Classic TDD: Write one test → implement → write next test → implement
// ✅ VibeTDD: Write focused test batch → verify they fail → implement together

@Test
fun `should accept valid amount`() { /* test 1 */ }

@Test  
fun `should reject when amount is invalid`() { /* test 2 */ }

// Then implement all amount validation together
```

**Benefits:**
- Reduces context switching overhead
- Keeps AI sessions manageable
- Maintains test-first discipline
- Allows better validation of test completeness

## Object Mother Pattern

### Single Factory Method with Valid Defaults
Create one factory method that always produces valid objects by default:

```kotlin
object PaymentMother {
    fun of(
        userId: String = Rand.userId(),
        amount: Double = Rand.amount(),
        currency: String = Rand.currency()
    ) = Payment(
        userId = userId,
        amount = amount,
        currency = currency
    )
}
```

### Usage Principles
- **Valid by default**: `PaymentMother.of()` always creates valid entity
- **Override when you care**: Only specify values that matter for the test
- **Random defaults**: Use `Rand` utility for realistic test data

```kotlin
// ✅ Good usage
val validPayment = PaymentMother.of()                    // Valid random
val invalidPayment = PaymentMother.of(amount = -10.0)    // Override for test
val specificPayment = PaymentMother.of(currency = "EUR") // Override when needed

// ❌ Bad usage  
val payment = PaymentMother.of(amount = 25.0)            // Don't hardcode unless necessary
```

## Random Data Utility

### Domain-Specific Valid Values
`Rand` object provides generic utilities for random values generation. It is located in testing library, in `base_package.libs.testing` package so no need to create it everytime.

Here are some examples:

```kotlin
object Rand {
    fun uuid(): UUID {}
    fun string(maxLength: Int = 10): String {}
    fun int(min: Int = 0, max: Int = 100): Int {}
    fun boolean(): Boolean {}
    fun amount(min: Double = 1.0, max: Double = 1000.0): BigDecimal {}
    fun currency(): String {}
    fun email(): String {}
}
```

**Rules:**
- No business rules in `Rand` (don't enforce amount limits here)
- Valid values only - `Rand` should never generate invalid data

## Test Structure and Naming

### Test Method Naming
Use descriptive names with backticks:

```kotlin
@Test
fun `should [expected behavior] when [condition]`() {
    // Test implementation
}

@Test
fun `should accept valid payment when all fields are correct`() { /* */ }

@Test
fun `should reject payment when amount is invalid`() { /* */ }
```

### Variable Naming Convention
Use descriptive prefixes for test variables:

```kotlin
@Test
fun `should process payment successfully when request is valid`() {
    // Given
    val givenPayment = PaymentMother.of()
    val expectedResult = PaymentResult.Success(givenPayment.id)
    
    // When
    val actualResult = paymentService.process(givenPayment)
    
    // Then
    actualResult shouldBe expectedResult
}
```

Note: in case of `ParameterizedTest` add `given` prefix for parameter names, e.q. `givenAmount`, `givenName`

### Test Structure
Always use Given-When-Then structure with comments:

```kotlin
@Test
fun `should reject payment when amount is invalid`() {
    // Given
    val givenPayment = PaymentMother.of(amount = -10.0)
    
    // When
    val exception = shouldThrow<ValidationException> {
        validator.validate(givenPayment)
    }
    
    // Then
    exception.code shouldBe PaymentErrorCode.INVALID_AMOUNT
}
```

## Error Collection Strategy

### Collect All Errors Before Failing
Prefer comprehensive error collection over fast-fail:

```kotlin
// ✅ Good - Error collection for better UX
class CurrencyValidator: Validator {
    fun validate(payment: Payment): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()
        
        if (payment.currency.isEmpty()) {
            errors.add(ValidationError(MISSING_CURRENCY, "Currency is required"))
        }
        
        return errors
    }
}

// Service collects all validation errors
class PaymentService(
    private val validators: List<Validator>
) {
    fun process(payment: Payment) {
        val allErrors = validators.flatMap { it.validate(payment) }
        
        if (allErrors.isNotEmpty()) {
            throw PaymentValidationException(allErrors)
        }
        
        // Process payment
    }
}
```

### Testing Error Collection
```kotlin
@Test
fun `should collect all validation errors before failing`() {
    // Given
    val givenPayment = PaymentMother.of(amount = -10.0, currency = "")
    
    // When
    val exception = shouldThrow<PaymentValidationException> {
        paymentService.process(givenPayment)
    }
    
    // Then
    exception.errors shouldHaveSize 2
    exception.errors.map { it.code } shouldContainExactlyInAnyOrder listOf(
        PaymentErrorCode.INVALID_AMOUNT,
        PaymentErrorCode.MISSING_CURRENCY
    )
}
```

## Error Code Testing

**CRITICAL**: Always test error codes, NEVER test error messages.

```kotlin
// ✅ Good - Test error code
exception.code shouldBe PaymentErrorCode.INVALID_AMOUNT

// ❌ Bad - Never test error messages
exception.message shouldBe "Amount must be positive"
```

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

Use parameterized tests to reduce duplication:

```kotlin
@ParameterizedTest
@ValueSource(doubles = [0.0, -5.0, -100.0])
fun `should reject payment when amount is invalid`(givenAmount: Double) {
    // Given
    val givenPayment = PaymentMother.of(amount = givenAmount)
    
    // When & Then
    val exception = shouldThrow<ValidationException> {
        validator.validate(givenPayment)
    }
    exception.code shouldBe PaymentErrorCode.INVALID_AMOUNT
}

@ParameterizedTest
@ValueSource(strings = ["", "   ", "INVALID"])
fun `should reject payment when currency is invalid`(givenCurrency: String) {
    // Given
    val givenPayment = PaymentMother.of(currency = givenCurrency)
    
    // When & Then
    assertThrows<ValidationException> { validator.validate(givenPayment) }
}
```

## Test Independence

### Independent Test Execution
- Tests should not depend on each other
- Each test should be able to run in isolation
- Avoid shared mutable state between tests

### One Concept Per Test
- Each test should verify one specific behavior
- Avoid testing multiple scenarios in a single test
- Keep tests focused and clear

```kotlin
// ✅ Good - One concept per test
@Test
fun `should reject payment when currency is invalid`() { /* */ }

@Test  
fun `should create payment when it is valid`() { /* */ }

// ❌ Bad - Multiple concepts in one test
@Test
fun `should validate and convert currency`() {
    // Testing multiple currency actions in one test
}
```