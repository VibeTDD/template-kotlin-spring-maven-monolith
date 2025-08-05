# Kotlin Common Conventions

## 1. Data Classes and Object Design

### 1.1 When to Use Data Classes
Use data classes for pure data containers with no business logic:

```kotlin
// ✅ Good - Pure data container
data class User(
    val name: String,
    val email: String,
)

// ❌ Bad - Business logic in data class
data class Payment(
    val amount: Double,
    val currency: String
) {
    fun validate() { ... }  // Business logic doesn't belong here
}
```

### 1.2 Regular Classes for Business Logic
Use regular classes when objects have behavior:

```kotlin
// ✅ Good - Business logic in regular class
class PaymentValidator(
    private val configuration: PaymentConfiguration
) {
    fun validate(payment: Payment) {
        // Validation logic here
    }
}
```

### 1.3 Immutability Preferences
Prefer `val` over `var` for immutable design:

```kotlin
// ✅ Good - Immutable properties
data class PaymentResult(
    val processedAt: Instant,
    val transactionId: String
)

// ❌ Bad unless necessary
data class MutableCounter(
    var count: Int = 0
) {
    fun increment() { count++ }  // Prefer functional approaches
}
```

## 2. Null Safety and Control Flow

### 2.1 Fast Return Strategy
Use early returns to reduce nesting and improve readability:

```kotlin
// ✅ Good - Fast return with exception
fun process(userId: String) {
    val user = find(userId) ?: throw NotFoundException("User not found: $userId")
    processData(user)
}

// ✅ Good - Fast return with null
fun findEmail(userId: String): String? {
    val user = find(userId) ?: return null
    return user.email
}

// ✅ Good - Fast return with default
fun getDisplayName(userId: String): String {
    val user = find(userId) ?: return "Unknown User"
    return user.name.ifEmpty { "Anonymous" }
}

// ❌ Bad - Nested structure
fun process(userId: String): ProcessedUser? {
    val user = find(userId)
    if (user != null) {
        return processData(user)
    } else {
        throw NotFoundException("User not found: $userId")
    }
}
```

### 2.2 Guard Clauses Over Nested Conditions
Use guard clauses to handle negative conditions early:

```kotlin
// ✅ Good - Guard clauses
fun processAddress(user: User) {
    val address = user.address ?: return

    validateAddress(address)
    enrichAddressData(address)
    updateAddressHistory(address)
}

// ✅ Good - Multiple guard clauses
fun validate(payment: Payment) {
    if (payment.amount <= 0) throw ValidationException("Invalid amount")
    if (payment.currency.isEmpty()) throw ValidationException("Currency required")
    if (payment.userId.isEmpty()) throw ValidationException("User ID required")
}

// ❌ Bad -  Nested if-else structure
fun processAddress(user: User) {
    val address = user.address
    if (address != null) {
        validateAddress(address)
        enrichAddressData(address)
        updateAddressHistory(address)
    } else {
        // Empty else branch
    }
}
```

### 2.3 Avoid Complex Conditional Expressions
Break complex conditions into readable parts:

```kotlin
// ✅ Good - Extract complex conditions
fun canProcessPayment(payment: Payment, user: User): Boolean {
    if (!user.isActive()) return false
    if (!isValidPaymentAmount(payment.amount)) return false
    if (!isSupportedCurrency(payment.currency)) return false

    return true
}

private fun isValidPaymentAmount(amount: Double): Boolean = amount > 0 && amount <= 10000
private fun isSupportedCurrency(currency: String): Boolean = currency in SUPPORTED_CURRENCIES

// ❌ Bad - Complex nested conditions  
fun canProcessPayment(payment: Payment, user: User): Boolean {
    if (user.isActive() && payment.amount > 0 && payment.amount <= 10000 &&
        (payment.currency == "EUR" || payment.currency == "USD" || payment.currency == "GBP")) {
        return true
    } else {
        return false
    }
}
```

### 2.4 Expression Body Functions
Use expression body syntax when functions have single expressions:

```kotlin
// ✅ Good - Expression body for simple functions
fun calculateTax(amount: Double): Double = amount * 0.21

fun isValidEmail(email: String): Boolean = email.contains("@") && email.contains(".")

fun getDisplayName(user: User): String = user.name.ifEmpty { "Anonymous" }

// ✅ Good - Expression body with type inference
fun findActiveUsers(users: List<User>): List<User> = users.filter { it.isActive() }

// ✅ Good - Block body for complex logic
fun processPayment(payment: Payment): PaymentResult {
    if (payment.amount <= 0) throw ValidationException("Invalid amount")
    if (!isSupportedCurrency(payment.currency)) throw ValidationException("Unsupported currency")

    return PaymentResult.success(processValidPayment(payment))
}

// ❌ Bad - Block body for simple function
fun calculateTax(amount: Double): Double {
    return amount * 0.21
}
```

