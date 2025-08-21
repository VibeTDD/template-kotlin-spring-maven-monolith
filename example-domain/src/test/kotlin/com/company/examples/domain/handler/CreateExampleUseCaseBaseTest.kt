package com.company.examples.domain.handler

import com.company.api.commons.model.Model
import com.company.api.commons.model.Range
import com.company.api.commons.utils.IdProvider
import com.company.api.commons.utils.TimeProvider
import com.company.api.commons.validation.exception.ValidationError
import com.company.api.commons.validation.exception.ValidationException
import com.company.examples.domain.model.Example
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.domain.mother.CreateExampleCommandMother
import com.company.examples.domain.port.config.ExampleConfigPort
import com.company.examples.domain.port.storage.ExampleStoragePort
import com.company.examples.domain.validator.CreateExampleCommandValidator
import com.company.libs.testing.Rand
import com.company.libs.testing.utils.shouldNotThrowAnyWithDetails
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import java.math.BigDecimal
import java.time.Instant
import java.util.*

abstract class CreateExampleUseCaseBaseTest {

    protected lateinit var useCase: CreateExampleUseCase

    protected var exampleConfigPort: ExampleConfigPort = mockk()
    protected var exampleStoragePort: ExampleStoragePort = mockk(relaxed = true)
    protected var idProvider: IdProvider<UUID> = mockk()
    protected var timeProvider: TimeProvider = mockk()

    protected lateinit var validCommand: CreateExampleCommand
    protected lateinit var configuredAllowedCurrencies: Set<String>
    protected lateinit var configuredRange: Range<BigDecimal>
    protected lateinit var generatedId: UUID
    protected lateinit var generatedTime: Instant

    @BeforeEach
    fun setUp() {
        useCase = CreateExampleUseCase(
            exampleValidator = CreateExampleCommandValidator(
                exampleConfigPort = exampleConfigPort,
                exampleStoragePort = exampleStoragePort
            ),
            storagePort = exampleStoragePort,
            idProvider = idProvider,
            timeProvider = timeProvider
        )

        validCommand = CreateExampleCommandMother.of(
            country = "CA",
            salary = Rand.amount(200.00, 300.00)
        )
        configuredAllowedCurrencies = setOf("USA", "CA")
        configuredRange = Range(BigDecimal(200.00), BigDecimal(300.00))
        generatedId = Rand.uuid()
        generatedTime = Rand.instant()

        setupValidMocks()
    }

    protected fun setupValidMocks() {
        every { exampleConfigPort.getAllowedCountries() } returns configuredAllowedCurrencies
        every { exampleConfigPort.getSalaryRange() } returns configuredRange
        every { idProvider.generate() } returns generatedId
        every { timeProvider.now() } returns generatedTime
    }

    protected fun shouldBeInvalid(command: CreateExampleCommand, errors: List<ValidationError>) {
        // When
        val actualException = shouldThrow<ValidationException> { useCase.execute(command) }

        // Then
        actualException.errors shouldBe errors
        verify(exactly = 0) { exampleStoragePort.create(any()) }
    }

    protected fun shouldBeInvalid(command: CreateExampleCommand, error: ValidationError) =
        shouldBeInvalid(command, listOf(error))

    protected fun shouldBeValid(command: CreateExampleCommand) {
        // When
        val actualResult = shouldNotThrowAnyWithDetails { useCase.execute(command) }

        // Then
        val expectedResult = Model(
            id = generatedId,
            version = 0,
            createdAt = generatedTime,
            updatedAt = generatedTime,
            data = Example(
                email = command.email,
                country = command.country,
                salary = command.salary,
            )
        )

        actualResult shouldBe expectedResult
        verify { exampleStoragePort.create(expectedResult) }
    }
}