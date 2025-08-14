package com.company.examples.domain.handler

import com.company.api.commons.model.Model
import com.company.api.commons.utils.IdProvider
import com.company.api.commons.utils.TimeProvider
import com.company.api.commons.validation.exception.ValidationException
import com.company.libs.testing.Rand
import com.company.libs.testing.mother.ValidationErrorMother
import com.company.examples.domain.model.Example
import com.company.examples.domain.mother.CreateExampleCommandMother
import com.company.examples.domain.port.storage.ExampleStoragePort
import com.company.examples.domain.validator.ExampleValidator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant
import java.util.*

@ExtendWith(MockKExtension::class)
class CreateExampleUseCaseTest {

    private lateinit var useCase: CreateExampleUseCase

    @MockK
    private lateinit var emailValidator: ExampleValidator

    @RelaxedMockK
    private lateinit var storagePort: ExampleStoragePort

    @MockK
    private lateinit var idProvider: IdProvider<UUID>

    @MockK
    private lateinit var timeProvider: TimeProvider

    @BeforeEach
    fun setUp() {
        useCase = CreateExampleUseCase(
            validators = listOf(emailValidator),
            storagePort = storagePort,
            idProvider = idProvider,
            timeProvider = timeProvider
        )
    }

    @Test
    fun `should create user when it is valid`() {
        // Given
        val givenCommand = CreateExampleCommandMother.of()
        val expectedId = Rand.uuid()
        val expectedTime = Instant.now()

        every { emailValidator.validate(givenCommand) } returns emptyList()
        every { idProvider.generate() } returns expectedId
        every { timeProvider.now() } returns expectedTime
        every { storagePort.create(any()) } just runs

        // When
        val actualResult = useCase.execute(givenCommand)

        // Then
        val expectedResult = Model(
            id = expectedId,
            version = 0,
            createdAt = expectedTime,
            updatedAt = expectedTime,
            data = Example(
                email = givenCommand.email,
                country = givenCommand.country
            )
        )

        actualResult shouldBe expectedResult
        verify { storagePort.create(expectedResult) }
    }

    @Test
    fun `should reject user when it is not valid`() {
        // Given
        val givenCommand = CreateExampleCommandMother.of()

        val expectedError = ValidationErrorMother.of()
        every { emailValidator.validate(givenCommand) } returns listOf(expectedError)

        // When
        val exception = shouldThrow<ValidationException> {
            useCase.execute(givenCommand)
        }

        // Then
        exception.errors shouldHaveSize 1
        exception.errors[0] shouldBe expectedError

        verify(exactly = 0) { storagePort.create(any()) }
    }
}