### 2.5 Avoid Nullable Collections
Prefer empty collections over null collections:

```kotlin
// ✅ Good - Return empty list instead of null
fun getPermissions(userId: String): List<Permission> = storage.findPermissions(userId) ?: emptyList()


// ❌ Bad -  Nullable collections are harder to work with
fun getPermissions(userId: String): List<Permission>? = storage.findPermissions(userId)
```

## 3. Collections and Functional Programming

### 3.1 Lambda Parameter Preferences
Use Kotlin's implicit `it` parameter for simple lambdas:

```kotlin
// ✅ Good - Use implicit `it` for simple operations
fun getActiveUserEmails(users: List<User>): List<String> = users
    .filter { it.status == UserStatus.ACTIVE }
    .map { it.email }
    .sorted()

// ✅ Good - Use implicit `it` for single operations
fun getAboveAge(users: List<User>, minAge: Int) = users.filter { it.age > minAge }

// ✅ Good - Named parameters for complex lambdas
fun process(users: List<User>): List<ProcessedUser> = users.map { user ->
    ProcessedUser(
        id = user.id,
        displayName = user.name.ifEmpty { "Anonymous" },
        permissions = calculatePermissions(user),
        lastActivity = user.lastLoginAt ?: user.createdAt
    )
}

// ❌ Bad -  Named parameter for simple operations
fun getActive(users: List<User>) = users.filter { user -> user.status == UserStatus.ACTIVE }
```

### 3.2 Collection Type Preferences
Choose appropriate collection types for use case:

```kotlin
// ✅ Good - List for ordered data
data class PaymentHistory(
    val payments: List<Payment>  // Order matters
)

// ✅ Good - Set for unique values
data class UserPermissions(
    val permissions: Set<Permission>  // No duplicates
)

// ✅ Good - Map for key-value relationships
class UserCache(
    private val cache: Map<String, User>
)
```

### 3.3 Mutable vs Immutable Collections
Prefer immutable collections in public APIs:

```kotlin
// ✅ Good - Immutable return type
class UserService {
    fun getList(): List<User> = users.toList()  // Immutable view
}

// ✅ Good - Mutable for internal processing
class UserProcessor {
    fun process(users: List<User>): List<ProcessedUser> {
        val mutableResults = mutableListOf<ProcessedUser>()
        users.forEach { mutableResults.add(processUser(it)) }
        return mutableResults.toList()  // Return immutable
    }
}
```

## 4. String Handling and Templates

### 4.1 String Templates vs Concatenation
Prefer string templates for readability:

```kotlin
// ✅ Good - String templates
fun createWelcomeMessage(user: User): String = "Welcome ${user.name}! Your account ${user.id} is now active."

// ✅ Good for complex expressions
fun formatPaymentSummary(payment: Payment): String
        = "Payment of ${payment.amount} ${payment.currency} " +
        "processed at ${payment.processedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)}"

// ❌ Bad -  String concatenation
fun createMessage(user: User): String = "Welcome " + user.name + "! Your account " + user.id + " is active."
```

### 4.2 Multi-line String Handling
Use raw strings for multi-line content:

```kotlin
// ✅ Good - Raw string for SQL or templates
fun getQuery(status: String): String = """
        SELECT u.id, u.name, u.email 
        FROM users u 
        WHERE u.status = '$status'
        ORDER BY u.created_at DESC
    """.trimIndent()

// ✅ Good - Raw string for JSON templates
fun createNotificationTemplate(user: User): String = """
        {
            "userId": "${user.id}",
            "message": "Welcome ${user.name}",
            "timestamp": "${Instant.now()}"
        }
    """.trimIndent()
```

## 5. Scope Functions Usage

### 5.1 When to Use Each Scope Function
Choose scope functions based on context and intent:

