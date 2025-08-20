package com.company.examples.domain.handler

import com.company.api.commons.utils.IdProvider
import com.company.api.commons.utils.TimeProvider
import com.company.api.commons.validation.exception.ValidationException
import com.company.examples.domain.model.command.CreateExampleCommand
import com.company.examples.domain.mother.CreateExampleCommandMother
import com.company.examples.domain.port.config.BusinessSpecificConfigPort
import com.company.examples.domain.port.storage.ExampleStoragePort
import com.company.examples.domain.validator.CreateExampleCommandValidator
import com.company.libs.testing.Rand
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import java.util.*

abstract class CreateExampleUseCaseBaseTest {

    protected lateinit var useCase: CreateExampleUseCase

    protected var businessSpecificConfigPort: BusinessSpecificConfigPort = mockk()
    protected var exampleStoragePort: ExampleStoragePort = mockk(relaxed = true)
    protected var idProvider: IdProvider<UUID> = mockk()
    protected var timeProvider: TimeProvider = mockk()

    protected lateinit var validCommand: CreateExampleCommand

    @BeforeEach
    fun setUp() {
        useCase = CreateExampleUseCase(
            exampleValidator = CreateExampleCommandValidator(
                businessSpecificConfigPort = businessSpecificConfigPort,
                exampleStoragePort = exampleStoragePort
            ),
            storagePort = exampleStoragePort,
            idProvider = idProvider,
            timeProvider = timeProvider
        )

        validCommand = CreateExampleCommandMother.of(
            country = "CA"
        )

        setupValidMocks()
    }

    protected fun setupValidMocks() {
        every { businessSpecificConfigPort.getAllowedCountries() } returns setOf("USA", "CA")
        every { idProvider.generate() } returns Rand.uuid()
        every { timeProvider.now() } returns Rand.instant()
    }

    protected fun shouldBeInvalid(command: CreateExampleCommand, vararg errorCodes: String) {
        // When
        val actualException = shouldThrow<ValidationException> {  useCase.execute(command) }

        // Then
        actualException.errors.map { it.code } shouldBe errorCodes.toList()
        verify(exactly = 0) { exampleStoragePort.create(any()) }
    }
}