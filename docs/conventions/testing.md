# Kotlin Testing Conventions

This document provides concrete Kotlin examples implementing the concepts from Basic TDD Conventions.

## 1. VibeTDD Batching Principle

### 1.1 Why Classic TDD Doesn't Work with AI
**Classic TDD (red-green-refactor) is incompatible with AI collaboration** due to:
- Context explosion with each cycle
- Memory consumption from constant iterations
- Time overhead from switching between test/implementation
- Session limits preventing meaningful feature completion

### 1.2 VibeTDD Solution
**Write small, focused sets of related tests first, then implement them together.**

**Process Flow:**
- Instead of: Write one test → implement → write next test → implement
- Do this: Write a focused set of tests → verify they fail → implement together

**Benefits:**
- Reduces context switching overhead
- Keeps AI sessions manageable
- Maintains test-first discipline
- Allows better validation of test completeness

## 2. Test Organization Principles

### 2.1 Test Categories
1. **Unit Tests**: Test business logic in isolation with mocks
2. **Integration Tests**: Test complete flow with real components
3. **Contract Tests**: Test contracts with external systems

### 2.2 Test Naming Convention
Use descriptive test names that explain the scenario:
- Follow pattern: `should [expected behavior] when [condition]`
- Be specific about the scenario being tested

```kotlin
@Test
fun `should process payment when data is valid`() {
    // Given
    val givenPayment = PaymentMother.of(amount = 25.0)
    val expectedResult = ProcessedPayment(givenPayment, "success")
    
    // When
    val actualResult = paymentService.process(givenPayment)
    
    // Then
    actualResult shouldBe expectedResult
}
```

### 2.3 Test Structure
- **Given**: Set up test data and dependencies
- **When**: Execute the code under test
- **Then**: Verify the expected outcome

### 2.4 Variable Naming Convention
Use descriptive prefixes for test variables:
- **given** prefix for input data (e.g., givenUser, givenPayment)
- **expected** prefix for expected outcomes (e.g., expectedResult, expectedResponse)
- **actual** prefix for actual results (e.g., actualResult, actualResponse)

## 3. Test Data Management

### 3.1 Object Mother Pattern
Use a single factory method with default parameters that creates valid objects by default.

**Principles:**
- **Valid by default**: Factory creates valid entities without parameters
- **Override when you care**: Only specify values that matter for the test
- **Random defaults**: Use Rand utility for realistic test data
- **Domain-specific values**: Rand should provide business-valid values

Create a single factory method with default parameters using Kotlin's default parameter syntax:

```kotlin
object PaymentMother {
    fun of(
        userId: String = Rand.validUserId(),
        amount: Double = Rand.amount(),
        currency: String = Rand.currency()
    ) = Payment(
        userId = userId,
        amount = amount,
        currency = currency
    )
}
```

### 3.2 Object Mother Best Practices
- Always creates valid entities by default
- Use Rand for default parameters, including domain-specific valid values
- Override parameters when the specific value matters for the test
- Let Mother decide when you don't care about the specific value

```kotlin
// ✅ Correct usage
val givenUser = UserMother.of()                           // Valid random user
val givenInvalidUser = UserMother.of(email = "")          // Override for invalid test
val givenSpecificUser = UserMother.of(type = UserType.PREMIUM)  // Override when value matters
val givenPayment = PaymentMother.of(userId = givenUser.id)      // Link related objects

// ❌ Wrong usage  
val givenUser = UserMother.of(name = "John")              // Don't hardcode unless necessary
val givenPayment = PaymentMother.of(amount = 25.0)        // Let Mother decide valid amount when it is not important for test
```

### 3.3 Generic Random Data Utility
The Rand object should provide both generic utilities and domain-specific valid values.