```kotlin
// ✅ Use `let` for null-safe operations
fun process(userId: String) {
    find(userId)?.let { user ->
        validate(user)
        enrichData(user)
        save(user)
    }
}

// ✅ Use `apply` for object configuration
fun createRequest(): PaymentRequest = PaymentRequest().apply {
    amount = 100.0
    currency = "EUR"
    description = "Service payment"
}

// ✅ Use `with` for multiple operations on same object
fun format(user: User): String = with(user) {
    "User: $name ($email) - Status: $status"
}

// ✅ Use `run` for code blocks with return value
fun calculateTotal(items: List<Item>): Double = items.run {
    filter { it.isValid }
        .map { it.price }
        .sum()
}

// ✅ Use `also` for side effects
fun process(payment: Payment): Payment = payment.also {
    logger.info("Processing payment ${it.id}")
    auditService.logPayment(it)
}
```

### 5.2 Scope Function Anti-Patterns
Avoid overusing or nesting scope functions:

```kotlin
// ❌ Bad -  Nested scope functions reduce readability
fun complexProcessing(user: User) {
    user.let { u ->
        u.address?.let { addr ->
            addr.country?.let { country ->
                validateCountry(country)
            }
        }
    }
}

// ✅ Better - Clear null checks
fun complexProcessing(user: User) {
    val address = user.address ?: return
    val country = address.country ?: return
    validateCountry(country)
}
```

## 6. Extension Functions and Object Mapping

### 6.1 When to Create Extension Functions
Create extensions for frequently used operations on types you don't control:

```kotlin
// ✅ Good - Extend external types
fun String.isValidEmail(): Boolean = this.contains("@") && this.contains(".")

fun Instant.toDisplayString(): String = this.atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

// ✅ Good - Domain-specific extensions
fun List<Payment>.totalAmount(): Double = this.sumOf { it.amount }

fun User.isActive(): Boolean = this.status == UserStatus.ACTIVE
```

### 6.2 Object Mapping Extensions
Use extension functions for model transformations between layers:

```kotlin
// UserDtoMapper.kt - DTO to Domain mapping
fun UserV1.toModel() = User(
    id = this.id,
    name = this.name,
    email = this.email,
    createdAt = Instant.parse(this.createdAt)
)

fun List<UserV1>.toModel() = this.map { it.toModel() }

// UserDocMapper.kt - Domain to Data mapping  
fun User.toDoc() = UserDoc(
    id = this.id.toString(),
    name = this.name,
    email = this.email,
    createdAt = this.createdAt.toString()
)

// UserEntityMapper.kt - Domain to SQL Entity mapping
fun User.toEntity() = UserEntity(
    id = this.id,
    name = this.name,
    email = this.email,
    createdAt = this.createdAt
)
```

### 6.3 Extension Function Organization
Organize mapping extensions in dedicated files:

```kotlin
// UserDtoMapper.kt - All DTO mappings
fun UserV1.toModel() = User()
fun CreateUserRequestV1.toModel() = CreateUserData()
fun User.toUserV1() = UserV1()

// UserDocMapper.kt - All NoSQL document mappings  
fun User.toDoc() = UserDoc()
fun UserDoc.toModel() = User()

// UserEntityMapper.kt - All SQL entity mappings
fun User.toEntity() = UserEntity()
fun UserEntity.toModel() = User()
```

### 6.4 Avoid Extensions for Core Business Logic
Don't put business logic in extensions:

```kotlin
// ✅ Good - Keep business logic in services
class PaymentService {
    fun process(payment: Payment): PaymentResult {
        // Business logic here
    }
}

// ❌ Bad -  Business logic shouldn't be in extensions
fun Payment.process(): PaymentResult {
    // Complex business logic here - belongs in service
}
```

## 7. Sealed Classes and Enums

### 7.1 Sealed Classes for State Representation
Use sealed classes for representing finite states:

```kotlin
// ✅ Good - Payment processing states
sealed class PaymentResult {
    data class Success(
        val transactionId: String,
        val processedAt: Instant
    ) : PaymentResult()

    data class Failed(
        val error: PaymentError,
        val retryable: Boolean
    ) : PaymentResult()

    data object Pending : PaymentResult()
}

// Usage with when expression
fun handlePaymentResult(result: PaymentResult) {
    when (result) {
        is PaymentResult.Success -> logger.info("Payment ${result.transactionId} completed")
        is PaymentResult.Failed -> handleFailure(result.error, result.retryable)
        is PaymentResult.Pending -> scheduleRetry()
    }
}
```

### 7.2 Enums for Simple Value Sets
Use enums for simple, stable value sets:

```kotlin
// ✅ Good - Stable value set
enum class PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELLED
}

// ✅ Good - With properties
enum class Currency(val symbol: String, val decimalPlaces: Int) {
    EUR("€", 2),
    USD("$", 2),
    JPY("¥", 0)
}
```

## 8. Naming Conventions

### 8.1 Method Names in Domain Services
Avoid repeating the domain name in method names when the context is clear:

```kotlin
// ✅ Good - Context is clear from class name
class UserService {
    fun create(userData: CreateUserData): User = ...
    fun findById(id: UUID): User? = ...
    fun update(id: UUID, updates: UserUpdates): User = ...
    fun delete(id: UUID): Boolean = ...
    fun list(filters: UserFilters): List<User> = ...
}

// ❌ Bad - Redundant domain name repetition
class UserService {
    fun createUser(userData: CreateUserData): User = ...
    fun findUser(id: UUID): User? = ...
    fun updateUser(id: UUID, updates: UserUpdates): User = ...
    fun deleteUser(id: UUID): Boolean = ...
    fun listUsers(filters: UserFilters): List<User> = ...
}
```

### 8.2 Exception to the Rule
Include domain name when it adds clarity or prevents ambiguity:

```kotlin
// ✅ Good - Domain name adds clarity
class NotificationService {
    fun sendEmail(emailData: EmailData): EmailResult = ...
    fun sendSms(smsData: SmsData): SmsResult = ...
    fun sendPushNotification(pushData: PushData): PushResult = ...
}

class ImportService {
    fun importUsers(csvData: CsvData): ImportResult = ...
    fun importPayments(xmlData: XmlData): ImportResult = ...
    fun exportUsers(filters: UserFilters): ExportResult = ...
}

// ✅ Good - Prevents ambiguity with generic operations
class DataMigrationService {
    fun migrateUsers(): MigrationResult = ...
    fun migratePayments(): MigrationResult = ...
    fun validateUserData(): ValidationResult = ...
    fun validatePaymentData(): ValidationResult = ...
}
```

### 8.3 Extension Function Naming
Apply the same principle to extension functions:

```kotlin
// ✅ Good - Context clear from receiver type
fun User.isActive(): Boolean = this.status == UserStatus.ACTIVE
fun Payment.isProcessed(): Boolean = this.status == PaymentStatus.COMPLETED
fun List<Payment>.totalAmount(): Double = this.sumOf { it.amount }

// ❌ Bad - Redundant repetition
fun User.isUserActive(): Boolean = this.status == UserStatus.ACTIVE
fun Payment.isPaymentProcessed(): Boolean = this.status == PaymentStatus.COMPLETED
```

## 9. Code Organization and Structure

### 9.1 Function Ordering in Classes
Order functions by visibility: public → protected → private:

```kotlin
class PaymentService {
    // Public functions first
    fun process(payment: Payment): PaymentResult = processInternal(payment)

    // Protected functions (if any)
    protected fun validateBeforeProcessing(payment: Payment) {
        // Implementation details
    }

    // Private functions last
    private fun processInternal(payment: Payment): PaymentResult {
        // Implementation details
    }
}
```

### 9.2 Type Specification Guidelines
Specify return types when not immediately clear:

```kotlin
// ✅ Good - Type inference is clear
fun UserDoc.toModel() = User()

// ✅ Good - Specify type when not obvious
fun processPayments(payments: List<Payment>): Map<String, PaymentResult>
        = payments.associate { it.id to processPayment(it) }

fun loadConfiguration(): PaymentConfiguration = configurationLoader.load()  // Return type not obvious from call

// ✅ Good - Specify type for public API clarity
interface PaymentProcessor {
    fun process(payment: Payment): PaymentResult  // Always specify in interfaces
}

// ❌ Bad - Type is not clear 
fun getActive(users: List<User>) = users.filter { it.isActive() }
fun process(userid: UUID) {
    val user = userService.get(userid)
}
```

## 10. Type Aliases for Clarity

### 10.1 When to Use Type Aliases
Create type aliases to improve code readability:

```kotlin
// ✅ Good - Clarify generic types
typealias UserId = UUID
typealias PaymentId = UUID
typealias ValidationErrors = List<ValidationError>

// ✅ Good - Simplify complex function types
typealias PaymentProcessor = (Payment) -> PaymentResult
typealias UserValidator = (User) -> ValidationErrors
```

### 10.2 Avoid Overusing Type Aliases
Don't create aliases for simple types:

```kotlin
// ❌ Bad -  Unnecessary aliases
typealias Name = String
typealias Age = Int
typealias IsActive = Boolean

// ✅ Better - Use meaningful names in context
data class User(
    val name: String,
    val age: Int,
    val isActive: Boolean
)
```