**Principles:**
- **Generic methods** for basic types (string, numbers, boolean)
- **Domain-specific methods** for business concepts (currency, email, amounts)
- **No business rules** in Rand (e.g., don't enforce amount limits here)
- **Valid values only** - Rand should never generate invalid data

```kotlin
object Rand {
    fun string(length: Int = 10): String = (1..length).map { ('a'..'z').random() }.joinToString("")
    fun int(min: Int = 0, max: Int = 100): Int = (min..max).random()
    fun boolean(): Boolean = kotlin.random.Random.nextBoolean()
    fun currency(): String = Currency.getAvailableCurrencies().random().currencyCode
    fun amount(min: Double = 1.0, max: Double = 1000.0): Double = double(min, max)
    fun email(): String = "${string(8)}@${string(6)}.com"
}
```

Domain-Specific Rand Usage Examples:

```kotlin
// ✅ Good domain-specific Rand usage
object PaymentMother {
    
    fun of(
        userId: String = Rand.validUserId(),
        amount: Double = Rand.amount(),
        currency: String = Rand.currency()
    ) = Payment(userId, amount, currency)
}

// ❌ Don't use generic Rand for domain concepts
object PaymentMother {
    fun of(
        amount: Double = Rand.double(),        // Could generate invalid amounts, e.q. negative
        currency: String = Rand.string()       // Could generate invalid currencies
    ) = Payment("user", amount, currency)
}
```

## 4. Test Quality Standards

### 4.1 One Concept Per Test
- Each test should verify one specific behavior
- Avoid testing multiple scenarios in a single test
- Keep tests focused and independent

### 4.2 Test Independence
- Tests should not depend on each other
- Each test should be able to run in isolation
- Avoid shared mutable state between tests

### 4.3 Meaningful Assertions
- Test behavior, not implementation details
- Use specific assertions that clearly indicate what went wrong
- Prefer multiple specific assertions over generic ones

## 5. Mocking Strategy

### 5.1 When to Mock vs Use Real Objects
- **Mock infrastructure dependencies**: Database layers, event senders, external API clients, file systems
- **Use real objects for business logic**: Validators, converters, builders, calculators, domain services
- **Mock configuration when testing behavior**: To control test scenarios and boundary conditions
- **Use real configuration for integration tests**: To test actual business constraints

### 5.2 Mock Setup Guidelines
- **Mock external boundaries**: Anything that crosses process/network boundaries
- **Keep business logic real**: Validators, converters, and domain services should be real objects
- **Mock for isolation**: When you want to test one component without its dependencies
- **Use real objects for integration**: When testing component collaboration

Good Mocking Strategy:

```kotlin
@ExtendWith(MockKExtension::class)
class PaymentServiceTest {
    
    @InjectMockKs
    private lateinit var paymentService: PaymentService

    @MockK(relaxed = true) // use relaxed mock to avoid just Runs
    private lateinit var paymentStorage: PaymentStorage          // Infrastructure - Mock
    
    @MockK  
    private lateinit var externalProcessor: ExternalProcessor   // External service - Mock
    
    @MockK
    private lateinit var configuration: PaymentConfiguration    // Mock for controlled scenarios
    
    @Test
    fun `should process valid payment`() {
        // Given
        val givenPayment = PaymentMother.of()
        every { configuration.getMaxAmount() } returns 100.0
        
        // When
        val actualResult = paymentService.process(givenPayment)
        
        // Then
        verify { paymentStorage.store(givenPayment) }
    }
}
```

Over-Mocking Anti-Pattern:

```kotlin
// ❌ Over-mocking business logic
@Test  
fun `should validate payment amount`() {
    every { amountValidator.validate(any()) } returns true  // Don't mock business logic!
    // This test doesn't actually test validation logic
    
    val givenPayment = PaymentMother.of()
    paymentService.process(givenPayment)
    
    verify { amountValidator.validate(givenPayment) }
}
```

## 6. Configuration Management in Tests

### 6.1 Dynamic Configuration Principles
All business parameters that can change should be externally configurable:

**Examples of configurable parameters:**
- Business limits (minimum/maximum amounts)
- Allowed value sets (currencies, statuses)
- Business rules thresholds
- Feature flags
- Validation constraints
- Time-based settings (timeouts, expiration periods)

Create interfaces for configuration access using Kotlin interface syntax:

```kotlin
interface BusinessConfiguration {
    fun getMaxAmount(): Double
    fun getAllowedCurrencies(): Set<String>
    fun getBusinessRuleThreshold(ruleType: String): Double
}

class DefaultBusinessConfiguration : BusinessConfiguration {
    override fun getMaxAmount(): Double = 30.0
    override fun getAllowedCurrencies(): Set<String> = setOf("EUR", "USD", "GBP")
    override fun getBusinessRuleThreshold(ruleType: String): Double = 
        when (ruleType) {
            "user_limit" -> 100.0
            "daily_limit" -> 500.0
            else -> throw IllegalArgumentException("Unknown rule type: $ruleType")
        }
}
```

### 6.2 Testing Configuration-Dependent Code
**Principles:**
- Mock configuration to control test scenarios
- Test both within and outside configured limits
- Verify configuration is retrieved with correct parameters

Using MockK for configuration mocking:

```kotlin
@ExtendWith(MockKExtension::class)
class BusinessValidatorTest {
    
    @InjectMockKs
    private lateinit var validator: BusinessValidator
    
    @MockK
    private lateinit var configuration: BusinessConfiguration
    
    @Test
    fun `should reject when amount exceeds configured limit`() {
        // Given
        every { configuration.getMaxAmount() } returns 100.0
        val givenEntity = EntityMother.of(amount = 150.0)
        
        // When & Then
        val exception = shouldThrow<ValidationException> {
            validator.validate(givenEntity)
        }
        
        exception.code shouldBe AMOUNT_EXCEEDED
    }
}
```

## 7. TDD Cycle Implementation

### 7.1 RED Phase - Write Failing Tests
1. **Start with simplest test case** (happy path)
2. **Write test method name that describes behavior**
3. **Use Given-When-Then structure** with comments to divide blocks
4. **One assertion per test** (prefer focused tests)
5. **Ensure test fails for the right reason**

### 7.2 GREEN Phase - Make Tests Pass
1. **Write minimal code** to make the failing test batch pass
2. **Avoid over-engineering** in this phase
3. **Focus on making it work**, not making it perfect
4. **Verify all tests in the batch pass**

### 7.3 Iterative Development
- **Next iteration**: Add new test cases for extended requirements
- **Preserve existing**: Ensure new implementation doesn't break existing tests
- **Incremental progress**: Build functionality step by step through test-driven cycles

## 8. Test Structure Examples

### 8.1 Unit Test Structure

```kotlin
@ExtendWith(MockKExtension::class)
class AmountValidatorTest {

    @InjectMockKs // use annotation when possible. Put a validated class on the top, then mocks
    private lateinit var validator: AmountValidator
    
    @Mockk
    private lateinit var configuration: PaymentConfiguration
    
    @BeforeEach
    fun setUp() {
        // do an extra setup if needed
    }
    
    // Always use annotations Given/When/Then
    @Test
    fun `should accept valid amount within limits`() {
        // Given
        every { configuration.getMaxAmount() } returns 100.0
        val givenPayment = PaymentMother.of(amount = 50.0)
        
        // When & Then
        validator.validate(givenPayment) // Should not throw
    }
    
    @Test
    fun `should reject amount exceeding maximum limit`() {
        // Given
        every { configuration.getMaxAmount() } returns 100.0
        val givenPayment = PaymentMother.of(amount = 150.0)
        
        // When
        val exception = shouldThrow<ValidationException> {
            validator.validate(givenPayment)
        }
        
        // Then
        exception.code shouldBe AMOUNT_EXCEEDED
        exception.message shouldContain "150.0"
        exception.message shouldContain "100.0"
    }
}
```

Use ParameterizedTest when possible to avoid tests duplications:

```kotlin
@ExtendWith(MockKExtension::class)
class BusinessValidatorTest {
    
    @ParameterizedTest
    @ValueSource(doubles = [0.0, -5.0, -100.0])
    fun `should throw exception when amount is zero or negative`(amount: Double) {
        // Given
        val givenEntity = EntityMother.of(amount = amount)
        
        // When & Then
        val exception = shouldThrow<ValidationException> {
            validator.validate(givenEntity)
        }
        exception.code shouldBe INVALID_AMOUNT
    }
}
```

### 8.2 Integration Test Structure

```kotlin
class PaymentServiceIntegrationTest {
    
    private lateinit var paymentService: PaymentService
    private lateinit var storage: PaymentStorage
    private lateinit var configuration: PaymentConfiguration
    
    @BeforeEach
    fun setUp() {
        storage = InMemoryPaymentStorage()
        configuration = DefaultPaymentConfiguration()
        
        val validators = listOf(
            AmountValidator(configuration),
            CurrencyValidator(configuration),
            UserLimitValidator(storage, configuration)
        )
        
        paymentService = PaymentService(storage, validators)
    }
    
    @Test
    fun `should process valid payment end-to-end`() {
        // Given
        val givenPayment = PaymentMother.of(
            amount = 25.0,      // Within limits
            currency = "EUR"    // Valid currency
        )
        
        // When
        paymentService.process(givenPayment)
        
        // Then
        val storedPayments = storage.findByUserId(givenPayment.userId)
        storedPayments shouldHaveSize 1
        storedPayments[0] shouldBe givenPayment
    }
    
    @Test
    fun `should enforce user total limit across multiple payments`() {
        // Given
        val givenUserId = Rand.validUserId()
        val givenFirstPayment = PaymentMother.of(userId = givenUserId, amount = 80.0)
        val givenSecondPayment = PaymentMother.of(userId = givenUserId, amount = 30.0)
        
        // When
        paymentService.process(givenFirstPayment) // Should succeed
        
        // Then
        val exception = shouldThrow<ValidationException> {
            paymentService.process(givenSecondPayment) // Should fail (80 + 30 > 100)
        }
        
        exception.code shouldBe USER_LIMIT_EXCEEDED
    }
}
```

## 9. Exception Testing

### 9.1 Typed Exception Hierarchy

```kotlin
enum class PaymentErrorCode {
    INVALID_AMOUNT,
    INVALID_CURRENCY,
    USER_LIMIT_EXCEEDED,
    REQUIRED_FIELD_MISSING
}

class InvalidPaymentException(
    val code: PaymentErrorCode,
    message: String
) : Exception(message)
```

### 9.2 Exception Testing Examples

```kotlin
@Test
fun `should throw typed exception with correct error code`() {
    // Given
    val givenInvalidPayment = PaymentMother.of(amount = -10.0)
    
    // When
    val exception = shouldThrow<InvalidPaymentException> {
        validator.validate(givenInvalidPayment)
    }
    
    // Then
    exception.code shouldBe PaymentErrorCode.INVALID_AMOUNT
}

@ParameterizedTest
@EnumSource(PaymentErrorCode::class)
fun `should handle all payment error codes`(errorCode: PaymentErrorCode) {
    // Given
    val givenInvalidPayment = createInvalidPaymentFor(errorCode)
    
    // When & Then
    val exception = shouldThrow<InvalidPaymentException> {
        validator.validate(givenInvalidPayment)
    }
    
    exception.code shouldBe errorCode
}

private fun createInvalidPaymentFor(errorCode: PaymentErrorCode): Payment =
    when (errorCode) {
        PaymentErrorCode.INVALID_AMOUNT -> PaymentMother.of(amount = -10.0)
        PaymentErrorCode.INVALID_CURRENCY -> PaymentMother.of(currency = "INVALID")
        PaymentErrorCode.USER_LIMIT_EXCEEDED -> createPaymentThatExceedsLimit()
        PaymentErrorCode.REQUIRED_FIELD_MISSING -> PaymentMother.of(userId = "")
    }

private fun createPaymentThatExceedsLimit(): Payment {
    val userId = Rand.validUserId()
    // Pre-populate storage to simulate user already near limit
    val existingPayment = PaymentMother.of(userId = userId, amount = 80.0)
    storage.store(existingPayment)
    return PaymentMother.of(userId = userId, amount = 30.0) // Would exceed 100 limit
}
```

## 10. Common Kotlin Testing Patterns

### 10.1 Extension Functions for Test Assertions

```kotlin
// Custom assertion extensions
fun Payment.shouldBeValid() {
    this.amount shouldBeGreaterThan 0.0
    this.userId shouldNotBe ""
    this.currency shouldBeIn setOf("EUR", "USD", "GBP")
}

fun List<Payment>.shouldContainPayment(expected: Payment) {
    this should contain(expected)
}

// Usage in tests
@Test
fun `should create valid payment`() {
    // Given & When
    val actualPayment = PaymentMother.of()
    
    // Then
    actualPayment.shouldBeValid()
}
```

### 10.2 Data-Driven Tests with CSV Source

```kotlin
@ParameterizedTest
@CsvSource(
    "user1, 25.0, EUR, true",
    "user2, 30.0, USD, true",
    "user3, 35.0, GBP, false",  // Amount too high
    "'', 25.0, EUR, false",      // Empty user ID
    "user4, -10.0, EUR, false",  // Negative amount
    "user5, 25.0, JPY, false"   // Invalid currency
)
fun `should validate payment with various inputs`(
    givenUserId: String,
    givenAmount: Double,
    givenCurrency: String,
    shouldBeValid: Boolean
) {
    // Given
    every { configuration.getMaxAmount() } returns 30.0
    every { configuration.getAllowedCurrencies() } returns setOf("EUR", "USD", "GBP")

    val givenPayment = Payment(givenUserId, givenAmount, givenCurrency)

    // When & Then
    if (shouldBeValid) {
        validator.validate(givenPayment) // Should not throw
    } else {
        shouldThrow<InvalidPaymentException> {
            validator.validate(givenPayment)
        }
    }
}
```

## 11. What not to test

- Object creation
- Standard types validation, e.q. UUID, LocalDate
